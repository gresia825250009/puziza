package main.java.model;
/*
 * Class: Customer
 * Deskripsi: Model data customer aplikasi.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// LocalDate menyimpan tanggal registrasi customer.
import java.time.LocalDate;

// Merepresentasikan customer dan memvalidasi nama serta nomor teleponnya.
public class Customer extends Person {
    // Email merupakan data khusus customer yang tidak dimiliki Person umum.
    private String email;
    // Mengisi atribut Person melalui parent dan menyimpan email customer.
    public Customer(int id, String name, String phone, LocalDate registeredAt, String email) {
        super(id, name, phone, registeredAt);
        this.email = email;
    }

    // Getter dan setter untuk membaca atau memperbarui email.
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Validasi mensyaratkan nama dan nomor telepon berisi 8 sampai 15 digit.
    @Override
    public boolean validate() {
        return getName() != null
                && !getName().isBlank()
                && getPhone() != null
                && getPhone().matches("\\d{8,15}");
    }

    // Mengembalikan label jenis person sebagai Customer.
    @Override
    public String getPersonType() {
        return "Customer";
    }
}
