package main.java.model;
/*
 * Class: Person
 * Deskripsi: Model dasar untuk customer dan kasir.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// LocalDate menyimpan tanggal pendaftaran person tanpa komponen waktu.
import java.time.LocalDate;

/** Abstract parent untuk Customer dan Cashier. */
// Menjadi kelas dasar untuk data umum customer dan kasir.
public abstract class Person {
    // Data identitas dasar yang digunakan bersama oleh Customer dan Cashier.
    private int id;
    private String name;
    private String phone;
    private LocalDate registeredAt;

    // Constructor parent hanya dapat dipanggil oleh turunan Person.
    protected Person(int id, String name, String phone, LocalDate registeredAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.registeredAt = registeredAt;
    }

    // Getter/setter menyediakan akses ke setiap data identitas dasar.
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDate registeredAt) {
        this.registeredAt = registeredAt;
    }

    // Setiap turunan harus menentukan validasi dan label jenisnya sendiri.
    public abstract boolean validate();
    public abstract String getrole();

    // Representasi ringkas person ketika objek dikonversi menjadi teks.
    @Override
    public String toString() {
        return name + " (" + getrole() + ")";
    }
}
