package main.java.controller;
/*
 * Class: InvoiceController
 * Deskripsi: Mengisi dan menampilkan teks invoice transaksi.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// Menghubungkan area invoice pada FXML dengan controller ini.
import javafx.fxml.FXML;
// TextArea menampilkan invoice dalam beberapa baris.
import javafx.scene.control.TextArea;

// Mengisi area teks pada tampilan invoice dengan hasil transaksi.
public class InvoiceController {
    // Area teks tempat controller pemesan mengirimkan isi invoice.
    @FXML private TextArea invoiceArea;

    // Mengisi area invoice setelah FXML berhasil dimuat.
    public void setInvoice(String text) {
        invoiceArea.setText(text);
    }
}
