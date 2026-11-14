package main.java.controller;
/*
 * Class: Session
 * Deskripsi: Menyimpan informasi kasir yang sedang login.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// Tipe data yang disimpan sebagai pengguna aktif.
import main.java.model.Cashier;

/** Menyimpan kasir yang terakhir login (dipakai sebagai pilihan awal di halaman order). */
// Menyimpan kasir aktif agar dapat dipakai lintas halaman.
final class Session {
    // Referensi kasir yang login; null berarti belum ada sesi kasir.
    static Cashier currentCashier;

    // Constructor privat karena Session hanya menyimpan data statis.
    private Session() {
    }
}
