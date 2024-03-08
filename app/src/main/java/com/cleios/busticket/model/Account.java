package com.cleios.busticket.model;

import android.net.Uri;

public class Account {

    private String uid;
    private String name;
    private String email;
    private Uri profilePhotoUri;
    private String userType;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getProfilePhotoUri() {
        return profilePhotoUri;
    }

    public void setProfilePhotoUri(Uri profilePhotoUri) {
        this.profilePhotoUri = profilePhotoUri;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

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
}
