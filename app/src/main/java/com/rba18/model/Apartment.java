package com.rba18.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Cody on 1/27/2018.
 */

public class Apartment implements Parcelable {
    private int id;
    private String street1;
    private String street2;
    private String city;
    //private int stateID;
    private String state;
    private String zip;
    private String description;
    private boolean isRented;
    private String notes;
    private String mainPic;
    private ArrayList<String> otherPics;
    private boolean isActive;

    public Apartment(int id, String street1, String street2, String city, String state, String zip,
                     String description, Boolean isRented, String notes, String mainPic, ArrayList<String> otherPics, boolean isActive) {
        this.id = id;
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        //this.stateID = stateID;
        this.state = state;
        this.zip = zip;
        this.description = description;
        this.isRented = isRented;
        this.notes = notes;
        this.mainPic = mainPic;
        this.otherPics = otherPics;
        this.isActive = isActive;
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
        //parcel.writeInt(this.stateID);
        parcel.writeString(this.state);
        parcel.writeString(this.zip);
        parcel.writeString(this.description);
        parcel.writeByte((byte) (isRented? 1 : 0));
        parcel.writeString(this.notes);
        if (this.mainPic != null) {
            parcel.writeInt(1);
            parcel.writeString(this.mainPic);
        } else {
            parcel.writeInt(-1);
        }
        if (this.otherPics != null) {
            parcel.writeInt(this.otherPics.size());
            parcel.writeList(this.otherPics);
        } else {
            parcel.writeInt(-1);
        }
        parcel.writeByte((byte) (isActive? 1 : 0));
    }

    private Apartment(Parcel in) {
        this.id = in.readInt();
        this.street1 = in.readString();
        this.street2 = in.readString();
        this.city = in.readString();
        //this.stateID = in.readInt();
        this.state = in.readString();
        this.zip = in.readString();
        this.description = in.readString();
        this.isRented = in.readByte() != 0;
        this.notes = in.readString();
        int size = in.readInt();
        if(size > -1){
            //this.mainPic = new byte[size];
            //in.readByteArray(this.mainPic);
            this.mainPic = in.readString();
        } else {
            this.mainPic = null;
        }
        int size2 = in.readInt();
        if(size2 > -1){
            this.otherPics = in.readArrayList(null);
        } else {
            this.otherPics = new ArrayList<>();
        }
        this.isActive = in.readByte() != 0;
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
        if(street2 != null) {
            return street2;
        } else {
            return "";
        }
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

    //public int getStateID() {
    //    return stateID;
    //}

    //public void setStateID(int stateID) {
    //    this.stateID = stateID;
    //}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addOtherPic(String otherPic){
        this.otherPics.add(otherPic);
    }

    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean rented) {
        isRented = rented;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCityStateZipString() {
        StringBuilder csz = new StringBuilder(city);
        csz.append(", ");
        csz.append(state);
        csz.append(" ");
        csz.append(zip);
        return csz.toString();
    }

    public String getStreet1AndStreet2String() {
        StringBuilder s1s2 = new StringBuilder(street1);
        if(street2 != null) {
            s1s2.append(" ");
            s1s2.append(street2);
        }
        return s1s2.toString();
    }

    public String getFullAddressString(){
        StringBuilder fa = new StringBuilder(street1);
        if(street2 != null){
            if(!street2.equals("")) {
                fa.append("\n");
                fa.append(street2);
            }
        }
        fa.append("\n");
        fa.append(getCityStateZipString());
        return fa.toString();
    }
}
