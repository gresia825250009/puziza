package main.java.controller;
/*
 * Class: UserMenuController
 * Deskripsi: Menampilkan menu pizza untuk pelanggan.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// Membuat daftar menu yang dapat diamati ListView.
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
// Menghubungkan field dan handler dengan FXML.
import javafx.fxml.FXML;
// Memuat FXML untuk berpindah halaman.
import javafx.fxml.FXMLLoader;
// Scene menggambarkan halaman aktif dalam window.
import javafx.scene.Scene;
// Alert menampilkan kegagalan proses kepada pengguna.
import javafx.scene.control.Alert;
// ListView menampilkan menu pizza untuk pelanggan.
import javafx.scene.control.ListView;
// Stage adalah jendela utama aplikasi.
import javafx.stage.Stage;
// DAO dan model untuk mengambil isi menu.
import main.java.database.PizzaDAO;
import main.java.model.Pizza;

// Menampilkan menu pizza dan mengarahkan pengguna ke alur pemesanan.
public class UserMenuController {
    // Daftar nama, ukuran, harga, dan diskon pizza yang tersedia.
    @FXML private ListView<String> menuList;

    // Memuat nama, ukuran, harga, dan informasi diskon untuk daftar menu.
    @FXML
    private void initialize() {
        // Membaca menu dari DAO, menyusun teks tiap item, lalu mengisi ListView.
        try {
            ObservableList<String> rows = FXCollections.observableArrayList();
            for (Pizza pizza : new PizzaDAO().getAll()) {
                rows.add(pizza.getName() + " | " + pizza.getSize() + " | Rp"
                        + String.format("%,.0f", pizza.getBasePrice())
                        + (pizza.getDiscountRate() > 0 ? " | Diskon 45%" : ""));
            }
            menuList.setItems(rows);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Menu gagal dimuat: " + e.getMessage())
                    .showAndWait();
        }
    }

    @FXML
    private void startOrder() {
        // Event tombol pesan: buka layar order pada window yang sama.
        try {
            Nav.order((Stage) menuList.getScene().getWindow());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
    // Event tombol kembali: menuju halaman sambutan.
    @FXML
    private void backToWelcome() {
        changePage("/main/resources/welcome.fxml", "Puziza Pizza");
    }

    // Mengganti halaman pada window yang sama dengan stylesheet aplikasi.
    private void changePage(String resource, String title) {
        // Memuat halaman tujuan dan CSS, kemudian mengganti isi window.
        try {
            Stage stage = (Stage) menuList.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Scene scene = new Scene(loader.load(), 1000, 650);
            scene.getStylesheets().add(
                    getClass().getResource("/main/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}