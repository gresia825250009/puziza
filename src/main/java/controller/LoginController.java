package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML private void handleLogin() {
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

    @FXML private void backToWelcome() {
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
