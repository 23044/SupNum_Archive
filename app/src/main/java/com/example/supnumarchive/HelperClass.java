package com.example.supnumarchive;

public class HelperClass {
String username , email, password;
    String profileImageUrl;
    String language;
    String location;
    String subscriptionType;

    public HelperClass() {
    }


    public HelperClass(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.language="francais";
        this.location="Nouackchoot";
        this.profileImageUrl="C:\\Users\\HP\\Downloads\\téléchargement.png";
        this.subscriptionType="USER";
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
}
