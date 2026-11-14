package main.java.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Helper pindah halaman supaya ukuran window ikut menyesuaikan tiap halaman. */
final class Nav {
    private Nav() {}

    static void go(Stage stage, String resource, String title, double w, double h, double minW, double minH) throws Exception {
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
        go(stage, "/main/resources/welcome.fxml", "Puziza Pizza", 1000, 650, 900, 600);
    }

    static void order(Stage stage) throws Exception {
        go(stage, "/main/resources/order.fxml", "Puziza - Pemesanan", 700, 540, 640, 480);
    }
}
