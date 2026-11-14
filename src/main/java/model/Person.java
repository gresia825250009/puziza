package main.java.model;
import java.time.LocalDate;

/** Abstract parent untuk Customer dan Cashier. */
public abstract class Person {
    private int id;
    private String name;
    private String phone;
    private LocalDate registeredAt;

    protected Person(int id, String name, String phone, LocalDate registeredAt) {
        this.id=id; this.name=name; this.phone=phone; this.registeredAt=registeredAt;
    }
    public int getId(){ return id; }
    public void setId(int id){ this.id=id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name=name; }
    public String getPhone(){ return phone; }
    public void setPhone(String phone){ this.phone=phone; }
    public LocalDate getRegisteredAt(){ return registeredAt; }
    public void setRegisteredAt(LocalDate d){ this.registeredAt=d; }
    public abstract boolean validate();
    public abstract String getPersonType();
    @Override public String toString(){ return name + " (" + getPersonType() + ")"; }
}
