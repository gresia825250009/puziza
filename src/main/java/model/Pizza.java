package main.java.model;
/*
 * Class: Pizza
 * Deskripsi: Model dasar varian pizza dan perhitungan diskon.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

/* Abstract parent untuk seluruh varian pizza. */
// Menjadi kelas dasar varian pizza dengan perhitungan diskon dan harga akhir.
public abstract class Pizza {
    // Atribut dasar menu yang dibaca dari atau disimpan ke tabel pizzas.
    private int id;
    private String size;
    private double basePrice;
    
    // Constructor parent menyiapkan ID, ukuran, dan harga dasar.
    protected Pizza(int id, String size, double basePrice) {
        this.id = id;
        this.size = size;
        this.basePrice = basePrice;
    }

    // Getter/setter untuk nilai dasar pizza.
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    // Tiap subtype wajib menyediakan nama pizza dan deskripsi topping.
    public abstract String getName();
    public abstract String getTopping();

    // Memberikan diskon khusus untuk ukuran Medium.
    public double getDiscountRate() {
        return "Medium".equalsIgnoreCase(size) ? 0.45 : 0.0;
    }

    public double getFinalPrice() {
        return basePrice * (1 - getDiscountRate());
    }

    // Representasi teks untuk ComboBox atau tampilan sederhana.
    @Override
    public String toString() {
        return getName() + " - " + size + " - Rp" + String.format("%,.0f", getFinalPrice());
    }
}
