package main.java.model;
/*
 * Interface: Invoice
 * Deskripsi: Kontrak untuk membuat teks invoice.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// Menentukan operasi untuk menghasilkan teks invoice transaksi.
public interface Invoice {
    // Kontrak bagi model transaksi untuk menyusun invoice dalam bentuk teks.
    String generateInvoice();
}
