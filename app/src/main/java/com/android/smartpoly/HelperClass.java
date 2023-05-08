package com.android.smartpoly;

public class HelperClass {
    String name;
    String email;
    String username;
    String password;
    Boolean isAdmin;

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HelperClass(String name, String email, String username, String password, String uid, Boolean isAdmin) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.uid=uid;
        this.isAdmin=isAdmin;
    }

    public HelperClass() {

    }
}
