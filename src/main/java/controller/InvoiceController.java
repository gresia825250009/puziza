package main.java.controller;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class InvoiceController {
    @FXML private TextArea invoiceArea;
    public void setInvoice(String text){ invoiceArea.setText(text); }
}
