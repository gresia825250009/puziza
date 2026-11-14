package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class DashboardController {
    @FXML private TabPane mainTabPane;

    @FXML private void showPizzaView() {
        openTab("Manage Pizza", "/main/resources/pizza.fxml");
    }

    @FXML private void showCustomerView() {
        openTab("Manage Customer", "/main/resources/customer.fxml");
    }

    @FXML private void showCashierView() {
        openTab("Manage Cashier", "/main/resources/cashier.fxml");
    }

    @FXML private void showSalesView() {
        openTab("Manage Sales", "/main/resources/sales.fxml");
    }

    private void openTab(String title, String resource) {
        for (Tab tab : mainTabPane.getTabs()) {
            if (title.equals(tab.getText())) {
                mainTabPane.getSelectionModel().select(tab);
                return;
            }
        }
        try {
            Node page = FXMLLoader.load(getClass().getResource(resource));
            Tab tab = new Tab(title, page);
            mainTabPane.getTabs().add(tab);
            mainTabPane.getSelectionModel().select(tab);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Tidak dapat membuka halaman: " + e.getMessage()).showAndWait();
        }
    }

    @FXML private void logout() {
        try {
            Nav.home((Stage) mainTabPane.getScene().getWindow());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Gagal kembali ke halaman utama: " + e.getMessage()).showAndWait();
        }
    }

    @FXML private void initialize() {
        showPizzaView();
    }
}
