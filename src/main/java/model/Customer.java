package main.java.model;
import java.time.LocalDate;

public class Customer extends Person {
    private String email;
    public Customer(int id, String name, String phone, LocalDate registeredAt, String email) {
        super(id,name,phone,registeredAt); this.email=email;
    }
    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email=email; }
    @Override public boolean validate(){ return getName()!=null && !getName().isBlank() && getPhone()!=null && getPhone().matches("\\d{8,15}"); }
    @Override public String getPersonType(){ return "Customer"; }
}
