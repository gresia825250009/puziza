package main.java.model;
public class CheeseMania extends Pizza {
    public CheeseMania(int id, String size, double price){ super(id,size,price); }
    @Override public String getName(){ return "CheeseMania"; }
    @Override public String getTopping(){ return "Mozzarella and cheese"; }
}
