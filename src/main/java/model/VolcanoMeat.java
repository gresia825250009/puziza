package main.java.model;
/*
 * Class: VolcanoMeat
 * Deskripsi: Model untuk varian pizza VolcanoMeat.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// Merepresentasikan varian pizza VolcanoMeat.
public class VolcanoMeat extends Pizza {
    // Meneruskan ID, ukuran, dan harga dasar ke kelas Pizza.
    public VolcanoMeat(int id, String size, double price) {
        super(id, size, price);
    }

    // Nama jenis pizza ini dipakai oleh tampilan dan DAO.
    @Override
    public String getName() {
        return "VolcanoMeat";
    }

    // Deskripsi topping VolcanoMeat.
    @Override
    public String getTopping() {
        return "Beef, sausage, spicy sauce";
    }
}
