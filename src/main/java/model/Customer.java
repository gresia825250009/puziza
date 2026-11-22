package main.java.model;

import java.time.LocalDate;

/** Model profil customer. */
public class Customer extends Person {
    private String email;
    private String gender;
    private LocalDate birthDate;
    private boolean subscribed;

    public Customer(int id, String name, String phone, LocalDate registeredAt,
            String email, String gender, LocalDate birthDate, boolean subscribed) {
        super(id, name, phone, registeredAt);
        this.email = email;
        this.gender = gender;
        this.birthDate = birthDate;
        this.subscribed = subscribed;
    }

    // Constructor lama tetap tersedia agar pemanggilan lama tidak langsung rusak.
    public Customer(int id, String name, String phone, LocalDate registeredAt, String email) {
        this(id, name, phone, registeredAt, email, null, null, false);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    @Override
    public boolean validate() {
        return getName() != null && !getName().isBlank()
                && getPhone() != null && getPhone().matches("\\d{8,15}")
                && gender != null && !gender.isBlank()
                && birthDate != null;
    }

    @Override
    public String getrole() {
        return "Buyer";
    }
}
