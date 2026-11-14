package main.java.model;
/*
 * Class: CheeseMania
 * Deskripsi: Model untuk varian pizza CheeseMania.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// Merepresentasikan varian pizza CheeseMania.
public class CheeseMania extends Pizza {
    // Meneruskan ID, ukuran, dan harga dasar ke kelas induk Pizza.
    public CheeseMania(int id, String size, double price) {
        super(id, size, price);
    }

    // Nama jenis pizza ini digunakan untuk tampilan dan pemetaan database.
    @Override
    public String getName() {
        return "CheeseMania";
    }

    // Deskripsi topping untuk pizza CheeseMania.
    @Override
    public String getTopping() {
        return "Mozzarella and cheese";
    }
}
