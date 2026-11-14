package main.java.controller;
/*
 * Class: WelcomeController
 * Deskripsi: Menangani pilihan akses dari halaman sambutan.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// FXML menghubungkan aksi tombol dan kontainer akar dengan controller.
import javafx.fxml.FXML;
// FXMLLoader membaca berkas FXML yang dipilih.
import javafx.fxml.FXMLLoader;
// Scene menjadi halaman visual di dalam window.
import javafx.scene.Scene;
// Stage adalah window yang dipakai ulang saat navigasi.
import javafx.stage.Stage;

// Mengarahkan pengguna dari halaman sambutan ke menu atau login admin.
public class WelcomeController {
    // Event tombol pengguna: pindah ke daftar menu pizza.
    @FXML
    private void handleUser() {
        changeScene("/main/resources/user_menu.fxml", "Puziza Pizza - Menu");
    }

    // Event tombol admin: pindah ke halaman login.
    @FXML
    private void handleAdmin() {
        changeScene("/main/resources/login.fxml", "Puziza Pizza - Admin Login");
    }

    // Memuat halaman tujuan dan memasangnya pada window saat ini.
    private void changeScene(String resource, String title) {
        // Membaca FXML tujuan, menambahkan CSS, lalu memasang Scene di Stage.
        try {
            Stage stage = (Stage) root.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Scene scene = new Scene(loader.load(), 1000, 650);
            scene.getStylesheets().add(getClass().getResource("/main/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Kontainer FXML yang dipakai untuk mengambil Stage aktif.
    @FXML private javafx.scene.layout.StackPane root;
}
