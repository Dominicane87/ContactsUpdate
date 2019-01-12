package com.vlavladada.hw2112.model;

import java.util.ArrayList;

public class ContactListDto {
    private ArrayList<Contact> contacts;

    public ContactListDto(){

    }

    public ContactListDto(ArrayList<Contact> contacts){
        this.contacts=contacts;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }
}
