package main.java.model;
/*
 * Class: OrderItem
 * Deskripsi: Model satu jenis pizza dan jumlah pesanannya.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

/* Association antara satu transaksi dengan pizza yang dipesan. */
// Menyimpan satu jenis pizza dan jumlahnya di dalam transaksi.
public class OrderItem {
    // Pizza yang dipesan dan jumlah unitnya.
    private Pizza pizza;
    private int listproduk;

    // Membentuk satu detail keranjang dari pizza dan jumlah pesanan.
    public OrderItem(Pizza pizza, int listproduk) {
        this.pizza = pizza;
        this.listproduk = listproduk;
    }

    // Getter menyediakan data item untuk controller, tabel, dan invoice.
    public Pizza getPizza() {
        return pizza;
    }

    public int getQuantity() {
        return listproduk;
    }

    // Subtotal dihitung dari harga setelah diskon dikali jumlah.
    public double getSubtotal() {
        return pizza.getFinalPrice() * listproduk;
    }
}
