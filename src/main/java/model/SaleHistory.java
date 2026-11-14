package main.java.model;
/*
 * Class: SaleHistory
 * Deskripsi: Model ringkasan untuk riwayat penjualan.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// Tipe untuk tanggal dan jam transaksi pada riwayat.
import java.time.LocalDateTime;

// Menyimpan ringkasan transaksi untuk ditampilkan pada riwayat penjualan.
public class SaleHistory {
    // Nilai ringkasan hasil query: kode, waktu, nilai total, nama customer, dan kasir.
    private final String saleCode;
    private final LocalDateTime createdAt;
    private final double total;
    private final String customerName;
    private final String cashierName;

    // Constructor mengisi seluruh nilai ringkasan yang ditampilkan tabel.
    public SaleHistory(String saleCode, LocalDateTime createdAt, double total,
                       String customerName, String cashierName) {
        this.saleCode = saleCode;
        this.createdAt = createdAt;
        this.total = total;
        this.customerName = customerName;
        this.cashierName = cashierName;
    }

    // Getter menyediakan nilai read-only untuk kolom tabel.
    public String getSaleCode() {
        return saleCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getTotal() {
        return total;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCashierName() {
        return cashierName;
    }
}
