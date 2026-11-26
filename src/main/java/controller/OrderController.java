package main.java.controller;
/*
 * Class: OrderController
 * Deskripsi: Mengelola menu, keranjang, checkout, dan invoice.
 * @author Gresia Valerina Widi
 * @author Garneta Auril Triasis
 */

// DAO transaksi untuk menyimpan penjualan dan detail item.
import main.java.database.PenjualanDAO;
// DAO untuk mengambil daftar menu pizza.
import main.java.database.PizzaDAO;
// DAO untuk mengambil data customer dan kasir.
import main.java.database.PersonDAO;
// Model yang dipilih sebagai kasir pada checkout.
import main.java.model.Cashier;
// Model customer dari database.
import main.java.model.Customer;
// Satu baris pizza dan jumlahnya di keranjang.
import java.util.Map;
// Model transaksi yang berisi customer, kasir, dan item.
import main.java.model.Penjualan;
// Model menu pizza yang dipilih pengguna.
import main.java.model.Pizza;

// Property teks untuk mengisi sel tabel dan daftar JavaFX yang diamati UI.
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
// Waktu transaksi ketika checkout dilakukan.
import java.time.LocalDateTime;
// List untuk menampung data kasir dari DAO.
import java.util.List;
// Membuat kode transaksi yang tidak mudah berulang.
import java.util.UUID;

// Mengelola pilihan menu, keranjang, checkout, dan pembuatan invoice.
public class OrderController {
    // Pilihan menu pizza, jumlah pesanan, customer, dan kasir di form order.
    @FXML private ComboBox<Pizza> pizzaCombo;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private ComboBox<String> customerCombo;
    @FXML private ComboBox<Cashier> cashierCombo;
    // Tabel keranjang dan kolom customer, nama pizza, ukuran, jumlah, subtotal.
    @FXML private TableView<Map.Entry<Pizza, Integer>> cartTable;
    @FXML private TableColumn<Map.Entry<Pizza, Integer>, String> customerColumn;
    @FXML private TableColumn<Map.Entry<Pizza, Integer>, String> nameColumn;
    @FXML private TableColumn<Map.Entry<Pizza, Integer>, String> sizeColumn;
    @FXML private TableColumn<Map.Entry<Pizza, Integer>, String> qtyColumn;
    @FXML private TableColumn<Map.Entry<Pizza, Integer>, String> subtotalColumn;
    // Label yang menampilkan perhitungan nilai belanja.
    @FXML private Label totalLabel;
    // Daftar item pesanan yang menjadi sumber baris cartTable.
    private final Penjualan cart = new Penjualan(0, null, null, null, null);
    // Akses database khusus data menu pizza.
    private final PizzaDAO pizzaDAO = new PizzaDAO();

