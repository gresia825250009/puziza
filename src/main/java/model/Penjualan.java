package main.java.model;
/*
 * Class: Penjualan
 * Deskripsi: Model transaksi beserta item dan perhitungan total.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// LocalDateTime menyimpan tanggal dan jam transaksi.
import java.time.LocalDateTime;
// NumberFormat dan Locale memformat nilai uang sesuai lokal Indonesia.
import java.text.NumberFormat;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

/** Association: transaksi memiliki customer, kasir, dan banyak item pizza. */
// Menyimpan transaksi, relasi customer/kasir, dan rincian item pesanan.
public class Penjualan implements Invoice {
    // Identitas database, kode transaksi, waktu, customer, dan kasir.
    private int id;
    private String saleCode;
    private LocalDateTime createdAt;
    private Customer customer;
    private Cashier cashier;

    // Rincian pesanan: pizza sebagai kunci, jumlah unit sebagai nilai.
    // LinkedHashMap menjaga urutan pizza sesuai urutan ditambahkan.
    private final Map<Pizza, Integer> items = new LinkedHashMap<>();
    
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

    // Setter, dipakai saat keranjang disimpan sebagai transaksi.
    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setCashier(Cashier cashier) {
        this.cashier = cashier;
    }
    // Jumlah unit untuk satu jenis pizza.
    public int getQuantity(Pizza pizza) {
        return items.getOrDefault(pizza, 0);
    }

    // Total seluruh unit dalam transaksi.
    public int getTotalQuantity() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Subtotal satu baris: harga setelah diskon dikali jumlah.
    public double getLineSubtotal(Pizza pizza) {
        return pizza.getFinalPrice() * getQuantity(pizza);
    }

    // Hanya jumlah positif yang boleh masuk. Pizza yang sama digabung dalam satu baris.
    public void addPizza(Pizza pizza, int jumlah) {
        if (pizza != null && jumlah > 0) {
            items.merge(pizza, jumlah, Integer::sum);
        }
    }

    public void removePizza(Pizza pizza) {
        items.remove(pizza);
    }

    public void clearItems() {
        items.clear();
    }

    // ===== Perhitungan total =====

    // Menjumlahkan subtotal seluruh baris.
    public double getSubtotal() {
        return items.keySet().stream().mapToDouble(this::getLineSubtotal).sum();
    }

    // Pajak dihitung 10% dari subtotal; total adalah subtotal ditambah pajak.
    public double getTax() {
        return getSubtotal() * 0.10;
    }

    public double getTotal() {
        return getSubtotal() + getTax();
    }

    // Menyusun rincian transaksi menjadi teks struk.
    @Override
    public String generateInvoice() {
        NumberFormat rp = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        StringBuilder b = new StringBuilder("PUZIZA PIZZA\n================================\n");
        b.append("Order: ").append(saleCode).append("\nDate: ").append(createdAt).append("\n");
        b.append("Customer: ")
                .append(customer == null ? "Walk-in" : customer.getName())
                .append("\n");
        b.append("Kasir: ")
                .append(cashier == null ? "-" : cashier.getName())
                .append("\n");

        for (Map.Entry<Pizza, Integer> e : items.entrySet()) {
            b.append(e.getValue())
                    .append(" x ")
                    .append(e.getKey())
                    .append(" = ")
                    .append(rp.format(getLineSubtotal(e.getKey())))
                    .append("\n");
        }

        b.append("--------------------------------\nSubtotal: ").append(rp.format(getSubtotal()));
        b.append("\nTax 10%: ").append(rp.format(getTax()));
        b.append("\nTOTAL: ").append(rp.format(getTotal())).append("\nTerima kasih!");
        return b.toString();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Map<Pizza, Integer> getItems() {
        return items;
    }
}