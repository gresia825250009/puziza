package main.java.controller;
import main.java.database.PizzaDAO;
import main.java.model.Pizza;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
public class PizzaController {
    @FXML private TableView<Pizza> pizzaTable;
    @FXML private TableColumn<Pizza,Integer> idColumn;
    @FXML private TableColumn<Pizza,String> nameColumn,sizeColumn;
    @FXML private TableColumn<Pizza,Number> priceColumn;
    @FXML private TextField nameField,sizeField,priceField;
    private final PizzaDAO dao=new PizzaDAO();
    @FXML private void initialize(){
        idColumn.setCellValueFactory(v->new javafx.beans.property.SimpleIntegerProperty(v.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(v->new javafx.beans.property.SimpleStringProperty(v.getValue().getName()));
        sizeColumn.setCellValueFactory(v->new javafx.beans.property.SimpleStringProperty(v.getValue().getSize()));
        priceColumn.setCellValueFactory(v->new javafx.beans.property.SimpleDoubleProperty(v.getValue().getBasePrice()));
        pizzaTable.getSelectionModel().selectedItemProperty().addListener((o,a,p)->{if(p!=null){nameField.setText(p.getName());sizeField.setText(p.getSize());priceField.setText(String.valueOf(p.getBasePrice()));}});
        refresh();
    }
    @FXML private void add(){try{dao.insert(nameField.getText(),sizeField.getText(),Double.parseDouble(priceField.getText()));refresh();clear();}catch(Exception e){error(e);}}
    @FXML private void update(){try{Pizza p=pizzaTable.getSelectionModel().getSelectedItem();if(p==null)return;dao.update(p.getId(),nameField.getText(),sizeField.getText(),Double.parseDouble(priceField.getText()));refresh();}catch(Exception e){error(e);}}
    @FXML private void delete(){try{Pizza p=pizzaTable.getSelectionModel().getSelectedItem();if(p!=null)dao.delete(p.getId());refresh();clear();}catch(Exception e){error(e);}}
    @FXML private void clear(){nameField.clear();sizeField.clear();priceField.clear();}
    private void refresh(){try{pizzaTable.setItems(FXCollections.observableArrayList(dao.getAll()));}catch(Exception e){error(e);}}
    private void error(Exception e){new Alert(Alert.AlertType.ERROR,e.getMessage()).showAndWait();}
}
