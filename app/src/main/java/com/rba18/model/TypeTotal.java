package com.rba18.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

public class TypeTotal implements Parcelable {
    private int typeID;
    private String typeLabel;
    private BigDecimal totalAmount;
    private int numberOfItems;

    public TypeTotal(int typeID, String typeLabel, BigDecimal totalAmount, int numberOfItems){
        this.typeID = typeID;
        this.typeLabel = typeLabel;
        this.totalAmount = totalAmount;
        this.numberOfItems = numberOfItems;
    }

    public TypeTotal(int typeID, String typeLabel){
        this.typeID = typeID;
        this.typeLabel = typeLabel;
        this.totalAmount = new BigDecimal(0);
        this.numberOfItems = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.typeID);
        parcel.writeString(this.typeLabel);
        String amountString = totalAmount.toPlainString();
        parcel.writeString(amountString);
        parcel.writeInt(numberOfItems);
    }

    private TypeTotal(Parcel in) {
        this.typeID = in.readInt();
        this.typeLabel = in.readString();
        String amountString = in.readString();
        this.totalAmount = new BigDecimal(amountString);
        this.numberOfItems = in.readInt();
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
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getTypeLabel() {
        return typeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }
}
