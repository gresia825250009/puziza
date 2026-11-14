package main.java.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.database.PizzaDAO;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
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
        launch(args);
    }
}
