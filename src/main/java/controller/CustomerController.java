package main.java.controller;
import main.java.database.PersonDAO;
import main.java.model.Customer;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class CustomerController {
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer,Integer> idColumn;
    @FXML private TableColumn<Customer,String> nameColumn,phoneColumn,emailColumn;
    @FXML private TextField nameField,phoneField,emailField;
    private final PersonDAO dao=new PersonDAO();
    @FXML private void initialize(){
        idColumn.setCellValueFactory(v->new javafx.beans.property.SimpleIntegerProperty(v.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(v->new javafx.beans.property.SimpleStringProperty(v.getValue().getName()));
        phoneColumn.setCellValueFactory(v->new javafx.beans.property.SimpleStringProperty(v.getValue().getPhone()));
        emailColumn.setCellValueFactory(v->new javafx.beans.property.SimpleStringProperty(v.getValue().getEmail()));
        customerTable.getSelectionModel().selectedItemProperty().addListener((o,a,p)->{if(p!=null){nameField.setText(p.getName());phoneField.setText(p.getPhone());emailField.setText(p.getEmail());}});
        refresh();
    }
    @FXML private void add(){try{Customer c=new Customer(0,nameField.getText(),phoneField.getText(),LocalDate.now(),emailField.getText());if(!c.validate())throw new IllegalArgumentException("Nama dan nomor telepon (8-15 digit) wajib valid.");dao.addCustomer(c);refresh();clear();}catch(Exception e){error(e);}}
    @FXML private void delete(){try{Customer c=customerTable.getSelectionModel().getSelectedItem();if(c!=null)dao.deletePerson(c.getId());refresh();clear();}catch(Exception e){error(e);}}
    @FXML private void clear(){nameField.clear();phoneField.clear();emailField.clear();}
    private void refresh(){try{customerTable.setItems(FXCollections.observableArrayList(dao.getCustomers()));}catch(Exception e){error(e);}}
    private void error(Exception e){new Alert(Alert.AlertType.ERROR,e.getMessage()).showAndWait();}
}
