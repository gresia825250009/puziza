package main.java.controller;
/*
 * Class: LoginController
 * Deskripsi: Memproses login admin atau kasir.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// FXML menghubungkan aksi dan field controller ke tampilan login.
import javafx.fxml.FXML;
// FXMLLoader dan Scene memuat serta menampilkan file FXML yang dipilih.
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
// Label, TextField, dan PasswordField adalah elemen form login.
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
// Stage mewakili jendela yang akan diganti halamannya.
import javafx.stage.Stage;

// Menangani autentikasi kasir dan perpindahan dari/ke halaman login.
public class LoginController {
    // Input username untuk proses autentikasi.
    @FXML private TextField usernameField;
    // Input password yang karakternya disamarkan.
    @FXML private PasswordField passwordField;
    // Area pesan jika login atau pemuatan halaman gagal.
    @FXML private Label errorLabel;

    // Memeriksa kredensial lalu membuka dashboard jika autentikasi berhasil.
    @FXML private void handleLogin() {
        // Event tombol Login: autentikasi ke DAO, simpan sesi, lalu buka dashboard.
        try {
            main.java.model.Cashier kasir = new main.java.database.PersonDAO()
                    .authenticate(usernameField.getText().trim(), passwordField.getText());
            if (kasir == null) {
                errorLabel.setText("Username atau password salah.");
                return;
            }
            Session.currentCashier = kasir;
            {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/dashboard.fxml"));
                Scene scene = new Scene(loader.load(), 1100, 700);
                scene.getStylesheets().add(getClass().getResource("/main/resources/css/style.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Puziza Pizza - Admin");
            }
        } catch (Exception e) {
            errorLabel.setText("Gagal login: " + e.getMessage());
        }
    }

    // Mengembalikan pengguna ke halaman sambutan.
    @FXML private void backToWelcome() {
        // Event tombol Kembali: muat ulang tampilan sambutan.
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/welcome.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 650);
            scene.getStylesheets().add(getClass().getResource("/main/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Puziza Pizza");
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }
}
