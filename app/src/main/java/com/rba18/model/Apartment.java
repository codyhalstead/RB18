package com.rba18.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Cody on 1/27/2018.
 */

public class Apartment implements Parcelable {
    private int mID;
    private String mStreet1;
    private String mStreet2;
    private String mCity;
    private String mState;
    private String mZIP;
    private String mDescription;
    private boolean mIsRented;
    private String mNotes;
    private String mMainPic;
    private ArrayList<String> mOtherPics;
    private boolean mIsActive;

    public Apartment(int id, String street1, String street2, String city, String state, String zip,
                     String description, Boolean isRented, String notes, String mainPic, ArrayList<String> otherPics, boolean isActive) {
        mID = id;
        mStreet1 = street1;
        mStreet2 = street2;
        mCity = city;
        mState = state;
        mZIP = zip;
        mDescription = description;
        mIsRented = isRented;
        mNotes = notes;
        mMainPic = mainPic;
        mOtherPics = otherPics;
        mIsActive = isActive;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mID);
        parcel.writeString(mStreet1);
        parcel.writeString(mStreet2);
        parcel.writeString(mCity);
        parcel.writeString(mState);
        parcel.writeString(mZIP);
        parcel.writeString(mDescription);
        parcel.writeByte((byte) (mIsRented ? 1 : 0));
        parcel.writeString(mNotes);
        if (mMainPic != null) {
            parcel.writeInt(1);
            parcel.writeString(mMainPic);
        } else {
            parcel.writeInt(-1);
        }
        if (mOtherPics != null) {
            parcel.writeInt(mOtherPics.size());
            parcel.writeList(mOtherPics);
        } else {
            parcel.writeInt(-1);
        }
        parcel.writeByte((byte) (mIsActive ? 1 : 0));
    }

    private Apartment(Parcel in) {
        mID = in.readInt();
        mStreet1 = in.readString();
        mStreet2 = in.readString();
        mCity = in.readString();
        mState = in.readString();
        mZIP = in.readString();
        mDescription = in.readString();
        mIsRented = in.readByte() != 0;
        mNotes = in.readString();
        int size = in.readInt();
        if(size > -1){
            mMainPic = in.readString();
        } else {
            mMainPic = null;
        }
        int size2 = in.readInt();
        if(size2 > -1){
            mOtherPics = in.readArrayList(null);
        } else {
            mOtherPics = new ArrayList<>();
        }
        mIsActive = in.readByte() != 0;
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
        return mStreet1;
    }

    public void setStreet1(String street1) {
        mStreet1 = street1;
    }

    public String getStreet2() {
        if(mStreet2 != null) {
            return mStreet2;
        } else {
            return "";
        }
    }

    public void setStreet2(String street2) {
        mStreet2 = street2;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getZip() {
        return mZIP;
    }

    public void setZip(String zip) {
        mZIP = zip;
    }

    public int getId() {
        return mID;
    }

    public void setId(int id) {
        mID = id;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public String getMainPic() {
        return mMainPic;
    }

    public void setMainPic(String mainPic) {
        mMainPic = mainPic;
    }

    public ArrayList<String> getOtherPics() {
        return mOtherPics;
    }

    public void setOtherPics(ArrayList<String> otherPics) {
        mOtherPics = otherPics;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void addOtherPic(String otherPic){
        mOtherPics.add(otherPic);
    }

    public boolean isRented() {
        return mIsRented;
    }

    public void setRented(boolean rented) {
        mIsRented = rented;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public void setActive(boolean active) {
        mIsActive = active;
    }

    public String getCityStateZipString() {
        StringBuilder csz = new StringBuilder(mCity);
        csz.append(", ");
        csz.append(mState);
        csz.append(" ");
        csz.append(mZIP);
        return csz.toString();
    }

    public String getStreet1AndStreet2String() {
        StringBuilder s1s2 = new StringBuilder(mStreet1);
        if(mStreet2 != null) {
            s1s2.append(" ");
            s1s2.append(mStreet2);
        }
        return s1s2.toString();
    }

    public String getFullAddressString(){
        StringBuilder fa = new StringBuilder(mStreet1);
        if(mStreet2 != null){
            if(!mStreet2.equals("")) {
                fa.append("\n");
                fa.append(mStreet2);
            }
        }
        fa.append("\n");
        fa.append(getCityStateZipString());
        return fa.toString();
    }
}
