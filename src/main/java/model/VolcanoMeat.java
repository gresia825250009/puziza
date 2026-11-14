package main.java.model;
public class VolcanoMeat extends Pizza {
    public VolcanoMeat(int id, String size, double price){ super(id,size,price); }
    @Override public String getName(){ return "VolcanoMeat"; }
    @Override public String getTopping(){ return "Beef, sausage, spicy sauce"; }
}
