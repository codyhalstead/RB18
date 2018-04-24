package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Cody on 1/27/2018.
 */

public class ExpenseLogEntry implements Parcelable {
    private int id;
    private Date expenseDate;
    private BigDecimal amount;
    private int apartmentID;
    private String description;
    private int typeID;
    private String typeLabel;
    private String receiptPic;

    public ExpenseLogEntry(int id, Date expenseDate, BigDecimal amount, int apartmentID, String description, int typeID, String typeLabel,
            String receiptPic) {
        this.id = id;
        this.expenseDate = expenseDate;
        this.amount = amount;
        this.apartmentID = apartmentID;
        this.description = description;
        this.typeID = typeID;
        this.typeLabel = typeLabel;
        this.receiptPic = receiptPic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        if(this.expenseDate != null){
            parcel.writeInt(1);
            parcel.writeLong(this.expenseDate.getTime());
        }else{
            parcel.writeInt(0);
        }
        String amountString = amount.toPlainString();
        parcel.writeString(amountString);
        parcel.writeInt(this.apartmentID);
        parcel.writeString(this.description);
        parcel.writeInt(this.typeID);
        parcel.writeString(this.typeLabel);
        if (this.receiptPic != null) {
            parcel.writeInt(1);
            parcel.writeString(this.receiptPic);
        } else {
            parcel.writeInt(-1);
        }
    }

    protected ExpenseLogEntry(Parcel in) {
        this.id = in.readInt();
        int expenseDateIsNotNull = in.readInt();
        if(expenseDateIsNotNull == 1) {
            this.expenseDate = new Date(in.readLong());
        }
        String amountString = in.readString();
        this.amount = new BigDecimal(amountString);
        this.apartmentID = in.readInt();
        this.description = in.readString();
        this.typeID = in.readInt();
        this.typeLabel = in.readString();
        int size = in.readInt();
        if(size > -1){
            //this.mainPic = new byte[size];
            //in.readByteArray(this.mainPic);
            this.receiptPic = in.readString();
        } else {
            this.receiptPic = null;
        }
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

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
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

    public String getTypeLabel() {
        return typeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
    }

}
