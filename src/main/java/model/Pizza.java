package main.java.model;
/** Abstract parent untuk seluruh varian pizza. */
public abstract class Pizza {
    private int id;
    private String size;
    private double basePrice;
    protected Pizza(int id, String size, double basePrice){ this.id=id; this.size=size; this.basePrice=basePrice; }
    public int getId(){ return id; }
    public void setId(int id){ this.id=id; }
    public String getSize(){ return size; }
    public void setSize(String size){ this.size=size; }
    public double getBasePrice(){ return basePrice; }
    public void setBasePrice(double p){ basePrice=p; }
    public abstract String getName();
    public abstract String getTopping();
    public double getDiscountRate(){ return "Medium".equalsIgnoreCase(size) ? 0.45 : 0.0; }
    public double getFinalPrice(){ return basePrice * (1-getDiscountRate()); }
    @Override public String toString(){ return getName()+" - "+size+" - Rp"+String.format("%,.0f",getFinalPrice()); }
}
