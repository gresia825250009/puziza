package main.java.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import main.java.database.PizzaDAO;
import main.java.model.Pizza;

public class UserMenuController {
    @FXML private ListView<String> menuList;

    @FXML private void initialize() {
        try {
            var rows = FXCollections.<String>observableArrayList();
            for (Pizza pizza : new PizzaDAO().getAll()) {
                rows.add(pizza.getName() + " | " + pizza.getSize() + " | Rp" +
                    String.format("%,.0f", pizza.getBasePrice()) +
                    (pizza.getDiscountRate() > 0 ? " | Diskon 45%" : ""));
            }
            menuList.setItems(rows);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Menu gagal dimuat: " + e.getMessage()).showAndWait();
        }
    }

    @FXML private void startOrder() {
        try { Nav.order((Stage) menuList.getScene().getWindow()); }
        catch (Exception e) { new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait(); }
    }
    @FXML private void backToWelcome() { changePage("/main/resources/welcome.fxml", "Puziza Pizza"); }

    private void changePage(String resource, String title) {
        try {
            Stage stage = (Stage) menuList.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Scene scene = new Scene(loader.load(), 1000, 650);
            scene.getStylesheets().add(getClass().getResource("/main/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}
