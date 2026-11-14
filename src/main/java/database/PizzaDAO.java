package main.java.database;
/*
 * Class: PizzaDAO
 * Deskripsi: Mengelola menu pizza di database.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// Model abstrak dan dua subtype menu pizza.
import main.java.model.Pizza;
import main.java.model.CheeseMania;
import main.java.model.VolcanoMeat;

// Reader untuk mengambil menu awal dari sumber daya teks.
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
// Encoding UTF-8 dan kelas JDBC untuk membaca/menyimpan data pizza.
import java.nio.charset.StandardCharsets;
import java.sql.*;
// Koleksi untuk menampung hasil dan data menu sebelum dimasukkan.
import java.util.ArrayList;
import java.util.List;
// Menyediakan operasi database serta seed untuk data pizza.
public class PizzaDAO {
    // Mengambil semua pizza dan membentuk subtype sesuai nama pizza.
    public List<Pizza> getAll() throws SQLException {
        // SELECT mengambil seluruh pizza berurutan berdasarkan ID.
        List<Pizza> list = new ArrayList<>();
        String sql = "SELECT p.id, p.pizza_name, p.size, p.base_price FROM pizzas p ORDER BY p.id";
        try (Connection c = PostgreSQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(create(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4)));
            }
        }
        return list;
    }

    public void insert(String name, String size, double price) throws SQLException {
        // INSERT menambahkan menu baru; kolom yang dimasukkan diisi parameter.
        String sql = "INSERT INTO pizzas(pizza_name,size,base_price) VALUES(?,?,?)";
        try (Connection c = PostgreSQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, size);
            ps.setDouble(3, price);
            ps.executeUpdate();
        }
        syncSubtype(name, size);
    }

    public void update(int id, String name, String size, double price) throws SQLException {
        // UPDATE mengganti nama, ukuran, atau harga untuk ID pizza tertentu.
        String sql = "UPDATE pizzas SET pizza_name=?,size=?,base_price=? WHERE id=?";
        try (Connection c = PostgreSQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, size);
            ps.setDouble(3, price);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
        syncSubtype(id, name, size);
    }

    public void delete(int id) throws SQLException {
        // DELETE meminta database menghapus pizza berdasarkan ID.
        try (Connection c = PostgreSQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM pizzas WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Membaca menu.txt, memvalidasi isinya, lalu memasukkan data yang belum ada.
    public static void seedMenuIfEmpty() throws SQLException, IOException {
        // Membaca menu.txt, mengecek format/harga, lalu melengkapi menu dan subtype.
        InputStream menu = PizzaDAO.class.getResourceAsStream("/main/resources/menu.txt");
        if (menu == null) {
            menu = PizzaDAO.class.getResourceAsStream("/menu.txt");
        }
        if (menu == null) {
            throw new IOException("File menu.txt tidak ditemukan di classpath.");
        }

        List<String[]> entries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(menu, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                if (fields.length != 3
                        || fields[0].trim().isEmpty()
                        || fields[1].trim().isEmpty()) {
                    throw new IOException("Format menu.txt tidak valid: " + line);
                }
                try {
                    double price = Double.parseDouble(fields[2].trim());
                    if (price < 0) {
                        throw new NumberFormatException("Harga tidak boleh negatif.");
                    }
                } catch (NumberFormatException e) {
                    throw new IOException("Harga pada menu.txt tidak valid: " + line, e);
                }
                entries.add(new String[] {fields[0].trim(), fields[1].trim(), fields[2].trim()});
            }
        }

        try (Connection c = PostgreSQLConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                for (String[] entry : entries) {
                    // INSERT ... ON CONFLICT melewati kombinasi nama/ukuran yang sudah ada.
                    try (PreparedStatement insert = c.prepareStatement(
                            "INSERT INTO pizzas(pizza_name,size,base_price) VALUES(?,?,?) " +
                                    "ON CONFLICT (pizza_name,size) DO NOTHING")) {
                        insert.setString(1, entry[0]);
                        insert.setString(2, entry[1]);
                        insert.setDouble(3, Double.parseDouble(entry[2]));
                        insert.executeUpdate();
                    }
                    // SELECT mengambil ID untuk menghubungkan baris pada tabel subtype.
                    try (PreparedStatement find = c.prepareStatement(
                            "SELECT id FROM pizzas WHERE pizza_name=? AND size=?")) {
                        find.setString(1, entry[0]);
                        find.setString(2, entry[1]);
                        try (ResultSet id = find.executeQuery()) {
                            if (!id.next()) {
                                throw new SQLException(
                                        "Pizza hasil seed tidak ditemukan: " + entry[0] + " " + entry[1]);
                            }
                            String table = "CheeseMania".equalsIgnoreCase(entry[0])
                                    ? "cheese_mania"
                                    : "volcano_meat";
                            // INSERT subtype dengan ON CONFLICT mencegah relasi ganda.
                            try (PreparedStatement subtype = c.prepareStatement(
                                    "INSERT INTO " + table + "(pizza_id) VALUES(?) "
                                            + "ON CONFLICT (pizza_id) DO NOTHING")) {
                                subtype.setInt(1, id.getInt(1));
                                subtype.executeUpdate();
                            }
                        }
                    }
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }
    // Mencari ID pizza berdasarkan nama dan ukuran untuk sinkronisasi subtype.
    private void syncSubtype(String name, String size) throws SQLException {
        // SELECT mencari ID berdasarkan nama/ukuran sebelum sinkronisasi subtype.
        try (Connection c = PostgreSQLConnection.getConnection();
             PreparedStatement p = c.prepareStatement("SELECT id FROM pizzas WHERE pizza_name=? AND size=?")) {
            p.setString(1, name);
            p.setString(2, size);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    syncSubtype(r.getInt(1), name, size);
                }
            }
        }
    }

    // Memastikan pizza hanya tercatat pada tabel subtype yang sesuai.
    private void syncSubtype(int id, String name, String size) throws SQLException {
        // Hapus relasi subtype lama, lalu pastikan relasi subtype yang benar tersedia.
        String table = "CheeseMania".equalsIgnoreCase(name) ? "cheese_mania" : "volcano_meat";
        String other = table.equals("cheese_mania") ? "volcano_meat" : "cheese_mania";
        try (Connection c = PostgreSQLConnection.getConnection()) {
            // DELETE membersihkan subtype lawan untuk pizza yang sedang diperbarui.
            try (PreparedStatement d = c.prepareStatement("DELETE FROM " + other + " WHERE pizza_id=?")) {
                d.setInt(1, id);
                d.executeUpdate();
            }
            // INSERT membuat relasi subtype yang sesuai; konflik ID yang sudah ada dilewati.
            String sql = "INSERT INTO " + table
                    + "(pizza_id) VALUES(?) "
                    + "ON CONFLICT (pizza_id) DO NOTHING";
            try (PreparedStatement p = c.prepareStatement(sql)) {
                p.setInt(1, id);
                p.executeUpdate();
            }
        }
    }

    private Pizza create(int id, String name, String size, double price) {
        // Memilih subtype model berdasarkan nama pizza hasil query.
        return "CheeseMania".equalsIgnoreCase(name)
                ? new CheeseMania(id, size, price)
                : new VolcanoMeat(id, size, price);
    }
}
