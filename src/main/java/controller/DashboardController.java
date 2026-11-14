package main.java.controller;
/*
 * Class: DashboardController
 * Deskripsi: Mengatur dashboard admin dan navigasi tab.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// Menghubungkan elemen dan aksi dari file FXML ke controller.
import javafx.fxml.FXML;
// Memuat halaman FXML lain saat tab dibuka.
import javafx.fxml.FXMLLoader;
// Node adalah tipe dasar konten yang dapat ditempatkan pada tab.
import javafx.scene.Node;
// Alert memberitahu pengguna jika halaman gagal dibuka.
import javafx.scene.control.Alert;
// Label menampilkan nama pengguna yang sedang masuk.
import javafx.scene.control.Label;
// Tab dan TabPane mengatur halaman admin berbentuk tab.
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
// Stage adalah jendela utama JavaFX.
import javafx.stage.Stage;

// Mengatur tab halaman admin dan identitas pengguna yang sedang masuk.
public class DashboardController {
    // Kontrol TabPane tempat halaman manajemen ditampilkan.
    @FXML private TabPane mainTabPane;
    // Label sapaan pada dashboard.
    @FXML private Label adminUserLabel;

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

    // Memilih tab yang sudah terbuka atau memuat halaman baru ke tab.
    private void openTab(String title, String resource) {
        // Cari tab dengan judul sama agar halaman yang sudah dibuka tidak diduplikasi.
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
        // Event Logout: kembali ke halaman sambutan pada jendela yang sama.
        try {
            Nav.home((Stage) mainTabPane.getScene().getWindow());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Gagal kembali ke halaman utama: " + e.getMessage()).showAndWait();
        }
    }

    // Menampilkan nama kasir/admin dan membuka halaman awal dashboard.
    @FXML private void initialize() {
        // Event inisialisasi FXML: tampilkan pengguna dan buka tab pizza pertama.
        String cashierName = Session.currentCashier == null
                ? "Administrator"
                : Session.currentCashier.getName();
        adminUserLabel.setText("Hi, " + cashierName);
        showPizzaView();
    }
}
