package com.rba18.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

public class TypeTotal implements Parcelable {
    private int mTypeID;
    private String mTypeLabel;
    private BigDecimal mTotalAmount;
    private int mNumberOfItems;

    public TypeTotal(int typeID, String typeLabel, BigDecimal totalAmount, int numberOfItems){
        mTypeID = typeID;
        mTypeLabel = typeLabel;
        mTotalAmount = totalAmount;
        mNumberOfItems = numberOfItems;
    }

    public TypeTotal(int typeID, String typeLabel){
        mTypeID = typeID;
        mTypeLabel = typeLabel;
        mTotalAmount = new BigDecimal(0);
        mNumberOfItems = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mTypeID);
        parcel.writeString(mTypeLabel);
        String amountString = mTotalAmount.toPlainString();
        parcel.writeString(amountString);
        parcel.writeInt(mNumberOfItems);
    }

    private TypeTotal(Parcel in) {
        mTypeID = in.readInt();
        mTypeLabel = in.readString();
        String amountString = in.readString();
        mTotalAmount = new BigDecimal(amountString);
        mNumberOfItems = in.readInt();
    }


    public static final Parcelable.Creator<TypeTotal> CREATOR
            = new Parcelable.Creator<TypeTotal>() {


        @Override
        public TypeTotal createFromParcel(Parcel source) {
            return new TypeTotal(source);

        }

        @Override
        public TypeTotal[] newArray(int size) {
            return new TypeTotal[size];
        }
    };

    public int getTypeID() {
        return mTypeID;
    }

    public void setTypeID(int typeID) {
        mTypeID = typeID;
    }

    public String getTypeLabel() {
        return mTypeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        mTypeLabel = typeLabel;
    }

    public BigDecimal getTotalAmount() {
        return mTotalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        mTotalAmount = totalAmount;
    }

    public int getNumberOfItems() {
        return mNumberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        mNumberOfItems = numberOfItems;
    }
}
