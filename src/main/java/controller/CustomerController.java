package main.java.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.database.PersonDAO;
import main.java.model.Customer;
import java.time.LocalDate;

/** Mengelola form dan tabel customer pada halaman admin. */
public class CustomerController {
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;
    @FXML private DatePicker birthDatePicker;
    @FXML private CheckBox subscribedCheckBox;

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> roleColumn;
    @FXML private TableColumn<Customer, String> genderColumn;
    @FXML private TableColumn<Customer, String> birthDateColumn;
    @FXML private TableColumn<Customer, String> subscribedColumn;
    @FXML private TableColumn<Customer, String> otherColumn;

    private final PersonDAO dao = new PersonDAO();

    @FXML
    private void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("Buyer"));
        roleComboBox.setValue("Buyer");

        nameColumn.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getName()));
        roleColumn.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getrole()));
        genderColumn.setCellValueFactory(v -> new SimpleStringProperty(displayGender(v.getValue().getGender())));
        birthDateColumn.setCellValueFactory(v -> new SimpleStringProperty(
                v.getValue().getBirthDate() == null ? "" : v.getValue().getBirthDate().toString()));
        subscribedColumn.setCellValueFactory(v -> new SimpleStringProperty(
                Boolean.toString(v.getValue().isSubscribed())));
        otherColumn.setCellValueFactory(v -> {
            Customer c = v.getValue();
            String details = "No HP: " + safe(c.getPhone());
            if (c.getEmail() != null && !c.getEmail().isBlank()) {
                details += " | Email: " + c.getEmail();
            }
            return new SimpleStringProperty(details);
        });

        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, customer) -> {
            if (customer != null) {
                nameField.setText(customer.getName());
                phoneField.setText(customer.getPhone());
                roleComboBox.setValue("Buyer");
                maleRadio.setSelected("Male".equalsIgnoreCase(customer.getGender()));
                femaleRadio.setSelected("Female".equalsIgnoreCase(customer.getGender()));
                birthDatePicker.setValue(customer.getBirthDate());
                subscribedCheckBox.setSelected(customer.isSubscribed());
            }
        });
        refreshList();
    }

    @FXML private void add() {
        try {
            dao.addCustomer(buildCustomer(0, LocalDate.now()));
            clear();
            refreshList();
            info("Customer berhasil ditambahkan.");
        } catch (Exception e) { error("Customer gagal ditambahkan: " + e.getMessage()); }
    }

    @FXML private void update() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) { warning("Pilih customer yang ingin diubah."); return; }
        try {
            dao.updateCustomer(buildCustomer(selected.getId(), selected.getRegisteredAt()));
            clear();
            refreshList();
            info("Data customer berhasil diubah.");
        } catch (Exception e) { error("Data customer gagal diubah: " + e.getMessage()); }
    }

    @FXML private void delete() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) { warning("Pilih customer yang ingin dihapus."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Hapus customer " + selected.getName() + "?",
                ButtonType.CANCEL, ButtonType.OK);
        confirm.setTitle("Konfirmasi Hapus");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            dao.deletePerson(selected.getId());
            clear();
            refreshList();
        } catch (Exception e) { error("Customer gagal dihapus: " + e.getMessage()); }
    }

    private Customer buildCustomer(int id, LocalDate registeredAt) {
        String gender = maleRadio.isSelected() ? "Male" : femaleRadio.isSelected() ? "Female" : null;
        return new Customer(id, nameField.getText(), phoneField.getText(), registeredAt,
                null, gender, birthDatePicker.getValue(), subscribedCheckBox.isSelected());
    }

    @FXML private void clear() {
        nameField.clear();
        phoneField.clear();
        roleComboBox.setValue("Buyer");
        maleRadio.setSelected(true);
        birthDatePicker.setValue(null);
        subscribedCheckBox.setSelected(false);
        customerTable.getSelectionModel().clearSelection();
    }

    private void refreshList() {
        try {
            customerTable.setItems(FXCollections.observableArrayList(dao.getCustomers()));
        } catch (Exception e) { error("Data customer gagal dimuat: " + e.getMessage()); }
    }

    private String displayGender(String value) { return value == null ? "" : value; }
    private String safe(String value) { return value == null ? "" : value; }
    private void info(String message) { new Alert(Alert.AlertType.INFORMATION, message).showAndWait(); }
    private void warning(String message) { new Alert(Alert.AlertType.WARNING, message).showAndWait(); }
    private void error(String message) { new Alert(Alert.AlertType.ERROR, message).showAndWait(); }
}
