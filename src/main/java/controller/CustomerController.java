package main.java.controller;
/*
 * Class: CustomerController
 * Deskripsi: Mengelola tampilan dan aksi data customer.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// DAO menangani query database untuk customer dan person.
import main.java.database.PersonDAO;
// Model customer yang digunakan sebagai data baris tabel.
import main.java.model.Customer;

// Utilitas untuk membuat daftar yang dapat dipantau kontrol JavaFX.
import javafx.collections.FXCollections;
// FXML menghubungkan kontrol dan aksi pada tampilan.
import javafx.fxml.FXML;
// Kontrol-kontrol form, tabel, kolom, dan pesan.
import javafx.scene.control.*;
// Tanggal registrasi customer.
import java.time.LocalDate;

// Menghubungkan form customer dengan tabel dan operasi penyimpanan.
public class CustomerController {
    // Tabel untuk menampilkan dan memilih data customer.
    @FXML private TableView<Customer> customerTable;
    // Kolom ID, nama, telepon, dan email pada tabel.
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn, phoneColumn, emailColumn;
    // Field input yang nilainya disalin ke/dari customer terpilih.
    @FXML private TextField nameField, phoneField, emailField;
    // Perantara akses data customer pada database.
    private final PersonDAO dao = new PersonDAO();

    // Menghubungkan kolom tabel ke properti customer dan mengisi tabel awal.
    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(v ->
                new javafx.beans.property.SimpleIntegerProperty(v.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(v ->
                new javafx.beans.property.SimpleStringProperty(v.getValue().getName()));
        phoneColumn.setCellValueFactory(v ->
                new javafx.beans.property.SimpleStringProperty(v.getValue().getPhone()));
        emailColumn.setCellValueFactory(v ->
                new javafx.beans.property.SimpleStringProperty(v.getValue().getEmail()));

        // Event pemilihan baris: isi form dengan nilai customer tersebut.
        customerTable.getSelectionModel().selectedItemProperty().addListener((observable, oldCustomer, customer) -> {
            if (customer != null) {
                nameField.setText(customer.getName());
                phoneField.setText(customer.getPhone());
                emailField.setText(customer.getEmail());
            }
        });
        refresh();
    }

    // Memvalidasi lalu menyimpan customer baru.
    @FXML
    private void add() {
        // Event Tambah: bentuk model baru, validasi, simpan, lalu muat ulang tabel.
        try {
            Customer customer = new Customer(
                    0, nameField.getText(), phoneField.getText(), LocalDate.now(), emailField.getText());
            if (!customer.validate()) {
                throw new IllegalArgumentException("Nama dan nomor telepon (8-15 digit) wajib valid.");
            }
            dao.addCustomer(customer);
            refresh();
            clear();
        } catch (Exception e) {
            error(e);
        }
    }

    // Memperbarui customer yang sedang dipilih pada tabel.
    @FXML
    private void update() {
        // Event Ubah: pertahankan ID dan tanggal registrasi dari data yang dipilih.
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            error(new IllegalArgumentException("Pilih customer yang ingin diubah."));
            return;
        }
        try {
            Customer customer = new Customer(
                    selected.getId(),
                    nameField.getText(),
                    phoneField.getText(),
                    selected.getRegisteredAt(),
                    emailField.getText());
            dao.updateCustomer(customer);
            refresh();
            clear();
        } catch (Exception e) {
            error(e);
        }
    }

    // Menghapus customer yang sedang dipilih.
    @FXML
    private void delete() {
        // Event Hapus: hapus ID customer terpilih melalui DAO.
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            error(new IllegalArgumentException("Pilih customer yang ingin dihapus."));
            return;
        }
        try {
            dao.deletePerson(selected.getId());
            refresh();
            clear();
        } catch (Exception e) {
            error(e);
        }
    }

    // Mengosongkan field input dan membatalkan pilihan baris.
    private void clear() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        customerTable.getSelectionModel().clearSelection();
    }

    // Mengambil ulang daftar customer dan memasangnya ke TableView.
    private void refresh() {
        try {
            customerTable.setItems(FXCollections.observableArrayList(dao.getCustomers()));
        } catch (Exception e) {
            error(e);
        }
    }

    // Menampilkan pesan kesalahan agar kegagalan tidak disembunyikan.
    private void error(Exception e) {
        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
    }
}
