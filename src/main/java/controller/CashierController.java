package main.java.controller;
/*
 * Class: CashierController
 * Deskripsi: Mengelola tampilan dan aksi data kasir.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// FXCollections membuat ObservableList yang dapat dipantau oleh kontrol JavaFX.
import javafx.collections.FXCollections;
// SimpleStringProperty menyediakan nilai teks untuk setiap sel tabel.
import javafx.beans.property.SimpleStringProperty;
// FXML menghubungkan field dan method controller dengan elemen pada berkas FXML.
import javafx.fxml.FXML;
// Alert menampilkan pesan informasi, peringatan, konfirmasi, atau kesalahan.
import javafx.scene.control.Alert;
// Kontrol pilihan role kasir.
import javafx.scene.control.ComboBox;
// Kontrol pemilih tanggal lahir.
import javafx.scene.control.DatePicker;
// Kolom kata sandi yang menyamarkan karakter yang diketik.
import javafx.scene.control.PasswordField;
// Tombol pilihan gender.
import javafx.scene.control.RadioButton;
// Kolom-kolom dan tabel untuk menampilkan daftar kasir.
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
// Kotak input teks untuk nama dan username.
import javafx.scene.control.TextField;
// DAO untuk membaca dan mengubah data person/kasir di PostgreSQL.
import main.java.database.PersonDAO;
// Model data kasir yang ditampilkan pada tabel.
import main.java.model.Cashier;
// Tipe tanggal yang digunakan oleh DatePicker dan model.
import java.time.LocalDate;

// Mengelola form, tabel, dan operasi CRUD untuk data kasir.
public class CashierController {
    // Field FXML untuk nama kasir.
    @FXML private TextField nameField;
    // Pilihan jenis role kasir.
    @FXML private ComboBox<String> roleComboBox;
    // Field FXML untuk username login.
    @FXML private TextField usernameField;
    // Field FXML untuk password baru atau password saat pembuatan akun.
    @FXML private PasswordField passwordField;
    // Pemilih tanggal lahir kasir.
    @FXML private DatePicker birthDatePicker;
    // Tombol radio gender laki-laki.
    @FXML private RadioButton maleRadio;
    // Tombol radio gender perempuan.
    @FXML private RadioButton femaleRadio;
    // Tabel daftar kasir; barisnya berisi objek Cashier.
    @FXML private TableView<Cashier> cashierTable;
    // Kolom tabel yang menampilkan nama, role, gender, tanggal lahir, dan username.
    @FXML private TableColumn<Cashier, String> nameColumn;
    @FXML private TableColumn<Cashier, String> roleColumn;
    @FXML private TableColumn<Cashier, String> genderColumn;
    @FXML private TableColumn<Cashier, String> birthDateColumn;
    @FXML private TableColumn<Cashier, String> usernameColumn;
    // DAO yang menjadi perantara antara controller ini dan database.
    private final PersonDAO dao = new PersonDAO();

    // Menyiapkan pilihan role, kolom tabel, dan pengisian form saat baris dipilih.
    @FXML private void initialize(){
        // Menentukan nilai awal kontrol dan cara mengambil nilai objek untuk tiap sel.
        roleComboBox.setItems(FXCollections.observableArrayList("Kasir"));
        roleComboBox.setValue("Kasir");
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        roleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPersonType()));
        genderColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(displayGenderValue(cell.getValue().getGender())));
        birthDateColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getBirthDate() == null ? "" : cell.getValue().getBirthDate().toString()));
        usernameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));
        // Saat baris dipilih, salin datanya ke form agar bisa dibaca atau diedit.
        cashierTable.getSelectionModel().selectedItemProperty().addListener((observable, oldCashier, cashier) -> {
            if (cashier != null) {
                nameField.setText(cashier.getName());
                usernameField.setText(cashier.getUsername());
                maleRadio.setSelected("Male".equals(cashier.getGender()));
                femaleRadio.setSelected("Female".equals(cashier.getGender()));
                birthDatePicker.setValue(cashier.getBirthDate());
                passwordField.clear();
            }
        });
        refreshList();
    }

    // Menyimpan kasir baru lalu memperbarui tampilan tabel.
    @FXML private void add() {
        // Event tombol Tambah: baca form, simpan melalui DAO, lalu segarkan tabel.
        try {
            dao.addCashier(nameField.getText(), usernameField.getText(), passwordField.getText(),
                    selectedGender(), birthDatePicker.getValue());
            clear();
            refreshList();
            new Alert(Alert.AlertType.INFORMATION, "Kasir berhasil ditambahkan.").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Kasir gagal ditambahkan: " + e.getMessage()).showAndWait();
        }
    }

    // Memvalidasi pilihan dan memperbarui data kasir yang sedang dipilih.
    @FXML private void update() {
        // Event tombol Ubah: gunakan baris terpilih sebagai identitas data yang diperbarui.
        Cashier selected = cashierTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Pilih kasir yang ingin diubah.").showAndWait();
            return;
        }
        try {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String gender = selectedGender();
            LocalDate birthDate = birthDatePicker.getValue();
            dao.updateCashier(selected.getId(), name, username, password, gender, birthDate);
            if (Session.currentCashier != null && Session.currentCashier.getId() == selected.getId()) {
                Session.currentCashier.setName(name);
                Session.currentCashier.setUsername(username);
                Session.currentCashier.setGender(gender);
                Session.currentCashier.setBirthDate(birthDate);
            }
            clear();
            refreshList();
            new Alert(Alert.AlertType.INFORMATION, "Data kasir berhasil diubah.").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Data kasir gagal diubah: " + e.getMessage()).showAndWait();
        }
    }

    // Meminta konfirmasi sebelum menghapus kasir yang dipilih.
    @FXML private void delete() {
        // Event tombol Hapus: minta konfirmasi sebelum menghapus data lewat DAO.
        Cashier selected = cashierTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Pilih kasir yang ingin dihapus.").showAndWait();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Hapus kasir " + selected.getName() + "?",
                javafx.scene.control.ButtonType.CANCEL,
                javafx.scene.control.ButtonType.OK);
        confirmation.setTitle("Konfirmasi Hapus");
        if (confirmation.showAndWait().orElse(javafx.scene.control.ButtonType.CANCEL)
                != javafx.scene.control.ButtonType.OK) {
            return;
        }
        try {
            dao.deletePerson(selected.getId());
            if (Session.currentCashier != null && Session.currentCashier.getId() == selected.getId()) {
                Session.currentCashier = null;
            }
            clear();
            refreshList();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Kasir gagal dihapus: " + e.getMessage()).showAndWait();
        }
    }

    // Mengosongkan input dan melepas pilihan pada tabel.
    @FXML private void clear() {
        // Event tombol Bersihkan: kosongkan semua input dan pilihan baris.
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
        birthDatePicker.setValue(null);
        maleRadio.setSelected(true);
        cashierTable.getSelectionModel().clearSelection();
    }

    // Mengubah pilihan radio gender menjadi nilai yang disimpan di database.
    private String selectedGender() {
        // Mengubah pilihan RadioButton menjadi teks Male/Female untuk disimpan.
        if (maleRadio.isSelected()) return "Male";
        if (femaleRadio.isSelected()) return "Female";
        return null;
    }

    private String displayGenderValue(String value) {
        // Menormalkan nilai gender sebelum ditampilkan pada kolom tabel.
        if (value == null) return "";
        return "Male".equalsIgnoreCase(value) ? "Male" : "Female".equalsIgnoreCase(value) ? "Female" : value;
    }

    // Memuat ulang data kasir agar tabel menampilkan isi database terbaru.
    private void refreshList() {
        // Memuat ulang daftar kasir dari database setelah perubahan data.
        try {
            cashierTable.setItems(FXCollections.observableArrayList(dao.getCashiers()));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Data kasir gagal dimuat: " + e.getMessage()).showAndWait();
        }
    }
}
