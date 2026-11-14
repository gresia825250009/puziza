package main.java.model;
import java.time.LocalDate;

public class Cashier extends Person {
    private String username;
    private String password;
    public Cashier(int id, String name, String phone, LocalDate registeredAt, String username, String password) {
        super(id,name,phone,registeredAt); this.username=username; this.password=password;
    }
    public String getUsername(){ return username; }
    public void setUsername(String s){ username=s; }
    public String getPassword(){ return password; }
    public void setPassword(String s){ password=s; }
    @Override public boolean validate(){ return getName()!=null && !getName().isBlank() && username!=null && !username.isBlank() && password!=null && password.length()>=6; }
    @Override public String getPersonType(){ return "Cashier"; }
}
