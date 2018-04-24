package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Cody on 1/27/2018.
 */

public class Tenant implements Parcelable{
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String emergencyFirstName;
    private String emergencyLastName;
    private String emergencyPhone;
    //private int apartmentID;
    //private Boolean isPrimary;
    private Boolean hasLease;
    //private Date paymentDay;
    //private int rentCost;
    //private int deposit;
    private String notes;
    //private Date leaseStart;
    //private Date leaseEnd;


    public Tenant(int id, String firstName, String lastName, String phone, String email, String emergencyFirstName,
                  String emergencyLastName, String emergencyPhone, Boolean hasLease, String notes) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.emergencyFirstName = emergencyFirstName;
        this.emergencyLastName = emergencyLastName;
        this.emergencyPhone = emergencyPhone;
        //this.apartmentID = apartmentID;
        //this.isPrimary = isPrimary;
        this.hasLease = hasLease;
        //this.paymentDay = paymentDay;
        //this.rentCost = rentCost;
        //this.deposit = deposit;
        this.notes = notes;
        //this.leaseStart = leaseStart;
        //this.leaseEnd = leaseEnd;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.firstName);
        parcel.writeString(this.lastName);
        parcel.writeString(this.phone);
        parcel.writeString(this.email);
        parcel.writeString(this.emergencyFirstName);
        parcel.writeString(this.emergencyLastName);
        parcel.writeString(this.emergencyPhone);
        //parcel.writeInt(this.apartmentID);
        //parcel.writeInt(this.rentCost);
        //parcel.writeInt(this.deposit);
        //parcel.writeByte((byte) (isPrimary ? 1 : 0));
        //if(this.paymentDay != null){
        //    parcel.writeInt(1);
        //    parcel.writeLong(this.paymentDay.getTime());
        //}else{
        //    parcel.writeInt(0);
        //}
        parcel.writeString(this.notes);
        //if(this.leaseStart != null){
        //    parcel.writeInt(1);
        //    parcel.writeLong(this.leaseStart.getTime());
        //}else{
        //    parcel.writeInt(0);
        //}
        //if(this.leaseEnd != null){
        //    parcel.writeInt(1);
        //parcel.writeLong(this.leaseEnd.getTime());
        //}else{
        //    parcel.writeInt(0);
        //}
    }

    private Tenant(Parcel in) {
        this.id = in.readInt();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.phone = in.readString();
        this.email= in.readString();
        this.emergencyFirstName = in.readString();
        this.emergencyLastName = in.readString();
        this.emergencyPhone = in.readString();
        //this.apartmentID = in.readInt();
        //this.rentCost = in.readInt();
        //this.deposit = in.readInt();
        //this.isPrimary = in.readByte() != 0;
        //int paymentDateIsNotNull = in.readInt();
        //if(paymentDateIsNotNull == 1) {
        //    this.paymentDay = new Date(in.readLong());
        //}
        this.notes = in.readString();
        //int leaseStartDateIsNotNull = in.readInt();
        //if(leaseStartDateIsNotNull == 1) {
        //    this.leaseStart = new Date(in.readLong());
        //}
        //int leaseEndDateIsNotNull = in.readInt();
        //if(leaseEndDateIsNotNull == 1) {
        //    this.leaseEnd = new Date(in.readLong());
        //}
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //public int getApartmentID() {
    //    return apartmentID;
    //}

    //public void setApartmentID(int apartmentID) {
    //    this.apartmentID = apartmentID;
    //}

    //public Date getPaymentDay() {
    //    return paymentDay;
    //}

    //public void setPaymentDay(Date paymentDay) {
    //    this.paymentDay = paymentDay;
    //}

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    //public Date getLeaseStart() {
    //    return leaseStart;
    //}

    //public void setLeaseStart(Date leaseStart) {
    //    this.leaseStart = leaseStart;
    //}

    //public Date getLeaseEnd() {
    //    return leaseEnd;
    //}

    //public void setLeaseEnd(Date leaseEnd) {
    //    this.leaseEnd = leaseEnd;
    //}

    //public Boolean getIsPrimary() {
    //    return isPrimary;
    //}

    //public void setIsPrimary(Boolean primary) {
    //    isPrimary = primary;
    //}

    public String getTenantEmail() {
        return email;
    }

    public void setTenantEmail(String email) {
        this.email = email;
    }

    public String getEmergencyFirstName() {
        return emergencyFirstName;
    }

    public void setEmergencyFirstName(String emergencyFirstName) {
        this.emergencyFirstName = emergencyFirstName;
    }

    public String getEmergencyLastName() {
        return emergencyLastName;
    }

    public void setEmergencyLastName(String emergencyLastName) {
        this.emergencyLastName = emergencyLastName;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    //public int getRentCost() {
    //    return rentCost;
    //}

    //public void setRentCost(int rentCost) {
    //    this.rentCost = rentCost;
    //}

    //public int getDeposit() {
    //    return deposit;
    //}

    //public void setDeposit(int deposit) {
    //    this.deposit = deposit;
    //}


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getHasLease() {
        return hasLease;
    }

    public void setHasLease(Boolean hasLease) {
        this.hasLease = hasLease;
    }
}
