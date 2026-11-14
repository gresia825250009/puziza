package main.java.controller;
import main.java.database.PenjualanDAO;
import main.java.database.PizzaDAO;
import main.java.database.PersonDAO;
import main.java.model.Cashier;
import main.java.model.Customer;
import main.java.model.OrderItem;
import main.java.model.Penjualan;
import main.java.model.Pizza;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderController {
    @FXML private ComboBox<Pizza> pizzaCombo;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private ComboBox<String> customerCombo;
    @FXML private ComboBox<Cashier> cashierCombo;
    @FXML private ListView<String> cartList;
    @FXML private Label totalLabel;
    private final ObservableList<OrderItem> cart=FXCollections.observableArrayList();
    private final PizzaDAO pizzaDAO=new PizzaDAO();
    @FXML private void initialize(){
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,20,1));
        try { pizzaCombo.setItems(FXCollections.observableArrayList(pizzaDAO.getAll())); }
        catch(Exception e){ showError(e); }
        try {
            var names=FXCollections.<String>observableArrayList();
            for(Customer c:new PersonDAO().getCustomers()) names.add(c.getName());
            customerCombo.setItems(names);
            var kasir=new PersonDAO().getCashiers();
            cashierCombo.setItems(FXCollections.observableArrayList(kasir));
            if(Session.currentCashier!=null)
                for(Cashier k:kasir) if(k.getId()==Session.currentCashier.getId()) cashierCombo.setValue(k);
        } catch(Exception e){ showError(e); }
        refreshCart();
    }
    @FXML private void addItem(){
        Pizza p=pizzaCombo.getValue(); if(p==null){warn("Pilih pizza terlebih dahulu.");return;}
        cart.add(new OrderItem(p,quantitySpinner.getValue())); refreshCart();
    }
    @FXML private void clearCart(){cart.clear();refreshCart();}
    @FXML private void checkout(){
        if(cart.isEmpty()){warn("Keranjang masih kosong.");return;}
        if(cashierCombo.getValue()==null){warn("Pilih kasir yang melayani terlebih dahulu.");return;}
        try {
            String nama=customerCombo.getEditor().getText();
            Customer customer=(nama==null||nama.isBlank())?null:new PersonDAO().findOrCreateCustomer(nama.trim());
            // Transaksi dapat disimpan sebagai walk-in. Customer terdaftar dapat dihubungkan
            // setelah dipilih dari daftar customer yang tersimpan di database.
            Penjualan sale=new Penjualan(0,"PZ-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(),LocalDateTime.now(),customer,cashierCombo.getValue());
            for(OrderItem item:cart) sale.addPizza(item.getPizza(),item.getQuantity());
            new PenjualanDAO().save(sale);
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/main/resources/invoice.fxml"));
            javafx.scene.Parent view=loader.load();
            InvoiceController controller=loader.getController(); controller.setInvoice(sale.generateInvoice());
            ButtonType lagi=new ButtonType("Pesan Lagi",ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType utama=new ButtonType("Kembali ke Halaman Utama",ButtonBar.ButtonData.OK_DONE);
            Dialog<ButtonType> dialog=new Dialog<>(); dialog.setTitle("Struk Puziza"); dialog.getDialogPane().setContent(view);
            dialog.getDialogPane().getButtonTypes().addAll(lagi,utama);
            ButtonType pilih=dialog.showAndWait().orElse(lagi);
            cart.clear(); refreshCart();
            if(pilih==utama) backToHome();
        } catch(Exception e){showError(e);}
    }
    @FXML private void backToHome(){
        try { Nav.home((javafx.stage.Stage) cartList.getScene().getWindow()); }
        catch(Exception e){ showError(e); }
    }
    private void refreshCart(){
        cartList.getItems().clear(); double total=0;
        for(OrderItem i:cart){cartList.getItems().add(i.getQuantity()+" x "+i.getPizza()); total+=i.getSubtotal();}
        totalLabel.setText(String.format("Total sebelum pajak: Rp%,.0f | Pajak 10%%: Rp%,.0f | Total: Rp%,.0f",total,total*.10,total*1.10));
    }
    private void warn(String s){new Alert(Alert.AlertType.WARNING,s).showAndWait();}
    private void showError(Exception e){new Alert(Alert.AlertType.ERROR,e.getMessage()).showAndWait();}
}
