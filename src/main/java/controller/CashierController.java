package main.java.controller;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import main.java.database.PersonDAO;
import main.java.model.Cashier;

public class CashierController {
    @FXML private ListView<String> cashierList;
    @FXML private void initialize(){
        try {
            var rows=FXCollections.<String>observableArrayList();
            for(Cashier k:new PersonDAO().getCashiers()) rows.add(k.getName()+"  |  username: "+k.getUsername());
            cashierList.setItems(rows);
        } catch(Exception e){ new Alert(Alert.AlertType.ERROR,"Data kasir gagal dimuat: "+e.getMessage()).showAndWait(); }
    }
}
