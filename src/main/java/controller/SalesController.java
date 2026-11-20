package main.java.controller;
/*
 * Class: SalesController
 * Deskripsi: Menampilkan dan memperbarui riwayat penjualan.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// DAO untuk mengambil riwayat penjualan dari database.
import main.java.database.PenjualanDAO;
// Model ringkasan transaksi untuk tiap baris tabel.
import main.java.model.SaleHistory;

// Property teks dan ObservableList dipakai untuk mengisi tabel JavaFX.
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
// Mengatur format tanggal dan jam pada kolom tanggal transaksi.
import java.time.format.DateTimeFormatter;

// Menampilkan riwayat transaksi dalam tabel penjualan.
public class SalesController {
    // Tabel berisi seluruh riwayat transaksi.
    @FXML private TableView<SaleHistory> salesTable;
    // Kolom kode, waktu, customer, kasir, dan jumlah unit terjual.
    @FXML private TableColumn<SaleHistory, String> codeColumn;
    @FXML private TableColumn<SaleHistory, String> dateColumn;
    @FXML private TableColumn<SaleHistory, String> customerColumn;
    @FXML private TableColumn<SaleHistory, String> cashierColumn;
    @FXML private TableColumn<SaleHistory, String> quantityColumn;

    // Menetapkan formatter kolom dan memuat riwayat transaksi awal.
    @FXML private void initialize() {
        // Mengubah nilai model menjadi teks tampilan, lalu memuat data awal.
        codeColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getSaleCode()));
        dateColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
        customerColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCustomerName()));
        cashierColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCashierName()));
        quantityColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(Integer.toString(cell.getValue().getQuantity())));
        refresh();
    }

    // Mengambil ulang data transaksi dari database.
    @FXML private void refresh() {
        // Event Refresh dan inisialisasi: ambil ulang data history dari DAO.
        try {
            salesTable.setItems(FXCollections.observableArrayList(new PenjualanDAO().getHistory()));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Riwayat transaksi gagal dimuat: " + e.getMessage()).showAndWait();
        }
    }
}
