package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Cody on 1/27/2018.
 */

public class Apartment implements Parcelable {
    private int id;
    private String street1;
    private String street2;
    private String city;
    private int stateID;
    private String state;
    private String zip;
    private String notes;
    private String mainPic;
    private ArrayList<String> otherPics;

    public Apartment(int id, String street1, String street2, String city, int stateID,
                     String state, String zip, String notes, String mainPic, ArrayList<String> otherPics){
        this.id = id;
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.stateID = stateID;
        this.state = state;
        this.zip = zip;
        this.notes = notes;
        this.mainPic = mainPic;
        //this.otherPics = otherPics;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.street1);
        parcel.writeString(this.street2);
        parcel.writeString(this.city);
        parcel.writeInt(this.stateID);
        parcel.writeString(this.state);
        parcel.writeString(this.zip);
        parcel.writeString(this.notes);
        parcel.writeString(this.mainPic);
       // parcel.writeStringList(this.otherPics);
    }

    private Apartment(Parcel in) {
        this.id = in.readInt();
        this.street1 = in.readString();
        this.street2 = in.readString();
        this.city = in.readString();
        this.stateID = in.readInt();
        this.state = in.readString();
        this.zip = in.readString();
        this.notes = in.readString();
        this.mainPic = in.readString();
      //  in.readStringList(this.otherPics);
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStateID() {
        return stateID;
    }

    public void setStateID(int stateID) {
        this.stateID = stateID;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMainPic() {
        return mainPic;
    }

    public void setMainPic(String mainPic) {
        this.mainPic = mainPic;
    }

    public ArrayList<String> getOtherPics() {
        return otherPics;
    }

    public void setOtherPics(ArrayList<String> otherPics) {
        this.otherPics = otherPics;
    }
}
