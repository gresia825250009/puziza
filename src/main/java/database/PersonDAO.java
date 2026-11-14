package main.java.database;
/*
 * Class: PersonDAO
 * Deskripsi: Mengelola data customer dan kasir di database.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// Model kasir dan customer yang dibuat dari baris-baris hasil query.
import main.java.model.Cashier;
import main.java.model.Customer;

// Kelas JDBC untuk menjalankan query, mengelola transaksi, dan membaca hasil.
import java.sql.*;
// LocalDate untuk mengubah nilai tanggal SQL ke tanggal Java.
import java.time.LocalDate;
// Koleksi untuk mengembalikan beberapa customer atau kasir.
import java.util.*;
// Menyediakan operasi database untuk customer dan kasir.
public class PersonDAO {
    // Mengambil data customer beserta alamat emailnya.
    public List<Customer> getCustomers() throws SQLException {
        // Menampung objek customer yang akan dikembalikan ke controller.
        List<Customer> out = new ArrayList<>();
        // SELECT menggabungkan identitas persons dengan email customers.
        // JOIN hanya mengambil person yang memang memiliki detail customer.
        String q = "SELECT p.id, p.full_name, p.phone, p.registered_at, c.email "
                + "FROM persons p JOIN customers c ON c.person_id = p.id";
        try (Connection c = PostgreSQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(q);
             ResultSet r = ps.executeQuery()) {
            while (r.next()) {
                out.add(new Customer(
                        r.getInt(1),
                        r.getString(2),
                        r.getString(3),
                        r.getDate(4).toLocalDate(),
                        r.getString(5)));
            }
        }
        return out;
    }
    // Menyimpan data person dan detail customer secara berurutan.
    public void addCustomer(Customer x) throws SQLException {
        // Transaksi memastikan data utama persons dan detail customers tersimpan bersama.
        try (Connection c = PostgreSQLConnection.getConnection()) {
            c.setAutoCommit(false);
            // INSERT persons membuat identitas customer; database mengembalikan ID baru.
            try (PreparedStatement p = c.prepareStatement(
                    "INSERT INTO persons(full_name,phone,registered_at,person_type) "
                            + "VALUES(?,?,?, 'CUSTOMER')",
                    Statement.RETURN_GENERATED_KEYS)) {
                p.setString(1, x.getName());
                p.setString(2, x.getPhone());
                p.setDate(3, java.sql.Date.valueOf(x.getRegisteredAt()));
                p.executeUpdate();
                // INSERT customers menyimpan email dengan foreign key ID dari persons.
                try (ResultSet k = p.getGeneratedKeys()) {
                    k.next();
                    try (PreparedStatement d = c.prepareStatement(
                            "INSERT INTO customers(person_id,email) VALUES(?,?)")) {
                        d.setInt(1, k.getInt(1));
                        d.setString(2, x.getEmail());
                        d.executeUpdate();
                    }
                }
            }
            c.commit();
        }
    }
    // Memvalidasi dan memperbarui data customer dalam satu transaksi.
    public void updateCustomer(Customer customer) throws SQLException {
        // Validasi sebelum query mencegah data yang tidak sah masuk ke database.
        if (customer.getId() <= 0) throw new IllegalArgumentException("Pilih customer yang ingin diubah.");
        if (!customer.validate()) {
            throw new IllegalArgumentException("Nama dan nomor telepon (8-15 digit) wajib valid.");
        }
        try (Connection c = PostgreSQLConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // UPDATE persons mengubah nama/telepon hanya untuk ID customer tersebut.
                try (PreparedStatement person = c.prepareStatement(
                        "UPDATE persons SET full_name=?,phone=? WHERE id=? AND person_type='CUSTOMER'")) {
                    person.setString(1, customer.getName().trim());
                    person.setString(2, customer.getPhone().trim());
                    person.setInt(3, customer.getId());
                    if (person.executeUpdate() != 1) throw new SQLException("Data customer tidak ditemukan.");
                }
                // UPDATE customers mengubah email pada baris detail customer.
                try (PreparedStatement details = c.prepareStatement(
                        "UPDATE customers SET email=? WHERE person_id=?")) {
                    details.setString(1, customer.getEmail());
                    details.setInt(2, customer.getId());
                    if (details.executeUpdate() != 1) throw new SQLException("Data customer tidak ditemukan.");
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }
    public void deletePerson(int id) throws SQLException {
        // DELETE menghapus person berdasarkan ID; aturan foreign key menentukan
        // apakah detail terkait ikut terhapus atau penghapusan ditolak.
        try (Connection c = PostgreSQLConnection.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM persons WHERE id=?")) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }

    // ================= KASIR =================
    // Mengubah password menjadi hash SHA-256 sebelum disimpan.
    private static String hash(String pw) {
        // Menghasilkan teks heksadesimal hash SHA-256 dari password sebelum disimpan.
        try {
            byte[] d = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(pw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    // Membentuk objek kasir dari satu baris hasil query database.
    private static Cashier cashierFrom(ResultSet r) throws SQLException {
        // Memetakan kolom-kolom hasil query kasir ke objek model Cashier.
        java.sql.Date birthDate = r.getDate(7);
        return new Cashier(
                r.getInt(1),
                r.getString(2),
                r.getString(3),
                r.getDate(4).toLocalDate(),
                r.getString(5),
                null,
                r.getString(6),
                birthDate == null ? null : birthDate.toLocalDate());
    }

    private static final String CASHIER_SQL =
        // Query dasar yang menggabungkan profil persons dan akun login cashiers.
        "SELECT p.id,p.full_name,p.phone,p.registered_at,k.username,p.gender,p.birth_date " +
        "FROM persons p JOIN cashiers k ON k.person_id=p.id";

    // Mengambil data profil dan login seluruh kasir.
    public List<Cashier> getCashiers() throws SQLException {
        // SELECT dasar ditambah ORDER BY untuk menampilkan kasir berdasarkan ID.
        List<Cashier> out = new ArrayList<>();
        try (Connection c = PostgreSQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(CASHIER_SQL + " ORDER BY p.id");
             ResultSet r = ps.executeQuery()) {
            while (r.next()) {
                out.add(cashierFrom(r));
            }
        }
        return out;
    }

    // Memvalidasi profil dan membuat person serta akun login kasir.
    public void addCashier(
            String name,
            String username,
            String password,
            String gender,
            LocalDate birthDate) throws SQLException {
        // Memeriksa input sebelum membuat baris person dan akun kasir.
        String cashierName = name == null ? "" : name.trim();
        String cashierUsername = username == null ? "" : username.trim();
        validateCashierProfile(cashierName, cashierUsername, gender, birthDate);
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password wajib diisi.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password kasir minimal 6 karakter.");
        }

        try (Connection c = PostgreSQLConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int personId;
                // INSERT persons menyimpan profil dan mengambil ID yang baru dibuat.
                try (PreparedStatement person = c.prepareStatement(
                        "INSERT INTO persons(full_name,registered_at,person_type,gender,birth_date) "
                                + "VALUES(?,CURRENT_DATE,'CASHIER',?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    person.setString(1, cashierName);
                    person.setString(2, gender);
                    person.setDate(3, java.sql.Date.valueOf(birthDate));
                    person.executeUpdate();
                    try (ResultSet keys = person.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("ID kasir baru tidak berhasil dibuat.");
                        personId = keys.getInt(1);
                    }
                }
                // INSERT cashiers menautkan username dan hash password ke person ID.
                try (PreparedStatement cashier = c.prepareStatement(
                        "INSERT INTO cashiers(person_id,username,password_hash) "
                                + "VALUES(?,?,?)")) {
                    cashier.setInt(1, personId);
                    cashier.setString(2, cashierUsername);
                    cashier.setString(3, hash(password));
                    cashier.executeUpdate();
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    // Memperbarui profil kasir dan mengganti password hanya jika diisi.
    public void updateCashier(int personId, String name, String username, String password,
                              String gender, LocalDate birthDate) throws SQLException {
        // Memvalidasi profil; password boleh kosong agar password lama tidak berubah.
        String cashierName = name == null ? "" : name.trim();
        String cashierUsername = username == null ? "" : username.trim();
        validateCashierProfile(cashierName, cashierUsername, gender, birthDate);
        if (personId <= 0) throw new IllegalArgumentException("Pilih kasir yang ingin diubah.");
        if (password != null && !password.isBlank() && password.length() < 6) {
            throw new IllegalArgumentException("Password kasir minimal 6 karakter.");
        }

        try (Connection c = PostgreSQLConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // UPDATE persons mengubah nama, gender, dan tanggal lahir profil kasir.
                try (PreparedStatement person = c.prepareStatement(
                        "UPDATE persons SET full_name=?,gender=?,birth_date=? " +
                                "WHERE id=? AND person_type='CASHIER'")) {
                    person.setString(1, cashierName);
                    person.setString(2, gender);
                    person.setDate(3, java.sql.Date.valueOf(birthDate));
                    person.setInt(4, personId);
                    if (person.executeUpdate() != 1) throw new SQLException("Data kasir tidak ditemukan.");
                }

                // Pilih UPDATE akun tanpa password atau sekaligus mengganti hash password.
                String sql = password == null || password.isBlank()
                        ? "UPDATE cashiers SET username=? WHERE person_id=?"
                        : "UPDATE cashiers SET username=?,password_hash=? WHERE person_id=?";
                try (PreparedStatement cashier = c.prepareStatement(sql)) {
                    cashier.setString(1, cashierUsername);
                    if (password == null || password.isBlank()) {
                        cashier.setInt(2, personId);
                    } else {
                        cashier.setString(2, hash(password));
                        cashier.setInt(3, personId);
                    }
                    if (cashier.executeUpdate() != 1) throw new SQLException("Data login kasir tidak ditemukan.");
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    // Memastikan data profil kasir memenuhi aturan sebelum disimpan.
    private static void validateCashierProfile(String name, String username, String gender, LocalDate birthDate) {
        // Menghentikan penyimpanan jika nama, username, gender, atau tanggal tidak valid.
        if (name.isEmpty() || username.isEmpty()) {
            throw new IllegalArgumentException("Nama dan username wajib diisi.");
        }
        if (!"Male".equals(gender) && !"Female".equals(gender)) {
            throw new IllegalArgumentException("Pilih gender kasir.");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("Tanggal lahir wajib diisi.");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal lahir tidak boleh di masa depan.");
        }
    }

    /** Mengembalikan kasir kalau username + password cocok, selain itu null. */
    // Mencari kasir dengan username dan hash password yang cocok.
    public Cashier authenticate(String username, String password) throws SQLException {
        // SELECT mencari username dan hash yang cocok; tidak ada hasil berarti login gagal.
        try (Connection c = PostgreSQLConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(
                 CASHIER_SQL + " WHERE k.username=? AND k.password_hash=?")) {
            ps.setString(1, username);
            ps.setString(2, hash(password));
            try (ResultSet r = ps.executeQuery()) {
                return r.next() ? cashierFrom(r) : null;
            }
        }
    }

    /** Membuat 2 kasir bawaan kalau belum ada. Aman dipanggil berulang. */
    // Membuat akun kasir awal hanya jika username belum terdaftar.
    public static void seedCashiers() throws SQLException {
        // Data seed dipakai hanya untuk akun bawaan yang belum tercatat.
        String[][] data = {{"Neta", "neta", "neta123"}, {"Gresia", "gresia", "gresia123"}};
        try (Connection c = PostgreSQLConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                for (String[] k : data) {
                    // SELECT 1 memeriksa keberadaan username agar seed tidak menggandakan akun.
                    try (PreparedStatement chk = c.prepareStatement("SELECT 1 FROM cashiers WHERE username=?")) {
                        chk.setString(1, k[1]);
                        try (ResultSet r = chk.executeQuery()) {
                            if (r.next()) {
                                continue;
                            }
                        }
                    }
                    // INSERT persons membuat profil awal dan menghasilkan ID untuk akun.
                    try (PreparedStatement p = c.prepareStatement(
                            "INSERT INTO persons(full_name,person_type) VALUES(?, 'CASHIER')",
                            Statement.RETURN_GENERATED_KEYS)) {
                        p.setString(1, k[0]);
                        p.executeUpdate();
                        try (ResultSet key = p.getGeneratedKeys()) {
                            key.next();
                            // INSERT cashiers menyimpan username dan hash password akun seed.
                            try (PreparedStatement d = c.prepareStatement(
                                    "INSERT INTO cashiers(person_id,username,password_hash) VALUES(?,?,?)")) {
                                d.setInt(1, key.getInt(1));
                                d.setString(2, k[1]);
                                d.setString(3, hash(k[2]));
                                d.executeUpdate();
                            }
                        }
                    }
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Cari customer berdasarkan nama (tanpa peduli huruf besar/kecil); kalau belum ada dibuat baru. */
    // Menggunakan customer bernama sama atau mendaftarkannya jika belum ada.
    public Customer findOrCreateCustomer(String name) throws SQLException {
        // SELECT memakai LOWER agar pencarian nama tidak membedakan kapitalisasi.
        // Jika belum ada, method membuat customer lalu menjalankan SELECT kembali.
        String q = "SELECT p.id, p.full_name, p.phone, p.registered_at, c.email "
                + "FROM persons p "
                + "JOIN customers c ON c.person_id = p.id "
                + "WHERE LOWER(p.full_name) = LOWER(?) "
                + "ORDER BY p.id LIMIT 1";
        try (Connection c = PostgreSQLConnection.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, name);
            try (ResultSet r = ps.executeQuery()) {
                if (r.next()) {
                    return new Customer(
                            r.getInt(1),
                            r.getString(2),
                            r.getString(3),
                            r.getDate(4).toLocalDate(),
                            r.getString(5));
                }
            }
        }
        Customer baru = new Customer(0, name, null, java.time.LocalDate.now(), null);
        addCustomer(baru);
        try (Connection c = PostgreSQLConnection.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, name);
            try (ResultSet r = ps.executeQuery()) {
                r.next();
                return new Customer(
                        r.getInt(1),
                        r.getString(2),
                        r.getString(3),
                        r.getDate(4).toLocalDate(),
                        r.getString(5));
            }
        }
    }
}
