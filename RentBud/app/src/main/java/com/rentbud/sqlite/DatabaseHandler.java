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
    public static final String TENANT_INFO_APARTMENT_ID_COLUMN_FK = "tenant_info_apartment_id";
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

    RandomNumberGenerator verificationGenerator;

    public DatabaseHandler(Context context) {
        super(context, DB_FILE_NAME, null, DATABASE_VERSION);
        verificationGenerator = new RandomNumberGenerator();
    }

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

    public User getUser(String email, String password){
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

    public void changeVerificationNumber(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(USER_INFO_VERIFICATION_NUMBER_COLUMN, verificationGenerator.gererateVerificationNumber(5));
        db.update(USER_INFO_TABLE, contentValues, USER_INFO_EMAIL_COLUMN + " = " + email, null);
        db.close();
    }


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

    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(USER_INFO_TABLE, USER_INFO_ID_COLUMN_PK + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public boolean checkUser(String email) {
        // array of columns to fetch
        String[] columns = {USER_INFO_ID_COLUMN_PK};
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = USER_INFO_EMAIL_COLUMN + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(USER_INFO_TABLE, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return (cursorCount > 0);
    }

    public boolean checkUser(String email, String password) {
        // array of columns to fetch
        String[] columns = {USER_INFO_ID_COLUMN_PK};
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = USER_INFO_EMAIL_COLUMN + " = ?" + " AND " + USER_INFO_PASSWORD_COLUMN + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(USER_INFO_TABLE, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        return (cursorCount > 0);
    }

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

    public int getUserID(String email){
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

    public TreeMap<String, Integer> getStateTreemap(){
        TreeMap<String, Integer> stateMap = new TreeMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + STATE_TABLE + " WHERE " + STATE_IS_ACTIVE_COLUMN + " = 1";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.moveToFirst()){
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

    public void addNewTenant(Tenant tenant, int userID){
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        values.put(TENANT_INFO_USER_ID_COLUMN_FK, userID);
        values.put(TENANT_INFO_FIRST_NAME_COLUMN, tenant.getFirstName());
        values.put(TENANT_INFO_LAST_NAME_COLUMN, tenant.getLastName());
        values.put(TENANT_INFO_PHONE_COLUMN, tenant.getPhone());
        values.put(TENANT_INFO_APARTMENT_ID_COLUMN_FK, tenant.getApartmentID());
        values.put(TENANT_INFO_PAYMENT_DAY_COLUMN, tenant.getPaymentDay());
        values.put(TENANT_INFO_NOTES_COLUMN, tenant.getNotes());
        values.put(TENANT_INFO_LEASE_START_COLUMN, tenant.getLeaseStart());
        values.put(TENANT_INFO_LEASE_END_COLUMN, tenant.getLeaseEnd());
        db.insert(TENANT_INFO_TABLE, null, values);
        db.close();
    }

    public void addNewApartment(Apartment apartment, int userID){
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        values.put(APARTMENT_INFO_USER_ID_COLUMN_FK, userID);
        values.put(APARTMENT_INFO_STREET1_COLUMN, apartment.getStreet1());
        values.put(APARTMENT_INFO_STREET2_COLUMN, apartment.getStreet2());
        values.put(APARTMENT_INFO_CITY_COLUMN, apartment.getCity());
        values.put(APARTMENT_INFO_STATE_COLUMN_FK, apartment.getStateID());
        values.put(APARTMENT_INFO_ZIP_COLUMN, apartment.getZip());
        values.put(APARTMENT_INFO_NOTES_COLUMN, apartment.getNotes());
        values.put(APARTMENT_INFO_MAIN_PIC_COLUMN, apartment.getMainPic());
        db.insert(APARTMENT_INFO_TABLE, null, values);
        db.close();
    }

    public void addPaymentLogEntry(PaymentLogEntry ple, int userID){
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
    public void addExpenseLogEntry(ExpenseLogEntry ele, int userID){
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
    public void addEventLogEntry(EventLogEntry ele, int userID){
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

    public ArrayList<Tenant> getUsersTenants(User user){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Tenant> tenants = new ArrayList<>();
        String Query = "Select * from " + TENANT_INFO_TABLE + " WHERE " + TENANT_INFO_USER_ID_COLUMN_FK + " = " + user.getId() + " AND " +
                TENANT_INFO_IS_ACTIVE_COLUMN + " = 1";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(TENANT_INFO_ID_COLUMN_PK));
                String firstName = cursor.getString(cursor.getColumnIndex(TENANT_INFO_FIRST_NAME_COLUMN));
                String lastname = cursor.getString(cursor.getColumnIndex(TENANT_INFO_LAST_NAME_COLUMN));
                String phone = cursor.getString(cursor.getColumnIndex(TENANT_INFO_PHONE_COLUMN));
                int aptID = cursor.getInt(cursor.getColumnIndex(TENANT_INFO_APARTMENT_ID_COLUMN_FK));
                String paymentDay = cursor.getString(cursor.getColumnIndex(TENANT_INFO_PAYMENT_DAY_COLUMN)); //TODO
                String notes = cursor.getString(cursor.getColumnIndex(TENANT_INFO_NOTES_COLUMN));
                String leaseStart = cursor.getString(cursor.getColumnIndex(TENANT_INFO_LEASE_START_COLUMN)); //TODO
                String leaseEnd = cursor.getString(cursor.getColumnIndex(TENANT_INFO_LEASE_END_COLUMN)); //TODO
                tenants.add(new Tenant(id, firstName, lastname, phone, aptID, paymentDay, notes, leaseStart, leaseEnd));
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

    public ArrayList<Apartment> getUsersApartments(User user){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Apartment> apartments = new ArrayList<>();
        String Query = "Select * from " + APARTMENT_INFO_TABLE + " WHERE " + APARTMENT_INFO_USER_ID_COLUMN_FK + " = " + user.getId() + " AND " +
                APARTMENT_INFO_IS_ACTIVE_COLUMN + " = 1";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(APARTMENT_INFO_ID_COLUMN_PK));
                String street1 = cursor.getString(cursor.getColumnIndex(APARTMENT_INFO_STREET1_COLUMN));
                String street2 = cursor.getString(cursor.getColumnIndex(APARTMENT_INFO_STREET2_COLUMN));
                String city = cursor.getString(cursor.getColumnIndex(APARTMENT_INFO_CITY_COLUMN));
                int stateID = 0; //TODO
                String state = "IA"; //cursor.getString(cursor.getColumnIndex(APARTMENT_INFO_STATE_COLUMN_FK)); //TODO
                String zip = cursor.getString(cursor.getColumnIndex(APARTMENT_INFO_ZIP_COLUMN));
                String notes = cursor.getString(cursor.getColumnIndex(APARTMENT_INFO_NOTES_COLUMN));
                String mainPic = cursor.getString(cursor.getColumnIndex(APARTMENT_INFO_MAIN_PIC_COLUMN));
                ArrayList<String> otherPics = new ArrayList<>(); //TODO
                apartments.add(new Apartment(id, street1, street2, city, stateID, state, zip, notes, mainPic, otherPics));
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

    public void changeProfilePic(User user, String pic){
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
        createTypesTable(db); //lookuptype
        createTypeLookupTable(db); //lookup
        createApartmentInfoTable(db);
        createApartmentPicsTable(db);
        createTenantInfoTable(db);
        createEventLogTable(db);
        createExpenseLogTable(db);
        createPaymentLogTable(db);
        populateStateTable(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=1;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
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
                TENANT_INFO_APARTMENT_ID_COLUMN_FK + " INTEGER REFERENCES " + APARTMENT_INFO_TABLE + "(" + APARTMENT_INFO_ID_COLUMN_PK + "), " +
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
                APARTMENT_INFO_NOTES_COLUMN + " VARCHAR(150), " +
                APARTMENT_INFO_MAIN_PIC_COLUMN + " BLOB, " +
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
                PAYMENT_LOG_TYPE_ID_COLUMN_FK + " INTEGER REFERENCES " + TYPES_TABLE + "(" + TYPES_ID_COLUMN_PK+ "), " +
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
                APARTMENT_PICS_PIC_COLUMN + " VARCHAR(150), " +
                APARTMENT_PICS_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                APARTMENT_PICS_LAST_UPDATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                APARTMENT_PICS_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(apartmentPicsTable);
    }

    private void populateStateTable(SQLiteDatabase db){
        String insert = "INSERT INTO '" + STATE_TABLE + "' ('" + STATE_STATE_ABR_COLUMN + "') VALUES " +
                "(\"AL\"),(\"AK\"),(\"AZ\"),(\"AR\"),(\"CA\"),(\"CO\"),(\"CT\"),(\"DE\"),(\"FL\"),(\"GA\"),(\"HI\"),(\"ID\"),(\"IL\"),(\"IN\")," +
                "(\"IA\"),(\"KS\"),(\"KY\"),(\"LA\"),(\"ME\"),(\"MD\"),(\"MA\"),(\"MI\"),(\"MN\"),(\"MS\"),(\"MO\"),(\"MT\"),(\"NE\"),(\"NV\")," +
                "(\"NH\"),(\"NJ\"),(\"NM\"),(\"NY\"),(\"NC\"),(\"ND\"),(\"OH\"),(\"OK\"),(\"OR\"),(\"PA\"),(\"RI\"),(\"SC\"),(\"SD\"),(\"TN\")," +
                "(\"TX\"),(\"UT\"),(\"VT\"),(\"VA\"),(\"WA\"),(\"WV\"),(\"WI\"),(\"WY\"),(\"AS\"),(\"DC\"),(\"FM\"),(\"GU\"),(\"MH\"),(\"MP\")," +
                "(\"PW\"),(\"PR\"),(\"VI\")";
        db.execSQL(insert);
    }

    public void addTestData(User user){
        Apartment apartment1 = new Apartment(0, "2390 Burlington Rd", "", "Letts", 1, "IA", "52754", "", "", new ArrayList<String>());
        Apartment apartment2 = new Apartment(1, "2495 McNair Farms Dr", "Apt 555", "Herndon", 2, "VA", "78978", "", "", new ArrayList<String>());
        Tenant tenant1 = new Tenant(0, "Cody", "Halstead", "563-299-9577", 1, "", "", "", "");
        Tenant tenant2 = new Tenant(1, "Monet", "Tomioka", "345-554-2323", 2, "", "", "", "");

        this.addNewApartment(apartment1, user.getId());
        this.addNewApartment(apartment2, user.getId());
        this.addNewTenant(tenant1, user.getId());
        this.addNewTenant(tenant2, user.getId());
    }
}
