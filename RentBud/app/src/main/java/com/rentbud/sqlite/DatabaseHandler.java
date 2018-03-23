package com.rentbud.sqlite;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.rentbud.helpers.RandomNumberGenerator;
import com.rentbud.model.Apartment;
import com.rentbud.model.EventLogEntry;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.model.Tenant;
import com.rentbud.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static android.content.ContentValues.TAG;

/**
 * Created by Cody on 12/12/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    public static int DATABASE_VERSION = 1;
    public static String DB_FILE_NAME = "allData.db";

    public static final String USER_INFO_TABLE = "user_info_table";
    public static final String USER_INFO_ID_COLUMN_PK = "_id";
    public static final String USER_INFO_NAME_COLUMN = "user_info_name";
    public static final String USER_INFO_EMAIL_COLUMN = "user_info_email";
    public static final String USER_INFO_PASSWORD_COLUMN = "user_info_password";
    public static final String USER_INFO_IS_VERIFIED_COLUMN = "user_info_is_verified";
    public static final String USER_INFO_PROFILE_PIC = "user_info_profile_pic";
    public static final String USER_INFO_DATE_CREATED_COLUMN = "user_info_date_created";
    public static final String USER_INFO_LAST_UPDATE_COLUMN = "user_info_last_update";
    public static final String USER_INFO_IS_ACTIVE_COLUMN = "user_info_is_active";

    public static final String TENANT_INFO_TABLE = "tenant_info_table";
    public static final String TENANT_INFO_ID_COLUMN_PK = "_id";
    public static final String TENANT_INFO_USER_ID_COLUMN_FK = "tenant_info_user_id";
    public static final String TENANT_INFO_FIRST_NAME_COLUMN = "tenant_info_first_name";
    public static final String TENANT_INFO_LAST_NAME_COLUMN = "tenant_info_last_name";
    public static final String TENANT_INFO_PHONE_COLUMN = "tenant_info_phone";
    public static final String TENANT_INFO_EMAIL_COLUMN = "tenant_info_email";
    public static final String TENANT_INFO_EMERGENCY_FIRST_NAME = "tenant_info_emergency_first_name";
    public static final String TENANT_INFO_EMERGENCY_LAST_NAME = "tenant_info_emergency_last_name";
    public static final String TENANT_INFO_EMERGENCY_PHONE = "tenant_info_emergency_phone";
    public static final String TENANT_INFO_APARTMENT_ID_COLUMN_FK = "tenant_info_apartment_id";
    public static final String TENANT_INFO_RENT_COST = "tenant_info_rent_cost";
    public static final String TENANT_INFO_DEPOSIT = "tenant_info_deposit";
    public static final String TENANT_INFO_IS_PRIMARY_TENANT_COLUMN = "tenant_info_is_primary";
    public static final String TENANT_INFO_PAYMENT_DAY_COLUMN = "tenant_info_payment_day";

    public static final String TENANT_INFO_NOTES_COLUMN = "tenant_info_notes";
    public static final String TENANT_INFO_LEASE_START_COLUMN = "tenant_info_lease_start";
    public static final String TENANT_INFO_LEASE_END_COLUMN = "tenant_info_lease_end";
    public static final String TENANT_INFO_DATE_CREATED_COLUMN = "tenant_info_date_created";
    public static final String TENANT_INFO_LAST_UPDATE_COLUMN = "tenant_info_last_update";
    public static final String TENANT_INFO_IS_ACTIVE_COLUMN = "tenant_info_is_active";

    public static final String APARTMENT_INFO_TABLE = "apartment_info_table";
    public static final String APARTMENT_INFO_ID_COLUMN_PK = "_id";
    public static final String APARTMENT_INFO_USER_ID_COLUMN_FK = "apartment_info_user_id";
    public static final String APARTMENT_INFO_STREET1_COLUMN = "apartment_info_street1";
    public static final String APARTMENT_INFO_STREET2_COLUMN = "apartment_info_street2";
    public static final String APARTMENT_INFO_CITY_COLUMN = "apartment_info_city";
    public static final String APARTMENT_INFO_STATE_COLUMN_FK = "apartment_info_state";
    public static final String APARTMENT_INFO_ZIP_COLUMN = "apartment_info_zip";
    public static final String APARTMENT_INFO_DESCRIPTION_COLUMN = "apartment_info_description";
    public static final String APARTMENT_INFO_NOTES_COLUMN = "apartment_info_notes";
    public static final String APARTMENT_INFO_MAIN_PIC_COLUMN = "apartment_info_main_pic";
    public static final String APARTMENT_INFO_DATE_CREATED_COLUMN = "apartment_info_date_created";
    public static final String APARTMENT_INFO_LAST_UPDATE_COLUMN = "apartment_info_last_update";
    public static final String APARTMENT_INFO_IS_ACTIVE_COLUMN = "apartment_info_is_active";

    public static final String PAYMENT_LOG_TABLE = "payment_log_table";
    public static final String PAYMENT_LOG_ID_COLUMN_PK = "_id";
    public static final String PAYMENT_LOG_USER_ID_COLUMN_FK = "payment_log_user_id";
    public static final String PAYMENT_LOG_PAYMENT_DATE_COLUMN = "payment_log_payment_date";
    public static final String PAYMENT_LOG_TYPE_ID_COLUMN_FK = "payment_log_type_id";
    public static final String PAYMENT_LOG_TENANT_ID_COLUMN_FK = "payment_log_tenant_id";
    public static final String PAYMENT_LOG_AMOUNT_COLUMN = "payment_log_amount";
    public static final String PAYMENT_LOG_DATE_CREATED_COLUMN = "payment_log_date_created";
    public static final String PAYMENT_LOG_LAST_UPDATE_COLUMN = "payment_log_last_update";
    public static final String PAYMENT_LOG_IS_ACTIVE_COLUMN = "payment_log_is_active";

    public static final String EXPENSE_LOG_TABLE = "expense_log_table";
    public static final String EXPENSE_LOG_ID_COLUMN_PK = "_id";
    public static final String EXPENSE_LOG_USER_ID_COLUMN_FK = "expense_log_user_id";
    public static final String EXPENSE_LOG_EXPENSE_DATE_COLUMN = "expense_log_expense_date";
    public static final String EXPENSE_LOG_AMOUNT_COLUMN = "expense_log_amount";
    public static final String EXPENSE_LOG_APARTMENT_ID_COLUMN_FK = "expense_log_apartment_id";
    public static final String EXPENSE_LOG_DESCRIPTION_COLUMN = "expense_log_description";
    public static final String EXPENSE_LOG_TYPE_ID_COLUMN_FK = "expense_log_type_id";
    public static final String EXPENSE_LOG_RECIPT_PIC = "expense_log_recipt_pic";
    public static final String EXPENSE_LOG_DATE_CREATED_COLUMN = "expense_log_date_created";
    public static final String EXPENSE_LOG_LAST_UPDATE_COLUMN = "expense_log_last_update";
    public static final String EXPENSE_LOG_IS_ACTIVE_COLUMN = "expense_log_is_active";

    public static final String EVENT_LOG_TABLE = "event_log_table";
    public static final String EVENT_LOG_ID_COLUMN_PK = "_id";
    public static final String EVENT_LOG_USER_ID_COLUMN_FK = "event_log_user_id";
    public static final String EVENT_LOG_EVENT_TIME_COLUMN = "event_log_event_time";
    public static final String EVENT_LOG_TYPE_ID_COLUMN_FK = "event_log_type_id";
    public static final String EVENT_LOG_DESCRIPTION_COLUMN = "event_log_description";
    public static final String EVENT_LOG_APARTMENT_ID_COLUMN_FK = "event_log_apartment_id";
    public static final String EVENT_LOG_TENANT_ID_COLUMN_FK = "event_log_tenant_id";
    public static final String EVENT_LOG_DATE_CREATED_COLUMN = "event_log_date_created";
    public static final String EVENT_LOG_LAST_UPDATE_COLUMN = "event_log_last_update";
    public static final String EVENT_LOG_IS_ACTIVE_COLUMN = "event_log_is_active";

    public static final String TYPES_TABLE = "types_table";
    public static final String TYPES_ID_COLUMN_PK = "_id";
    public static final String TYPES_TYPE_COLUMN = "types_type";
    public static final String TYPES_DATE_CREATED_COLUMN = "types_date_created";
    public static final String TYPES_LAST_UPDATE_COLUMN = "types_last_update";
    public static final String TYPES_IS_ACTIVE_COLUMN = "types_is_active";

    public static final String TYPE_LOOKUP_TABLE = "type_lookup_table";
    public static final String TYPE_LOOKUP_ID_COLUMN_PK = "_id";
    public static final String TYPE_LOOKUP_TYPE_ID_COLUMN_FK = "type_lookup_type_id";
    public static final String TYPE_LOOKUP_LABEL_COLUMN = "type_lookup_label";
    public static final String TYPE_LOOKUP_DATE_CREATED_COLUMN = "type_lookup_date_created";
    public static final String TYPE_LOOKUP_LAST_UPDATE_COLUMN = "type_lookup_last_update";
    public static final String TYPE_LOOKUP_IS_ACTIVE_COLUMN = "type_lookup_is_active";

    public static final String STATE_TABLE = "state_table";
    public static final String STATE_ID_COLUMN_PK = "_id";
    public static final String STATE_STATE_ABR_COLUMN = "state_state_abr";
    public static final String STATE_DATE_CREATED_COLUMN = "state_date_created";
    public static final String STATE_LAST_UPDATE_COLUMN = "state_last_update";
    public static final String STATE_IS_ACTIVE_COLUMN = "state_is_active";

    public static final String APARTMENT_PICS_TABLE = "apartment_pics_table";
    public static final String APARTMENT_PICS_ID_COLUMN_PK = "_id";
    public static final String APARTMENT_PICS_USER_ID_COLUMN_FK = "apartment_pics_user_id";
    public static final String APARTMENT_PICS_APARTMENT_ID_COLUMN_FK = "apartment_pics_apartment_id";
    public static final String APARTMENT_PICS_PIC_COLUMN = "apartment_pics_pic";
    public static final String APARTMENT_PICS_DATE_CREATED_COLUMN = "apartment_pics_date_created";
    public static final String APARTMENT_PICS_LAST_UPDATED_COLUMN = "apartment_pics_last_update";
    public static final String APARTMENT_PICS_IS_ACTIVE_COLUMN = "appartment_pics_is_active";

    public static final String APARTMENTS_VIEW = "apartments_view";
    public static final String APARTMENTS_VIEW_USER_ID = "user_id";
    public static final String APARTMENTS_VIEW_APARTMENT_ID = "apartment_id";
    public static final String APARTMENTS_VIEW_STREET_1 = "street_1";
    public static final String APARTMENTS_VIEW_STREET_2 = "street_2";
    public static final String APARTMENTS_VIEW_CITY = "city";
    public static final String APARTMENTS_VIEW_STATE_ID = "state_id";
    public static final String APARTMENTS_VIEW_STATE = "state";
    public static final String APARTMENTS_VIEW_ZIP = "ZIP";
    public static final String APARTMENTS_VIEW_DESCRIPTION = "description";
    public static final String APARTMENTS_VIEW_NOTES = "notes";
    public static final String APARTMENTS_VIEW_MAIN_PIC = "main_pic";
    public static final String APARTMENTS_VIEW_IS_RENTED = "is_apartment_rented";
    public static final String APARTMENTS_VIEW_DATE_CREATED = "date_created";
    public static final String APARTMENTS_VIEW_LAST_UPDATE = "last_update";
    public static final String APARTMENTS_VIEW_IS_ACTIVE = "is_active";

    public static final String TENANTS_VIEW = "tenants_view";
    public static final String TENANTS_VIEW_USER_ID = "user_id";
    public static final String TENANTS_VIEW_TENANT_ID = "tenant_id";
    public static final String TENANTS_VIEW_FIRST_NAME = "first_name";
    public static final String TENANTS_VIEW_LAST_NAME = "last_name";
    public static final String TENANTS_VIEW_PHONE = "phone";
    public static final String TENANTS_VIEW_RENTED_APARTMENT_ID = "apartment_currently_renting";
    public static final String TENANTS_VIEW_PAYMENT_DAY_ = "payment_day";
    public static final String TENANTS_VIEW_NOTES = "notes";
    public static final String TENANTS_VIEW_LEASE_START = "lease_start";
    public static final String TENANTS_VIEW_LEASE_END = "lease_end";
    public static final String TENANTS_VIEW_DATE_CREATED = "date_created";
    public static final String TENANTS_VIEW_LAST_UPDATE = "last_update";
    public static final String TENANTS_VIEW_IS_ACTIVE = "is_active";

    RandomNumberGenerator verificationGenerator;

    public DatabaseHandler(Context context) {
        super(context, DB_FILE_NAME, null, DATABASE_VERSION);
        verificationGenerator = new RandomNumberGenerator();
    }

    //Add new user
    public void addUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_INFO_NAME_COLUMN, name);
        contentValues.put(USER_INFO_EMAIL_COLUMN, email);
        contentValues.put(USER_INFO_PASSWORD_COLUMN, password);
        //contentValues.put(USER_INFO_VERIFICATION_NUMBER_COLUMN, verificationGenerator.gererateVerificationNumber(5));
        db.insert(USER_INFO_TABLE, null, contentValues);
        db.close();
    }

    //Get user (Can be used to complete partial user object)
    public User getUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + USER_INFO_TABLE + " where " + USER_INFO_EMAIL_COLUMN + " like '%" + email + "%' AND "
                + USER_INFO_PASSWORD_COLUMN + " like '%" + password + "%' AND " + USER_INFO_IS_ACTIVE_COLUMN + " = 1 LIMIT 1";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(USER_INFO_ID_COLUMN_PK));
            String name = cursor.getString(cursor.getColumnIndex(USER_INFO_NAME_COLUMN));
            String profilePic = cursor.getString(cursor.getColumnIndex(USER_INFO_PROFILE_PIC));
            User user = new User(id, name, email, password, profilePic);
            cursor.close();
            db.close();
            return user;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    //Used to change user name, email, and/or password
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_INFO_NAME_COLUMN, user.getName());
        values.put(USER_INFO_EMAIL_COLUMN, user.getEmail());
        values.put(USER_INFO_PASSWORD_COLUMN, user.getPassword());

        // updating row
        db.update(USER_INFO_TABLE, values, USER_INFO_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(user.getId())});
        db.close();
    }

    //Delete user
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(USER_INFO_TABLE, USER_INFO_ID_COLUMN_PK + " = ? ",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    //check if user exists (By Email)
    public boolean checkUser(String email) {
        //Array of columns to fetch
        String[] columns = {USER_INFO_ID_COLUMN_PK};
        SQLiteDatabase db = this.getReadableDatabase();
        //Selection criteria
        String selection = USER_INFO_EMAIL_COLUMN + " = ?";
        //Selection argument
        String[] selectionArgs = {email};
        //Query user table with condition
        Cursor cursor = db.query(USER_INFO_TABLE, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return (cursorCount > 0);
    }

    //check if user exists (By Email and password)
    public boolean checkUser(String email, String password) {
        //Array of columns to fetch
        String[] columns = {USER_INFO_ID_COLUMN_PK};
        SQLiteDatabase db = this.getReadableDatabase();
        //Selection criteria
        String selection = USER_INFO_EMAIL_COLUMN + " = ?" + " AND " + USER_INFO_PASSWORD_COLUMN + " = ?";
        //Selection arguments
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(USER_INFO_TABLE, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return (cursorCount > 0);
    }

    //Get user name
    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + USER_INFO_TABLE + " where " + USER_INFO_EMAIL_COLUMN + " like '%" + email + "%' LIMIT 1";
        Cursor cursor = db.rawQuery(Query, null);
        String answer = "";
        if (cursor.moveToFirst()) {
            answer = cursor.getString(cursor.getColumnIndex(USER_INFO_NAME_COLUMN));
            cursor.close();
            db.close();
            return answer;
        } else {
            cursor.close();
            db.close();
            return answer;
        }
    }

    //Get user ID, returns -1 if user doesn't exist
    public int getUserID(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + USER_INFO_TABLE + " where " + USER_INFO_EMAIL_COLUMN + " like '%" + email + "%' LIMIT 1";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(USER_INFO_ID_COLUMN_PK));
            cursor.close();
            db.close();
            return id;
        } else {
            cursor.close();
            db.close();
            return -1;
        }
    }

    //Get state tree map, sorted alphabetically. State = key, ID = value
    public TreeMap<String, Integer> getStateTreemap() {
        TreeMap<String, Integer> stateMap = new TreeMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + STATE_TABLE + " WHERE " + STATE_IS_ACTIVE_COLUMN + " = 1";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(STATE_ID_COLUMN_PK));
                String abr = cursor.getString(cursor.getColumnIndex(STATE_STATE_ABR_COLUMN));
                stateMap.put(abr, id);
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return stateMap;
        } else {
            cursor.close();
            db.close();
            return stateMap;
        }
    }

    //Add tenant
    public void addNewTenant(Tenant tenant, int userID) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        values.put(TENANT_INFO_USER_ID_COLUMN_FK, userID);
        values.put(TENANT_INFO_FIRST_NAME_COLUMN, tenant.getFirstName());
        values.put(TENANT_INFO_LAST_NAME_COLUMN, tenant.getLastName());
        values.put(TENANT_INFO_PHONE_COLUMN, tenant.getPhone());
        values.put(TENANT_INFO_EMAIL_COLUMN, tenant.getTenantEmail());
        values.put(TENANT_INFO_EMERGENCY_FIRST_NAME, tenant.getEmergencyFirstName());
        values.put(TENANT_INFO_EMERGENCY_LAST_NAME, tenant.getEmergencyLastName());
        values.put(TENANT_INFO_EMERGENCY_PHONE, tenant.getEmergencyPhone());
        values.putNull(TENANT_INFO_APARTMENT_ID_COLUMN_FK);
        values.put(TENANT_INFO_RENT_COST, 0);
        values.put(TENANT_INFO_DEPOSIT, 0);
        values.put(TENANT_INFO_PAYMENT_DAY_COLUMN, tenant.getPaymentDay());
        values.put(TENANT_INFO_NOTES_COLUMN, tenant.getNotes());
        values.put(TENANT_INFO_LEASE_START_COLUMN, tenant.getLeaseStart());
        values.put(TENANT_INFO_LEASE_END_COLUMN, tenant.getLeaseEnd());
        db.insert(TENANT_INFO_TABLE, null, values);
        db.close();
    }

    //Update tenant
    public void editTenant(Tenant tenant) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TENANT_INFO_FIRST_NAME_COLUMN, tenant.getFirstName());
        values.put(TENANT_INFO_LAST_NAME_COLUMN, tenant.getLastName());
        values.put(TENANT_INFO_PHONE_COLUMN, tenant.getPhone());
        values.put(TENANT_INFO_EMAIL_COLUMN, tenant.getTenantEmail());
        values.put(TENANT_INFO_EMERGENCY_FIRST_NAME, tenant.getEmergencyFirstName());
        values.put(TENANT_INFO_EMERGENCY_LAST_NAME, tenant.getEmergencyLastName());
        values.put(TENANT_INFO_EMERGENCY_PHONE, tenant.getEmergencyPhone());
        values.put(TENANT_INFO_NOTES_COLUMN, tenant.getNotes());
        if (tenant.getApartmentID() != 0) {
            values.put(TENANT_INFO_APARTMENT_ID_COLUMN_FK, tenant.getApartmentID());
        } else {
            values.putNull(TENANT_INFO_APARTMENT_ID_COLUMN_FK);
        }
        values.put(TENANT_INFO_RENT_COST, tenant.getRentCost());
        values.put(TENANT_INFO_DEPOSIT, tenant.getDeposit());
        values.put(TENANT_INFO_IS_PRIMARY_TENANT_COLUMN, tenant.getIsPrimary());
        values.put(TENANT_INFO_PAYMENT_DAY_COLUMN, tenant.getPaymentDay());
        values.put(TENANT_INFO_LEASE_START_COLUMN, tenant.getLeaseStart());
        values.put(TENANT_INFO_LEASE_END_COLUMN, tenant.getLeaseEnd());
        // updating row
        db.update(TENANT_INFO_TABLE, values, TENANT_INFO_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(tenant.getId())});
        db.close();
    }

    public void setTenantInactive(Tenant tenant){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TENANT_INFO_IS_ACTIVE_COLUMN, 0);
        // updating row
        db.update(TENANT_INFO_TABLE, values, TENANT_INFO_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(tenant.getId())});
        db.close();
    }

    //Add apartment
    public void addNewApartment(Apartment apartment, int userID) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        values.put(APARTMENT_INFO_USER_ID_COLUMN_FK, userID);
        values.put(APARTMENT_INFO_STREET1_COLUMN, apartment.getStreet1());
        values.put(APARTMENT_INFO_STREET2_COLUMN, apartment.getStreet2());
        values.put(APARTMENT_INFO_CITY_COLUMN, apartment.getCity());
        values.put(APARTMENT_INFO_STATE_COLUMN_FK, apartment.getStateID());
        values.put(APARTMENT_INFO_ZIP_COLUMN, apartment.getZip());
        values.put(APARTMENT_INFO_DESCRIPTION_COLUMN, apartment.getDescription());
        values.put(APARTMENT_INFO_NOTES_COLUMN, apartment.getNotes());
        values.putNull(APARTMENT_INFO_MAIN_PIC_COLUMN);
        db.insert(APARTMENT_INFO_TABLE, null, values);
        db.close();
    }

    public void addApartmentOtherPic(Apartment apartment, String otherPic, User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APARTMENT_PICS_APARTMENT_ID_COLUMN_FK, apartment.getId());
        values.put(APARTMENT_PICS_USER_ID_COLUMN_FK, user.getId());
        values.put(APARTMENT_PICS_PIC_COLUMN, otherPic);
        db.insert(APARTMENT_PICS_TABLE, null, values);
        db.close();
    }

    public void removeApartmentOtherPic(String otherPic, User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(APARTMENT_PICS_TABLE, APARTMENT_PICS_PIC_COLUMN + " = ? AND " + APARTMENT_PICS_USER_ID_COLUMN_FK + " = ? ",
                new String[]{String.valueOf(otherPic), String.valueOf(user.getId())});
        db.close();
    }

    //Update apartment
    public void editApartment(Apartment apartment, int userID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(APARTMENT_INFO_STREET1_COLUMN, apartment.getStreet1());
        values.put(APARTMENT_INFO_STREET2_COLUMN, apartment.getStreet2());
        values.put(APARTMENT_INFO_CITY_COLUMN, apartment.getCity());
        values.put(APARTMENT_INFO_STATE_COLUMN_FK, apartment.getStateID());
        values.put(APARTMENT_INFO_ZIP_COLUMN, apartment.getZip());
        values.put(APARTMENT_INFO_DESCRIPTION_COLUMN, apartment.getDescription());
        values.put(APARTMENT_INFO_NOTES_COLUMN, apartment.getNotes());
        if (apartment.getMainPic() != null) {
            values.put(APARTMENT_INFO_MAIN_PIC_COLUMN, apartment.getMainPic());
        } else {
            values.putNull(APARTMENT_INFO_MAIN_PIC_COLUMN);
        }
        // updating row
        db.update(APARTMENT_INFO_TABLE, values, APARTMENT_INFO_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(apartment.getId())});
        db.close();
    }

    public void setApartmentInactive(Apartment apartment){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(APARTMENT_INFO_IS_ACTIVE_COLUMN, 0);
        // updating row
        db.update(APARTMENT_INFO_TABLE, values, APARTMENT_INFO_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(apartment.getId())});
        db.close();
    }

    public void changeApartmentMainPic(Apartment apartment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APARTMENT_INFO_MAIN_PIC_COLUMN, apartment.getMainPic());
        db.update(APARTMENT_INFO_TABLE, values, APARTMENT_INFO_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(apartment.getId())});
        db.close();
    }

    public void removeApartmentMainPic(Apartment apartment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull(APARTMENT_INFO_MAIN_PIC_COLUMN);
        db.update(APARTMENT_INFO_TABLE, values, APARTMENT_INFO_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(apartment.getId())});
        db.close();
    }

    private ArrayList<String> getApartmentOtherPics(SQLiteDatabase db, int apartmentID) {
        ArrayList<String> otherPics = new ArrayList<>();
        String Query = "Select * from " + APARTMENT_PICS_TABLE + " WHERE " + APARTMENT_PICS_APARTMENT_ID_COLUMN_FK + " = " + apartmentID + " AND " +
                APARTMENT_PICS_IS_ACTIVE_COLUMN + " = 1";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                //if (!cursor.isNull(cursor.getColumnIndex(APARTMENT_PICS_PIC_COLUMN))) {
                String pic = cursor.getString(cursor.getColumnIndex(APARTMENT_PICS_PIC_COLUMN));
                otherPics.add(pic);
                //}
                cursor.moveToNext();
            }
        }
        cursor.close();
        return otherPics;
    }

    //Add payment log entry
    public void addPaymentLogEntry(PaymentLogEntry ple, int userID) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        values.put(PAYMENT_LOG_USER_ID_COLUMN_FK, userID);
        values.put(PAYMENT_LOG_PAYMENT_DATE_COLUMN, ple.getPaymentDate());
        values.put(PAYMENT_LOG_TYPE_ID_COLUMN_FK, ple.getTypeID()); //TODO
        values.put(PAYMENT_LOG_TENANT_ID_COLUMN_FK, ple.getTenantID()); //TODO
        values.put(PAYMENT_LOG_AMOUNT_COLUMN, ple.getAmount());
        db.insert(PAYMENT_LOG_TABLE, null, values);
        db.close();
    }

    //TODO
    //Add expense log entry
    public void addExpenseLogEntry(ExpenseLogEntry ele, int userID) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        values.put(EXPENSE_LOG_USER_ID_COLUMN_FK, userID);
        values.put(EXPENSE_LOG_EXPENSE_DATE_COLUMN, ele.getExpenseDate());
        values.put(EXPENSE_LOG_AMOUNT_COLUMN, ele.getAmount());
        values.put(EXPENSE_LOG_APARTMENT_ID_COLUMN_FK, ele.getApartmentID()); //TODO
        values.put(EXPENSE_LOG_DESCRIPTION_COLUMN, ele.getDescription());
        values.put(EXPENSE_LOG_TYPE_ID_COLUMN_FK, ele.getTypeID()); //TODO
        values.put(EXPENSE_LOG_RECIPT_PIC, ele.getReceiptPic());
        db.insert(EXPENSE_LOG_TABLE, null, values);
        db.close();
    }

    //TODO
    //Add event log entry
    public void addEventLogEntry(EventLogEntry ele, int userID) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        values.put(EVENT_LOG_USER_ID_COLUMN_FK, userID);
        values.put(EVENT_LOG_EVENT_TIME_COLUMN, ele.getEventTime());
        values.put(EVENT_LOG_TYPE_ID_COLUMN_FK, ele.getTypeID()); //TODO
        values.put(EVENT_LOG_DESCRIPTION_COLUMN, ele.getDescription());
        values.put(EVENT_LOG_APARTMENT_ID_COLUMN_FK, ele.getApartmentID()); //TODO
        values.put(EVENT_LOG_TENANT_ID_COLUMN_FK, ele.getTenantID()); //TODO
        db.insert(EVENT_LOG_TABLE, null, values);
        db.close();
    }

    public void createNewLease(Apartment apartment, Tenant primaryTenant, ArrayList<Tenant> secondaryTenants) {
        //SQLiteDatabase db = this.getReadableDatabase();
        //storeApartmentLease(apartment);
        //storeTenantLease(primaryTenant, apartment, db);
        //storeSecondaryTenantLease();
        editTenant(primaryTenant);
        for (int i = 0; i < secondaryTenants.size(); i++) {
            editTenant(secondaryTenants.get(i));
        }
    }

    //Gets a users active tenants
    public ArrayList<Tenant> getUsersTenants(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Tenant> tenants = new ArrayList<>();
        String Query = "Select * from " + TENANT_INFO_TABLE +
                " WHERE " + TENANT_INFO_USER_ID_COLUMN_FK + " = " + user.getId() + " AND " + TENANT_INFO_IS_ACTIVE_COLUMN + " = 1" +
                " ORDER BY " + TENANT_INFO_APARTMENT_ID_COLUMN_FK + " IS NULL " + ", " +
                " UPPER(" + TENANT_INFO_FIRST_NAME_COLUMN + ")" + ", " +
                " UPPER(" + TENANT_INFO_LAST_NAME_COLUMN + ")";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(TENANT_INFO_ID_COLUMN_PK));
                String firstName = cursor.getString(cursor.getColumnIndex(TENANT_INFO_FIRST_NAME_COLUMN));
                String lastName = cursor.getString(cursor.getColumnIndex(TENANT_INFO_LAST_NAME_COLUMN));
                String phone = cursor.getString(cursor.getColumnIndex(TENANT_INFO_PHONE_COLUMN));
                String email = cursor.getString(cursor.getColumnIndex(TENANT_INFO_EMAIL_COLUMN));
                String emergencyFirstName = cursor.getString(cursor.getColumnIndex(TENANT_INFO_EMERGENCY_FIRST_NAME));
                String emergencyLastName = cursor.getString(cursor.getColumnIndex(TENANT_INFO_EMERGENCY_LAST_NAME));
                String emergencyPhone = cursor.getString(cursor.getColumnIndex(TENANT_INFO_EMERGENCY_PHONE));
                int aptID = cursor.getInt(cursor.getColumnIndex(TENANT_INFO_APARTMENT_ID_COLUMN_FK));
                int rentCost = cursor.getInt(cursor.getColumnIndex(TENANT_INFO_RENT_COST));
                int deposit = cursor.getInt(cursor.getColumnIndex(TENANT_INFO_DEPOSIT));
                Boolean isPrimary = cursor.getInt(cursor.getColumnIndex(TENANT_INFO_IS_PRIMARY_TENANT_COLUMN)) > 0;
                String paymentDay = cursor.getString(cursor.getColumnIndex(TENANT_INFO_PAYMENT_DAY_COLUMN));
                String notes = cursor.getString(cursor.getColumnIndex(TENANT_INFO_NOTES_COLUMN));
                String leaseStart = cursor.getString(cursor.getColumnIndex(TENANT_INFO_LEASE_START_COLUMN));
                String leaseEnd = cursor.getString(cursor.getColumnIndex(TENANT_INFO_LEASE_END_COLUMN));
                tenants.add(new Tenant(id, firstName, lastName, phone, email, emergencyFirstName, emergencyLastName, emergencyPhone,
                        aptID, rentCost, deposit, isPrimary, paymentDay, notes, leaseStart, leaseEnd));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return tenants;
        } else {
            cursor.close();
            db.close();
            return tenants;
        }
    }

    //Gets a users active apartments
    public ArrayList<Apartment> getUsersApartments(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Apartment> apartments = new ArrayList<>();
        String Query = "Select * from " + APARTMENTS_VIEW +
                " WHERE " + APARTMENTS_VIEW_USER_ID + " = " + user.getId() + " AND " + APARTMENTS_VIEW_IS_ACTIVE + " = 1" +
                " ORDER BY " + APARTMENTS_VIEW_IS_RENTED + " DESC, " +
                " UPPER(" + APARTMENTS_VIEW_CITY + ")";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(APARTMENTS_VIEW_APARTMENT_ID));
                String street1 = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_STREET_1));
                String street2 = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_STREET_2));
                String city = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_CITY));
                int stateID = cursor.getInt(cursor.getColumnIndex(APARTMENTS_VIEW_STATE_ID));
                String state = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_STATE));
                String zip = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_ZIP));
                String description = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_DESCRIPTION));
                Boolean isRented = cursor.getInt(cursor.getColumnIndex(APARTMENTS_VIEW_IS_RENTED)) > 0;
                String notes = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_NOTES));
                String mainPic = null;
                if (!cursor.isNull(cursor.getColumnIndex(APARTMENTS_VIEW_MAIN_PIC))) {
                    mainPic = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_MAIN_PIC));
                }
                ArrayList<String> otherPics = getApartmentOtherPics(db, id);
                apartments.add(new Apartment(id, street1, street2, city, stateID, state, zip, description, isRented, notes, mainPic, otherPics));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return apartments;
        } else {
            cursor.close();
            db.close();
            return apartments;
        }
    }

    //Change users profile pic string
    public void changeProfilePic(User user, String pic) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_INFO_PROFILE_PIC, pic);
        db.update(USER_INFO_TABLE, values, USER_INFO_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(user.getId())});
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUserInfoTable(db);
        createStateTable(db);
        createTypesTable(db); //lookuptype //TODO
        createTypeLookupTable(db); //lookup
        createApartmentInfoTable(db);
        createApartmentPicsTable(db);
        createTenantInfoTable(db);
        createEventLogTable(db);
        createExpenseLogTable(db);
        createPaymentLogTable(db);
        populateStateTable(db);
        createApartmentView(db);
        //createTenantView(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
    }

    //Allows foreign key use
    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            db.execSQL("PRAGMA foreign_keys=1;");
        } else {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createUserInfoTable(SQLiteDatabase db) {
        String userTable = "CREATE TABLE IF NOT EXISTS " + USER_INFO_TABLE + " ( " +
                USER_INFO_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_INFO_NAME_COLUMN + " VARCHAR(10), " +
                USER_INFO_EMAIL_COLUMN + " VARCHAR(25), " +
                USER_INFO_PASSWORD_COLUMN + " VARCHAR(15), " +
                USER_INFO_IS_VERIFIED_COLUMN + " BOOLEAN NOT NULL DEFAULT 0, " +
                USER_INFO_PROFILE_PIC + " BLOB, " +
                USER_INFO_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                USER_INFO_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                USER_INFO_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(userTable);
    }

    private void createTenantInfoTable(SQLiteDatabase db) {
        String tenantTable = "CREATE TABLE IF NOT EXISTS " + TENANT_INFO_TABLE + " ( " +
                TENANT_INFO_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TENANT_INFO_USER_ID_COLUMN_FK + " INTEGER REFERENCES " + USER_INFO_TABLE + "(" + USER_INFO_ID_COLUMN_PK + "), " +
                TENANT_INFO_FIRST_NAME_COLUMN + " VARCHAR(10), " +
                TENANT_INFO_LAST_NAME_COLUMN + " VARCHAR(15), " +
                TENANT_INFO_PHONE_COLUMN + " VARCHAR(12), " +
                TENANT_INFO_EMAIL_COLUMN + " VARCHAR(30), " +
                TENANT_INFO_EMERGENCY_FIRST_NAME + " VARCHAR(10), " +
                TENANT_INFO_EMERGENCY_LAST_NAME + " VARCHAR(15), " +
                TENANT_INFO_EMERGENCY_PHONE + " VARCHAR(12), " +
                TENANT_INFO_APARTMENT_ID_COLUMN_FK + " INTEGER REFERENCES " + APARTMENT_INFO_TABLE + "(" + APARTMENT_INFO_ID_COLUMN_PK + "), " +
                TENANT_INFO_RENT_COST + " INTEGER, " +
                TENANT_INFO_DEPOSIT + " INTEGER, " +
                TENANT_INFO_IS_PRIMARY_TENANT_COLUMN + " BOOLEAN NOT NULL DEFAULT 0, " +
                TENANT_INFO_PAYMENT_DAY_COLUMN + " INTEGER, " +
                TENANT_INFO_NOTES_COLUMN + " VARCHAR(150), " +
                TENANT_INFO_LEASE_START_COLUMN + " DATETIME, " +
                TENANT_INFO_LEASE_END_COLUMN + " DATETIME, " +
                TENANT_INFO_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                TENANT_INFO_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                TENANT_INFO_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(tenantTable);
    }

    private void createApartmentInfoTable(SQLiteDatabase db) {
        String apartmentTable = "CREATE TABLE IF NOT EXISTS " + APARTMENT_INFO_TABLE + " ( " +
                APARTMENT_INFO_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                APARTMENT_INFO_USER_ID_COLUMN_FK + " INTEGER REFERENCES " + USER_INFO_TABLE + "(" + USER_INFO_ID_COLUMN_PK + "), " +
                APARTMENT_INFO_STREET1_COLUMN + " VARCHAR(20), " +
                APARTMENT_INFO_STREET2_COLUMN + " VARCHAR(20), " +
                APARTMENT_INFO_CITY_COLUMN + " VARCHAR(15), " +
                APARTMENT_INFO_STATE_COLUMN_FK + " INTEGER REFERENCES " + STATE_TABLE + "(" + STATE_ID_COLUMN_PK + "), " +
                APARTMENT_INFO_ZIP_COLUMN + " VARCHAR(5), " +
                APARTMENT_INFO_DESCRIPTION_COLUMN + " VARCHAR(150), " +
                APARTMENT_INFO_NOTES_COLUMN + " VARCHAR(150), " +
                APARTMENT_INFO_MAIN_PIC_COLUMN + " VARCHAR(50), " +
                APARTMENT_INFO_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                APARTMENT_INFO_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                APARTMENT_INFO_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(apartmentTable);
    }

    private void createPaymentLogTable(SQLiteDatabase db) {
        String paymentTable = "CREATE TABLE IF NOT EXISTS " + PAYMENT_LOG_TABLE + " ( " +
                PAYMENT_LOG_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PAYMENT_LOG_USER_ID_COLUMN_FK + " INTEGER REFERENCES " + USER_INFO_TABLE + "(" + USER_INFO_ID_COLUMN_PK + "), " +
                PAYMENT_LOG_PAYMENT_DATE_COLUMN + " DATETIME, " +
                PAYMENT_LOG_TYPE_ID_COLUMN_FK + " INTEGER REFERENCES " + TYPES_TABLE + "(" + TYPES_ID_COLUMN_PK + "), " +
                PAYMENT_LOG_TENANT_ID_COLUMN_FK + " INTEGER REFERENCES " + TENANT_INFO_TABLE + "(" + TENANT_INFO_ID_COLUMN_PK + "), " +
                PAYMENT_LOG_AMOUNT_COLUMN + " INTEGER, " +
                PAYMENT_LOG_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                PAYMENT_LOG_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                PAYMENT_LOG_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(paymentTable);
    }

    private void createExpenseLogTable(SQLiteDatabase db) {
        String expenseTable = "CREATE TABLE IF NOT EXISTS " + EXPENSE_LOG_TABLE + " ( " +
                EXPENSE_LOG_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EXPENSE_LOG_USER_ID_COLUMN_FK + " INTEGER REFERENCES " + USER_INFO_TABLE + "(" + USER_INFO_ID_COLUMN_PK + "), " +
                EXPENSE_LOG_EXPENSE_DATE_COLUMN + " DATETIME, " +
                EXPENSE_LOG_AMOUNT_COLUMN + " INTEGER, " +
                EXPENSE_LOG_APARTMENT_ID_COLUMN_FK + " INTEGER REFERENCES " + APARTMENT_INFO_TABLE + "(" + APARTMENT_INFO_ID_COLUMN_PK + "), " +
                EXPENSE_LOG_DESCRIPTION_COLUMN + " VARCHAR(150), " +
                EXPENSE_LOG_TYPE_ID_COLUMN_FK + " INTEGER REFERENCES " + TYPES_TABLE + "(" + TYPES_ID_COLUMN_PK + "), " +
                EXPENSE_LOG_RECIPT_PIC + " BLOB, " +
                EXPENSE_LOG_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                EXPENSE_LOG_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                EXPENSE_LOG_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(expenseTable);
    }

    private void createEventLogTable(SQLiteDatabase db) {
        String eventTable = "CREATE TABLE IF NOT EXISTS " + EVENT_LOG_TABLE + " ( " +
                EVENT_LOG_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EVENT_LOG_USER_ID_COLUMN_FK + " INTEGER REFERENCES " + USER_INFO_TABLE + "(" + USER_INFO_ID_COLUMN_PK + "), " +
                EVENT_LOG_EVENT_TIME_COLUMN + " DATETIME, " +
                EVENT_LOG_TYPE_ID_COLUMN_FK + " INTEGER REFERENCES " + TYPES_TABLE + "(" + TYPES_ID_COLUMN_PK + "), " +
                EVENT_LOG_DESCRIPTION_COLUMN + " VARCHAR(150), " +
                EVENT_LOG_APARTMENT_ID_COLUMN_FK + " INTEGER REFERENCES " + APARTMENT_INFO_TABLE + "(" + APARTMENT_INFO_ID_COLUMN_PK + "), " +
                EVENT_LOG_TENANT_ID_COLUMN_FK + " INTEGER REFERENCES " + TENANT_INFO_TABLE + "(" + TENANT_INFO_ID_COLUMN_PK + "), " +
                EVENT_LOG_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                EVENT_LOG_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                EVENT_LOG_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(eventTable);
    }

    private void createTypesTable(SQLiteDatabase db) {
        String typeTable = "CREATE TABLE IF NOT EXISTS " + TYPES_TABLE + " ( " +
                TYPES_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TYPES_TYPE_COLUMN + " VARCHAR(10), " +
                TYPES_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                TYPES_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                TYPES_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(typeTable);
    }

    private void createTypeLookupTable(SQLiteDatabase db) {
        String typeLookupTable = "CREATE TABLE IF NOT EXISTS " + TYPE_LOOKUP_TABLE + " ( " +
                TYPE_LOOKUP_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TYPE_LOOKUP_TYPE_ID_COLUMN_FK + " INTEGER REFERENCES " + TYPES_TABLE + "(" + TYPES_ID_COLUMN_PK + "), " +
                TYPE_LOOKUP_LABEL_COLUMN + " VARCHAR(15), " +
                TYPE_LOOKUP_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                TYPE_LOOKUP_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                TYPE_LOOKUP_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(typeLookupTable);
    }

    private void createStateTable(SQLiteDatabase db) {
        String stateTable = "CREATE TABLE IF NOT EXISTS " + STATE_TABLE + " ( " +
                STATE_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                STATE_STATE_ABR_COLUMN + " VARCHAR(2), " +
                STATE_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                STATE_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                STATE_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(stateTable);
    }

    private void createApartmentPicsTable(SQLiteDatabase db) {
        String apartmentPicsTable = "CREATE TABLE IF NOT EXISTS " + APARTMENT_PICS_TABLE + " ( " +
                APARTMENT_PICS_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                APARTMENT_PICS_USER_ID_COLUMN_FK + " INTEGER REFERENCES " + USER_INFO_TABLE + "(" + USER_INFO_ID_COLUMN_PK + "), " +
                APARTMENT_PICS_APARTMENT_ID_COLUMN_FK + " INTEGER REFERENCES " + APARTMENT_INFO_TABLE + "(" + APARTMENT_INFO_ID_COLUMN_PK + "), " +
                APARTMENT_PICS_PIC_COLUMN + " VARCHAR(30), " +
                APARTMENT_PICS_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                APARTMENT_PICS_LAST_UPDATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                APARTMENT_PICS_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(apartmentPicsTable);
    }

    private void populateStateTable(SQLiteDatabase db) {
        String insert = "INSERT INTO '" + STATE_TABLE + "' ('" + STATE_STATE_ABR_COLUMN + "') VALUES " +
                "(\"AL\"),(\"AK\"),(\"AZ\"),(\"AR\"),(\"CA\"),(\"CO\"),(\"CT\"),(\"DE\"),(\"FL\"),(\"GA\"),(\"HI\"),(\"ID\"),(\"IL\"),(\"IN\")," +
                "(\"IA\"),(\"KS\"),(\"KY\"),(\"LA\"),(\"ME\"),(\"MD\"),(\"MA\"),(\"MI\"),(\"MN\"),(\"MS\"),(\"MO\"),(\"MT\"),(\"NE\"),(\"NV\")," +
                "(\"NH\"),(\"NJ\"),(\"NM\"),(\"NY\"),(\"NC\"),(\"ND\"),(\"OH\"),(\"OK\"),(\"OR\"),(\"PA\"),(\"RI\"),(\"SC\"),(\"SD\"),(\"TN\")," +
                "(\"TX\"),(\"UT\"),(\"VT\"),(\"VA\"),(\"WA\"),(\"WV\"),(\"WI\"),(\"WY\"),(\"AS\"),(\"DC\"),(\"FM\"),(\"GU\"),(\"MH\"),(\"MP\")," +
                "(\"PW\"),(\"PR\"),(\"VI\")";
        db.execSQL(insert);
    }

    private void createApartmentView(SQLiteDatabase db) {
        String insert = "CREATE VIEW IF NOT EXISTS " + APARTMENTS_VIEW + " AS" +
                " SELECT " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_USER_ID_COLUMN_FK + " AS " + APARTMENTS_VIEW_USER_ID + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_ID_COLUMN_PK + " AS " + APARTMENTS_VIEW_APARTMENT_ID + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_STREET1_COLUMN + " AS " + APARTMENTS_VIEW_STREET_1 + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_STREET2_COLUMN + " AS " + APARTMENTS_VIEW_STREET_2 + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_CITY_COLUMN + " AS " + APARTMENTS_VIEW_CITY + ", " +
                STATE_TABLE + "." + STATE_ID_COLUMN_PK + " AS " + APARTMENTS_VIEW_STATE_ID + ", " +
                STATE_TABLE + "." + STATE_STATE_ABR_COLUMN + " AS " + APARTMENTS_VIEW_STATE + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_ZIP_COLUMN + " AS " + APARTMENTS_VIEW_ZIP + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_DESCRIPTION_COLUMN + " AS " + APARTMENTS_VIEW_DESCRIPTION + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_MAIN_PIC_COLUMN + " AS " + APARTMENTS_VIEW_MAIN_PIC + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_NOTES_COLUMN + " AS " + APARTMENTS_VIEW_NOTES + ", " +
                " CASE WHEN " + TENANT_INFO_TABLE + ".totalCount > 0 THEN 1 ELSE 0 END " + APARTMENTS_VIEW_IS_RENTED + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_DATE_CREATED_COLUMN + " AS " + APARTMENTS_VIEW_DATE_CREATED + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_LAST_UPDATE_COLUMN + " AS " + APARTMENTS_VIEW_LAST_UPDATE + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_IS_ACTIVE_COLUMN + " AS " + APARTMENTS_VIEW_IS_ACTIVE + " " +
                " FROM " +
                APARTMENT_INFO_TABLE +
                " INNER JOIN " + STATE_TABLE + " ON " + STATE_TABLE + "." + STATE_ID_COLUMN_PK + " = " + APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_STATE_COLUMN_FK +
                " LEFT JOIN (" +
                " SELECT " + TENANT_INFO_APARTMENT_ID_COLUMN_FK + ", " +
                " COUNT(DISTINCT " + TENANT_INFO_APARTMENT_ID_COLUMN_FK + ")" + " AS totalCount " +
                " FROM " + TENANT_INFO_TABLE +
                " WHERE " + TENANT_INFO_IS_ACTIVE_COLUMN + " = 1" +
                " GROUP BY " + TENANT_INFO_APARTMENT_ID_COLUMN_FK +
                ") " + TENANT_INFO_TABLE + " ON " + APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_ID_COLUMN_PK + " = " + TENANT_INFO_TABLE + "." + TENANT_INFO_APARTMENT_ID_COLUMN_FK +
                ";";
        db.execSQL(insert);
    }

    private void createTenantView(SQLiteDatabase db) {
        String insert = "CREATE VIEW IF NOT EXISTS " + TENANTS_VIEW + " AS" +
                " SELECT " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_USER_ID_COLUMN_FK + " AS " + TENANTS_VIEW_USER_ID + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_ID_COLUMN_PK + " AS " + TENANTS_VIEW_TENANT_ID + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_FIRST_NAME_COLUMN + " AS " + TENANTS_VIEW_FIRST_NAME + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_LAST_NAME_COLUMN + " AS " + TENANTS_VIEW_LAST_NAME + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_PHONE_COLUMN + " AS " + TENANTS_VIEW_PHONE + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_APARTMENT_ID_COLUMN_FK + " AS " + TENANTS_VIEW_RENTED_APARTMENT_ID + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_PAYMENT_DAY_COLUMN + " AS " + TENANTS_VIEW_PAYMENT_DAY_ + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_NOTES_COLUMN + " AS " + TENANTS_VIEW_NOTES + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_LEASE_START_COLUMN + " AS " + TENANTS_VIEW_LEASE_START + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_LEASE_END_COLUMN + " AS " + TENANTS_VIEW_LEASE_END + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_DATE_CREATED_COLUMN + " AS " + TENANTS_VIEW_DATE_CREATED + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_LAST_UPDATE_COLUMN + " AS " + TENANTS_VIEW_LAST_UPDATE + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_IS_ACTIVE_COLUMN + " AS " + TENANTS_VIEW_IS_ACTIVE + ", " +
                "FROM " +
                TENANT_INFO_TABLE +
                ";";
        db.execSQL(insert);
    }

    public void addTestData(User user) {
        //Temporary method
        //Apartment apartment1 = new Apartment(0, "2390 Burlington Rd", "", "Letts", 1, "IA", "52754", "", "", "", new ArrayList<String>());
        //Apartment apartment2 = new Apartment(1, "2495 McNair Farms Dr", "Apt 555", "Herndon", 2, "VA", "78978", "", "", "", new ArrayList<String>());
        //Tenant tenant1 = new Tenant(0, "Cody", "Halstead", "563-299-9577", 1, false, "", "", "", "");
        //Tenant tenant2 = new Tenant(1, "Monet", "Tomioka", "345-554-2323", 2, true, "", "", "", "");

        //this.addNewApartment(apartment1, user.getId());
        //this.addNewApartment(apartment2, user.getId());
        //this.addNewTenant(tenant1, user.getId());
        //this.addNewTenant(tenant2, user.getId());
    }
}
