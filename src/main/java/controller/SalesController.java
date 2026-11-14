package main.java.controller;
import main.java.database.PenjualanDAO;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
public class SalesController {
    @FXML private ListView<String> salesList;
    @FXML private void initialize(){refresh();}
    @FXML private void refresh(){try{salesList.setItems(FXCollections.observableArrayList(new PenjualanDAO().getHistory()));}catch(Exception e){new Alert(Alert.AlertType.ERROR,e.getMessage()).showAndWait();}}
}
