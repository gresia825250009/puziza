package main.java.model;
/** Association antara satu transaksi dengan pizza yang dipesan. */
public class OrderItem {
    private Pizza pizza;
    private int quantity;
    public OrderItem(Pizza pizza,int quantity){ this.pizza=pizza; this.quantity=quantity; }
    public Pizza getPizza(){ return pizza; }
    public int getQuantity(){ return quantity; }
    public double getSubtotal(){ return pizza.getFinalPrice()*quantity; }
}
