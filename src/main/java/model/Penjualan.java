package main.java.model;
/*
 * Class: Penjualan
 * Deskripsi: Model transaksi beserta item dan perhitungan total.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// LocalDateTime menyimpan tanggal dan jam transaksi.
import java.time.LocalDateTime;
// ArrayList/List menampung rincian item dalam suatu transaksi.
import java.util.ArrayList;
import java.util.List;
// NumberFormat dan Locale memformat nilai uang sesuai lokal Indonesia.
import java.text.NumberFormat;
import java.util.Locale;

/** Association: transaksi memiliki customer, kasir, dan banyak item pizza. */
// Menyimpan transaksi, relasi customer/kasir, dan rincian item pesanan.
public class Penjualan implements Invoice {
    // Identitas database, kode transaksi, waktu, customer, dan kasir.
    private int id;
    private String saleCode;
    private LocalDateTime createdAt;
    private Customer customer;
    private Cashier cashier;
    // Daftar pizza dan kuantitas yang menjadi rincian transaksi.
    private final List<OrderItem> items = new ArrayList<>();
    // Constructor menetapkan informasi utama transaksi.
    public Penjualan(int id, String saleCode, LocalDateTime createdAt, Customer customer, Cashier cashier) {
        this.id = id;
        this.saleCode = saleCode;
        this.createdAt = createdAt;
        this.customer = customer;
        this.cashier = cashier;
    }

    // Getter menyediakan data transaksi kepada DAO dan controller.
    public int getId() {
        return id;
    }

    public String getSaleCode() {
        return saleCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    // Hanya jumlah positif yang boleh menjadi item transaksi.
    public void addPizza(Pizza pizza, int quantity) {
        if (quantity > 0) {
            items.add(new OrderItem(pizza, quantity));
        }
    }

    // Menjumlahkan subtotal seluruh item dalam transaksi.
    public double getSubtotal() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    // Pajak dihitung 10% dari subtotal; total adalah subtotal ditambah pajak.
    public double getTax() {
        return getSubtotal() * 0.10;
    }

    public double getTotal() {
        return getSubtotal() + getTax();
    }

    // Menyusun rincian transaksi menjadi teks yang siap ditampilkan atau dicetak.
    @Override
    public String generateInvoice() {
        // Menyusun nomor, waktu, customer/kasir, detail, pajak, dan total sebagai teks.
        NumberFormat rp = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        StringBuilder b = new StringBuilder("PUZIZA PIZZA\n================================\n");
        b.append("Order: ").append(saleCode).append("\nDate: ").append(createdAt).append("\n");
        b.append("Customer: ")
                .append(customer == null ? "Walk-in" : customer.getName())
                .append("\n");
        b.append("Kasir: ")
                .append(cashier == null ? "-" : cashier.getName())
                .append("\n");

        for (OrderItem i : items) {
            b.append(i.getQuantity())
                    .append(" x ")
                    .append(i.getPizza())
                    .append(" = ")
                    .append(rp.format(i.getSubtotal()))
                    .append("\n");
        }

        b.append("--------------------------------\nSubtotal: ").append(rp.format(getSubtotal()));
        b.append("\nTax 10%: ").append(rp.format(getTax()));
        b.append("\nTOTAL: ").append(rp.format(getTotal())).append("\nTerima kasih!");
        return b.toString();
    }
}
