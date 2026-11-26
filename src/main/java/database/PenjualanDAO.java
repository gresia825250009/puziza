package main.java.database;

import main.java.model.Pizza;
import main.java.model.Penjualan;
import main.java.model.SaleHistory;

import java.sql.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/** Menyimpan transaksi sesuai struktur tabel sales dan membaca riwayatnya. */
public class PenjualanDAO {

    /**
     * Simpan satu transaksi. Kolom sales pada skema adalah:
     * id, sale_code, created_at, customer_id, cashier_id, quantity, listproduk.
     * listproduk menyimpan ID pizza; quantity menyimpan total unit yang dipesan.
     */
    public int save(Penjualan penjualan) throws SQLException {
        if (penjualan == null || penjualan.getItems().isEmpty()) {
            throw new IllegalArgumentException("Transaksi harus memiliki minimal satu produk.");
        }

        String sql = "INSERT INTO sales(sale_code, created_at, customer_id, cashier_id, quantity, listproduk) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = PostgreSQLConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int quantity = penjualan.getTotalQuantity();
                List<Integer> ids = new ArrayList<>();
                for (Map.Entry<Pizza, Integer> entry : penjualan.getItems().entrySet()) {
                    Pizza pizza = entry.getKey();
                    if (pizza.getId() <= 0) {
                        throw new SQLException("Ada produk tanpa ID pizza yang valid.");
                    }
                    // Simpan ID berulang sesuai jumlah unit agar isi array tetap merepresentasikan item.
                    for (int i = 0; i < entry.getValue(); i++) {
                        ids.add(pizza.getId());
                    }
                }
                Integer[] productIds = ids.toArray(new Integer[0]);
                Array productArray = c.createArrayOf("integer", productIds);

                try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, penjualan.getSaleCode());
                    ps.setTimestamp(2, Timestamp.valueOf(penjualan.getCreatedAt()));
                    if (penjualan.getCustomer() == null) ps.setNull(3, Types.INTEGER);
                    else ps.setInt(3, penjualan.getCustomer().getId());
                    if (penjualan.getCashier() == null) ps.setNull(4, Types.INTEGER);
                    else ps.setInt(4, penjualan.getCashier().getId());
                    ps.setInt(5, quantity);
                    ps.setArray(6, productArray);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Gagal mendapatkan ID transaksi.");
                        int generatedId = keys.getInt(1);
                        c.commit();
                        return generatedId;
                    }
                } finally {
                    productArray.free();
                }
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Riwayat hanya menampilkan data yang memang tersedia pada tabel sales. */
    public List<SaleHistory> getHistory() throws SQLException {
        List<SaleHistory> rows = new ArrayList<>();
        String sql = "SELECT s.sale_code, s.created_at, s.quantity, "
                + "COALESCE(pc.full_name, 'Walk-in') AS customer_name, "
                + "COALESCE(pk.full_name, '-') AS cashier_name "
                + "FROM sales s "
                + "LEFT JOIN persons pc ON pc.id = s.customer_id "
                + "LEFT JOIN persons pk ON pk.id = s.cashier_id "
                + "ORDER BY s.id DESC";
        try (Connection c = PostgreSQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("created_at");
                rows.add(new SaleHistory(
                        rs.getString("sale_code"),
                        timestamp == null ? null : timestamp.toLocalDateTime(),
                        rs.getInt("quantity"),
                        rs.getString("customer_name"),
                        rs.getString("cashier_name")));
            }
        }
        return rows;
    }
}