package com.example.authenticationfatma;

public class User {
    String fullname , email,password;


    public User(String fullname, String email) {
        this.fullname = fullname;
        this.email = email;
    }

    public User() {
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
