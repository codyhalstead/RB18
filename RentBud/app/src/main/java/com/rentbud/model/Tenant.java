package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cody on 1/27/2018.
 */

public class Tenant implements Parcelable{
    private String firstName;
    private String lastName;
    private String phone;

    public Tenant(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    private Tenant(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.phone = in.readString();
    }


    public static final Parcelable.Creator<Tenant> CREATOR
            = new Parcelable.Creator<Tenant>() {


        @Override
        public Tenant createFromParcel(Parcel source) {
            return new Tenant(source);

        }

        @Override
        public Tenant[] newArray(int size) {
            return new Tenant[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
