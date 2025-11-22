package com.example.railwaymanagementsystem.models;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * User Model - Represents a user (passenger or admin) in the system
 */
public class User {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty phone;
    private final StringProperty role; // "passenger" or "admin"
    private final StringProperty password; // In production, this would be hashed
    private final StringProperty cnic;
    private final ObjectProperty<LocalDate> dateOfBirth;
    private final StringProperty gender;
    private final StringProperty address;
    private final StringProperty city;
    private final StringProperty postalCode;

    public User() {
        this("", "", "", "", "passenger", "");
    }

    public User(String id, String name, String email, String phone,
                String role, String password) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.role = new SimpleStringProperty(role);
        this.password = new SimpleStringProperty(password);
        this.cnic = new SimpleStringProperty("");
        this.dateOfBirth = new SimpleObjectProperty<>(null);
        this.gender = new SimpleStringProperty("Unspecified");
        this.address = new SimpleStringProperty("");
        this.city = new SimpleStringProperty("");
        this.postalCode = new SimpleStringProperty("");
    }

    // ID
    public String getId() { return id.get(); }
    public void setId(String value) { id.set(value); }
    public StringProperty idProperty() { return id; }

    // Name
    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    // Email
    public String getEmail() { return email.get(); }
    public void setEmail(String value) { email.set(value); }
    public StringProperty emailProperty() { return email; }

    // Phone
    public String getPhone() { return phone.get(); }
    public void setPhone(String value) { phone.set(value); }
    public StringProperty phoneProperty() { return phone; }

    // Role
    public String getRole() { return role.get(); }
    public void setRole(String value) { role.set(value); }
    public StringProperty roleProperty() { return role; }

    // Password
    public String getPassword() { return password.get(); }
    public void setPassword(String value) { password.set(value); }
    public StringProperty passwordProperty() { return password; }

    // CNIC
    public String getCnic() { return cnic.get(); }
    public void setCnic(String value) { cnic.set(value); }
    public StringProperty cnicProperty() { return cnic; }

    // Date of Birth
    public LocalDate getDateOfBirth() { return dateOfBirth.get(); }
    public void setDateOfBirth(LocalDate value) { dateOfBirth.set(value); }
    public ObjectProperty<LocalDate> dateOfBirthProperty() { return dateOfBirth; }

    // Gender
    public String getGender() { return gender.get(); }
    public void setGender(String value) { gender.set(value); }
    public StringProperty genderProperty() { return gender; }

    // Address
    public String getAddress() { return address.get(); }
    public void setAddress(String value) { address.set(value); }
    public StringProperty addressProperty() { return address; }

    // City
    public String getCity() { return city.get(); }
    public void setCity(String value) { city.set(value); }
    public StringProperty cityProperty() { return city; }

    // Postal Code
    public String getPostalCode() { return postalCode.get(); }
    public void setPostalCode(String value) { postalCode.set(value); }
    public StringProperty postalCodeProperty() { return postalCode; }
}