    // Memuat data pizza, customer, dan kasir serta menyiapkan kolom keranjang.
    @FXML
    private void initialize() {
        // Menetapkan jumlah minimum/maksimum dan mengisi pilihan menu/orang.
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,20,1));
        try {
            pizzaCombo.setItems(FXCollections.observableArrayList(pizzaDAO.getAll()));
        } catch (Exception e) {
            showError(e);
        }

        try {
            ObservableList<String> names = FXCollections.observableArrayList();
            for (Customer customer : new PersonDAO().getCustomers()) {
                names.add(customer.getName());
            }
            customerCombo.setItems(names);
            List<Cashier> cashiers = new PersonDAO().getCashiers();
            cashierCombo.setItems(FXCollections.observableArrayList(cashiers));
            if (Session.currentCashier != null) {
                for (Cashier cashier : cashiers) {
                    if (cashier.getId() == Session.currentCashier.getId()) {
                        cashierCombo.setValue(cashier);
                    }
                }
            }
        } catch (Exception e) {
            showError(e);
        }

        // Kolom customer mengikuti editor ComboBox; perubahan nama menyegarkan tabel.
        customerColumn.setCellValueFactory(d -> new SimpleStringProperty(currentCustomerName()));
        customerCombo.getEditor().textProperty().addListener((observable, oldName, newName) -> cartTable.refresh());
        customerCombo.valueProperty().addListener((observable, oldName, newName) -> cartTable.refresh());
        nameColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getKey().getName()));
        sizeColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getKey().getSize()));
        qtyColumn.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getValue())));
        subtotalColumn.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("Rp%,.0f", cart.getLineSubtotal(d.getValue().getKey()))));
        refreshCart();
    }

    // Menambahkan pizza terpilih ke keranjang sesuai jumlah yang diminta.
    @FXML
    private void addItem() {
        // Event Tambah Item: pastikan pizza dipilih, lalu masukkan ke keranjang.
        Pizza p = pizzaCombo.getValue();
        if (p == null) {
            warn("Pilih pizza terlebih dahulu.");
            return;
        }
        cart.addPizza(p, quantitySpinner.getValue());
        refreshCart();
    }

    // Event Bersihkan Keranjang: hapus semua item dan hitung ulang total.
    @FXML
    private void clearCart() {
        cart.clearItems();
        refreshCart();
    }

    // Menyimpan transaksi, menampilkan invoice, dan mengosongkan keranjang.
    @FXML
    private void checkout() {
        // Event Checkout: validasi keranjang/kasir, simpan transaksi, dan tampilkan invoice.
        if (cart.isEmpty()) {
            warn("Keranjang masih kosong.");
            return;
        }
        if (cashierCombo.getValue() == null) {
            warn("Pilih kasir yang melayani terlebih dahulu.");
            return;
        }

        try {
            String nama = customerCombo.getEditor().getText();
            Customer customer = (nama == null || nama.isBlank())
                    ? null
                    : new PersonDAO().findOrCreateCustomer(nama.trim());
            // Transaksi dapat disimpan sebagai walk-in. Customer terdaftar dapat dihubungkan
            // setelah dipilih dari daftar customer yang tersimpan di database.
            cart.setSaleCode("PZ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            cart.setCreatedAt(LocalDateTime.now());
            cart.setCustomer(customer);
            cart.setCashier(cashierCombo.getValue());
            new PenjualanDAO().save(cart);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/invoice.fxml"));
            javafx.scene.Parent view = loader.load();
            InvoiceController controller = loader.getController();
            controller.setInvoice(cart.generateInvoice());

            ButtonType lagi = new ButtonType("Pesan Lagi", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType utama = new ButtonType("Kembali ke Halaman Utama", ButtonBar.ButtonData.OK_DONE);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Struk Puziza");
            dialog.getDialogPane().setContent(view);
            dialog.getDialogPane().getButtonTypes().addAll(lagi, utama);
            ButtonType pilih = dialog.showAndWait().orElse(lagi);

            cart.clearItems();
            refreshCart();
            if (pilih == utama) {
                backToHome();
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    @FXML
    private void backToHome() {
        // Event kembali: pindah ke halaman sambutan melalui helper navigasi.
        try {
            Nav.home((javafx.stage.Stage) cartTable.getScene().getWindow());
        } catch (Exception e) {
            showError(e);
        }
    }

    // Menyegarkan isi tabel keranjang dari data Penjualan, lalu hitung ulang total.
    private void refreshCart() {
        cartTable.setItems(FXCollections.observableArrayList(cart.getItems().entrySet()));
        refreshTotal();
    }

    // Menampilkan subtotal, pajak, dan total dari keranjang.
    private void refreshTotal() {
        totalLabel.setText(String.format(
                "Total sebelum pajak: Rp%,.0f | Pajak 10%%: Rp%,.0f | Total: Rp%,.0f",
                cart.getSubtotal(),
                cart.getTax(),
                cart.getTotal()));
    }

    // Menampilkan Walk-in jika nama customer belum diisi.
    private String currentCustomerName() {
        // Nama kosong berarti pelanggan walk-in/tidak terdaftar.
        String name = customerCombo.getEditor().getText();
        return name == null || name.isBlank() ? "Walk-in" : name.trim();
    }

    // Menampilkan peringatan atas input yang belum memenuhi syarat.
    private void warn(String s) {
        new Alert(Alert.AlertType.WARNING, s).showAndWait();
    }

    // Menampilkan detail kesalahan dari proses data atau database.
    private void showError(Exception e) {
        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
    }
}