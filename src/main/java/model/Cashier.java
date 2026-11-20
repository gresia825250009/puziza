package main.java.model;
/*
 * Class: Cashier
 * Deskripsi: Model data kasir dan akun login kasir.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// LocalDate menyimpan tanggal lahir dan tanggal registrasi tanpa jam.
import java.time.LocalDate;

// Merepresentasikan kasir beserta data login dan profil tambahannya.
public class Cashier extends Person {
    // Nama akun untuk proses login kasir.
    private String username;
    // Kata sandi model; saat disimpan database menggunakan hash.
    private String password;
    // Gender dan tanggal lahir adalah data profil tambahan kasir.
    private String gender;
    private LocalDate birthDate;

    // Constructor sederhana untuk data yang belum memiliki gender/tanggal lahir.
    public Cashier(int id, String name, String phone, LocalDate registeredAt, String username, String password, String gender, LocalDate birthDate) {
        super(id, name, phone, registeredAt);
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    // Getter/setter menyediakan akses baca dan ubah untuk atribut kasir.
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    // Memastikan nama, username, dan password memenuhi syarat dasar model.
    @Override
    public boolean validate() {
        return getName() != null
                && !getName().isBlank()
                && username != null
                && !username.isBlank()
                && password != null
                && password.length() >= 6;
    }

    // Mengembalikan label jenis person untuk ditampilkan atau dibedakan.
    @Override
    public String getrole() {
        return "Cashier";
    }

}