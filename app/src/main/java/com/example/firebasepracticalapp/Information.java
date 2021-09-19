package com.example.firebasepracticalapp;

public class Information {
    String Email,name;

    public Information() {
    }

    public Information(String Email, String name) {
        this.Email = Email;
        this.name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }
}
