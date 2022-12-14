package com.conestoga.weatherfits;

public class UserDetails {
    public String name,gender,email;
    public  int id;

    public UserDetails(String name, String email, String gender) {
        this.name=name;
        this.email=email;
        this.gender=gender;

    }

    public UserDetails(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
