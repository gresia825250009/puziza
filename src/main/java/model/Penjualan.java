package main.java.model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

/** Association: transaksi memiliki customer, kasir, dan banyak item pizza. */
public class Penjualan implements Invoice {
    private int id;
    private String saleCode;
    private LocalDateTime createdAt;
    private Customer customer;
    private Cashier cashier;
    private final List<OrderItem> items = new ArrayList<>();
    public Penjualan(int id,String saleCode,LocalDateTime createdAt,Customer customer,Cashier cashier){
        this.id=id; this.saleCode=saleCode; this.createdAt=createdAt; this.customer=customer; this.cashier=cashier;
    }
    public int getId(){return id;}
    public String getSaleCode(){return saleCode;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public Customer getCustomer(){return customer;}
    public Cashier getCashier(){return cashier;}
    public List<OrderItem> getItems(){return items;}
    public void addPizza(Pizza pizza,int quantity){ if(quantity>0) items.add(new OrderItem(pizza,quantity)); }
    public double getSubtotal(){ return items.stream().mapToDouble(OrderItem::getSubtotal).sum(); }
    public double getTax(){ return getSubtotal()*0.10; }
    public double getTotal(){ return getSubtotal()+getTax(); }
    @Override public String generateInvoice(){
        NumberFormat rp=NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        StringBuilder b=new StringBuilder("PUZIZA PIZZA\n================================\n");
        b.append("Order: ").append(saleCode).append("\nDate: ").append(createdAt).append("\n");
        b.append("Customer: ").append(customer==null?"Walk-in":customer.getName()).append("\n");
        b.append("Kasir: ").append(cashier==null?"-":cashier.getName()).append("\n");
        for(OrderItem i:items) b.append(i.getQuantity()).append(" x ").append(i.getPizza()).append(" = ").append(rp.format(i.getSubtotal())).append("\n");
        b.append("--------------------------------\nSubtotal: ").append(rp.format(getSubtotal()));
        b.append("\nTax 10%: ").append(rp.format(getTax()));
        b.append("\nTOTAL: ").append(rp.format(getTotal())).append("\nTerima kasih!");
        return b.toString();
    }
}
