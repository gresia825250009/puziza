package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WelcomeController {
    @FXML private void handleUser() { changeScene("/main/resources/user_menu.fxml", "Puziza Pizza - Menu"); }
    @FXML private void handleAdmin() { changeScene("/main/resources/login.fxml", "Puziza Pizza - Admin Login"); }

    private void changeScene(String resource, String title) {
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

    @FXML private javafx.scene.layout.StackPane root;
}
