package main.java.view;
/*
 * Class: Main
 * Deskripsi: Menjalankan aplikasi JavaFX dan membuka layar awal.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// Application adalah kelas dasar untuk program JavaFX.
import javafx.application.Application;
// FXMLLoader membaca tampilan sambutan dari file FXML.
import javafx.fxml.FXMLLoader;
// Scene dan Stage membentuk isi dan jendela utama.
import javafx.scene.Scene;
import javafx.stage.Stage;
// DAO memuat menu awal ke database bila diperlukan.
import main.java.database.PizzaDAO;

// Menyiapkan data awal dan membuka halaman pertama aplikasi JavaFX.
public class Main extends Application {
    @Override
    // Memuat data menu/kasir sebelum menampilkan halaman sambutan.
    public void start(Stage stage) throws Exception {
        // Menyiapkan data awal sebelum FXML dibuka dan ditampilkan ke pengguna.
        PizzaDAO.seedMenuIfEmpty();
        main.java.database.PersonDAO.seedCashiers();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/welcome.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 650);
        scene.getStylesheets().add(getClass().getResource("/main/resources/css/style.css").toExternalForm());
        stage.setTitle("Puziza Pizza");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // launch menyerahkan pengelolaan aplikasi kepada JavaFX.
        launch(args);
    }
}
