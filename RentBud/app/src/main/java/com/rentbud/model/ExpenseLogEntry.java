package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cody on 1/27/2018.
 */

public class ExpenseLogEntry implements Parcelable {
    private int id;
    private String expenseDate;
    private int amount;
    private int apartmentID;
    private String description;
    private int typeID;
    private String receiptPic;

    public ExpenseLogEntry(int id, String expenseDate, int amount, int apartmentID, String description, int typeID, String receiptPic) {
        this.id = id;
        this.expenseDate = expenseDate;
        this.amount = amount;
        this.apartmentID = apartmentID;
        this.description = description;
        this.typeID = typeID;
        this.receiptPic = receiptPic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.expenseDate);
        parcel.writeInt(this.amount);
        parcel.writeInt(this.apartmentID);
        parcel.writeString(this.description);
        parcel.writeInt(this.typeID);
        parcel.writeString(this.receiptPic);
    }

    protected ExpenseLogEntry(Parcel in) {
        this.id = in.readInt();
        this.expenseDate = in.readString();
        this.amount = in.readInt();
        this.apartmentID = in.readInt();
        this.description = in.readString();
        this.typeID = in.readInt();
        this.receiptPic = in.readString();
    }

    public static final Creator<ExpenseLogEntry> CREATOR = new Creator<ExpenseLogEntry>() {
        @Override
        public ExpenseLogEntry createFromParcel(Parcel in) {
            return new ExpenseLogEntry(in);
        }

        @Override
        public ExpenseLogEntry[] newArray(int size) {
            return new ExpenseLogEntry[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getApartmentID() {
        return apartmentID;
    }

    public void setApartmentID(int apartmentID) {
        this.apartmentID = apartmentID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getReceiptPic() {
        return receiptPic;
    }

    public void setReceiptPic(String receiptPic) {
        this.receiptPic = receiptPic;
    }
}
