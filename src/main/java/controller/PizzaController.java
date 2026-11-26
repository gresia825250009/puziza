package main.java.controller;
/*
 * Class: PizzaController
 * Deskripsi: Mengelola tampilan dan aksi data pizza.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// DAO menjadi perantara operasi pizza ke database.
import main.java.database.PizzaDAO;
// Model pizza yang menjadi isi baris tabel.
import main.java.model.Pizza;

// Mengelola daftar JavaFX yang tampil pada tabel.
import javafx.collections.FXCollections;
// Menghubungkan kontrol dan aksi FXML.
import javafx.fxml.FXML;
// Kontrol form, tabel, kolom, dan alert.
import javafx.scene.control.*;
// Menghubungkan form dan tabel admin dengan operasi data pizza.
public class PizzaController {
    // Tabel semua menu; baris yang dipilih menjadi target ubah/hapus.
    @FXML private TableView<Pizza> pizzaTable;
    // Kolom ID, nama, ukuran, dan harga dasar pada tabel.
    @FXML private TableColumn<Pizza, Integer> idColumn;
    @FXML private TableColumn<Pizza, String> nameColumn, sizeColumn;
    @FXML private TableColumn<Pizza, Number> priceColumn;
    // Field input untuk nilai pizza yang dibuat atau diperbarui.
    @FXML private TextField nameField, sizeField, priceField;
    // Akses DAO untuk membaca dan mengubah data pizza.
    private final PizzaDAO dao = new PizzaDAO();

    // Menyiapkan kolom tabel, listener pilihan baris, dan data awal.
    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(v ->
                new javafx.beans.property.SimpleIntegerProperty(v.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(v ->
                new javafx.beans.property.SimpleStringProperty(v.getValue().getName()));
        sizeColumn.setCellValueFactory(v ->
                new javafx.beans.property.SimpleStringProperty(v.getValue().getSize()));
        priceColumn.setCellValueFactory(v ->
                new javafx.beans.property.SimpleDoubleProperty(v.getValue().getBasePrice()));

        pizzaTable.getSelectionModel().selectedItemProperty().addListener((observable, oldPizza, pizza) -> {
            if (pizza != null) {
                nameField.setText(pizza.getName());
                sizeField.setText(pizza.getSize());
                priceField.setText(String.valueOf(pizza.getBasePrice()));
            }
        });
        refresh();
    }

    // Event Tambah: ubah teks harga menjadi angka, simpan, muat ulang, lalu kosongkan form.
    @FXML
    private void add() {
        try {
            dao.insert(nameField.getText(), sizeField.getText(), Double.parseDouble(priceField.getText()));
            refresh();
            clear();
        } catch (Exception e) {
            error(e);
        }
    }

    // Event Ubah: gunakan ID baris terpilih dan nilai form untuk memperbarui pizza.
    @FXML
    private void update() {
        try {
            Pizza pizza = pizzaTable.getSelectionModel().getSelectedItem();
            if (pizza == null) {
                return;
            }
            dao.update(
                    pizza.getId(),
                    nameField.getText(),
                    sizeField.getText(),
                    Double.parseDouble(priceField.getText()));
            refresh();
        } catch (Exception e) {
            error(e);
        }
    }

    // Event Hapus: hapus ID dari baris terpilih, lalu muat ulang tabel.
    @FXML
    private void delete() {
        try {
            Pizza pizza = pizzaTable.getSelectionModel().getSelectedItem();
            if (pizza != null) {
                dao.delete(pizza.getId());
            }
            refresh();
            clear();
        } catch (Exception e) {
            error(e);
        }
    }

    // Mengosongkan nilai input.
    @FXML
    private void clear() {
        nameField.clear();
        sizeField.clear();
        priceField.clear();
    }

    // Memuat ulang data pizza dari DAO ke TableView.
    private void refresh() {
        try {
            pizzaTable.setItems(FXCollections.observableArrayList(dao.getAll()));
        } catch (Exception e) {
            error(e);
        }
    }

    // Menampilkan kesalahan proses kepada pengguna.
    private void error(Exception e) {
        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
    }
}