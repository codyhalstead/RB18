package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cody on 12/12/2017.
 */

public class User implements Parcelable {
    private int id;
    private String name;
    private String email;
    private String password;
    private String profilePic;

    public User() {
        this.id = -1;
        this.name = "";
        this.email = "";
        this.password = "";
        this.profilePic = "";
    }

    public User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profilePic = "";
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profilePic = "";
    }

    public User(int id, String name, String email, String password, String profilePic) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profilePic = profilePic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(profilePic);
    }

    private User(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        this.profilePic = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);

        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
