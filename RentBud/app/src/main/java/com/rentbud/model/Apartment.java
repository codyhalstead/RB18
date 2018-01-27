package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cody on 1/27/2018.
 */

public class Apartment implements Parcelable {
    private String street1;
    private String street2;
    private String city;
    private String state;
    private int zip;

    public Apartment(String street1, String street2, String city, String state, int zip){
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    private Apartment(Parcel in) {
        this.street1 = in.readString();
        this.street2 = in.readString();
        this.city = in.readString();
        this.state = in.readString();
        this.zip = in.readInt();
    }


    public static final Parcelable.Creator<Apartment> CREATOR
            = new Parcelable.Creator<Apartment>() {


        @Override
        public Apartment createFromParcel(Parcel source) {
            return new Apartment(source);

        }

        @Override
        public Apartment[] newArray(int size) {
            return new Apartment[size];
        }
    };

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }
}
