package main.java.controller;
/*
 * Class: PersonDAO
 * Deskripsi: Mengelola data customer dan kasir di database.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

import main.java.database.PostgreSQLConnection;
// Model kasir dan customer yang dibuat dari baris-baris hasil query.
import main.java.model.Cashier;
import main.java.model.Customer;

// BCrypt dipakai untuk hashing & verifikasi password kasir.
import org.mindrot.jbcrypt.BCrypt;

// Kelas JDBC untuk menjalankan query, mengelola transaksi, dan membaca hasil.
import java.sql.*;
// LocalDate untuk mengubah nilai tanggal SQL ke tanggal Java.
import java.time.LocalDate;
// Koleksi untuk mengembalikan beberapa customer atau kasir.
import java.util.*;
// Menyediakan operasi database untuk customer dan kasir.
public class PersonDAO {
    // ================= CUSTOMER =================
    // Mengambil profil customer, termasuk gender, tanggal lahir, dan status member.
    public List<Customer> getCustomers() throws SQLException {
        List<Customer> out = new ArrayList<>();
        String q = "SELECT p.id,p.full_name,p.phone,p.registered_at,c.email,p.gender,p.birth_date,c.subscribed "
                + "FROM persons p JOIN customers c ON c.person_id=p.id ORDER BY p.id";
        try (Connection c = PostgreSQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(q);
             ResultSet r = ps.executeQuery()) {
            while (r.next()) {
                java.sql.Date birth = r.getDate(7);
                out.add(new Customer(r.getInt(1), r.getString(2), r.getString(3),
                        r.getDate(4).toLocalDate(), r.getString(5), r.getString(6),
                        birth == null ? null : birth.toLocalDate(), r.getBoolean(8)));
            }
        }
        return out;
    }

    // Menyimpan profil person dan detail customer dalam satu transaksi.
    public void addCustomer(Customer x) throws SQLException {
        if (!x.validate()) {
            throw new IllegalArgumentException("Nama, nomor HP (8-15 digit), gender, dan birthdate wajib diisi dengan benar.");
        }
        try (Connection c = PostgreSQLConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int personId;
                try (PreparedStatement p = c.prepareStatement(
                        "INSERT INTO persons(full_name,phone,registered_at,role,gender,birth_date) "
                                + "VALUES(?,?,CURRENT_DATE,'CUSTOMER',?,?)", Statement.RETURN_GENERATED_KEYS)) {
                    p.setString(1, x.getName().trim());
                    p.setString(2, x.getPhone().trim());
                    p.setString(3, x.getGender());
                    p.setDate(4, java.sql.Date.valueOf(x.getBirthDate()));
                    p.executeUpdate();
                    try (ResultSet keys = p.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("ID customer baru tidak berhasil dibuat.");
                        personId = keys.getInt(1);
                    }
                }
                try (PreparedStatement d = c.prepareStatement(
                        "INSERT INTO customers(person_id,email,subscribed) VALUES(?,?,?)")) {
                    d.setInt(1, personId);
                    d.setString(2, x.getEmail());
                    d.setBoolean(3, x.isSubscribed());
                    d.executeUpdate();
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    // Memperbarui profil customer beserta status keanggotaannya.
    public void updateCustomer(Customer customer) throws SQLException {
        if (customer.getId() <= 0) throw new IllegalArgumentException("Pilih customer yang ingin diubah.");
        if (!customer.validate()) {
            throw new IllegalArgumentException("Nama, nomor HP (8-15 digit), gender, dan birthdate wajib diisi dengan benar.");
        }
        try (Connection c = PostgreSQLConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement person = c.prepareStatement(
                        "UPDATE persons SET full_name=?,phone=?,gender=?,birth_date=? "
                                + "WHERE id=? AND role='CUSTOMER'")) {
                    person.setString(1, customer.getName().trim());
                    person.setString(2, customer.getPhone().trim());
                    person.setString(3, customer.getGender());
                    person.setDate(4, java.sql.Date.valueOf(customer.getBirthDate()));
                    person.setInt(5, customer.getId());
                    if (person.executeUpdate() != 1) throw new SQLException("Data customer tidak ditemukan.");
                }
                try (PreparedStatement details = c.prepareStatement(
                        "UPDATE customers SET email=?,subscribed=? WHERE person_id=?")) {
                    details.setString(1, customer.getEmail());
                    details.setBoolean(2, customer.isSubscribed());
                    details.setInt(3, customer.getId());
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
    // Mengubah password menjadi hash BCrypt sebelum disimpan (lebih aman dari SHA-256 biasa
    // karena BCrypt otomatis memakai salt acak dan sengaja dibuat lambat agar tahan brute-force).
    private static String hash(String pw) {
        // gensalt() menghasilkan salt acak baru setiap kali dipanggil, log_rounds default = 10
        // (work factor = 2^10). Salt ini ikut disimpan di dalam string hash yang dikembalikan,
        // jadi tidak perlu disimpan terpisah di kolom lain.
        return BCrypt.hashpw(pw, BCrypt.gensalt());
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
        // Kolom k.password_hash ditambahkan di akhir (kolom ke-8) khusus untuk authenticate();
        // cashierFrom() tetap aman karena hanya membaca kolom 1-7.
        "SELECT p.id,p.full_name,p.phone,p.registered_at,k.username,p.gender,p.birth_date,k.password_hash " +
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
                        "INSERT INTO persons(full_name,registered_at,role,gender,birth_date) "
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
                                "WHERE id=? AND role='CASHIER'")) {
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
    // Mencari kasir berdasarkan username, lalu memverifikasi password dengan BCrypt.checkpw().
    public Cashier authenticate(String username, String password) throws SQLException {
        // Dengan BCrypt, hash SELALU berbeda-beda walau password-nya sama (karena salt acak),
        // jadi kita tidak bisa lagi WHERE password_hash = ?. Caranya: ambil dulu baris
        // berdasarkan username saja, lalu cocokkan password dengan hash yang tersimpan.
        try (Connection c = PostgreSQLConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(
                 CASHIER_SQL + " WHERE k.username=?")) {
            ps.setString(1, username);
            try (ResultSet r = ps.executeQuery()) {
                if (!r.next()) {
                    return null;
                }
                String storedHash = r.getString(8);
                // checkpw mengekstrak salt dari storedHash sendiri, lalu hash ulang password
                // kandidat dengan salt itu dan membandingkan hasilnya.
                if (!BCrypt.checkpw(password, storedHash)) {
                    return null;
                }
                return cashierFrom(r);
            }
        }
    }

    /** Membuat 2 kasir bawaan kalau belum ada. Aman dipanggil berulang. */
    // Membuat akun kasir awal hanya jika username belum terdaftar.
    public static void seedCashiers() throws SQLException {
    String[][] data = {
            {"Neta", "neta", "neta123"},
            {"Gresia", "gresia", "gresia123"}
    };

    try (Connection c = PostgreSQLConnection.getConnection()) {
        c.setAutoCommit(false);

        try {
            for (String[] k : data) {

                Integer personId = null;
                String storedHash = null;

                // cek apakah username sudah ada
                try (PreparedStatement chk = c.prepareStatement(
                        "SELECT person_id, password_hash FROM cashiers WHERE username=?")) {

                    chk.setString(1, k[1]);

                    try (ResultSet r = chk.executeQuery()) {
                        if (r.next()) {
                            personId = r.getInt("person_id");
                            storedHash = r.getString("password_hash");
                        }
                    }
                }

                // username sudah ada
                if (personId != null) {

                    // jika hash lama (bukan BCrypt), update ke BCrypt
                    if (storedHash == null ||
                            !(storedHash.startsWith("$2a$")
                                    || storedHash.startsWith("$2b$")
                                    || storedHash.startsWith("$2y$"))) {

                        try (PreparedStatement upd = c.prepareStatement(
                                "UPDATE cashiers SET password_hash=? WHERE person_id=?")) {

                            upd.setString(1, hash(k[2]));
                            upd.setInt(2, personId);
                            upd.executeUpdate();
                        }
                    }

                    continue;
                }

                // buat akun baru kalau belum ada
                try (PreparedStatement p = c.prepareStatement(
                        "INSERT INTO persons(full_name,role) VALUES(?, 'CASHIER')",
                        Statement.RETURN_GENERATED_KEYS)) {

                    p.setString(1, k[0]);
                    p.executeUpdate();

                    try (ResultSet key = p.getGeneratedKeys()) {
                        key.next();

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

    /** Cari customer berdasarkan nama; customer POS yang dibuat otomatis boleh tanpa profil lengkap. */
    public Customer findOrCreateCustomer(String name) throws SQLException {
        String q = "SELECT p.id,p.full_name,p.phone,p.registered_at,c.email "
                + "FROM persons p JOIN customers c ON c.person_id=p.id "
                + "WHERE LOWER(p.full_name)=LOWER(?) ORDER BY p.id LIMIT 1";
        try (Connection c = PostgreSQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, name);
            try (ResultSet r = ps.executeQuery()) {
                if (r.next()) {
                    return new Customer(r.getInt(1), r.getString(2), r.getString(3),
                            r.getDate(4).toLocalDate(), r.getString(5));
                }
            }
        }

        // Customer walk-in dari halaman penjualan tidak wajib mengisi profil admin.
        try (Connection c = PostgreSQLConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement person = c.prepareStatement(
                        "INSERT INTO persons(full_name,registered_at,role) VALUES(?,CURRENT_DATE,'CUSTOMER')",
                        Statement.RETURN_GENERATED_KEYS)) {
                    person.setString(1, name);
                    person.executeUpdate();
                    try (ResultSet keys = person.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Customer tidak berhasil dibuat.");
                        id = keys.getInt(1);
                    }
                }
                try (PreparedStatement detail = c.prepareStatement(
                        "INSERT INTO customers(person_id,email,subscribed) VALUES(?,NULL,FALSE)")) {
                    detail.setInt(1, id);
                    detail.executeUpdate();
                }
                c.commit();
                return new Customer(id, name, null, LocalDate.now(), null);
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

}