package main.java.database;
/*
 * Class: PenjualanDAO
 * Deskripsi: Menyimpan transaksi dan mengambil riwayat penjualan.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// Model detail pizza, transaksi, dan ringkasan hasil riwayat.
import main.java.model.OrderItem;
import main.java.model.Penjualan;
import main.java.model.SaleHistory;

// JDBC: koneksi, statement, timestamp, tipe SQL, hasil query, dan exception.
import java.sql.*;
// List menyimpan hasil transaksi yang dibaca.
import java.util.List;
// Menyimpan transaksi beserta itemnya dan membaca riwayat penjualan.
public class PenjualanDAO {
    // Menyimpan header dan detail transaksi dalam satu transaksi database.
    public int save(Penjualan sale) throws SQLException {
        // SQL INSERT membuat header transaksi; tanda ? diisi PreparedStatement.
        String q = "INSERT INTO sales(sale_code,created_at,customer_id,cashier_id,subtotal,tax,total) "
                + "VALUES(?,?,?,?,?,?,?)";
        try (Connection c = PostgreSQLConnection.getConnection()) {
            // Header dan rincian disimpan dalam satu transaksi agar konsisten.
            c.setAutoCommit(false);
            try (PreparedStatement p = c.prepareStatement(q, Statement.RETURN_GENERATED_KEYS)) {
                p.setString(1, sale.getSaleCode());
                p.setTimestamp(2, Timestamp.valueOf(sale.getCreatedAt()));
                if (sale.getCustomer() == null) {
                    p.setNull(3, Types.INTEGER);
                } else {
                    p.setInt(3, sale.getCustomer().getId());
                }
                if (sale.getCashier() == null) {
                    p.setNull(4, Types.INTEGER);
                } else {
                    p.setInt(4, sale.getCashier().getId());
                }
                p.setDouble(5, sale.getSubtotal());
                p.setDouble(6, sale.getTax());
                p.setDouble(7, sale.getTotal());
                p.executeUpdate();

                try (ResultSet k = p.getGeneratedKeys()) {
                    k.next();
                    int id = k.getInt(1);
                    // INSERT ini menyimpan detail item dan mengaitkannya dengan ID header.
                    try (PreparedStatement d = c.prepareStatement(
                            "INSERT INTO sale_items(sale_id,pizza_id,quantity,unit_price) VALUES(?,?,?,?)")) {
                        for (OrderItem item : sale.getItems()) {
                            d.setInt(1, id);
                            d.setInt(2, item.getPizza().getId());
                            d.setInt(3, item.getQuantity());
                            d.setDouble(4, item.getPizza().getFinalPrice());
                            d.addBatch();
                        }
                        d.executeBatch();
                    }
                    c.commit();
                    return id;
                }
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    // Mengambil riwayat dengan nama customer/kasir untuk ditampilkan di tabel.
    public List<SaleHistory> getHistory() throws SQLException {
        // List hasil query yang nanti dipasang pada tabel riwayat penjualan.
        List<SaleHistory> rows = new java.util.ArrayList<>();
        // SELECT menggabungkan sales dengan nama customer/kasir; LEFT JOIN menjaga
        // transaksi walk-in tetap tampil, sedangkan COALESCE memberi teks pengganti.
        String sql = "SELECT s.sale_code, s.created_at, s.total, "
                + "COALESCE(pc.full_name, 'Walk-in'), COALESCE(pk.full_name, '-') "
                + "FROM sales s "
                + "LEFT JOIN persons pc ON pc.id = s.customer_id "
                + "LEFT JOIN persons pk ON pk.id = s.cashier_id "
                + "ORDER BY s.id DESC";
        try (Connection c = PostgreSQLConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                rows.add(new SaleHistory(
                        r.getString(1),
                        r.getTimestamp(2).toLocalDateTime(),
                        r.getDouble(3),
                        r.getString(4),
                        r.getString(5)));
            }
        }
        return rows;
    }
}
