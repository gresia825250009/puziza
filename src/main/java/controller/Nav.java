package main.java.controller;
/*
 * Class: Nav
 * Deskripsi: Helper untuk berpindah antarhalaman aplikasi.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// Memuat tampilan FXML tujuan.
import javafx.fxml.FXMLLoader;
// Scene adalah isi visual di dalam Stage.
import javafx.scene.Scene;
// Stage adalah jendela aplikasi yang dipakai ulang.
import javafx.stage.Stage;

/** Helper pindah halaman supaya ukuran window ikut menyesuaikan tiap halaman. */
// Menyediakan helper navigasi dengan ukuran window yang sesuai tiap halaman.
final class Nav {
    // Constructor privat mencegah pembuatan objek helper navigasi.
    private Nav() {
    }

    // Memuat FXML dan stylesheet, lalu mengatur ukuran serta judul window.
    static void go(
            Stage stage,
            String resource,
            String title,
            double w,
            double h,
            double minW,
            double minH) throws Exception {
        // Memasang FXML dan stylesheet, lalu mengatur ukuran/judul window.
        FXMLLoader loader = new FXMLLoader(Nav.class.getResource(resource));
        Scene scene = new Scene(loader.load(), w, h);
        scene.getStylesheets().add(Nav.class.getResource("/main/resources/css/style.css").toExternalForm());
        stage.setMinWidth(minW);
        stage.setMinHeight(minH);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setWidth(w);   // paksa ukuran window mengikuti halaman baru
        stage.setHeight(h);
        stage.centerOnScreen();
    }

    static void home(Stage stage) throws Exception {
        // Navigasi menuju layar sambutan dengan ukuran window yang sesuai.
        go(stage, "/main/resources/welcome.fxml", "Puziza Pizza", 1000, 650, 900, 600);
    }

    static void order(Stage stage) throws Exception {
        // Navigasi menuju layar pemesanan dengan ukuran window yang sesuai.
        go(stage, "/main/resources/order.fxml", "Puziza - Pemesanan", 700, 540, 640, 480);
    }
}