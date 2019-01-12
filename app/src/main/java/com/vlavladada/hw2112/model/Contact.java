package com.vlavladada.hw2112.model;

public class Contact {
    private String name, lastName, email, phone, address, description;
    private int id;

    public Contact() {
    }

    public Contact(String name, String lastName, String email, String phone, String address, String description, int id) {
        this.name = name;
        this.lastName=lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.description = description;
        this.id=id;

    }

    @Override
    public String toString() {
        return name + "," + lastName+","+email + "," + phone + "," + address + "," + description+","+id;
    }

    public static Contact newInstance(String data){
        String[] arr = data.split(",");
        return new Contact(arr[0],arr[1],arr[2],arr[3], arr[4], arr[5], Integer.parseInt(arr[6]));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
