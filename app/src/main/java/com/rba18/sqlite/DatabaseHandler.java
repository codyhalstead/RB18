package com.rba18.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.rba18.R;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.helpers.RandomNumberGenerator;
import com.rba18.model.Apartment;
import com.rba18.model.ExpenseLogEntry;
import com.rba18.model.Lease;
import com.rba18.model.MoneyLogEntry;
import com.rba18.model.PaymentLogEntry;
import com.rba18.model.Tenant;
import com.rba18.model.TypeTotal;
import com.rba18.model.User;
import com.rba18.model.WizardDueDate;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static android.content.ContentValues.TAG;

/**
 * Created by Cody on 12/12/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    public static int DATABASE_VERSION = 1;
    public static String DB_FILE_NAME = "allData.db";
    private Context context;

    public static final String USER_INFO_TABLE = "user_info_table";
    public static final String USER_INFO_ID_COLUMN_PK = "_id";
    public static final String USER_INFO_NAME_COLUMN = "user_info_name";
    public static final String USER_INFO_EMAIL_COLUMN = "user_info_email";
    public static final String USER_INFO_PASSWORD_COLUMN = "user_info_password";
    public static final String USER_INFO_IS_VERIFIED_COLUMN = "user_info_is_verified";
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
    public static final String TENANT_INFO_NOTES_COLUMN = "tenant_info_notes";
    public static final String TENANT_INFO_DATE_CREATED_COLUMN = "tenant_info_date_created";
    public static final String TENANT_INFO_LAST_UPDATE_COLUMN = "tenant_info_last_update";
    public static final String TENANT_INFO_IS_ACTIVE_COLUMN = "tenant_info_is_active";

    public static final String APARTMENT_INFO_TABLE = "apartment_info_table";
    public static final String APARTMENT_INFO_ID_COLUMN_PK = "_id";
    public static final String APARTMENT_INFO_USER_ID_COLUMN_FK = "apartment_info_user_id";
    public static final String APARTMENT_INFO_STREET1_COLUMN = "apartment_info_street1";
    public static final String APARTMENT_INFO_STREET2_COLUMN = "apartment_info_street2";
    public static final String APARTMENT_INFO_CITY_COLUMN = "apartment_info_city";
    public static final String APARTMENT_INFO_STATE_COLUMN = "apartment_info_state";
    public static final String APARTMENT_INFO_ZIP_COLUMN = "apartment_info_zip";
    public static final String APARTMENT_INFO_DESCRIPTION_COLUMN = "apartment_info_description";
    public static final String APARTMENT_INFO_NOTES_COLUMN = "apartment_info_notes";
    public static final String APARTMENT_INFO_MAIN_PIC_COLUMN = "apartment_info_main_pic";
    public static final String APARTMENT_INFO_DATE_CREATED_COLUMN = "apartment_info_date_created";
    public static final String APARTMENT_INFO_LAST_UPDATE_COLUMN = "apartment_info_last_update";
    public static final String APARTMENT_INFO_IS_ACTIVE_COLUMN = "apartment_info_is_active";

    public static final String PAYMENT_LOG_TABLE = "payment_log_table";
    public static final String PAYMENT_LOG_ID_COLUMN_PK = "_id";
    public static final String PAYMENT_LOG_LEASE_ID_COLUMN_FK = "payment_log_lease_id";
    public static final String PAYMENT_LOG_USER_ID_COLUMN_FK = "payment_log_user_id";
    public static final String PAYMENT_LOG_PAYMENT_DATE_COLUMN = "payment_log_payment_date";
    public static final String PAYMENT_LOG_TYPE_ID_COLUMN_FK = "payment_log_type_id";
    public static final String PAYMENT_LOG_RECEIPT_PIC = "payment_log_receipt_pic";
    public static final String PAYMENT_LOG_TENANT_ID_COLUMN_FK = "payment_log_tenant_id";
    public static final String PAYMENT_LOG_APARTMENT_ID_COLUMN_FK = "payment_log_apartment_id";
    public static final String PAYMENT_LOG_AMOUNT_COLUMN = "payment_log_amount";
    public static final String PAYMENT_LOG_WAS_RECEIVED_COLUMN = "payment_log_was_received";
    public static final String PAYMENT_LOG_DESCRIPTION_COLUMN = "payment_log_description";
    public static final String PAYMENT_LOG_DATE_CREATED_COLUMN = "payment_log_date_created";
    public static final String PAYMENT_LOG_LAST_UPDATE_COLUMN = "payment_log_last_update";
    public static final String PAYMENT_LOG_IS_ACTIVE_COLUMN = "payment_log_is_active";

    public static final String EXPENSE_LOG_TABLE = "expense_log_table";
    public static final String EXPENSE_LOG_ID_COLUMN_PK = "_id";
    public static final String EXPENSE_LOG_LEASE_ID_COLUMN_FK = "expense_log_lease_id";
    public static final String EXPENSE_LOG_USER_ID_COLUMN_FK = "expense_log_user_id";
    public static final String EXPENSE_LOG_EXPENSE_DATE_COLUMN = "expense_log_expense_date";
    public static final String EXPENSE_LOG_AMOUNT_COLUMN = "expense_log_amount";
    public static final String EXPENSE_LOG_WAS_PAID_COLUMN = "expense_log_was_paid";
    public static final String EXPENSE_LOG_TENANT_ID_COLUMN_FK = "expense_log_tenant_id";
    public static final String EXPENSE_LOG_APARTMENT_ID_COLUMN_FK = "expense_log_apartment_id";
    public static final String EXPENSE_LOG_DESCRIPTION_COLUMN = "expense_log_description";
    public static final String EXPENSE_LOG_TYPE_ID_COLUMN_FK = "expense_log_type_id";
    public static final String EXPENSE_LOG_RECEIPT_PIC = "expense_log_receipt_pic";
    public static final String EXPENSE_LOG_DATE_CREATED_COLUMN = "expense_log_date_created";
    public static final String EXPENSE_LOG_LAST_UPDATE_COLUMN = "expense_log_last_update";
    public static final String EXPENSE_LOG_IS_ACTIVE_COLUMN = "expense_log_is_active";

    public static final String TYPE_LOOKUP_TABLE = "type_lookup_table";
    public static final String TYPE_LOOKUP_ID_COLUMN_PK = "_id";
    public static final String TYPE_LOOKUP_LABEL_COLUMN = "type_lookup_category_label";
    public static final String TYPE_LOOKUP_DATE_CREATED_COLUMN = "type_lookup_date_created";
    public static final String TYPE_LOOKUP_LAST_UPDATE_COLUMN = "type_lookup_last_update";
    public static final String TYPE_LOOKUP_IS_ACTIVE_COLUMN = "type_lookup_is_active";

    public static final String TYPES_TABLE = "types_table";
    public static final String TYPES_ID_COLUMN_PK = "_id";
    public static final String TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK = "type_scategory_id";
    public static final String TYPES_LABEL_COLUMN = "types_lookup_label";
    public static final String TYPES_DATE_CREATED_COLUMN = "types_date_created";
    public static final String TYPES_LAST_UPDATE_COLUMN = "types_last_update";
    public static final String TYPES_IS_ACTIVE_COLUMN = "types_is_active";

    public static final String FREQUENCY_LOOKUP_TABLE = "frequency_lookup_table";
    public static final String FREQUENCY_LOOKUP_ID_COLUMN_PK = "_id";
    public static final String FREQUENCY_LOOKUP_TYPE_COLUMN = "frequency_lookup_type";
    public static final String FREQUENCY_LOOKUP_CREATED_COLUMN = "frequency_lookup_date_created";
    public static final String FREQUENCY_LOOKUP_LAST_UPDATE_COLUMN = "frequency_lookup_last_update";
    public static final String FREQUENCY_LOOKUP_IS_ACTIVE_COLUMN = "frequency_lookup_is_active";

    public static final String PAYMENT_DATE_TABLE = "payment_due_date_table";
    public static final String PAYMENT_DATE_ID_COLUMN_PK = "_id";
    public static final String PAYMENT_DATE_TYPE_ID_COLUMN_FK = "payment_due_date_type_id";
    public static final String PAYMENT_DATE_LABEL_COLUMN = "payment_due_date_label";
    public static final String PAYMENT_DATE_DATE_CREATED_COLUMN = "payment_due_date_date_created";
    public static final String PAYMENT_DATE_LAST_UPDATE_COLUMN = "payment_due_date_last_update";
    public static final String PAYMENT_DATE_IS_ACTIVE_COLUMN = "payment_due_date_is_active";

    //public static final String STATE_TABLE = "state_table";
    //public static final String STATE_ID_COLUMN_PK = "_id";
    //public static final String STATE_STATE_ABR_COLUMN = "state_state_abr";
    //public static final String STATE_DATE_CREATED_COLUMN = "state_date_created";
    //public static final String STATE_LAST_UPDATE_COLUMN = "state_last_update";
    //public static final String STATE_IS_ACTIVE_COLUMN = "state_is_active";

    public static final String APARTMENT_PICS_TABLE = "apartment_pics_table";
    public static final String APARTMENT_PICS_ID_COLUMN_PK = "_id";
    public static final String APARTMENT_PICS_USER_ID_COLUMN_FK = "apartment_pics_user_id";
    public static final String APARTMENT_PICS_APARTMENT_ID_COLUMN_FK = "apartment_pics_apartment_id";
    public static final String APARTMENT_PICS_PIC_COLUMN = "apartment_pics_pic";
    public static final String APARTMENT_PICS_DATE_CREATED_COLUMN = "apartment_pics_date_created";
    public static final String APARTMENT_PICS_LAST_UPDATED_COLUMN = "apartment_pics_last_update";
    public static final String APARTMENT_PICS_IS_ACTIVE_COLUMN = "appartment_pics_is_active";

    public static final String LEASE_TABLE = "lease_table";
    public static final String LEASE_START_DATE_COLUMN = "lease_start_date";
    public static final String LEASE_END_DATE_COLUMN = "lease_end_date";
    public static final String LEASE_PAYMENT_DAY_COLUMN_FK = "lease_payment_day";
    public static final String LEASE_MONTHLY_RENT_COST_COLUMN = "lease_monthly_rent_cost";
    public static final String LEASE_ID_COLUMN_PK = "_id";
    public static final String LEASE_USER_ID_COLUMN_FK = "lease_user_id";
    public static final String LEASE_APARTMENT_ID_COLUMN = "lease_apartment_id";
    public static final String LEASE_PRIMARY_TENANT_ID_COLUMN = "lease_primary_tenant";
    public static final String LEASE_DEPOSIT_AMOUNT_COLUMN = "lease_deposit";
    public static final String LEASE_PAYMENT_FREQUENCY_ID_FK = "lease_payment_frequency_id";
    public static final String LEASE_NOTES_COLUMN = "lease_notes";
    public static final String LEASE_DATE_CREATED_COLUMN = "lease_date_created";
    public static final String LEASE_LAST_UPDATED_COLUMN = "lease_last_update";
    public static final String LEASE_IS_ACTIVE_COLUMN = "lease_is_active";

    public static final String LEASE_SECONDARY_TENANTS_TABLE = "lease_secondary_tenants_table";
    public static final String LEASE_SECONDARY_TENANTS_ID_COLUMN_PK = "_id";
    public static final String LEASE_SECONDARY_TENANTS_TENANT_ID_COLUMN_FK = "lease_secondary_tenants_tenant_id";
    public static final String LEASE_SECONDARY_TENANTS_LEASE_ID_COLUMN_FK = "lease_secondary_tenants_lease_id";
    public static final String LEASE_SECONDARY_TENANTS_DATE_CREATED_COLUMN = "lease_secondary_tenants_date_created";
    public static final String LEASE_SECONDARY_TENANTS_LAST_UPDATED_COLUMN = "lease_secondary_tenants_last_update";
    public static final String LEASE_SECONDARY_TENANTS_IS_ACTIVE_COLUMN = "lease_secondary_tenants_is_active";

    public static final String LEASE_FREQUENCY_TABLE = "lease_frequency_table";
    public static final String LEASE_FREQUENCY_ID_COLUMN_PK = "_id";
    public static final String LEASE_FREQUENCY_TYPE_ID_COLUMN_FK = "lease_frequency_type_id";
    public static final String LEASE_FREQUENCY_STRING_COLUMN = "lease_frequency_string";
    public static final String LEASE_FREQUENCY_DATE_CREATED_COLUMN = "lease_frequency_date_created";
    public static final String LEASE_FREQUENCY_LAST_UPDATED_COLUMN = "lease_frequency_last_update";
    public static final String LEASE_FREQUENCY_IS_ACTIVE_COLUMN = "lease_frequency_is_active";

    public static final String APARTMENTS_VIEW = "apartments_view";
    public static final String APARTMENTS_VIEW_USER_ID = "user_id";
    public static final String APARTMENTS_VIEW_APARTMENT_ID = "apartment_id";
    public static final String APARTMENTS_VIEW_STREET_1 = "street_1";
    public static final String APARTMENTS_VIEW_STREET_2 = "street_2";
    public static final String APARTMENTS_VIEW_CITY = "city";
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
    public static final String TENANTS_VIEW_EMAIL = "email";
    public static final String TENANTS_VIEW_EMERGENCY_FIRST_NAME = "emergency_first_name";
    public static final String TENANTS_VIEW_EMERGENCY_LAST_NAME = "emergency_last_name";
    public static final String TENANTS_VIEW_EMERGENCY_PHONE = "emergency_phone";
    public static final String TENANTS_VIEW_DOES_TENANT_CURRENTLY_HAVE_LEASE = "does_tenant_currently_have_lease";
    public static final String TENANTS_VIEW_NOTES = "notes";
    public static final String TENANTS_VIEW_DATE_CREATED = "date_created";
    public static final String TENANTS_VIEW_LAST_UPDATE = "last_update";
    public static final String TENANTS_VIEW_IS_ACTIVE = "is_active";

    public static final String SECONDARY_TENANTS_VIEW = "secondary_tenants_view";
    public static final String SECONDARY_TENANTS_VIEW_LEASE_ID = "lease_id";
    public static final String SECONDARY_TENANTS_VIEW_TENANT_ID = "tenant_id";
    public static final String SECONDARY_TENANTS_VIEW_LEASE_START = "lease_start";
    public static final String SECONDARY_TENANTS_VIEW_LEASE_END = "lease_end";
    public static final String SECONDARY_TENANTS_VIEW_DATE_CREATED = "date_created";
    public static final String SECONDARY_TENANTS_VIEW_LAST_UPDATE = "last_update";
    public static final String SECONDARY_TENANTS_VIEW_IS_ACTIVE = "is_active";

    public static final String EXPENSES_VIEW = "expenses_view";
    public static final String EXPENSES_VIEW_EXPENSE_ID = "expense_id";
    public static final String EXPENSES_VIEW_USER_ID = "user_id";
    public static final String EXPENSES_VIEW_APARTMENT_ID = "apartment_id";
    public static final String EXPENSES_VIEW_LEASE_ID = "lease_id";
    public static final String EXPENSES_VIEW_TENANT_ID = "tenant_id";
    public static final String EXPENSES_VIEW_EXPENSE_DATE = "expense_date";
    public static final String EXPENSES_VIEW_AMOUNT = "expense_amount";
    public static final String EXPENSES_VIEW_WAS_PAID = "expense_was_paid";
    public static final String EXPENSES_VIEW_DESCRIPTION = "expense_description";
    public static final String EXPENSES_VIEW_TYPE_ID = "expense_type_id";
    public static final String EXPENSES_VIEW_TYPE_LABEL = "expense_type_label";
    public static final String EXPENSES_VIEW_RECEIPT_PIC = "receipt_pic";
    public static final String EXPENSES_VIEW_DATE_CREATED = "date_created";
    public static final String EXPENSES_VIEW_LAST_UPDATE = "last_update";
    public static final String EXPENSES_VIEW_IS_ACTIVE = "is_active";

    public static final String INCOME_VIEW = "income_view";
    public static final String INCOME_VIEW_INCOME_ID = "income_id";
    public static final String INCOME_VIEW_USER_ID = "user_id";
    public static final String INCOME_VIEW_APARTMENT_ID = "apartment_id";
    public static final String INCOME_VIEW_LEASE_ID = "lease_id";
    public static final String INCOME_VIEW_TENANT_ID = "tenant_id";
    public static final String INCOME_VIEW_INCOME_DATE = "income_date";
    public static final String INCOME_VIEW_AMOUNT = "income_amount";
    public static final String INCOME_VIEW_WAS_RECEIVED = "income_was_received";
    public static final String INCOME_VIEW_DESCRIPTION = "income_description";
    public static final String INCOME_VIEW_TYPE_ID = "income_type_id";
    public static final String INCOME_VIEW_TYPE_LABEL = "income_type_label";
    public static final String INCOME_VIEW_RECEIPT_PIC = "receipt_pic";
    public static final String INCOME_VIEW_DATE_CREATED = "date_created";
    public static final String INCOME_VIEW_LAST_UPDATE = "last_update";
    public static final String INCOME_VIEW_IS_ACTIVE = "is_active";

    private RandomNumberGenerator verificationGenerator;

    public DatabaseHandler(Context context) {
        super(context, DB_FILE_NAME, null, DATABASE_VERSION);
        verificationGenerator = new RandomNumberGenerator();
        this.context = context;
    }

    public void importBackupDB(File backup) {
        try {
            String currentDBPath = context.getDatabasePath(DatabaseHandler.DB_FILE_NAME).getAbsolutePath();
            File currentDB = new File(currentDBPath);
            Boolean deleted = currentDB.delete();
            if (deleted) {
                FileChannel src = new FileInputStream(backup).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context, R.string.backup_restored, Toast.LENGTH_LONG).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Add new user
    public void addUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_INFO_NAME_COLUMN, name);
        contentValues.put(USER_INFO_EMAIL_COLUMN, email);
        contentValues.put(USER_INFO_PASSWORD_COLUMN, password);
        db.insert(USER_INFO_TABLE, null, contentValues);
        db.close();
    }

    public void setUserInactive(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_INFO_IS_ACTIVE_COLUMN, 0);
        values.put(USER_INFO_LAST_UPDATE_COLUMN, " time('now') ");
        // updating row
        db.update(USER_INFO_TABLE, values, USER_INFO_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(user.getId())});
        db.close();
        db.close();
    }

    public boolean isUserTableEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM " + USER_INFO_TABLE;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
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
            User user = new User(id, name, email, password);
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
        values.put(USER_INFO_LAST_UPDATE_COLUMN, " time('now') ");
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
        String selection = USER_INFO_EMAIL_COLUMN + " = ?" + " AND " + USER_INFO_IS_ACTIVE_COLUMN + " = 1";
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
        String selection = USER_INFO_EMAIL_COLUMN + " = ?" + " AND " + USER_INFO_PASSWORD_COLUMN + " = ?" + " AND " + USER_INFO_IS_ACTIVE_COLUMN + " = 1";
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
        values.put(TENANT_INFO_LAST_UPDATE_COLUMN, " time('now') ");
        // updating row
        db.update(TENANT_INFO_TABLE, values, TENANT_INFO_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(tenant.getId())});
        db.close();
    }

    public void setTenantInactive(Tenant tenant) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TENANT_INFO_IS_ACTIVE_COLUMN, 0);
        values.put(TENANT_INFO_LAST_UPDATE_COLUMN, " time('now') ");
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
        values.put(APARTMENT_INFO_STATE_COLUMN, apartment.getState());
        values.put(APARTMENT_INFO_ZIP_COLUMN, apartment.getZip());
        values.put(APARTMENT_INFO_DESCRIPTION_COLUMN, apartment.getDescription());
        values.put(APARTMENT_INFO_NOTES_COLUMN, apartment.getNotes());
        if (apartment.getMainPic() != null) {
            if (!apartment.getMainPic().equals("")) {
                values.put(APARTMENT_INFO_MAIN_PIC_COLUMN, apartment.getMainPic());
            } else {
                values.putNull(APARTMENT_INFO_MAIN_PIC_COLUMN);
            }
        } else {
            values.putNull(APARTMENT_INFO_MAIN_PIC_COLUMN);
        }

        db.insert(APARTMENT_INFO_TABLE, null, values);

        if (apartment.getOtherPics() != null) {
            if (!apartment.getOtherPics().isEmpty()) {
                String query = "SELECT last_insert_rowid() FROM " + APARTMENT_INFO_TABLE;
                Cursor cursor = db.rawQuery(query, null);
                int id = 0;
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(0);
                }
                cursor.close();
                addApartmentOtherPics(id, apartment.getOtherPics(), db);
            }
        }

        db.close();
    }

    private void addApartmentOtherPics(int apartmentID, ArrayList<String> otherPics, SQLiteDatabase db) {
        if (apartmentID > 0) {
            for (int i = 0; i < otherPics.size(); i++) {
                ContentValues values = new ContentValues();
                values.put(APARTMENT_PICS_APARTMENT_ID_COLUMN_FK, apartmentID);
                values.put(APARTMENT_PICS_PIC_COLUMN, otherPics.get(i));
                db.insert(APARTMENT_PICS_TABLE, null, values);
            }
        }
    }

    public void addApartmentOtherPic(Apartment apartment, String otherPic, User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APARTMENT_PICS_APARTMENT_ID_COLUMN_FK, apartment.getId());
        values.put(APARTMENT_PICS_USER_ID_COLUMN_FK, user.getId());
        values.put(APARTMENT_PICS_PIC_COLUMN, otherPic);
        values.put(APARTMENT_PICS_LAST_UPDATED_COLUMN, " time('now') ");
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
        values.put(APARTMENT_INFO_STATE_COLUMN, apartment.getState());
        values.put(APARTMENT_INFO_ZIP_COLUMN, apartment.getZip());
        values.put(APARTMENT_INFO_DESCRIPTION_COLUMN, apartment.getDescription());
        values.put(APARTMENT_INFO_NOTES_COLUMN, apartment.getNotes());
        if (apartment.getMainPic() != null) {
            values.put(APARTMENT_INFO_MAIN_PIC_COLUMN, apartment.getMainPic());
        } else {
            values.putNull(APARTMENT_INFO_MAIN_PIC_COLUMN);
        }
        values.put(APARTMENT_INFO_LAST_UPDATE_COLUMN, " time('now') ");
        // updating row
        db.update(APARTMENT_INFO_TABLE, values, APARTMENT_INFO_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(apartment.getId())});
        db.close();
    }

    public void setApartmentInactive(Apartment apartment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(APARTMENT_INFO_IS_ACTIVE_COLUMN, 0);
        values.put(APARTMENT_INFO_LAST_UPDATE_COLUMN, " time('now') ");
        // updating row
        db.update(APARTMENT_INFO_TABLE, values, APARTMENT_INFO_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(apartment.getId())});
        db.close();
    }

    public void changeApartmentMainPic(Apartment apartment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APARTMENT_INFO_MAIN_PIC_COLUMN, apartment.getMainPic());
        values.put(APARTMENT_INFO_LAST_UPDATE_COLUMN, " time('now') ");
        db.update(APARTMENT_INFO_TABLE, values, APARTMENT_INFO_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(apartment.getId())});
        db.close();
    }

    public void removeApartmentMainPic(Apartment apartment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull(APARTMENT_INFO_MAIN_PIC_COLUMN);
        values.put(APARTMENT_INFO_LAST_UPDATE_COLUMN, " time('now') ");
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
                String pic = cursor.getString(cursor.getColumnIndex(APARTMENT_PICS_PIC_COLUMN));
                otherPics.add(pic);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return otherPics;
    }

    public void changePaymentLogReceiptPic(PaymentLogEntry paymentLogEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PAYMENT_LOG_RECEIPT_PIC, paymentLogEntry.getReceiptPic());
        values.put(PAYMENT_LOG_LAST_UPDATE_COLUMN, " time('now') ");
        db.update(PAYMENT_LOG_TABLE, values, PAYMENT_LOG_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(paymentLogEntry.getId())});
        db.close();
    }

    public void removePaymentLogReceiptPic(PaymentLogEntry paymentLogEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull(PAYMENT_LOG_RECEIPT_PIC);
        values.put(PAYMENT_LOG_LAST_UPDATE_COLUMN, " time('now') ");
        db.update(PAYMENT_LOG_TABLE, values, PAYMENT_LOG_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(paymentLogEntry.getId())});
        db.close();
    }

    //Add payment log entry
    public void addPaymentLogEntry(PaymentLogEntry ple, int userID) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        values.put(PAYMENT_LOG_USER_ID_COLUMN_FK, userID);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        if (ple.getDate() != null) {
            String paymentDayString = formatter.format(ple.getDate());
            values.put(PAYMENT_LOG_PAYMENT_DATE_COLUMN, paymentDayString);
        } else {
            values.putNull(PAYMENT_LOG_PAYMENT_DATE_COLUMN);
        }
        values.put(PAYMENT_LOG_TYPE_ID_COLUMN_FK, ple.getTypeID());
        if (ple.getTenantID() != 0) {
            values.put(PAYMENT_LOG_TENANT_ID_COLUMN_FK, ple.getTenantID());
        } else {
            values.putNull(PAYMENT_LOG_TENANT_ID_COLUMN_FK);
        }
        if (ple.getApartmentID() != 0) {
            values.put(PAYMENT_LOG_APARTMENT_ID_COLUMN_FK, ple.getApartmentID());
        } else {
            values.putNull(PAYMENT_LOG_APARTMENT_ID_COLUMN_FK);
        }
        if (ple.getLeaseID() != 0) {
            values.put(PAYMENT_LOG_LEASE_ID_COLUMN_FK, ple.getLeaseID());
        } else {
            values.putNull(PAYMENT_LOG_LEASE_ID_COLUMN_FK);
        }
        values.put(PAYMENT_LOG_WAS_RECEIVED_COLUMN, ple.getIsCompleted());
        values.put(PAYMENT_LOG_AMOUNT_COLUMN, ple.getAmount().multiply(new BigDecimal(100)).toPlainString());
        values.put(PAYMENT_LOG_DESCRIPTION_COLUMN, ple.getDescription());
        if (ple.getReceiptPic() != null) {
            if (!ple.getReceiptPic().equals("")) {
                values.put(PAYMENT_LOG_RECEIPT_PIC, ple.getReceiptPic());
            } else {
                values.putNull(PAYMENT_LOG_RECEIPT_PIC);
            }
        } else {
            values.putNull(PAYMENT_LOG_RECEIPT_PIC);
        }
        db.insert(PAYMENT_LOG_TABLE, null, values);
        db.close();
    }

    public float[] getIncomeTotalsForLineGraph(User user, Date startRange, Date endRange) {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startRange);
        String endDateString = formatter.format(endRange);
        String Query = "Select * from " + INCOME_VIEW +
                " WHERE " + INCOME_VIEW_USER_ID + " = " + user.getId() +
                " AND " + INCOME_VIEW_IS_ACTIVE + " = 1" +
                " AND " + INCOME_VIEW_INCOME_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + INCOME_VIEW_INCOME_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        BigDecimal jan = new BigDecimal(0);
        BigDecimal feb = new BigDecimal(0);
        BigDecimal mar = new BigDecimal(0);
        BigDecimal apr = new BigDecimal(0);
        BigDecimal may = new BigDecimal(0);
        BigDecimal jun = new BigDecimal(0);
        BigDecimal jul = new BigDecimal(0);
        BigDecimal aug = new BigDecimal(0);
        BigDecimal sep = new BigDecimal(0);
        BigDecimal oct = new BigDecimal(0);
        BigDecimal nov = new BigDecimal(0);
        BigDecimal dec = new BigDecimal(0);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String expenseDateString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_INCOME_DATE));
                Date expenseDate = null;
                try {
                    expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (expenseDate != null && amount != null) {
                    String monthNumber = (String) DateFormat.format("MM", expenseDate);
                    switch (monthNumber) {
                        case "01":
                            jan = jan.add(amount);
                            break;

                        case "02":
                            feb = feb.add(amount);
                            break;

                        case "03":
                            mar = mar.add(amount);
                            break;

                        case "04":
                            apr = apr.add(amount);
                            break;

                        case "05":
                            may = may.add(amount);
                            break;

                        case "06":
                            jun = jun.add(amount);
                            break;

                        case "07":
                            jul = jul.add(amount);
                            break;

                        case "08":
                            aug = aug.add(amount);
                            break;

                        case "09":
                            sep = sep.add(amount);
                            break;

                        case "10":
                            oct = oct.add(amount);
                            break;

                        case "11":
                            nov = nov.add(amount);
                            break;

                        case "12":
                            dec = dec.add(amount);
                            break;

                        default:
                            break;
                    }
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return new float[]{jan.floatValue(), feb.floatValue(), mar.floatValue(), apr.floatValue(), may.floatValue(), jun.floatValue(),
                jul.floatValue(), aug.floatValue(), sep.floatValue(), oct.floatValue(), nov.floatValue(), dec.floatValue()};
    }


    public float[] getIncomeTotalsForLineGraphOnlyReceived(User user, Date startRange, Date endRange) {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startRange);
        String endDateString = formatter.format(endRange);
        String Query = "Select * from " + INCOME_VIEW +
                " WHERE " + INCOME_VIEW_USER_ID + " = " + user.getId() +
                " AND " + INCOME_VIEW_WAS_RECEIVED + " = 1" +
                " AND " + INCOME_VIEW_IS_ACTIVE + " = 1" +
                " AND " + INCOME_VIEW_INCOME_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + INCOME_VIEW_INCOME_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        BigDecimal jan = new BigDecimal(0);
        BigDecimal feb = new BigDecimal(0);
        BigDecimal mar = new BigDecimal(0);
        BigDecimal apr = new BigDecimal(0);
        BigDecimal may = new BigDecimal(0);
        BigDecimal jun = new BigDecimal(0);
        BigDecimal jul = new BigDecimal(0);
        BigDecimal aug = new BigDecimal(0);
        BigDecimal sep = new BigDecimal(0);
        BigDecimal oct = new BigDecimal(0);
        BigDecimal nov = new BigDecimal(0);
        BigDecimal dec = new BigDecimal(0);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String expenseDateString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_INCOME_DATE));
                Date expenseDate = null;
                try {
                    expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (expenseDate != null && amount != null) {
                    String monthNumber = (String) DateFormat.format("MM", expenseDate);
                    switch (monthNumber) {
                        case "01":
                            jan = jan.add(amount);
                            break;

                        case "02":
                            feb = feb.add(amount);
                            break;

                        case "03":
                            mar = mar.add(amount);
                            break;

                        case "04":
                            apr = apr.add(amount);
                            break;

                        case "05":
                            may = may.add(amount);
                            break;

                        case "06":
                            jun = jun.add(amount);
                            break;

                        case "07":
                            jul = jul.add(amount);
                            break;

                        case "08":
                            aug = aug.add(amount);
                            break;

                        case "09":
                            sep = sep.add(amount);
                            break;

                        case "10":
                            oct = oct.add(amount);
                            break;

                        case "11":
                            nov = nov.add(amount);
                            break;

                        case "12":
                            dec = dec.add(amount);
                            break;

                        default:
                            break;
                    }
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return new float[]{jan.floatValue(), feb.floatValue(), mar.floatValue(), apr.floatValue(), may.floatValue(), jun.floatValue(),
                jul.floatValue(), aug.floatValue(), sep.floatValue(), oct.floatValue(), nov.floatValue(), dec.floatValue()};
    }

    public void addPaymentLogEntryArray(ArrayList<PaymentLogEntry> pleArray, int userID) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        for (int i = 0; i < pleArray.size(); i++) {
            PaymentLogEntry ple = pleArray.get(i);
            values.put(PAYMENT_LOG_USER_ID_COLUMN_FK, userID);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            if (ple.getDate() != null) {
                String paymentDayString = formatter.format(ple.getDate());
                values.put(PAYMENT_LOG_PAYMENT_DATE_COLUMN, paymentDayString);
            } else {
                values.putNull(PAYMENT_LOG_PAYMENT_DATE_COLUMN);
            }
            values.put(PAYMENT_LOG_TYPE_ID_COLUMN_FK, ple.getTypeID());
            values.put(PAYMENT_LOG_TENANT_ID_COLUMN_FK, ple.getTenantID());
            values.put(PAYMENT_LOG_APARTMENT_ID_COLUMN_FK, ple.getApartmentID());
            values.put(PAYMENT_LOG_LEASE_ID_COLUMN_FK, ple.getLeaseID());
            values.put(PAYMENT_LOG_AMOUNT_COLUMN, ple.getAmount().multiply(new BigDecimal(100)).toPlainString());
            values.put(PAYMENT_LOG_DESCRIPTION_COLUMN, ple.getDescription());
            if (ple.getReceiptPic() != null) {
                if (!ple.getReceiptPic().equals("")) {
                    values.put(PAYMENT_LOG_RECEIPT_PIC, ple.getReceiptPic());
                } else {
                    values.putNull(PAYMENT_LOG_RECEIPT_PIC);
                }
            } else {
                values.putNull(PAYMENT_LOG_RECEIPT_PIC);
            }
            db.insert(PAYMENT_LOG_TABLE, null, values);
        }
        db.close();
    }

    public void editPaymentLogEntry(PaymentLogEntry ple) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        if (ple.getDate() != null) {
            String paymentDayString = formatter.format(ple.getDate());
            values.put(PAYMENT_LOG_PAYMENT_DATE_COLUMN, paymentDayString);
        } else {
            values.putNull(PAYMENT_LOG_PAYMENT_DATE_COLUMN);
        }
        if (ple.getReceiptPic() != null) {
            values.put(PAYMENT_LOG_RECEIPT_PIC, ple.getReceiptPic());
        } else {
            values.putNull(PAYMENT_LOG_RECEIPT_PIC);
        }
        if (ple.getTenantID() != 0) {
            values.put(PAYMENT_LOG_TENANT_ID_COLUMN_FK, ple.getTenantID());
        } else {
            values.putNull(PAYMENT_LOG_TENANT_ID_COLUMN_FK);
        }
        if (ple.getApartmentID() != 0) {
            values.put(PAYMENT_LOG_APARTMENT_ID_COLUMN_FK, ple.getApartmentID());
        } else {
            values.putNull(PAYMENT_LOG_APARTMENT_ID_COLUMN_FK);
        }
        if (ple.getLeaseID() != 0) {
            values.put(PAYMENT_LOG_LEASE_ID_COLUMN_FK, ple.getLeaseID());
        } else {
            values.putNull(PAYMENT_LOG_LEASE_ID_COLUMN_FK);
        }
        values.put(PAYMENT_LOG_AMOUNT_COLUMN, ple.getAmount().multiply(new BigDecimal(100)).toPlainString());
        values.put(PAYMENT_LOG_TYPE_ID_COLUMN_FK, ple.getTypeID());
        values.put(PAYMENT_LOG_WAS_RECEIVED_COLUMN, ple.getIsCompleted());
        values.put(PAYMENT_LOG_DESCRIPTION_COLUMN, ple.getDescription());
        values.put(PAYMENT_LOG_LAST_UPDATE_COLUMN, " time('now') ");
        // updating row
        db.update(PAYMENT_LOG_TABLE, values, PAYMENT_LOG_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(ple.getId())});
        db.close();
    }

    public void setPaymentLogEntryInactive(PaymentLogEntry ple) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PAYMENT_LOG_IS_ACTIVE_COLUMN, 0);
        values.put(PAYMENT_LOG_LAST_UPDATE_COLUMN, " time('now') ");
        // updating row
        db.update(PAYMENT_LOG_TABLE, values, PAYMENT_LOG_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(ple.getId())});
        db.close();
    }

    public PaymentLogEntry getPaymentLogEntryByID(int id, User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + INCOME_VIEW + " WHERE " + INCOME_VIEW_INCOME_ID + " = " + id + " AND " +
                INCOME_VIEW_USER_ID + " = " + user.getId() + " AND " +
                INCOME_VIEW_IS_ACTIVE + " = 1 LIMIT 1";
        Cursor cursor = db.rawQuery(Query, null);
        PaymentLogEntry ple = null;
        if (cursor.moveToFirst()) {
            int pleID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_INCOME_ID));
            String incomeDateString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_INCOME_DATE));
            Date incomeDate = null;
            try {
                incomeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(incomeDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
            BigDecimal amount = new BigDecimal(amountString);
            amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
            boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_WAS_RECEIVED)) > 0;
            int tenantID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TENANT_ID));
            int leaseID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_LEASE_ID));
            int apartmentID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_APARTMENT_ID));
            String description = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_DESCRIPTION));
            int typeID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TYPE_ID));
            String typeLabel = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_TYPE_LABEL));
            String receiptPic = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_RECEIPT_PIC));
            ple = new PaymentLogEntry(pleID, incomeDate, typeID, typeLabel, tenantID, leaseID, apartmentID, amount, description, receiptPic, wasCompleted);

        }
        cursor.close();
        db.close();
        return ple;
    }

    public void changeExpenseLogReceiptPic(ExpenseLogEntry expenseLogEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EXPENSE_LOG_RECEIPT_PIC, expenseLogEntry.getReceiptPic());
        values.put(EXPENSE_LOG_LAST_UPDATE_COLUMN, " time('now') ");
        db.update(EXPENSE_LOG_TABLE, values, EXPENSE_LOG_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(expenseLogEntry.getId())});
        db.close();
    }

    public void removeExpenseLogReceiptPic(ExpenseLogEntry expenseLogEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull(EXPENSE_LOG_RECEIPT_PIC);
        values.put(EXPENSE_LOG_LAST_UPDATE_COLUMN, " time('now') ");
        db.update(EXPENSE_LOG_TABLE, values, EXPENSE_LOG_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(expenseLogEntry.getId())});
        db.close();
    }

    //Add expense log entry
    public void addExpenseLogEntry(ExpenseLogEntry ele, int userID) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        values.put(EXPENSE_LOG_USER_ID_COLUMN_FK, userID);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        if (ele.getDate() != null) {
            String expenseDayString = formatter.format(ele.getDate());
            values.put(EXPENSE_LOG_EXPENSE_DATE_COLUMN, expenseDayString);
        } else {
            values.putNull(EXPENSE_LOG_EXPENSE_DATE_COLUMN);
        }
        values.put(EXPENSE_LOG_AMOUNT_COLUMN, ele.getAmount().multiply(new BigDecimal(100)).toPlainString());
        if (ele.getApartmentID() != 0) {
            values.put(EXPENSE_LOG_APARTMENT_ID_COLUMN_FK, ele.getApartmentID());
        } else {
            values.putNull(EXPENSE_LOG_APARTMENT_ID_COLUMN_FK);
        }
        if (ele.getTenantID() != 0) {
            values.put(EXPENSE_LOG_TENANT_ID_COLUMN_FK, ele.getTenantID());
        } else {
            values.putNull(EXPENSE_LOG_TENANT_ID_COLUMN_FK);
        }
        if (ele.getLeaseID() != 0) {
            values.put(EXPENSE_LOG_LEASE_ID_COLUMN_FK, ele.getLeaseID());
        } else {
            values.putNull(EXPENSE_LOG_LEASE_ID_COLUMN_FK);
        }
        values.put(EXPENSE_LOG_DESCRIPTION_COLUMN, ele.getDescription());
        values.put(EXPENSE_LOG_TYPE_ID_COLUMN_FK, ele.getTypeID());
        values.put(EXPENSE_LOG_WAS_PAID_COLUMN, ele.getIsCompleted());
        if (ele.getReceiptPic() != null) {
            if (!ele.getReceiptPic().equals("")) {
                values.put(EXPENSE_LOG_RECEIPT_PIC, ele.getReceiptPic());
            } else {
                values.putNull(EXPENSE_LOG_RECEIPT_PIC);
            }
        } else {
            values.putNull(EXPENSE_LOG_RECEIPT_PIC);
        }
        db.insert(EXPENSE_LOG_TABLE, null, values);
        db.close();
    }

    public void editExpenseLogEntry(ExpenseLogEntry ele) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        if (ele.getDate() != null) {
            String expenseDayString = formatter.format(ele.getDate());
            values.put(EXPENSE_LOG_EXPENSE_DATE_COLUMN, expenseDayString);
        } else {
            values.putNull(EXPENSE_LOG_EXPENSE_DATE_COLUMN);
        }
        if (ele.getReceiptPic() != null) {
            values.put(EXPENSE_LOG_RECEIPT_PIC, ele.getReceiptPic());
        } else {
            values.putNull(EXPENSE_LOG_RECEIPT_PIC);
        }
        values.put(EXPENSE_LOG_AMOUNT_COLUMN, ele.getAmount().multiply(new BigDecimal(100)).toPlainString());
        if (ele.getApartmentID() != 0) {
            values.put(EXPENSE_LOG_APARTMENT_ID_COLUMN_FK, ele.getApartmentID());
        } else {
            values.putNull(EXPENSE_LOG_APARTMENT_ID_COLUMN_FK);
        }
        if (ele.getTenantID() != 0) {
            values.put(EXPENSE_LOG_TENANT_ID_COLUMN_FK, ele.getTenantID());
        } else {
            values.putNull(EXPENSE_LOG_TENANT_ID_COLUMN_FK);
        }
        if (ele.getLeaseID() != 0) {
            values.put(EXPENSE_LOG_LEASE_ID_COLUMN_FK, ele.getLeaseID());
        } else {
            values.putNull(EXPENSE_LOG_LEASE_ID_COLUMN_FK);
        }
        values.put(EXPENSE_LOG_TYPE_ID_COLUMN_FK, ele.getTypeID());
        values.put(EXPENSE_LOG_DESCRIPTION_COLUMN, ele.getDescription());
        values.put(EXPENSE_LOG_WAS_PAID_COLUMN, ele.getIsCompleted());
        values.put(EXPENSE_LOG_LAST_UPDATE_COLUMN, " time('now') ");
        // updating row
        db.update(EXPENSE_LOG_TABLE, values, EXPENSE_LOG_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(ele.getId())});
        db.close();
    }

    public void setExpenseInactive(ExpenseLogEntry ele) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EXPENSE_LOG_IS_ACTIVE_COLUMN, 0);
        values.put(EXPENSE_LOG_LAST_UPDATE_COLUMN, " time('now') ");
        // updating row
        db.update(EXPENSE_LOG_TABLE, values, EXPENSE_LOG_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(ele.getId())});
        db.close();
    }


    public ExpenseLogEntry getExpenseLogEntryByID(int id, User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + EXPENSES_VIEW + " WHERE " + EXPENSES_VIEW_EXPENSE_ID + " = " + id + " AND " +
                EXPENSES_VIEW_USER_ID + " = " + user.getId() + " AND " +
                EXPENSES_VIEW_IS_ACTIVE + " = 1 LIMIT 1";
        Cursor cursor = db.rawQuery(Query, null);
        ExpenseLogEntry ele = null;
        if (cursor.moveToFirst()) {
            int eleID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_ID));
            String expenseDateString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_DATE));
            Date expenseDate = null;
            try {
                expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
            BigDecimal amount = new BigDecimal(amountString);
            amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
            int apartmentID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_APARTMENT_ID));
            int tenantID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TENANT_ID));
            int leaseID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_LEASE_ID));
            String description = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_DESCRIPTION));
            int typeID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_ID));
            String typeLabel = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_LABEL));
            String receiptPic = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_RECEIPT_PIC));
            boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_WAS_PAID)) > 0;
            ele = new ExpenseLogEntry(eleID, expenseDate, amount, apartmentID, leaseID, tenantID, description, typeID, typeLabel, receiptPic, wasCompleted);
        }
        cursor.close();
        db.close();
        return ele;
    }

    public float[] getExpenseTotalsForLineGraph(User user, Date startRange, Date endRange) {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startRange);
        String endDateString = formatter.format(endRange);
        String Query = "Select * from " + EXPENSES_VIEW +
                " WHERE " + EXPENSES_VIEW_USER_ID + " = " + user.getId() +
                " AND " + EXPENSES_VIEW_IS_ACTIVE + " = 1" +
                " AND " + EXPENSES_VIEW_EXPENSE_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + EXPENSES_VIEW_EXPENSE_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        BigDecimal jan = new BigDecimal(0);
        BigDecimal feb = new BigDecimal(0);
        BigDecimal mar = new BigDecimal(0);
        BigDecimal apr = new BigDecimal(0);
        BigDecimal may = new BigDecimal(0);
        BigDecimal jun = new BigDecimal(0);
        BigDecimal jul = new BigDecimal(0);
        BigDecimal aug = new BigDecimal(0);
        BigDecimal sep = new BigDecimal(0);
        BigDecimal oct = new BigDecimal(0);
        BigDecimal nov = new BigDecimal(0);
        BigDecimal dec = new BigDecimal(0);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(-100), BigDecimal.ROUND_FLOOR);
                String expenseDateString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_DATE));
                Date expenseDate = null;
                try {
                    expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (expenseDate != null && amount != null) {
                    String monthNumber = (String) DateFormat.format("MM", expenseDate);
                    switch (monthNumber) {
                        case "01":
                            jan = jan.add(amount);
                            break;

                        case "02":
                            feb = feb.add(amount);
                            break;

                        case "03":
                            mar = mar.add(amount);
                            break;

                        case "04":
                            apr = apr.add(amount);
                            break;

                        case "05":
                            may = may.add(amount);
                            break;

                        case "06":
                            jun = jun.add(amount);
                            break;

                        case "07":
                            jul = jul.add(amount);
                            break;

                        case "08":
                            aug = aug.add(amount);
                            break;

                        case "09":
                            sep = sep.add(amount);
                            break;

                        case "10":
                            oct = oct.add(amount);
                            break;

                        case "11":
                            nov = nov.add(amount);
                            break;

                        case "12":
                            dec = dec.add(amount);
                            break;

                        default:
                            break;
                    }
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return new float[]{jan.floatValue(), feb.floatValue(), mar.floatValue(), apr.floatValue(), may.floatValue(), jun.floatValue(),
                jul.floatValue(), aug.floatValue(), sep.floatValue(), oct.floatValue(), nov.floatValue(), dec.floatValue()};
    }

    public float[] getExpenseTotalsForLineGraphOnlyPaid(User user, Date startRange, Date endRange) {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startRange);
        String endDateString = formatter.format(endRange);
        String Query = "Select * from " + EXPENSES_VIEW +
                " WHERE " + EXPENSES_VIEW_USER_ID + " = " + user.getId() +
                " AND " + EXPENSES_VIEW_WAS_PAID + " = 1" +
                " AND " + EXPENSES_VIEW_IS_ACTIVE + " = 1" +
                " AND " + EXPENSES_VIEW_EXPENSE_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + EXPENSES_VIEW_EXPENSE_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        BigDecimal jan = new BigDecimal(0);
        BigDecimal feb = new BigDecimal(0);
        BigDecimal mar = new BigDecimal(0);
        BigDecimal apr = new BigDecimal(0);
        BigDecimal may = new BigDecimal(0);
        BigDecimal jun = new BigDecimal(0);
        BigDecimal jul = new BigDecimal(0);
        BigDecimal aug = new BigDecimal(0);
        BigDecimal sep = new BigDecimal(0);
        BigDecimal oct = new BigDecimal(0);
        BigDecimal nov = new BigDecimal(0);
        BigDecimal dec = new BigDecimal(0);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(-100), BigDecimal.ROUND_FLOOR);
                String expenseDateString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_DATE));
                Date expenseDate = null;
                try {
                    expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (expenseDate != null && amount != null) {
                    String monthNumber = (String) DateFormat.format("MM", expenseDate);
                    switch (monthNumber) {
                        case "01":
                            jan = jan.add(amount);
                            break;

                        case "02":
                            feb = feb.add(amount);
                            break;

                        case "03":
                            mar = mar.add(amount);
                            break;

                        case "04":
                            apr = apr.add(amount);
                            break;

                        case "05":
                            may = may.add(amount);
                            break;

                        case "06":
                            jun = jun.add(amount);
                            break;

                        case "07":
                            jul = jul.add(amount);
                            break;

                        case "08":
                            aug = aug.add(amount);
                            break;

                        case "09":
                            sep = sep.add(amount);
                            break;

                        case "10":
                            oct = oct.add(amount);
                            break;

                        case "11":
                            nov = nov.add(amount);
                            break;

                        case "12":
                            dec = dec.add(amount);
                            break;

                        default:
                            break;
                    }
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return new float[]{jan.floatValue(), feb.floatValue(), mar.floatValue(), apr.floatValue(), may.floatValue(), jun.floatValue(),
                jul.floatValue(), aug.floatValue(), sep.floatValue(), oct.floatValue(), nov.floatValue(), dec.floatValue()};
    }

    public void addLease(Lease lease, int userID) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        values.put(LEASE_USER_ID_COLUMN_FK, userID);
        values.put(LEASE_PRIMARY_TENANT_ID_COLUMN, lease.getPrimaryTenantID());
        values.put(LEASE_APARTMENT_ID_COLUMN, lease.getApartmentID());
        values.put(LEASE_PAYMENT_DAY_COLUMN_FK, lease.getPaymentDayID());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        if (lease.getLeaseStart() != null) {
            String leaseStartString = formatter.format(lease.getLeaseStart());
            values.put(LEASE_START_DATE_COLUMN, leaseStartString);
        } else {
            values.putNull(LEASE_START_DATE_COLUMN);
        }
        if (lease.getLeaseEnd() != null) {
            String leaseEndString = formatter.format(lease.getLeaseEnd());
            values.put(LEASE_END_DATE_COLUMN, leaseEndString);
        } else {
            values.putNull(LEASE_END_DATE_COLUMN);
        }
        values.put(LEASE_MONTHLY_RENT_COST_COLUMN, lease.getMonthlyRentCost().multiply(new BigDecimal(100)).toPlainString());
        values.put(LEASE_DEPOSIT_AMOUNT_COLUMN, lease.getDeposit().multiply(new BigDecimal(100)).toPlainString());
        values.put(LEASE_PAYMENT_FREQUENCY_ID_FK, lease.getPaymentFrequencyID());
        values.put(LEASE_NOTES_COLUMN, lease.getNotes());
        db.insert(LEASE_TABLE, null, values);
        String query = "SELECT last_insert_rowid() FROM " + LEASE_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        int id = 0;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        addSecondaryTenantsToLease(id, lease.getSecondaryTenantIDs(), db);
        db.close();
    }

    public int addLeaseAndReturnID(Lease lease, int userID) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        values.put(LEASE_USER_ID_COLUMN_FK, userID);
        values.put(LEASE_PRIMARY_TENANT_ID_COLUMN, lease.getPrimaryTenantID());
        values.put(LEASE_APARTMENT_ID_COLUMN, lease.getApartmentID());
        values.put(LEASE_PAYMENT_DAY_COLUMN_FK, lease.getPaymentDayID());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        if (lease.getLeaseStart() != null) {
            String leaseStartString = formatter.format(lease.getLeaseStart());
            values.put(LEASE_START_DATE_COLUMN, leaseStartString);
        } else {
            values.putNull(LEASE_START_DATE_COLUMN);
        }
        if (lease.getLeaseEnd() != null) {
            String leaseEndString = formatter.format(lease.getLeaseEnd());
            values.put(LEASE_END_DATE_COLUMN, leaseEndString);
        } else {
            values.putNull(LEASE_END_DATE_COLUMN);
        }
        values.put(LEASE_MONTHLY_RENT_COST_COLUMN, lease.getMonthlyRentCost().multiply(new BigDecimal(100)).toPlainString());
        values.put(LEASE_DEPOSIT_AMOUNT_COLUMN, lease.getDeposit().multiply(new BigDecimal(100)).toPlainString());
        values.put(LEASE_PAYMENT_FREQUENCY_ID_FK, lease.getPaymentFrequencyID());
        values.put(LEASE_NOTES_COLUMN, lease.getNotes());
        db.insert(LEASE_TABLE, null, values);
        String query = "SELECT last_insert_rowid() FROM " + LEASE_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        int id = 0;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        addSecondaryTenantsToLease(id, lease.getSecondaryTenantIDs(), db);
        db.close();
        return id;
    }

    private void addSecondaryTenantsToLease(int leaseID, ArrayList<Integer> secondaryTenantIDs, SQLiteDatabase db) {
        if (leaseID > 0) {
            for (int i = 0; i < secondaryTenantIDs.size(); i++) {
                ContentValues values = new ContentValues();
                values.put(LEASE_SECONDARY_TENANTS_LEASE_ID_COLUMN_FK, leaseID);
                values.put(LEASE_SECONDARY_TENANTS_TENANT_ID_COLUMN_FK, secondaryTenantIDs.get(i));
                db.insert(LEASE_SECONDARY_TENANTS_TABLE, null, values);
            }
        }
    }

    public void editLease(Lease lease) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        values.put(LEASE_PRIMARY_TENANT_ID_COLUMN, lease.getPrimaryTenantID());
        values.put(LEASE_APARTMENT_ID_COLUMN, lease.getApartmentID());
        values.put(LEASE_PAYMENT_DAY_COLUMN_FK, lease.getPaymentDayID());
        if (lease.getLeaseStart() != null) {
            String leaseStartString = formatter.format(lease.getLeaseStart());
            values.put(LEASE_START_DATE_COLUMN, leaseStartString);
        } else {
            values.putNull(LEASE_START_DATE_COLUMN);
        }
        if (lease.getLeaseEnd() != null) {
            String leaseEndString = formatter.format(lease.getLeaseEnd());
            values.put(LEASE_END_DATE_COLUMN, leaseEndString);
        } else {
            values.putNull(LEASE_END_DATE_COLUMN);
        }
        values.put(LEASE_MONTHLY_RENT_COST_COLUMN, lease.getMonthlyRentCost().multiply(new BigDecimal(100)).toPlainString());
        values.put(LEASE_DEPOSIT_AMOUNT_COLUMN, lease.getDeposit().multiply(new BigDecimal(100)).toPlainString());
        values.put(LEASE_PAYMENT_FREQUENCY_ID_FK, lease.getPaymentFrequencyID());
        values.put(LEASE_NOTES_COLUMN, lease.getNotes());
        values.put(LEASE_LAST_UPDATED_COLUMN, " time('now') ");
        // updating row
        db.update(LEASE_TABLE, values, LEASE_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(lease.getId())});
        editSecondaryTenantToLease(lease.getId(), lease.getSecondaryTenantIDs(), db);
        db.close();
    }

    private void editSecondaryTenantToLease(int leaseID, ArrayList<Integer> secondaryTenantIDs, SQLiteDatabase db) {
        if (leaseID > 0) {
            db.delete(LEASE_SECONDARY_TENANTS_TABLE, LEASE_SECONDARY_TENANTS_LEASE_ID_COLUMN_FK + " = ? ",
                    new String[]{String.valueOf(leaseID)});
            for (int i = 0; i < secondaryTenantIDs.size(); i++) {
                ContentValues values = new ContentValues();
                values.put(LEASE_SECONDARY_TENANTS_LEASE_ID_COLUMN_FK, leaseID);
                values.put(LEASE_SECONDARY_TENANTS_TENANT_ID_COLUMN_FK, secondaryTenantIDs.get(i));
                db.insert(LEASE_SECONDARY_TENANTS_TABLE, null, values);
            }
        }
    }

    //Gets a users active tenants
    public ArrayList<Tenant> getUsersTenants(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Tenant> tenants = new ArrayList<>();
        String Query = "Select * from " + TENANTS_VIEW +
                " WHERE " + TENANTS_VIEW_USER_ID + " = " + user.getId() + " AND " + TENANTS_VIEW_IS_ACTIVE + " = 1" +
                " ORDER BY " + TENANTS_VIEW_DOES_TENANT_CURRENTLY_HAVE_LEASE + " DESC, " +
                " UPPER(" + TENANTS_VIEW_FIRST_NAME + ")" + ", " +
                " UPPER(" + TENANTS_VIEW_LAST_NAME + ")";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(TENANTS_VIEW_TENANT_ID));
                String firstName = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_LAST_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_PHONE));
                String email = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_EMAIL));
                String emergencyFirstName = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_EMERGENCY_FIRST_NAME));
                String emergencyLastName = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_EMERGENCY_LAST_NAME));
                String emergencyPhone = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_EMERGENCY_PHONE));
                boolean hasLease = cursor.getInt(cursor.getColumnIndex(TENANTS_VIEW_DOES_TENANT_CURRENTLY_HAVE_LEASE)) > 0;
                String notes = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_NOTES));
                tenants.add(new Tenant(id, firstName, lastName, phone, email, emergencyFirstName, emergencyLastName, emergencyPhone, hasLease, notes, true));
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

    public ArrayList<Tenant> getUsersTenantsIncludingInactive(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Tenant> tenants = new ArrayList<>();
        String Query = "Select * from " + TENANTS_VIEW +
                " WHERE " + TENANTS_VIEW_USER_ID + " = " + user.getId() +
                " ORDER BY " + TENANTS_VIEW_DOES_TENANT_CURRENTLY_HAVE_LEASE + " DESC, " +
                " UPPER(" + TENANTS_VIEW_FIRST_NAME + ")" + ", " +
                " UPPER(" + TENANTS_VIEW_LAST_NAME + ")";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(TENANTS_VIEW_TENANT_ID));
                String firstName = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_LAST_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_PHONE));
                String email = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_EMAIL));
                String emergencyFirstName = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_EMERGENCY_FIRST_NAME));
                String emergencyLastName = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_EMERGENCY_LAST_NAME));
                String emergencyPhone = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_EMERGENCY_PHONE));
                Boolean hasLease = cursor.getInt(cursor.getColumnIndex(TENANTS_VIEW_DOES_TENANT_CURRENTLY_HAVE_LEASE)) > 0;
                String notes = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_NOTES));
                boolean isActive = cursor.getInt(cursor.getColumnIndex(TENANTS_VIEW_IS_ACTIVE)) > 0;
                tenants.add(new Tenant(id, firstName, lastName, phone, email, emergencyFirstName, emergencyLastName, emergencyPhone, hasLease, notes, isActive));
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

    public Tenant getTenantByID(int tenantID, User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Tenant tenant = null;
        String Query = "Select * from " + TENANTS_VIEW +
                " WHERE " + TENANTS_VIEW_USER_ID + " = " + user.getId() + " AND " + TENANTS_VIEW_TENANT_ID + " = " + tenantID +
                " LIMIT 1";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(TENANTS_VIEW_TENANT_ID));
            String firstName = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_LAST_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_PHONE));
            String email = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_EMAIL));
            String emergencyFirstName = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_EMERGENCY_FIRST_NAME));
            String emergencyLastName = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_EMERGENCY_LAST_NAME));
            String emergencyPhone = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_EMERGENCY_PHONE));
            Boolean hasLease = cursor.getInt(cursor.getColumnIndex(TENANTS_VIEW_DOES_TENANT_CURRENTLY_HAVE_LEASE)) > 0;
            String notes = cursor.getString(cursor.getColumnIndex(TENANTS_VIEW_NOTES));
            boolean isActive = cursor.getInt(cursor.getColumnIndex(TENANTS_VIEW_IS_ACTIVE)) > 0;
            tenant = new Tenant(id, firstName, lastName, phone, email, emergencyFirstName, emergencyLastName, emergencyPhone, hasLease, notes, isActive);
        }
        cursor.close();
        db.close();
        return tenant;
    }

    //Gets a users active apartments
    public ArrayList<Apartment> getUsersApartments(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Apartment> apartments = new ArrayList<>();
        String Query = "Select * from " + APARTMENTS_VIEW +
                " WHERE " + APARTMENTS_VIEW_USER_ID + " = " + user.getId() + " AND " + APARTMENTS_VIEW_IS_ACTIVE + " = 1" +
                " ORDER BY "
                + APARTMENTS_VIEW_IS_RENTED + " DESC, "
                + " UPPER(" + APARTMENTS_VIEW_CITY + ")";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(APARTMENTS_VIEW_APARTMENT_ID));
                String street1 = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_STREET_1));
                String street2 = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_STREET_2));
                String city = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_CITY));
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
                apartments.add(new Apartment(id, street1, street2, city, state, zip, description, isRented, notes, mainPic, otherPics, true));
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

    //Gets a users active apartments
    public ArrayList<Apartment> getUsersApartmentsIncludingInactive(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Apartment> apartments = new ArrayList<>();
        String Query = "Select * from " + APARTMENTS_VIEW +
                " WHERE " + APARTMENTS_VIEW_USER_ID + " = " + user.getId() +
                " ORDER BY "
                + APARTMENTS_VIEW_IS_RENTED + " DESC, "
                + " UPPER(" + APARTMENTS_VIEW_CITY + ")";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(APARTMENTS_VIEW_APARTMENT_ID));
                String street1 = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_STREET_1));
                String street2 = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_STREET_2));
                String city = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_CITY));
                String state = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_STATE));
                String zip = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_ZIP));
                String description = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_DESCRIPTION));
                Boolean isRented = cursor.getInt(cursor.getColumnIndex(APARTMENTS_VIEW_IS_RENTED)) > 0;
                String notes = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_NOTES));
                String mainPic = null;
                if (!cursor.isNull(cursor.getColumnIndex(APARTMENTS_VIEW_MAIN_PIC))) {
                    mainPic = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_MAIN_PIC));
                }
                boolean isActive = cursor.getInt(cursor.getColumnIndex(APARTMENTS_VIEW_IS_ACTIVE)) > 0;
                ArrayList<String> otherPics = getApartmentOtherPics(db, id);
                apartments.add(new Apartment(id, street1, street2, city, state, zip, description, isRented, notes, mainPic, otherPics, isActive));
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

    public Apartment getApartmentByID(int apartmentID, User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Apartment apartment = null;
        String Query = "Select * from " + APARTMENTS_VIEW +
                " WHERE " + APARTMENTS_VIEW_USER_ID + " = " + user.getId() + " AND " + APARTMENTS_VIEW_APARTMENT_ID + " = " + apartmentID +
                " LIMIT 1";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(APARTMENTS_VIEW_APARTMENT_ID));
            String street1 = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_STREET_1));
            String street2 = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_STREET_2));
            String city = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_CITY));
            String state = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_STATE));
            String zip = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_ZIP));
            String description = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_DESCRIPTION));
            Boolean isRented = cursor.getInt(cursor.getColumnIndex(APARTMENTS_VIEW_IS_RENTED)) > 0;
            String notes = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_NOTES));
            String mainPic = null;
            if (!cursor.isNull(cursor.getColumnIndex(APARTMENTS_VIEW_MAIN_PIC))) {
                mainPic = cursor.getString(cursor.getColumnIndex(APARTMENTS_VIEW_MAIN_PIC));
            }
            boolean isActive = cursor.getInt(cursor.getColumnIndex(APARTMENTS_VIEW_IS_ACTIVE)) > 0;
            ArrayList<String> otherPics = getApartmentOtherPics(db, id);
            apartment = new Apartment(id, street1, street2, city, state, zip, description, isRented, notes, mainPic, otherPics, isActive);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return apartment;
    }

    public Lease getLeaseByID(User user, int leaseID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + LEASE_TABLE + " WHERE " + LEASE_ID_COLUMN_PK + " = " + leaseID + " AND " +
                LEASE_USER_ID_COLUMN_FK + " = " + user.getId() +
                " LIMIT 1";
        Cursor cursor = db.rawQuery(Query, null);
        Lease lease = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(LEASE_ID_COLUMN_PK));
            int primaryTenantID = cursor.getInt(cursor.getColumnIndex(LEASE_PRIMARY_TENANT_ID_COLUMN));
            ArrayList<Integer> secondaryTenantIDs = getSecondaryTenantsForLease(db, id);
            int apartmentID = cursor.getInt(cursor.getColumnIndex(LEASE_APARTMENT_ID_COLUMN));
            String startDateString = cursor.getString(cursor.getColumnIndex(LEASE_START_DATE_COLUMN));
            Date startDate = null;
            try {
                startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(startDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String endDateString = cursor.getString(cursor.getColumnIndex(LEASE_END_DATE_COLUMN));
            Date endDate = null;
            try {
                endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(endDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int paymentDateID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_DAY_COLUMN_FK));
            String rentCostString = cursor.getString(cursor.getColumnIndex(LEASE_MONTHLY_RENT_COST_COLUMN));
            BigDecimal rentCost = new BigDecimal(rentCostString);
            rentCost = rentCost.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
            String depositString = cursor.getString(cursor.getColumnIndex(LEASE_DEPOSIT_AMOUNT_COLUMN));
            BigDecimal deposit = new BigDecimal(depositString);
            deposit = deposit.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
            int paymentFrequencyID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_FREQUENCY_ID_FK));
            String notes = cursor.getString(cursor.getColumnIndex(LEASE_NOTES_COLUMN));
            lease = new Lease(id, primaryTenantID, secondaryTenantIDs, apartmentID, startDate, endDate, paymentDateID, rentCost, deposit, paymentFrequencyID, notes);
        }
        cursor.close();
        db.close();
        return lease;
    }

    public ArrayList<Lease> getLeasesStartingOrEndingOnDate(User user, Date date) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Lease> leases = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateString = formatter.format(date);
        String query = "Select * from " + LEASE_TABLE +
                " WHERE " +
                LEASE_USER_ID_COLUMN_FK + " = " + user.getId() +
                " AND " + LEASE_IS_ACTIVE_COLUMN + " = 1" +
                " AND " + LEASE_START_DATE_COLUMN + " = '" + dateString + "'" +
                " OR " + LEASE_END_DATE_COLUMN + " = '" + dateString + "'" +
                " AND " + LEASE_IS_ACTIVE_COLUMN + " = 1 " +
                " AND " + LEASE_USER_ID_COLUMN_FK + " = " + user.getId();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(LEASE_ID_COLUMN_PK));
                int primaryTenantID = cursor.getInt(cursor.getColumnIndex(LEASE_PRIMARY_TENANT_ID_COLUMN));
                ArrayList<Integer> secondaryTenantIDs = getSecondaryTenantsForLease(db, id);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(LEASE_APARTMENT_ID_COLUMN));
                String startDateString = cursor.getString(cursor.getColumnIndex(LEASE_START_DATE_COLUMN));
                Date startDate = null;
                try {
                    startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String endDateString = cursor.getString(cursor.getColumnIndex(LEASE_END_DATE_COLUMN));
                Date endDate = null;
                try {
                    endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(endDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int paymentDateID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_DAY_COLUMN_FK));
                String rentCostString = cursor.getString(cursor.getColumnIndex(LEASE_MONTHLY_RENT_COST_COLUMN));
                BigDecimal rentCost = new BigDecimal(rentCostString);
                rentCost = rentCost.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String depositString = cursor.getString(cursor.getColumnIndex(LEASE_DEPOSIT_AMOUNT_COLUMN));
                BigDecimal deposit = new BigDecimal(depositString);
                deposit = deposit.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int paymentFrequencyID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_FREQUENCY_ID_FK));
                String notes = cursor.getString(cursor.getColumnIndex(LEASE_NOTES_COLUMN));
                leases.add(new Lease(id, primaryTenantID, secondaryTenantIDs, apartmentID, startDate, endDate, paymentDateID, rentCost, deposit, paymentFrequencyID, notes));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return leases;
        }
        cursor.close();
        db.close();
        return leases;
    }

    public ArrayList<Lease> getLeasesStartingOrEndingInDateRange(User user, Date startDateRange, Date endDateRange) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Lease> leases = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateRangeString = formatter.format(startDateRange);
        String endDateRangeString = formatter.format(endDateRange);
        String query = "Select * from " + LEASE_TABLE +
                " WHERE " +
                LEASE_USER_ID_COLUMN_FK + " = " + user.getId() +
                " AND " + LEASE_IS_ACTIVE_COLUMN + " = 1" +
                " AND " + LEASE_START_DATE_COLUMN + " BETWEEN '" + startDateRangeString + "' AND '" + endDateRangeString +
                "' OR " +
                LEASE_END_DATE_COLUMN + " BETWEEN '" + startDateRangeString + "' AND '" + endDateRangeString +
                "' AND " + LEASE_IS_ACTIVE_COLUMN + " = 1 " +
                " AND " + LEASE_USER_ID_COLUMN_FK + " = " + user.getId();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(LEASE_ID_COLUMN_PK));
                int primaryTenantID = cursor.getInt(cursor.getColumnIndex(LEASE_PRIMARY_TENANT_ID_COLUMN));
                ArrayList<Integer> secondaryTenantIDs = getSecondaryTenantsForLease(db, id);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(LEASE_APARTMENT_ID_COLUMN));
                String startDateString = cursor.getString(cursor.getColumnIndex(LEASE_START_DATE_COLUMN));
                Date startDate = null;
                try {
                    startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String endDateString = cursor.getString(cursor.getColumnIndex(LEASE_END_DATE_COLUMN));
                Date endDate = null;
                try {
                    endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(endDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int paymentDateID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_DAY_COLUMN_FK));
                String rentCostString = cursor.getString(cursor.getColumnIndex(LEASE_MONTHLY_RENT_COST_COLUMN));
                BigDecimal rentCost = new BigDecimal(rentCostString);
                rentCost = rentCost.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String depositString = cursor.getString(cursor.getColumnIndex(LEASE_DEPOSIT_AMOUNT_COLUMN));
                BigDecimal deposit = new BigDecimal(depositString);
                deposit = deposit.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int paymentFrequencyID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_FREQUENCY_ID_FK));
                String notes = cursor.getString(cursor.getColumnIndex(LEASE_NOTES_COLUMN));
                leases.add(new Lease(id, primaryTenantID, secondaryTenantIDs, apartmentID, startDate, endDate, paymentDateID, rentCost, deposit, paymentFrequencyID, notes));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return leases;
        }
        cursor.close();
        db.close();
        return leases;
    }

    public ArrayList<Lease> getUsersActiveLeases(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Lease> leases = new ArrayList<>();
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String todaysDateString = formatter.format(today);
        String query = "Select * from " + LEASE_TABLE +
                " WHERE " +
                LEASE_USER_ID_COLUMN_FK + " = " + user.getId() +
                " AND " + LEASE_IS_ACTIVE_COLUMN + " = 1" +
                " AND " + LEASE_START_DATE_COLUMN + " <= '" + todaysDateString + "'" +
                " AND " + LEASE_END_DATE_COLUMN + " >= '" + todaysDateString + "'" +
                " ORDER BY " + LEASE_START_DATE_COLUMN + " DESC ";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(LEASE_ID_COLUMN_PK));
                int primaryTenantID = cursor.getInt(cursor.getColumnIndex(LEASE_PRIMARY_TENANT_ID_COLUMN));
                ArrayList<Integer> secondaryTenantIDs = getSecondaryTenantsForLease(db, id);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(LEASE_APARTMENT_ID_COLUMN));
                String startDateString = cursor.getString(cursor.getColumnIndex(LEASE_START_DATE_COLUMN));
                Date startDate = null;
                try {
                    startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String endDateString = cursor.getString(cursor.getColumnIndex(LEASE_END_DATE_COLUMN));
                Date endDate = null;
                try {
                    endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(endDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int paymentDateID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_DAY_COLUMN_FK));
                String rentCostString = cursor.getString(cursor.getColumnIndex(LEASE_MONTHLY_RENT_COST_COLUMN));
                BigDecimal rentCost = new BigDecimal(rentCostString);
                rentCost = rentCost.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String depositString = cursor.getString(cursor.getColumnIndex(LEASE_DEPOSIT_AMOUNT_COLUMN));
                BigDecimal deposit = new BigDecimal(depositString);
                deposit = deposit.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String notes = cursor.getString(cursor.getColumnIndex(LEASE_NOTES_COLUMN));
                int paymentFrequencyID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_FREQUENCY_ID_FK));
                leases.add(new Lease(id, primaryTenantID, secondaryTenantIDs, apartmentID, startDate, endDate, paymentDateID, rentCost, deposit, paymentFrequencyID, notes));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return leases;
        }
        cursor.close();
        db.close();
        return leases;
    }

    public ArrayList<Lease> getUsersLeasesForTenantAndApartment(User user, int primaryTenantID, int apartmentID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Lease> leases = new ArrayList<>();
        String query = "Select * from " + LEASE_TABLE +
                " WHERE " +
                LEASE_USER_ID_COLUMN_FK + " = " + user.getId() +
                " AND " + LEASE_IS_ACTIVE_COLUMN + " = 1" +
                " AND " + LEASE_APARTMENT_ID_COLUMN + " = " + apartmentID +
                " AND " + LEASE_PRIMARY_TENANT_ID_COLUMN + " = " + primaryTenantID +
                " ORDER BY " + LEASE_START_DATE_COLUMN + " DESC ";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(LEASE_ID_COLUMN_PK));
                ArrayList<Integer> secondaryTenantIDs = getSecondaryTenantsForLease(db, id);
                String startDateString = cursor.getString(cursor.getColumnIndex(LEASE_START_DATE_COLUMN));
                Date startDate = null;
                try {
                    startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String endDateString = cursor.getString(cursor.getColumnIndex(LEASE_END_DATE_COLUMN));
                Date endDate = null;
                try {
                    endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(endDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int paymentDateID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_DAY_COLUMN_FK));
                String rentCostString = cursor.getString(cursor.getColumnIndex(LEASE_MONTHLY_RENT_COST_COLUMN));
                BigDecimal rentCost = new BigDecimal(rentCostString);
                rentCost = rentCost.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String depositString = cursor.getString(cursor.getColumnIndex(LEASE_DEPOSIT_AMOUNT_COLUMN));
                BigDecimal deposit = new BigDecimal(depositString);
                deposit = deposit.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int paymentFrequencyID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_FREQUENCY_ID_FK));
                String notes = cursor.getString(cursor.getColumnIndex(LEASE_NOTES_COLUMN));
                leases.add(new Lease(id, primaryTenantID, secondaryTenantIDs, apartmentID, startDate, endDate, paymentDateID, rentCost, deposit, paymentFrequencyID, notes));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return leases;
        }
        cursor.close();
        db.close();
        return leases;
    }

    public ArrayList<Lease> getPrimaryAndSecondaryLeasesForTenant(User user, int tenantID) {
        MainArrayDataMethods dm = new MainArrayDataMethods();
        ArrayList<Lease> leases = new ArrayList<>();
        leases.addAll(getUsersLeasesForSecondaryTenant(user, tenantID));
        leases.addAll(getUsersLeasesForTenant(user, tenantID));
        dm.sortLeaseArrayByStartDateDesc(leases);
        return leases;
    }

    public ArrayList<Lease> getUsersLeasesForTenant(User user, int primaryTenantID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Lease> leases = new ArrayList<>();
        String query = "Select * from " + LEASE_TABLE +
                " WHERE " +
                LEASE_USER_ID_COLUMN_FK + " = " + user.getId() +
                " AND " + LEASE_IS_ACTIVE_COLUMN + " = 1" +
                " AND " + LEASE_PRIMARY_TENANT_ID_COLUMN + " = " + primaryTenantID +
                " ORDER BY " + LEASE_START_DATE_COLUMN + " DESC ";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(LEASE_ID_COLUMN_PK));
                ArrayList<Integer> secondaryTenantIDs = getSecondaryTenantsForLease(db, id);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(LEASE_APARTMENT_ID_COLUMN));
                String startDateString = cursor.getString(cursor.getColumnIndex(LEASE_START_DATE_COLUMN));
                Date startDate = null;
                try {
                    startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String endDateString = cursor.getString(cursor.getColumnIndex(LEASE_END_DATE_COLUMN));
                Date endDate = null;
                try {
                    endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(endDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int paymentDateID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_DAY_COLUMN_FK));
                String rentCostString = cursor.getString(cursor.getColumnIndex(LEASE_MONTHLY_RENT_COST_COLUMN));
                BigDecimal rentCost = new BigDecimal(rentCostString);
                rentCost = rentCost.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String depositString = cursor.getString(cursor.getColumnIndex(LEASE_DEPOSIT_AMOUNT_COLUMN));
                BigDecimal deposit = new BigDecimal(depositString);
                deposit = deposit.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int paymentFrequencyID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_FREQUENCY_ID_FK));
                String notes = cursor.getString(cursor.getColumnIndex(LEASE_NOTES_COLUMN));
                leases.add(new Lease(id, primaryTenantID, secondaryTenantIDs, apartmentID, startDate, endDate, paymentDateID, rentCost, deposit, paymentFrequencyID, notes));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return leases;
        }
        cursor.close();
        db.close();
        return leases;
    }

    public ArrayList<Lease> getUsersLeasesForSecondaryTenant(User user, int tenantID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Lease> leases;
        ArrayList<Integer> leaseIDsToGet = new ArrayList<>();
        String query = "Select * from " + SECONDARY_TENANTS_VIEW +
                " WHERE " +
                SECONDARY_TENANTS_VIEW_IS_ACTIVE + " = 1" +
                " AND " + SECONDARY_TENANTS_VIEW_TENANT_ID + " = " + tenantID;
        //" ORDER BY " + LEASE_START_DATE_COLUMN + " ASC ";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int leaseID = cursor.getInt(cursor.getColumnIndex(SECONDARY_TENANTS_VIEW_LEASE_ID));
                leaseIDsToGet.add(leaseID);
                cursor.moveToNext();
            }
        }
        leases = getLeasesFromLeaseIDArray(user, leaseIDsToGet, db);
        cursor.close();
        db.close();
        return leases;
    }

    private ArrayList<Lease> getLeasesFromLeaseIDArray(User user, ArrayList<Integer> ids, SQLiteDatabase db) {
        ArrayList<Lease> leases = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            String query = "Select * from " + LEASE_TABLE + " WHERE " + LEASE_ID_COLUMN_PK + " = " + ids.get(i) + " AND " +
                    LEASE_USER_ID_COLUMN_FK + " = " + user.getId() + " AND " +
                    LEASE_IS_ACTIVE_COLUMN + " = 1 LIMIT 1";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(LEASE_ID_COLUMN_PK));
                int primaryTenantID = cursor.getInt(cursor.getColumnIndex(LEASE_PRIMARY_TENANT_ID_COLUMN));
                ArrayList<Integer> secondaryTenantIDs = getSecondaryTenantsForLease(db, id);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(LEASE_APARTMENT_ID_COLUMN));
                String startDateString = cursor.getString(cursor.getColumnIndex(LEASE_START_DATE_COLUMN));
                Date startDate = null;
                try {
                    startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String endDateString = cursor.getString(cursor.getColumnIndex(LEASE_END_DATE_COLUMN));
                Date endDate = null;
                try {
                    endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(endDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int paymentDateID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_DAY_COLUMN_FK));
                String rentCostString = cursor.getString(cursor.getColumnIndex(LEASE_MONTHLY_RENT_COST_COLUMN));
                BigDecimal rentCost = new BigDecimal(rentCostString);
                rentCost = rentCost.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String depositString = cursor.getString(cursor.getColumnIndex(LEASE_DEPOSIT_AMOUNT_COLUMN));
                BigDecimal deposit = new BigDecimal(depositString);
                deposit = deposit.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int paymentFrequencyID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_FREQUENCY_ID_FK));
                String notes = cursor.getString(cursor.getColumnIndex(LEASE_NOTES_COLUMN));
                leases.add(new Lease(id, primaryTenantID, secondaryTenantIDs, apartmentID, startDate, endDate, paymentDateID, rentCost, deposit, paymentFrequencyID, notes));
                cursor.close();
            }
        }
        return leases;
    }

    public ArrayList<Lease> getUsersLeasesForApartment(User user, int apartmentID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Lease> leases = new ArrayList<>();
        String query = "Select * from " + LEASE_TABLE +
                " WHERE " +
                LEASE_USER_ID_COLUMN_FK + " = " + user.getId() +
                " AND " + LEASE_IS_ACTIVE_COLUMN + " = 1" +
                " AND " + LEASE_APARTMENT_ID_COLUMN + " = " + apartmentID +
                " ORDER BY " + LEASE_START_DATE_COLUMN + " DESC ";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(LEASE_ID_COLUMN_PK));
                int primaryTenantID = cursor.getInt(cursor.getColumnIndex(LEASE_PRIMARY_TENANT_ID_COLUMN));
                ArrayList<Integer> secondaryTenantIDs = getSecondaryTenantsForLease(db, id);
                String startDateString = cursor.getString(cursor.getColumnIndex(LEASE_START_DATE_COLUMN));
                Date startDate = null;
                try {
                    startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String endDateString = cursor.getString(cursor.getColumnIndex(LEASE_END_DATE_COLUMN));
                Date endDate = null;
                try {
                    endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(endDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int paymentDateID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_DAY_COLUMN_FK));
                String rentCostString = cursor.getString(cursor.getColumnIndex(LEASE_MONTHLY_RENT_COST_COLUMN));
                BigDecimal rentCost = new BigDecimal(rentCostString);
                rentCost = rentCost.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String depositString = cursor.getString(cursor.getColumnIndex(LEASE_DEPOSIT_AMOUNT_COLUMN));
                BigDecimal deposit = new BigDecimal(depositString);
                deposit = deposit.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int paymentFrequencyID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_FREQUENCY_ID_FK));
                String notes = cursor.getString(cursor.getColumnIndex(LEASE_NOTES_COLUMN));
                leases.add(new Lease(id, primaryTenantID, secondaryTenantIDs, apartmentID, startDate, endDate, paymentDateID, rentCost, deposit, paymentFrequencyID, notes));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return leases;
        }
        cursor.close();
        db.close();
        return leases;
    }

    public ArrayList<Lease> getUsersActiveLeasesWithinDates(User user, Date startDate, Date
            endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Lease> leases = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startDate);
        String endDateString = formatter.format(endDate);

        String Query = "Select * from " + LEASE_TABLE +
                " WHERE " +
                LEASE_USER_ID_COLUMN_FK + " = " + user.getId() +
                " AND " + LEASE_IS_ACTIVE_COLUMN + " = 1" +
                " AND " + LEASE_START_DATE_COLUMN + " <= '" + endDateString + "'" +
                " AND " + LEASE_END_DATE_COLUMN + " >= '" + startDateString + "'" +
                " ORDER BY " + LEASE_START_DATE_COLUMN + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(LEASE_ID_COLUMN_PK));
                int primaryTenantID = cursor.getInt(cursor.getColumnIndex(LEASE_PRIMARY_TENANT_ID_COLUMN));
                ArrayList<Integer> secondaryTenantIDs = getSecondaryTenantsForLease(db, id);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(LEASE_APARTMENT_ID_COLUMN));
                String leaseStartDateString = cursor.getString(cursor.getColumnIndex(LEASE_START_DATE_COLUMN));
                Date leaseStartDate = null;
                try {
                    leaseStartDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(leaseStartDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String leaseEndDateString = cursor.getString(cursor.getColumnIndex(LEASE_END_DATE_COLUMN));
                Date leaseEndDate = null;
                try {
                    leaseEndDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(leaseEndDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int paymentDateID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_DAY_COLUMN_FK));
                String rentCostString = cursor.getString(cursor.getColumnIndex(LEASE_MONTHLY_RENT_COST_COLUMN));
                BigDecimal rentCost = new BigDecimal(rentCostString);
                rentCost = rentCost.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String depositString = cursor.getString(cursor.getColumnIndex(LEASE_DEPOSIT_AMOUNT_COLUMN));
                BigDecimal deposit = new BigDecimal(depositString);
                deposit = deposit.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int paymentFrequencyID = cursor.getInt(cursor.getColumnIndex(LEASE_PAYMENT_FREQUENCY_ID_FK));
                String notes = cursor.getString(cursor.getColumnIndex(LEASE_NOTES_COLUMN));
                leases.add(new Lease(id, primaryTenantID, secondaryTenantIDs, apartmentID, leaseStartDate, leaseEndDate, paymentDateID, rentCost, deposit, paymentFrequencyID, notes));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return leases;
        }
        cursor.close();
        db.close();
        return leases;
    }

    private ArrayList<Integer> getSecondaryTenantsForLease(SQLiteDatabase db, int leaseID) {
        ArrayList<Integer> secondaryTenants = new ArrayList<>();
        String Query = "Select * from " + LEASE_SECONDARY_TENANTS_TABLE + " WHERE " + LEASE_SECONDARY_TENANTS_LEASE_ID_COLUMN_FK + " = " + leaseID + " AND " +
                LEASE_SECONDARY_TENANTS_IS_ACTIVE_COLUMN + " = 1";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int secondaryTenantID = cursor.getInt(cursor.getColumnIndex(LEASE_SECONDARY_TENANTS_TENANT_ID_COLUMN_FK));
                secondaryTenants.add(secondaryTenantID);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return secondaryTenants;
    }

    public void setLeaseInactive(Lease lease) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LEASE_IS_ACTIVE_COLUMN, 0);
        values.put(LEASE_LAST_UPDATED_COLUMN, " time('now') ");
        // updating row
        db.update(LEASE_TABLE, values, LEASE_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(lease.getId())});

        ContentValues values2 = new ContentValues();
        values2.put(LEASE_SECONDARY_TENANTS_IS_ACTIVE_COLUMN, 0);
        values2.put(LEASE_SECONDARY_TENANTS_LAST_UPDATED_COLUMN, " time('now') ");
        db.update(LEASE_SECONDARY_TENANTS_TABLE, values2, LEASE_SECONDARY_TENANTS_LEASE_ID_COLUMN_FK + " = ?", new String[]{String.valueOf(lease.getId())});

        db.close();
    }

    public void setAllExpensesRelatedToLeaseInactive(int leaseID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EXPENSE_LOG_IS_ACTIVE_COLUMN, 0);
        values.put(EXPENSE_LOG_LAST_UPDATE_COLUMN, " time('now') ");
        // updating row
        db.update(EXPENSE_LOG_TABLE, values, EXPENSE_LOG_LEASE_ID_COLUMN_FK + " = ?", new String[]{String.valueOf(leaseID)});
        db.close();
    }

    public void setAllIncomeRelatedToLeaseInactive(int leaseID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PAYMENT_LOG_IS_ACTIVE_COLUMN, 0);
        values.put(PAYMENT_LOG_LAST_UPDATE_COLUMN, " time('now') ");
        // updating row
        db.update(PAYMENT_LOG_TABLE, values, PAYMENT_LOG_LEASE_ID_COLUMN_FK + " = ?", new String[]{String.valueOf(leaseID)});
        db.close();
    }

    public void testLeaseSecondaryTenantsView(int leaseID) {
        //ArrayList<Integer> secondaryTenants = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + SECONDARY_TENANTS_VIEW + " WHERE " + SECONDARY_TENANTS_VIEW_LEASE_ID + " = " + leaseID + " AND " +
                SECONDARY_TENANTS_VIEW_IS_ACTIVE + " = 1";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int secondaryTenantID = cursor.getInt(cursor.getColumnIndex(SECONDARY_TENANTS_VIEW_TENANT_ID));
                String leaseStartDateString = cursor.getString(cursor.getColumnIndex(SECONDARY_TENANTS_VIEW_LEASE_START));
                Date leaseStartDate = null;
                try {
                    leaseStartDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(leaseStartDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String leaseEndDateString = cursor.getString(cursor.getColumnIndex(SECONDARY_TENANTS_VIEW_LEASE_END));
                Date leaseEndDate = null;
                try {
                    leaseEndDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(leaseEndDateString);
                    Log.d(TAG, "testLeaseSecondaryTenantsView: LEASE END = " + leaseEndDate);
                } catch (ParseException e) {
                    Log.d(TAG, "testLeaseSecondaryTenantsView: LEASE END = NULL");
                    e.printStackTrace();
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
    }

    public ArrayList<ExpenseLogEntry> getUsersExpenses(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ExpenseLogEntry> expenses = new ArrayList<>();
        String Query = "Select * from " + EXPENSES_VIEW +
                " WHERE " + EXPENSES_VIEW_USER_ID + " = " + user.getId() + " AND " + EXPENSES_VIEW_IS_ACTIVE + " = 1" +
                " ORDER BY " + EXPENSES_VIEW_EXPENSE_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_ID));
                String expenseDateString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_DATE));
                Date expenseDate = null;
                try {
                    expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_APARTMENT_ID));
                int tenantID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TENANT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_LEASE_ID));
                String description = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_ID));
                String typeLabel = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_LABEL));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_WAS_PAID)) > 0;
                String receiptPic = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_RECEIPT_PIC));
                expenses.add(new ExpenseLogEntry(id, expenseDate, amount, apartmentID, leaseID, tenantID, description, typeID, typeLabel, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return expenses;
        }
        cursor.close();
        db.close();
        return expenses;
    }

    public ArrayList<ExpenseLogEntry> getUsersExpensesByApartmentID(User user, int apartmentID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ExpenseLogEntry> expenses = new ArrayList<>();
        String Query = "Select * from " + EXPENSES_VIEW +
                " WHERE " + EXPENSES_VIEW_USER_ID + " = " + user.getId() + " AND " + EXPENSES_VIEW_IS_ACTIVE + " = 1" +
                " AND " + EXPENSES_VIEW_APARTMENT_ID + " = " + apartmentID +
                " ORDER BY " + EXPENSES_VIEW_EXPENSE_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_ID));
                String expenseDateString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_DATE));
                Date expenseDate = null;
                try {
                    expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int tenantID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TENANT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_LEASE_ID));
                String description = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_ID));
                String typeLabel = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_LABEL));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_WAS_PAID)) > 0;
                String receiptPic = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_RECEIPT_PIC));
                expenses.add(new ExpenseLogEntry(id, expenseDate, amount, apartmentID, leaseID, tenantID, description, typeID, typeLabel, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return expenses;
        }
        cursor.close();
        db.close();
        return expenses;
    }

    public ArrayList<ExpenseLogEntry> getUsersExpensesByApartmentIDWithinDates(User user, int apartmentID, Date startDate, Date endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ExpenseLogEntry> expenses = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startDate);
        String endDateString = formatter.format(endDate);
        String Query = "Select * from " + EXPENSES_VIEW +
                " WHERE " + EXPENSES_VIEW_USER_ID + " = " + user.getId() + " AND " + EXPENSES_VIEW_IS_ACTIVE + " = 1" +
                " AND " + EXPENSES_VIEW_APARTMENT_ID + " = " + apartmentID +
                " AND " + EXPENSES_VIEW_EXPENSE_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + EXPENSES_VIEW_EXPENSE_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_ID));
                String expenseDateString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_DATE));
                Date expenseDate = null;
                try {
                    expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int tenantID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TENANT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_LEASE_ID));
                String description = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_ID));
                String typeLabel = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_LABEL));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_WAS_PAID)) > 0;
                String receiptPic = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_RECEIPT_PIC));
                expenses.add(new ExpenseLogEntry(id, expenseDate, amount, apartmentID, leaseID, tenantID, description, typeID, typeLabel, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return expenses;
        }
        cursor.close();
        db.close();
        return expenses;
    }

    public ArrayList<ExpenseLogEntry> getUsersExpensesWithinDates(User user, Date
            startDate, Date endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ExpenseLogEntry> expenses = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startDate);
        String endDateString = formatter.format(endDate);

        String Query = "Select * from " + EXPENSES_VIEW +
                " WHERE " +
                EXPENSES_VIEW_USER_ID + " = " + user.getId() + " AND " +
                EXPENSES_VIEW_IS_ACTIVE + " = 1" + " AND " + EXPENSES_VIEW_EXPENSE_DATE +
                " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + EXPENSES_VIEW_EXPENSE_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_ID));
                String expenseDateString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_DATE));
                Date expenseDate = null;
                try {
                    expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_APARTMENT_ID));
                int tenantID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TENANT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_LEASE_ID));
                String description = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_ID));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_WAS_PAID)) > 0;
                String typeLabel = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_RECEIPT_PIC));
                expenses.add(new ExpenseLogEntry(id, expenseDate, amount, apartmentID, leaseID, tenantID, description, typeID, typeLabel, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return expenses;
        }
        cursor.close();
        db.close();
        return expenses;
    }

    public ArrayList<ExpenseLogEntry> getUsersExpensesByTenantIDWithinDates(User user, int tenantID, Date
            startDate, Date endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ExpenseLogEntry> expenses = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startDate);
        String endDateString = formatter.format(endDate);

        String Query = "Select * from " + EXPENSES_VIEW +
                " WHERE " +
                EXPENSES_VIEW_USER_ID + " = " + user.getId() +
                " AND " + EXPENSES_VIEW_TENANT_ID + " = " + tenantID +
                " AND " + EXPENSES_VIEW_IS_ACTIVE + " = 1" +
                " AND " + EXPENSES_VIEW_EXPENSE_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + EXPENSES_VIEW_EXPENSE_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_ID));
                String expenseDateString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_DATE));
                Date expenseDate = null;
                try {
                    expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_APARTMENT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_LEASE_ID));
                String description = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_ID));
                String typeLabel = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_RECEIPT_PIC));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_WAS_PAID)) > 0;
                expenses.add(new ExpenseLogEntry(id, expenseDate, amount, apartmentID, leaseID, tenantID, description, typeID, typeLabel, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return expenses;
        }
        cursor.close();
        db.close();
        return expenses;
    }

    public ArrayList<ExpenseLogEntry> getUsersExpensesByLeaseID(User user, int leaseID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ExpenseLogEntry> expenses = new ArrayList<>();
        String Query = "Select * from " + EXPENSES_VIEW +
                " WHERE " +
                EXPENSES_VIEW_USER_ID + " = " + user.getId() +
                " AND " + EXPENSES_VIEW_LEASE_ID + " = " + leaseID +
                " AND " + EXPENSES_VIEW_IS_ACTIVE + " = 1" +
                " ORDER BY " + EXPENSES_VIEW_EXPENSE_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_ID));
                String expenseDateString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_DATE));
                Date expenseDate = null;
                try {
                    expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_APARTMENT_ID));
                int tenantID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TENANT_ID));
                String description = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_ID));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_WAS_PAID)) > 0;
                String typeLabel = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_RECEIPT_PIC));
                expenses.add(new ExpenseLogEntry(id, expenseDate, amount, apartmentID, leaseID, tenantID, description, typeID, typeLabel, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return expenses;
        }
        cursor.close();
        db.close();
        return expenses;
    }

    public ArrayList<PaymentLogEntry> getUsersIncome(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PaymentLogEntry> income = new ArrayList<>();
        String Query = "Select * from " + INCOME_VIEW +
                " WHERE " + INCOME_VIEW_USER_ID + " = " + user.getId() + " AND " + INCOME_VIEW_IS_ACTIVE + " = 1" +
                " ORDER BY " + INCOME_VIEW_INCOME_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_INCOME_ID));
                String incomeDateString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_INCOME_DATE));
                Date incomeDate = null;
                try {
                    incomeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(incomeDateString);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int leaseID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_LEASE_ID));
                int tenantID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TENANT_ID));
                int apartmentID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_APARTMENT_ID));
                String description = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TYPE_ID));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_WAS_RECEIVED)) > 0;
                String typeLabel = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_RECEIPT_PIC));
                income.add(new PaymentLogEntry(id, incomeDate, typeID, typeLabel, tenantID, leaseID, apartmentID, amount, description, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return income;
        }
        cursor.close();
        db.close();
        return income;
    }

    public ArrayList<PaymentLogEntry> getUsersIncomeByLeaseID(User user, int leaseID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PaymentLogEntry> income = new ArrayList<>();
        String Query = "Select * from " + INCOME_VIEW +
                " WHERE " + INCOME_VIEW_USER_ID + " = " + user.getId() + " AND " + INCOME_VIEW_IS_ACTIVE + " = 1" +
                " AND " + INCOME_VIEW_LEASE_ID + " = " + leaseID +
                " ORDER BY " + INCOME_VIEW_INCOME_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_INCOME_ID));
                String incomeDateString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_INCOME_DATE));
                Date incomeDate = null;
                try {
                    incomeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(incomeDateString);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int tenantID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TENANT_ID));
                int apartmentID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_APARTMENT_ID));
                String description = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_DESCRIPTION));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_WAS_RECEIVED)) > 0;
                int typeID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TYPE_ID));
                String typeLabel = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_RECEIPT_PIC));
                income.add(new PaymentLogEntry(id, incomeDate, typeID, typeLabel, tenantID, leaseID, apartmentID, amount, description, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return income;
        }
        cursor.close();
        db.close();
        return income;
    }

    public ArrayList<PaymentLogEntry> getUsersIncomeByApartmentID(User user, int apartmentID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PaymentLogEntry> income = new ArrayList<>();
        String Query = "Select * from " + INCOME_VIEW +
                " WHERE " + INCOME_VIEW_USER_ID + " = " + user.getId() + " AND " + INCOME_VIEW_IS_ACTIVE + " = 1" +
                " AND " + INCOME_VIEW_APARTMENT_ID + " = " + apartmentID +
                " ORDER BY " + INCOME_VIEW_INCOME_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_INCOME_ID));
                String incomeDateString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_INCOME_DATE));
                Date incomeDate = null;
                try {
                    incomeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(incomeDateString);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int tenantID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TENANT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_LEASE_ID));
                String description = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TYPE_ID));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_WAS_RECEIVED)) > 0;
                String typeLabel = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_RECEIPT_PIC));
                income.add(new PaymentLogEntry(id, incomeDate, typeID, typeLabel, tenantID, leaseID, apartmentID, amount, description, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return income;
        }
        cursor.close();
        db.close();
        return income;
    }

    public ArrayList<PaymentLogEntry> getUsersIncomeByApartmentIDWithinDates(User user, int apartmentID, Date startDate, Date endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PaymentLogEntry> income = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startDate);
        String endDateString = formatter.format(endDate);


        String Query = "Select * from " + INCOME_VIEW +
                " WHERE " + INCOME_VIEW_USER_ID + " = " + user.getId() + " AND " + INCOME_VIEW_IS_ACTIVE + " = 1" +
                " AND " + INCOME_VIEW_APARTMENT_ID + " = " + apartmentID +
                " AND " + INCOME_VIEW_INCOME_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + INCOME_VIEW_INCOME_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_INCOME_ID));
                String incomeDateString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_INCOME_DATE));
                Date incomeDate = null;
                try {
                    incomeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(incomeDateString);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int tenantID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TENANT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_LEASE_ID));
                String description = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TYPE_ID));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_WAS_RECEIVED)) > 0;
                String typeLabel = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_RECEIPT_PIC));
                income.add(new PaymentLogEntry(id, incomeDate, typeID, typeLabel, tenantID, leaseID, apartmentID, amount, description, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return income;
        }
        cursor.close();
        db.close();
        return income;
    }

    public ArrayList<PaymentLogEntry> getUsersIncomeByTenantID(User user, int tenantID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PaymentLogEntry> income = new ArrayList<>();
        String Query = "Select * from " + INCOME_VIEW +
                " WHERE " + INCOME_VIEW_USER_ID + " = " + user.getId() + " AND " + INCOME_VIEW_IS_ACTIVE + " = 1" +
                " AND " + INCOME_VIEW_TENANT_ID + " = " + tenantID +
                " ORDER BY " + INCOME_VIEW_INCOME_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_INCOME_ID));
                String incomeDateString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_INCOME_DATE));
                Date incomeDate = null;
                try {
                    incomeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(incomeDateString);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_APARTMENT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_LEASE_ID));
                String description = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TYPE_ID));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_WAS_RECEIVED)) > 0;
                String typeLabel = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_RECEIPT_PIC));
                income.add(new PaymentLogEntry(id, incomeDate, typeID, typeLabel, tenantID, leaseID, apartmentID, amount, description, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return income;
        }
        cursor.close();
        db.close();
        return income;
    }

    public ArrayList<PaymentLogEntry> getUsersIncomeByTenantIDWithinDates(User user, int tenantID, Date startDate, Date endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PaymentLogEntry> income = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startDate);
        String endDateString = formatter.format(endDate);
        String Query = "Select * from " + INCOME_VIEW +
                " WHERE " + INCOME_VIEW_USER_ID + " = " + user.getId() + " AND " + INCOME_VIEW_IS_ACTIVE + " = 1" +
                " AND " + INCOME_VIEW_TENANT_ID + " = " + tenantID +
                " AND " + INCOME_VIEW_INCOME_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + INCOME_VIEW_INCOME_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_INCOME_ID));
                String incomeDateString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_INCOME_DATE));
                Date incomeDate = null;
                try {
                    incomeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(incomeDateString);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_APARTMENT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_LEASE_ID));
                String description = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_DESCRIPTION));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_WAS_RECEIVED)) > 0;
                int typeID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TYPE_ID));
                String typeLabel = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_RECEIPT_PIC));
                income.add(new PaymentLogEntry(id, incomeDate, typeID, typeLabel, tenantID, leaseID, apartmentID, amount, description, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return income;
        }
        cursor.close();
        db.close();
        return income;
    }

    public ArrayList<PaymentLogEntry> getUsersIncomeWithinDates(User user, Date startDate, Date
            endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PaymentLogEntry> income = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startDate);
        String endDateString = formatter.format(endDate);

        String Query = "Select * from " + INCOME_VIEW +
                " WHERE " +
                INCOME_VIEW_USER_ID + " = " + user.getId() + " AND " +
                INCOME_VIEW_IS_ACTIVE + " = 1" + " AND " + INCOME_VIEW_INCOME_DATE +
                " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + INCOME_VIEW_INCOME_DATE + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_INCOME_ID));
                String incomeDateString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_INCOME_DATE));
                Date incomeDate = null;
                try {
                    incomeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(incomeDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int tenantID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_APARTMENT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_LEASE_ID));
                int apartmentID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_APARTMENT_ID));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_WAS_RECEIVED)) > 0;
                String description = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TYPE_ID));
                String typeLabel = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_RECEIPT_PIC));
                income.add(new PaymentLogEntry(id, incomeDate, typeID, typeLabel, tenantID, leaseID, apartmentID, amount, description, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return income;
        }
        cursor.close();
        db.close();
        return income;
    }

    public ArrayList<MoneyLogEntry> getIncomeAndExpensesForDate(User user, Date date) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<MoneyLogEntry> incomeAndExpenses = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateString = formatter.format(date);

        String Query = "Select * from " + INCOME_VIEW +
                " WHERE " +
                INCOME_VIEW_USER_ID + " = " + user.getId() + " AND " +
                INCOME_VIEW_IS_ACTIVE + " = 1" +
                " AND " + INCOME_VIEW_INCOME_DATE + " = '" + dateString + "'" +
                " ORDER BY " + INCOME_VIEW_INCOME_ID + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_INCOME_ID));
                String incomeDateString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_INCOME_DATE));
                Date incomeDate = null;
                try {
                    incomeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(incomeDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int tenantID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_APARTMENT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_LEASE_ID));
                int apartmentID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_APARTMENT_ID));
                String description = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TYPE_ID));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_WAS_RECEIVED)) > 0;
                String typeLabel = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_RECEIPT_PIC));
                incomeAndExpenses.add(new PaymentLogEntry(id, incomeDate, typeID, typeLabel, tenantID, leaseID, apartmentID, amount, description, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
        }

        Query = "Select * from " + EXPENSES_VIEW +
                " WHERE " +
                EXPENSES_VIEW_USER_ID + " = " + user.getId() + " AND " +
                EXPENSES_VIEW_IS_ACTIVE + " = 1" +
                " AND " + EXPENSES_VIEW_EXPENSE_DATE + " = '" + dateString + "'" +
                " ORDER BY " + EXPENSES_VIEW_EXPENSE_ID + " DESC ";
        cursor = db.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_ID));
                String expenseDateString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_DATE));
                Date expenseDate = null;
                try {
                    expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_APARTMENT_ID));
                int tenantID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TENANT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_LEASE_ID));
                String description = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_ID));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_WAS_PAID)) > 0;
                String typeLabel = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_RECEIPT_PIC));
                incomeAndExpenses.add(new ExpenseLogEntry(id, expenseDate, amount, apartmentID, leaseID, tenantID, description, typeID, typeLabel, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return incomeAndExpenses;
    }

    public ArrayList<MoneyLogEntry> getIncomeAndExpensesBetweenDates(User user, Date startDateRange, Date endDateRange) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<MoneyLogEntry> incomeAndExpenses = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startDateRange);
        String endDateString = formatter.format(endDateRange);

        String Query = "Select * from " + INCOME_VIEW +
                " WHERE " +
                INCOME_VIEW_USER_ID + " = " + user.getId() + " AND " +
                INCOME_VIEW_IS_ACTIVE + " = 1" +
                " AND " + INCOME_VIEW_INCOME_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + INCOME_VIEW_INCOME_ID + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_INCOME_ID));
                String incomeDateString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_INCOME_DATE));
                Date incomeDate = null;
                try {
                    incomeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(incomeDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int tenantID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_APARTMENT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_LEASE_ID));
                int apartmentID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_APARTMENT_ID));
                String description = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TYPE_ID));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_WAS_RECEIVED)) > 0;
                String typeLabel = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_RECEIPT_PIC));
                incomeAndExpenses.add(new PaymentLogEntry(id, incomeDate, typeID, typeLabel, tenantID, leaseID, apartmentID, amount, description, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
        }

        Query = "Select * from " + EXPENSES_VIEW +
                " WHERE " +
                EXPENSES_VIEW_USER_ID + " = " + user.getId() + " AND " +
                EXPENSES_VIEW_IS_ACTIVE + " = 1" +
                " AND " + EXPENSES_VIEW_EXPENSE_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + EXPENSES_VIEW_EXPENSE_ID + " DESC ";
        cursor = db.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_ID));
                String expenseDateString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_DATE));
                Date expenseDate = null;
                try {
                    expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_APARTMENT_ID));
                int tenantID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TENANT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_LEASE_ID));
                String description = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_ID));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_WAS_PAID)) > 0;
                String typeLabel = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_RECEIPT_PIC));
                incomeAndExpenses.add(new ExpenseLogEntry(id, expenseDate, amount, apartmentID, leaseID, tenantID, description, typeID, typeLabel, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        MainArrayDataMethods mainArrayDataMethods = new MainArrayDataMethods();
        mainArrayDataMethods.sortMoneyByDateAsc(incomeAndExpenses);
        return incomeAndExpenses;
    }

    public ArrayList<MoneyLogEntry> getIncomeAndExpensesBetweenDatesNotCompleted(User user, Date startDateRange, Date endDateRange) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<MoneyLogEntry> incomeAndExpenses = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startDateRange);
        String endDateString = formatter.format(endDateRange);

        String Query = "Select * from " + INCOME_VIEW +
                " WHERE " +
                INCOME_VIEW_USER_ID + " = " + user.getId() + " AND " +
                INCOME_VIEW_IS_ACTIVE + " = 1" + " AND " +
                INCOME_VIEW_WAS_RECEIVED + " = 0 " +
                " AND " + INCOME_VIEW_INCOME_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + INCOME_VIEW_INCOME_ID + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_INCOME_ID));
                String incomeDateString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_INCOME_DATE));
                Date incomeDate = null;
                try {
                    incomeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(incomeDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int tenantID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_APARTMENT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_LEASE_ID));
                int apartmentID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_APARTMENT_ID));
                String description = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_TYPE_ID));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(INCOME_VIEW_WAS_RECEIVED)) > 0;
                String typeLabel = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_RECEIPT_PIC));
                incomeAndExpenses.add(new PaymentLogEntry(id, incomeDate, typeID, typeLabel, tenantID, leaseID, apartmentID, amount, description, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
        }

        Query = "Select * from " + EXPENSES_VIEW +
                " WHERE " +
                EXPENSES_VIEW_USER_ID + " = " + user.getId() + " AND " +
                EXPENSES_VIEW_IS_ACTIVE + " = 1" + " AND " +
                EXPENSES_VIEW_WAS_PAID + " = 0" +
                " AND " + EXPENSES_VIEW_EXPENSE_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                "' ORDER BY " + EXPENSES_VIEW_EXPENSE_ID + " DESC ";
        cursor = db.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_ID));
                String expenseDateString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_EXPENSE_DATE));
                Date expenseDate = null;
                try {
                    expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expenseDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
                BigDecimal amount = new BigDecimal(amountString);
                amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                int apartmentID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_APARTMENT_ID));
                int tenantID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TENANT_ID));
                int leaseID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_LEASE_ID));
                String description = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_DESCRIPTION));
                int typeID = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_ID));
                boolean wasCompleted = cursor.getInt(cursor.getColumnIndex(EXPENSES_VIEW_WAS_PAID)) > 0;
                String typeLabel = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_TYPE_LABEL));
                String receiptPic = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_RECEIPT_PIC));
                incomeAndExpenses.add(new ExpenseLogEntry(id, expenseDate, amount, apartmentID, leaseID, tenantID, description, typeID, typeLabel, receiptPic, wasCompleted));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        MainArrayDataMethods mainArrayDataMethods = new MainArrayDataMethods();
        mainArrayDataMethods.sortMoneyByDateAsc(incomeAndExpenses);
        return incomeAndExpenses;
    }

    public ArrayList<MoneyLogEntry> getIncomeAndExpensesByTenantIDWithinDates(User user, int tenantID, Date startDate, Date endDate) {
        ArrayList<MoneyLogEntry> incomeAndExpenses = new ArrayList<>();
        MainArrayDataMethods dm = new MainArrayDataMethods();

        incomeAndExpenses.addAll(getUsersExpensesByTenantIDWithinDates(user, tenantID, startDate, endDate));
        incomeAndExpenses.addAll(getUsersIncomeByTenantIDWithinDates(user, tenantID, startDate, endDate));
        dm.sortMoneyByDateDesc(incomeAndExpenses);
        return incomeAndExpenses;
    }

    public ArrayList<MoneyLogEntry> getIncomeAndExpensesByApartmentIDWithinDates(User user, int apartmentID, Date startDate, Date endDate) {
        ArrayList<MoneyLogEntry> incomeAndExpenses = new ArrayList<>();
        MainArrayDataMethods dm = new MainArrayDataMethods();

        incomeAndExpenses.addAll(getUsersExpensesByApartmentIDWithinDates(user, apartmentID, startDate, endDate));
        incomeAndExpenses.addAll(getUsersIncomeByApartmentIDWithinDates(user, apartmentID, startDate, endDate));
        dm.sortMoneyByDateDesc(incomeAndExpenses);
        return incomeAndExpenses;
    }

    public void addNewIncomeType(String label) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        values.put(TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK, 1);
        values.put(TYPES_LABEL_COLUMN, label);
        db.insert(TYPES_TABLE, null, values);
        db.close();
    }

    public void setTypeInactive(TypeTotal type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TYPES_IS_ACTIVE_COLUMN, 0);
        values.put(TYPES_LAST_UPDATE_COLUMN, " time('now') ");
        // updating row
        db.update(TYPES_TABLE, values, TYPES_ID_COLUMN_PK + " = ?", new String[]{String.valueOf(type.getTypeID())});
        db.close();
    }

    public void addNewExpenseType(String label) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        values.put(TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK, 2);
        values.put(TYPES_LABEL_COLUMN, label);
        db.insert(TYPES_TABLE, null, values);
        db.close();
    }

    public ArrayList<TypeTotal> getTotalForExpenseTypesWithinDates(User user, Date startDate, Date endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        TreeMap<String, Integer> expenseTypesTreeMap = getExpenseTypeLabelsIncludingInactiveTreeMap(db);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startDate);
        String endDateString = formatter.format(endDate);
        ArrayList<TypeTotal> typeTotals = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : expenseTypesTreeMap.entrySet()) {
            int typeID = entry.getValue();
            String typeLabel = entry.getKey();
            BigDecimal total = new BigDecimal(0);
            int numberOfItems = 0;

            String Query = "Select * from " + EXPENSES_VIEW +
                    " WHERE " +
                    EXPENSES_VIEW_USER_ID + " = " + user.getId() +
                    " AND " + EXPENSES_VIEW_IS_ACTIVE + " = 1" +
                    " AND " + EXPENSES_VIEW_EXPENSE_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                    "' AND " + EXPENSES_VIEW_TYPE_ID + " = " + typeID;
            Cursor cursor = db.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
                    BigDecimal amount = new BigDecimal(amountString);
                    amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                    total = total.add(amount);
                    numberOfItems++;
                    cursor.moveToNext();
                }
            }
            if (numberOfItems > 0) {
                TypeTotal typeTotal = new TypeTotal(typeID, typeLabel, total, numberOfItems);
                typeTotals.add(typeTotal);
            }
            cursor.close();
        }
        db.close();
        return typeTotals;
    }

    public ArrayList<TypeTotal> getTotalForExpenseTypesWithinDatesOnlyPaid(User user, Date startDate, Date endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        TreeMap<String, Integer> expenseTypesTreeMap = getExpenseTypeLabelsIncludingInactiveTreeMap(db);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startDate);
        String endDateString = formatter.format(endDate);
        ArrayList<TypeTotal> typeTotals = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : expenseTypesTreeMap.entrySet()) {
            int typeID = entry.getValue();
            String typeLabel = entry.getKey();
            BigDecimal total = new BigDecimal(0);
            int numberOfItems = 0;

            String Query = "Select * from " + EXPENSES_VIEW +
                    " WHERE " +
                    EXPENSES_VIEW_USER_ID + " = " + user.getId() +
                    " AND " + EXPENSES_VIEW_IS_ACTIVE + " = 1" +
                    " AND " + EXPENSES_VIEW_WAS_PAID + " " +
                    " AND " + EXPENSES_VIEW_EXPENSE_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                    "' AND " + EXPENSES_VIEW_TYPE_ID + " = " + typeID;
            Cursor cursor = db.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String amountString = cursor.getString(cursor.getColumnIndex(EXPENSES_VIEW_AMOUNT));
                    BigDecimal amount = new BigDecimal(amountString);
                    amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                    total = total.add(amount);
                    numberOfItems++;
                    cursor.moveToNext();
                }
            }
            if (numberOfItems > 0) {
                TypeTotal typeTotal = new TypeTotal(typeID, typeLabel, total, numberOfItems);
                typeTotals.add(typeTotal);
            }
            cursor.close();
        }
        db.close();
        return typeTotals;
    }

    public ArrayList<TypeTotal> getTotalForIncomeTypesWithinDates(User user, Date startDate, Date endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        TreeMap<String, Integer> incomeTypesTreeMap = getIncomeTypeLabelsIncludingInactiveTreeMap(db);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startDate);
        String endDateString = formatter.format(endDate);
        ArrayList<TypeTotal> typeTotals = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : incomeTypesTreeMap.entrySet()) {
            int typeID = entry.getValue();
            String typeLabel = entry.getKey();
            BigDecimal total = new BigDecimal(0);
            int numberOfItems = 0;

            String Query = "Select * from " + INCOME_VIEW +
                    " WHERE " +
                    INCOME_VIEW_USER_ID + " = " + user.getId() +
                    " AND " + INCOME_VIEW_IS_ACTIVE + " = 1" +
                    " AND " + INCOME_VIEW_INCOME_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                    "' AND " + INCOME_VIEW_TYPE_ID + " = " + typeID;
            Cursor cursor = db.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                    BigDecimal amount = new BigDecimal(amountString);
                    amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                    total = total.add(amount);
                    numberOfItems++;
                    cursor.moveToNext();
                }
            }
            if (!total.equals(new BigDecimal(0))) {
                TypeTotal typeTotal = new TypeTotal(typeID, typeLabel, total, numberOfItems);
                typeTotals.add(typeTotal);
            }
            cursor.close();
        }
        db.close();
        return typeTotals;
    }

    public ArrayList<TypeTotal> getTotalForIncomeTypesWithinDatesOnlyReceived(User user, Date startDate, Date endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        TreeMap<String, Integer> incomeTypesTreeMap = getIncomeTypeLabelsIncludingInactiveTreeMap(db);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = formatter.format(startDate);
        String endDateString = formatter.format(endDate);
        ArrayList<TypeTotal> typeTotals = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : incomeTypesTreeMap.entrySet()) {
            int typeID = entry.getValue();
            String typeLabel = entry.getKey();
            BigDecimal total = new BigDecimal(0);
            int numberOfItems = 0;

            String Query = "Select * from " + INCOME_VIEW +
                    " WHERE " +
                    INCOME_VIEW_USER_ID + " = " + user.getId() +
                    " AND " + INCOME_VIEW_WAS_RECEIVED + " = 1" +
                    " AND " + INCOME_VIEW_IS_ACTIVE + " = 1" +
                    " AND " + INCOME_VIEW_INCOME_DATE + " BETWEEN '" + startDateString + "' AND '" + endDateString +
                    "' AND " + INCOME_VIEW_TYPE_ID + " = " + typeID;
            Cursor cursor = db.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String amountString = cursor.getString(cursor.getColumnIndex(INCOME_VIEW_AMOUNT));
                    BigDecimal amount = new BigDecimal(amountString);
                    amount = amount.setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                    total = total.add(amount);
                    numberOfItems++;
                    cursor.moveToNext();
                }
            }
            if (!total.equals(new BigDecimal(0))) {
                TypeTotal typeTotal = new TypeTotal(typeID, typeLabel, total, numberOfItems);
                typeTotals.add(typeTotal);
            }
            cursor.close();
        }
        db.close();
        return typeTotals;
    }

    public ArrayList<TypeTotal> getIncomeTypeLabelsForRemoval() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<TypeTotal> types = new ArrayList<>();
        String Query = "Select * from " + TYPES_TABLE +
                " WHERE " + TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK + " = " + 1 + " AND " + TYPES_IS_ACTIVE_COLUMN + " = 1 " +
                " AND " + TYPES_ID_COLUMN_PK + " != " + 1 + " AND " + TYPES_ID_COLUMN_PK + " != " + 2;
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String label = cursor.getString(cursor.getColumnIndex(TYPES_LABEL_COLUMN));
                int id = cursor.getInt(cursor.getColumnIndex(TYPES_ID_COLUMN_PK));
                types.add(new TypeTotal(id, label));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return types;
    }

    public TypeTotal getIncomeTypeByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        TypeTotal type = null;
        String Query = "Select * from " + TYPES_TABLE +
                " WHERE " + TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK + " = " + 1 +
                " AND " + TYPES_ID_COLUMN_PK + " = " + id + " LIMIT 1 ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            String label = cursor.getString(cursor.getColumnIndex(TYPES_LABEL_COLUMN));
            type = new TypeTotal(id, label);
        }
        cursor.close();
        db.close();
        return type;
    }

    private TreeMap<String, Integer> getIncomeTypeLabelsIncludingInactiveTreeMap(SQLiteDatabase db) {
        TreeMap<String, Integer> incomeTypeLabels = new TreeMap<>();
        String Query = "Select * from " + TYPES_TABLE +
                " WHERE " + TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK + " = " + 1;
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String label = cursor.getString(cursor.getColumnIndex(TYPES_LABEL_COLUMN));
                int id = cursor.getInt(cursor.getColumnIndex(TYPES_ID_COLUMN_PK));
                incomeTypeLabels.put(label, id);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return incomeTypeLabels;
    }

    public WizardDueDate[] getMonthlyDateOptions() {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + PAYMENT_DATE_TABLE +
                " WHERE " + PAYMENT_DATE_TYPE_ID_COLUMN_FK + " = " + 1;
        Cursor cursor = db.rawQuery(Query, null);
        WizardDueDate[] monthlyDateOptions = new WizardDueDate[cursor.getCount()];
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                String label = cursor.getString(cursor.getColumnIndex(PAYMENT_DATE_LABEL_COLUMN));
                int id = cursor.getInt(cursor.getColumnIndex(PAYMENT_DATE_ID_COLUMN_PK));
                WizardDueDate wizardDueDate = new WizardDueDate(id, label);
                monthlyDateOptions[i] = wizardDueDate;
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return monthlyDateOptions;
    }

    public WizardDueDate[] getWeeklyDateOptions() {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + PAYMENT_DATE_TABLE +
                " WHERE " + PAYMENT_DATE_TYPE_ID_COLUMN_FK + " = " + 2;
        Cursor cursor = db.rawQuery(Query, null);
        WizardDueDate[] weeklyDateOptions = new WizardDueDate[cursor.getCount()];
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                String label = cursor.getString(cursor.getColumnIndex(PAYMENT_DATE_LABEL_COLUMN));
                int id = cursor.getInt(cursor.getColumnIndex(PAYMENT_DATE_ID_COLUMN_PK));
                WizardDueDate wizardDueDate = new WizardDueDate(id, label);
                weeklyDateOptions[i] = wizardDueDate;
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return weeklyDateOptions;
    }

    private TreeMap<String, Integer> getExpenseTypeLabelsIncludingInactiveTreeMap(SQLiteDatabase db) {
        TreeMap<String, Integer> expenseTypeLabels = new TreeMap<>();
        String Query = "Select * from " + TYPES_TABLE +
                " WHERE " + TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK + " = " + 2;// + " AND " + TYPE_LOOKUP_IS_ACTIVE_COLUMN + " = 1";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String label = cursor.getString(cursor.getColumnIndex(TYPES_LABEL_COLUMN));
                int id = cursor.getInt(cursor.getColumnIndex(TYPES_ID_COLUMN_PK));
                expenseTypeLabels.put(label, id);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return expenseTypeLabels;
    }

    public TreeMap<String, Integer> getIncomeTypeLabelsTreeMap() {
        SQLiteDatabase db = this.getReadableDatabase();
        TreeMap<String, Integer> incomeTypeLabels = new TreeMap<>();
        String Query = "Select * from " + TYPES_TABLE +
                " WHERE " + TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK + " = " + 1 + " AND " + TYPES_IS_ACTIVE_COLUMN + " = 1";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String label = cursor.getString(cursor.getColumnIndex(TYPES_LABEL_COLUMN));
                int id = cursor.getInt(cursor.getColumnIndex(TYPES_ID_COLUMN_PK));
                incomeTypeLabels.put(label, id);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return incomeTypeLabels;
    }

    public TreeMap<String, Integer> getExpenseTypeLabelsTreeMap() {
        SQLiteDatabase db = this.getReadableDatabase();
        TreeMap<String, Integer> expenseTypeLabels = new TreeMap<>();
        String Query = "Select * from " + TYPES_TABLE +
                " WHERE " + TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK + " = " + 2 + " AND " + TYPES_IS_ACTIVE_COLUMN + " = 1";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String label = cursor.getString(cursor.getColumnIndex(TYPES_LABEL_COLUMN));
                int id = cursor.getInt(cursor.getColumnIndex(TYPES_ID_COLUMN_PK));
                expenseTypeLabels.put(label, id);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return expenseTypeLabels;
    }

    public LinkedHashMap<String, Integer> getFrequencyLabelsMap() {
        SQLiteDatabase db = this.getReadableDatabase();
        LinkedHashMap<String, Integer> frequencyLabels = new LinkedHashMap<>();
        String Query = "Select * from " + LEASE_FREQUENCY_TABLE;
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String label = cursor.getString(cursor.getColumnIndex(LEASE_FREQUENCY_STRING_COLUMN));
                int id = cursor.getInt(cursor.getColumnIndex(LEASE_FREQUENCY_ID_COLUMN_PK));
                frequencyLabels.put(label, id);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return frequencyLabels;
    }

    public TypeTotal getExpenseTypeLabelByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        TypeTotal type = null;
        String Query = "Select * from " + TYPES_TABLE +
                " WHERE " + TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK + " = " + 2 +
                " AND " + TYPES_ID_COLUMN_PK + " = " + id + " LIMIT 1 ";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            String label = cursor.getString(cursor.getColumnIndex(TYPES_LABEL_COLUMN));
            type = new TypeTotal(id, label);
        }
        cursor.close();
        db.close();
        return type;
    }

    public ArrayList<TypeTotal> getExpenseTypeLabelsForRemoval() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<TypeTotal> types = new ArrayList<>();
        String Query = "Select * from " + TYPES_TABLE +
                " WHERE " + TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK + " = " + 2 + " AND " + TYPES_IS_ACTIVE_COLUMN + " = 1 " +
                " AND " + TYPES_ID_COLUMN_PK + " != " + 1 + " AND " + TYPES_ID_COLUMN_PK + " != " + 4;
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String label = cursor.getString(cursor.getColumnIndex(TYPES_LABEL_COLUMN));
                int id = cursor.getInt(cursor.getColumnIndex(TYPES_ID_COLUMN_PK));
                types.add(new TypeTotal(id, label));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return types;
    }

    //Methods for calendar querying
    public HashMap<String, Integer> getLeaseStartHMForCalendar(DateTime startRange, DateTime endRange, User user) {
        HashMap<String, Integer> leaseStartHM = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Date dateStart = startRange.toDate();
        Date dateEnd = endRange.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startRangeString = formatter.format(dateStart);
        String endRangeString = formatter.format(dateEnd);

        String Query = "Select * from " + LEASE_TABLE +
                " WHERE " +
                LEASE_USER_ID_COLUMN_FK + " = " + user.getId() +
                " AND " + LEASE_IS_ACTIVE_COLUMN + " = 1" +
                " AND " + LEASE_START_DATE_COLUMN + " BETWEEN '" + startRangeString + "' AND '" + endRangeString +
                "' ORDER BY " + LEASE_START_DATE_COLUMN + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String startDateString = cursor.getString(cursor.getColumnIndex(LEASE_START_DATE_COLUMN));
                Date startDate = null;
                try {
                    startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (leaseStartHM.containsKey(startDate.toString())) {
                    int i = leaseStartHM.get(startDate.toString());
                    i++;
                    leaseStartHM.put(startDate.toString(), i);
                } else {
                    leaseStartHM.put(startDate.toString(), 1);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return leaseStartHM;
    }

    public HashMap<String, Integer> getLeaseEndHMForCalendar(DateTime startRange, DateTime endRange, User user) {
        HashMap<String, Integer> leaseEndHM = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Date dateStart = startRange.toDate();
        Date dateEnd = endRange.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startRangeString = formatter.format(dateStart);
        String endRangeString = formatter.format(dateEnd);

        String Query = "Select * from " + LEASE_TABLE +
                " WHERE " +
                LEASE_USER_ID_COLUMN_FK + " = " + user.getId() +
                " AND " + LEASE_IS_ACTIVE_COLUMN + " = 1" +
                " AND " + LEASE_END_DATE_COLUMN + " BETWEEN '" + startRangeString + "' AND '" + endRangeString +
                "' ORDER BY " + LEASE_START_DATE_COLUMN + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String startDateString = cursor.getString(cursor.getColumnIndex(LEASE_END_DATE_COLUMN));
                Date startDate = null;
                try {
                    startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (leaseEndHM.containsKey(startDate.toString())) {
                    int i = leaseEndHM.get(startDate.toString());
                    i++;
                    leaseEndHM.put(startDate.toString(), i);
                } else {
                    leaseEndHM.put(startDate.toString(), 1);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return leaseEndHM;
    }

    public HashMap<String, Integer> getExpensesHMForCalendar(DateTime startRange, DateTime endRange, User user) {
        HashMap<String, Integer> expenseHM = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Date dateStart = startRange.toDate();
        Date dateEnd = endRange.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startRangeString = formatter.format(dateStart);
        String endRangeString = formatter.format(dateEnd);

        String Query = "Select * from " + EXPENSE_LOG_TABLE +
                " WHERE " +
                EXPENSE_LOG_USER_ID_COLUMN_FK + " = " + user.getId() +
                " AND " + EXPENSE_LOG_IS_ACTIVE_COLUMN + " = 1" +
                " AND " + EXPENSE_LOG_EXPENSE_DATE_COLUMN + " BETWEEN '" + startRangeString + "' AND '" + endRangeString +
                "' ORDER BY " + EXPENSE_LOG_EXPENSE_DATE_COLUMN + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String dateString = cursor.getString(cursor.getColumnIndex(EXPENSE_LOG_EXPENSE_DATE_COLUMN));
                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (expenseHM.containsKey(date.toString())) {
                    int i = expenseHM.get(date.toString());
                    i++;
                    expenseHM.put(date.toString(), i);
                } else {
                    expenseHM.put(date.toString(), 1);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return expenseHM;
    }

    public HashMap<String, Integer> getIncomeHMForCalendar(DateTime startRange, DateTime endRange, User user) {
        HashMap<String, Integer> incomeHM = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Date dateStart = startRange.toDate();
        Date dateEnd = endRange.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startRangeString = formatter.format(dateStart);
        String endRangeString = formatter.format(dateEnd);

        String Query = "Select * from " + PAYMENT_LOG_TABLE +
                " WHERE " +
                PAYMENT_LOG_USER_ID_COLUMN_FK + " = " + user.getId() +
                " AND " + PAYMENT_LOG_IS_ACTIVE_COLUMN + " = 1" +
                " AND " + PAYMENT_LOG_PAYMENT_DATE_COLUMN + " BETWEEN '" + startRangeString + "' AND '" + endRangeString +
                "' ORDER BY " + PAYMENT_LOG_PAYMENT_DATE_COLUMN + " DESC ";
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String dateString = cursor.getString(cursor.getColumnIndex(PAYMENT_LOG_PAYMENT_DATE_COLUMN));
                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (incomeHM.containsKey(date.toString())) {
                    int i = incomeHM.get(date.toString());
                    i++;
                    incomeHM.put(date.toString(), i);
                } else {
                    incomeHM.put(date.toString(), 1);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return incomeHM;
    }

    public String getFrequencyByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String frequency = "";
        String Query = "Select * from " + LEASE_FREQUENCY_TABLE +
                " WHERE " +
                LEASE_FREQUENCY_ID_COLUMN_PK + " = " + id + " LIMIT 1";
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            frequency = cursor.getString(cursor.getColumnIndex(LEASE_FREQUENCY_STRING_COLUMN));
        }
        cursor.close();
        db.close();
        return frequency;
    }

    public String getDueDateByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String dueDate = "";
        String Query = "Select * from " + PAYMENT_DATE_TABLE +
                " WHERE " +
                PAYMENT_DATE_ID_COLUMN_PK + " = " + id + " LIMIT 1";
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            dueDate = cursor.getString(cursor.getColumnIndex(PAYMENT_DATE_LABEL_COLUMN));
        }
        cursor.close();
        db.close();
        return dueDate;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUserInfoTable(db);
        createTypeLookupTable(db);
        createTypesTable(db);
        createApartmentInfoTable(db);
        createApartmentPicsTable(db);
        createTenantInfoTable(db);
        createLeaseTable(db);
        createLeaseSecondaryTenantsTable(db);
        createSecondaryTenantLeaseView(db);
        createLeaseFrequencyTable(db);
        createFrequencyLookupTable(db);
        createDueDateTable(db);
        createExpenseLogTable(db);
        createPaymentLogTable(db);
        populateTypeLookupTable(db);
        populateTypesTable(db);
        populateLeaseFrequencyTable(db);
        populateFrequencyLookupTable(db);
        populatePaymentDatesTable(db);
        createApartmentView(db);
        createTenantView(db);
        createExpenseView(db);
        createIncomeView(db);
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
       // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
       //     db.execSQL("PRAGMA foreign_keys=1;");
       // } else {
       //     db.setForeignKeyConstraintsEnabled(true);
       // }
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
                TENANT_INFO_NOTES_COLUMN + " VARCHAR(150), " +
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
                APARTMENT_INFO_STATE_COLUMN + " VARCHAR(15), " +
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

    private void createLeaseTable(SQLiteDatabase db) {
        String apartmentTable = "CREATE TABLE IF NOT EXISTS " + LEASE_TABLE + " ( " +
                LEASE_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LEASE_USER_ID_COLUMN_FK + " INTEGER REFERENCES " + USER_INFO_TABLE + "(" + USER_INFO_ID_COLUMN_PK + "), " +
                LEASE_START_DATE_COLUMN + " DATETIME, " +
                LEASE_END_DATE_COLUMN + " DATETIME, " +
                LEASE_PAYMENT_DAY_COLUMN_FK + " INTEGER REFERENCES " + PAYMENT_DATE_TABLE + "(" + PAYMENT_DATE_ID_COLUMN_PK + ")," +
                LEASE_MONTHLY_RENT_COST_COLUMN + " INTEGER, " +
                LEASE_DEPOSIT_AMOUNT_COLUMN + " INTEGER, " +
                LEASE_PAYMENT_FREQUENCY_ID_FK + " INTEGER REFERENCES " + LEASE_FREQUENCY_TABLE + "(" +LEASE_FREQUENCY_ID_COLUMN_PK + "), " +
                LEASE_APARTMENT_ID_COLUMN + " INTEGER REFERENCES " + APARTMENT_INFO_TABLE + "(" + APARTMENT_INFO_ID_COLUMN_PK + "), " +
                LEASE_PRIMARY_TENANT_ID_COLUMN + " INTEGER REFERENCES " + TENANT_INFO_TABLE + "(" + TENANT_INFO_ID_COLUMN_PK + "), " +
                LEASE_NOTES_COLUMN + " VARCHAR(150), " +
                LEASE_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                LEASE_LAST_UPDATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                LEASE_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(apartmentTable);
    }

    private void createLeaseSecondaryTenantsTable(SQLiteDatabase db) {
        String apartmentTable = "CREATE TABLE IF NOT EXISTS " + LEASE_SECONDARY_TENANTS_TABLE + " ( " +
                LEASE_SECONDARY_TENANTS_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LEASE_SECONDARY_TENANTS_TENANT_ID_COLUMN_FK + " INTEGER REFERENCES " + TENANT_INFO_TABLE + "(" + TENANT_INFO_ID_COLUMN_PK + "), " +
                LEASE_SECONDARY_TENANTS_LEASE_ID_COLUMN_FK + " INTEGER REFERENCES " + LEASE_TABLE + "(" + LEASE_ID_COLUMN_PK + "), " +
                LEASE_SECONDARY_TENANTS_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                LEASE_SECONDARY_TENANTS_LAST_UPDATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                LEASE_SECONDARY_TENANTS_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(apartmentTable);
    }

    private void createLeaseFrequencyTable(SQLiteDatabase db) {
        String apartmentTable = "CREATE TABLE IF NOT EXISTS " + LEASE_FREQUENCY_TABLE + " ( " +
                LEASE_FREQUENCY_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LEASE_FREQUENCY_TYPE_ID_COLUMN_FK + " INTEGER REFERENCES " + FREQUENCY_LOOKUP_TABLE + "(" + FREQUENCY_LOOKUP_ID_COLUMN_PK + "), " +
                LEASE_FREQUENCY_STRING_COLUMN + " VARCHAR(13), " +
                LEASE_FREQUENCY_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                LEASE_FREQUENCY_LAST_UPDATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                LEASE_FREQUENCY_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(apartmentTable);
    }

    private void createPaymentLogTable(SQLiteDatabase db) {
        String paymentTable = "CREATE TABLE IF NOT EXISTS " + PAYMENT_LOG_TABLE + " ( " +
                PAYMENT_LOG_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PAYMENT_LOG_USER_ID_COLUMN_FK + " INTEGER REFERENCES " + USER_INFO_TABLE + "(" + USER_INFO_ID_COLUMN_PK + "), " +
                PAYMENT_LOG_PAYMENT_DATE_COLUMN + " DATETIME, " +
                PAYMENT_LOG_TYPE_ID_COLUMN_FK + " INTEGER REFERENCES " + TYPES_TABLE + "(" + TYPES_ID_COLUMN_PK + "), " +
                PAYMENT_LOG_RECEIPT_PIC + "  VARCHAR(50), " +
                PAYMENT_LOG_APARTMENT_ID_COLUMN_FK + " INTEGER REFERENCES " + APARTMENT_INFO_TABLE + "(" + APARTMENT_INFO_ID_COLUMN_PK + "), " +
                PAYMENT_LOG_TENANT_ID_COLUMN_FK + " INTEGER REFERENCES " + TENANT_INFO_TABLE + "(" + TENANT_INFO_ID_COLUMN_PK + "), " +
                PAYMENT_LOG_LEASE_ID_COLUMN_FK + " INTEGER REFERENCES " + LEASE_TABLE + "(" + LEASE_ID_COLUMN_PK + "), " +
                PAYMENT_LOG_DESCRIPTION_COLUMN + " VARCHAR(150), " +
                PAYMENT_LOG_AMOUNT_COLUMN + " INTEGER, " +
                PAYMENT_LOG_WAS_RECEIVED_COLUMN + " BOOLEAN NOT NULL DEFAULT 0, " +
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
                EXPENSE_LOG_WAS_PAID_COLUMN + " BOOLEAN NOT NULL DEFAULT 0, " +
                EXPENSE_LOG_APARTMENT_ID_COLUMN_FK + " INTEGER REFERENCES " + APARTMENT_INFO_TABLE + "(" + APARTMENT_INFO_ID_COLUMN_PK + "), " +
                EXPENSE_LOG_TENANT_ID_COLUMN_FK + " INTEGER REFERENCES " + TENANT_INFO_TABLE + "(" + TENANT_INFO_ID_COLUMN_PK + "), " +
                EXPENSE_LOG_LEASE_ID_COLUMN_FK + " INTEGER REFERENCES " + LEASE_TABLE + "(" + LEASE_ID_COLUMN_PK + "), " +
                EXPENSE_LOG_DESCRIPTION_COLUMN + " VARCHAR(150), " +
                EXPENSE_LOG_TYPE_ID_COLUMN_FK + " INTEGER REFERENCES " + TYPES_TABLE + "(" + TYPES_ID_COLUMN_PK + "), " +
                EXPENSE_LOG_RECEIPT_PIC + "  VARCHAR(50), " +
                EXPENSE_LOG_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                EXPENSE_LOG_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                EXPENSE_LOG_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(expenseTable);
    }

    private void createTypeLookupTable(SQLiteDatabase db) {
        String typeTable = "CREATE TABLE IF NOT EXISTS " + TYPE_LOOKUP_TABLE + " ( " +
                TYPE_LOOKUP_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TYPE_LOOKUP_LABEL_COLUMN + " VARCHAR(10), " +
                TYPE_LOOKUP_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                TYPE_LOOKUP_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                TYPE_LOOKUP_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(typeTable);
    }

    private void createTypesTable(SQLiteDatabase db) {
        String typeLookupTable = "CREATE TABLE IF NOT EXISTS " + TYPES_TABLE + " ( " +
                TYPES_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK + " INTEGER REFERENCES " + TYPE_LOOKUP_TABLE + "(" + TYPE_LOOKUP_ID_COLUMN_PK + "), " +
                TYPES_LABEL_COLUMN + " VARCHAR(15), " +
                TYPES_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                TYPES_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                TYPES_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(typeLookupTable);
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

    private void createFrequencyLookupTable(SQLiteDatabase db) {
        String typeTable = "CREATE TABLE IF NOT EXISTS " + FREQUENCY_LOOKUP_TABLE + " ( " +
                FREQUENCY_LOOKUP_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FREQUENCY_LOOKUP_TYPE_COLUMN + " VARCHAR(10), " +
                FREQUENCY_LOOKUP_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                FREQUENCY_LOOKUP_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                FREQUENCY_LOOKUP_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(typeTable);
    }

    private void createDueDateTable(SQLiteDatabase db) {
        String typeLookupTable = "CREATE TABLE IF NOT EXISTS " + PAYMENT_DATE_TABLE + " ( " +
                PAYMENT_DATE_ID_COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PAYMENT_DATE_TYPE_ID_COLUMN_FK + " INTEGER REFERENCES " + FREQUENCY_LOOKUP_TABLE + "(" + FREQUENCY_LOOKUP_ID_COLUMN_PK + "), " +
                PAYMENT_DATE_LABEL_COLUMN + " VARCHAR(15), " +
                PAYMENT_DATE_DATE_CREATED_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                PAYMENT_DATE_LAST_UPDATE_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                PAYMENT_DATE_IS_ACTIVE_COLUMN + " BOOLEAN NOT NULL DEFAULT 1 " +
                ");";
        db.execSQL(typeLookupTable);
    }

    private void populateTypeLookupTable(SQLiteDatabase db) {
        ContentValues cv;
        String[] typesArray = context.getResources().getStringArray(R.array.DBTypes_array);
        for (String i : typesArray) {
            cv = new ContentValues();
            cv.put(TYPE_LOOKUP_LABEL_COLUMN, i);
            db.insert(TYPE_LOOKUP_TABLE, null, cv);
        }
    }

    private void populateTypesTable(SQLiteDatabase db) {
        ContentValues cv;
        String[] incomeTypeLabels = context.getResources().getStringArray(R.array.DBIncomeLookupTypes_array);
        String[] expenseTypeLabels = context.getResources().getStringArray(R.array.DBExpenseLookupTypes_array);
        for (String i : incomeTypeLabels) {
            cv = new ContentValues();
            cv.put(TYPES_LABEL_COLUMN, i);
            cv.put(TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK, 1);
            db.insert(TYPES_TABLE, null, cv);
        }
        for (String i : expenseTypeLabels) {
            cv = new ContentValues();
            cv.put(TYPES_LABEL_COLUMN, i);
            cv.put(TYPES_CATEGORY_LOOKUP_ID_COLUMN_FK, 2);
            db.insert(TYPES_TABLE, null, cv);
        }
    }

    private void populateFrequencyLookupTable(SQLiteDatabase db) {
        ContentValues cv;
        String[] typesArray = context.getResources().getStringArray(R.array.DBFrequencyLookupTypes_array);
        for (String i : typesArray) {
            cv = new ContentValues();
            cv.put(FREQUENCY_LOOKUP_TYPE_COLUMN, i);
            db.insert(FREQUENCY_LOOKUP_TABLE, null, cv);
        }
    }

    private void populatePaymentDatesTable(SQLiteDatabase db) {
        ContentValues cv;
        String[] monthlyChoices = context.getResources().getStringArray(R.array.date_array);
        String[] weeklyChoices = context.getResources().getStringArray(R.array.day_array);
        for (String i : monthlyChoices) {
            cv = new ContentValues();
            cv.put(PAYMENT_DATE_LABEL_COLUMN, i);
            cv.put(PAYMENT_DATE_TYPE_ID_COLUMN_FK, 1);
            db.insert(PAYMENT_DATE_TABLE, null, cv);
        }
        for (String i : weeklyChoices) {
            cv = new ContentValues();
            cv.put(PAYMENT_DATE_LABEL_COLUMN, i);
            cv.put(PAYMENT_DATE_TYPE_ID_COLUMN_FK, 2);
            db.insert(PAYMENT_DATE_TABLE, null, cv);
        }
    }

    private void populateLeaseFrequencyTable(SQLiteDatabase db) {
        ContentValues cv;
        String[] monthlyChoices = context.getResources().getStringArray(R.array.frequency_array_monthly);
        String[] weeklyChoices = context.getResources().getStringArray(R.array.frequency_array_weekly);
        for (String i : monthlyChoices) {
            cv = new ContentValues();
            cv.put(LEASE_FREQUENCY_TYPE_ID_COLUMN_FK, 1);
            cv.put(LEASE_FREQUENCY_STRING_COLUMN, i);
            db.insert(LEASE_FREQUENCY_TABLE, null, cv);
        }
        for (String i : weeklyChoices) {
            cv = new ContentValues();
            cv.put(LEASE_FREQUENCY_TYPE_ID_COLUMN_FK, 2);
            cv.put(LEASE_FREQUENCY_STRING_COLUMN, i);
            db.insert(LEASE_FREQUENCY_TABLE, null, cv);
        }
    }

    private void createApartmentView(SQLiteDatabase db) {
        String insert = "CREATE VIEW IF NOT EXISTS " + APARTMENTS_VIEW + " AS" +
                " SELECT " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_USER_ID_COLUMN_FK + " AS " + APARTMENTS_VIEW_USER_ID + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_ID_COLUMN_PK + " AS " + APARTMENTS_VIEW_APARTMENT_ID + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_STREET1_COLUMN + " AS " + APARTMENTS_VIEW_STREET_1 + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_STREET2_COLUMN + " AS " + APARTMENTS_VIEW_STREET_2 + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_CITY_COLUMN + " AS " + APARTMENTS_VIEW_CITY + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_STATE_COLUMN + " AS " + APARTMENTS_VIEW_STATE + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_ZIP_COLUMN + " AS " + APARTMENTS_VIEW_ZIP + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_DESCRIPTION_COLUMN + " AS " + APARTMENTS_VIEW_DESCRIPTION + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_MAIN_PIC_COLUMN + " AS " + APARTMENTS_VIEW_MAIN_PIC + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_NOTES_COLUMN + " AS " + APARTMENTS_VIEW_NOTES + ", " +
                " CASE WHEN " + LEASE_TABLE + ".totalCount > 0 THEN 1 ELSE 0 END " + APARTMENTS_VIEW_IS_RENTED + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_DATE_CREATED_COLUMN + " AS " + APARTMENTS_VIEW_DATE_CREATED + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_LAST_UPDATE_COLUMN + " AS " + APARTMENTS_VIEW_LAST_UPDATE + ", " +
                APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_IS_ACTIVE_COLUMN + " AS " + APARTMENTS_VIEW_IS_ACTIVE + " " +
                " FROM " +
                APARTMENT_INFO_TABLE +
                " LEFT JOIN (" +
                " SELECT " + LEASE_APARTMENT_ID_COLUMN + ", " +
                " COUNT(DISTINCT " + LEASE_APARTMENT_ID_COLUMN + ")" + " AS totalCount " +
                " FROM " + LEASE_TABLE +
                " WHERE " + LEASE_IS_ACTIVE_COLUMN + " = 1 " +
                " AND " + LEASE_END_DATE_COLUMN + " >= datetime(CURRENT_TIMESTAMP, 'localtime') " +
                " AND " + LEASE_START_DATE_COLUMN + " <= datetime(CURRENT_TIMESTAMP, 'localtime') " +
                " GROUP BY " + LEASE_APARTMENT_ID_COLUMN +
                ") " + LEASE_TABLE + " ON " + APARTMENT_INFO_TABLE + "." + APARTMENT_INFO_ID_COLUMN_PK + " = " + LEASE_TABLE + "." + LEASE_APARTMENT_ID_COLUMN +
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
                TENANT_INFO_TABLE + "." + TENANT_INFO_EMAIL_COLUMN + " AS " + TENANTS_VIEW_EMAIL + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_EMERGENCY_FIRST_NAME + " AS " + TENANTS_VIEW_EMERGENCY_FIRST_NAME + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_EMERGENCY_LAST_NAME + " AS " + TENANTS_VIEW_EMERGENCY_LAST_NAME + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_EMERGENCY_PHONE + " AS " + TENANTS_VIEW_EMERGENCY_PHONE + ", " +
                " CASE WHEN " + LEASE_TABLE + ".totalPrimaryCount > 0 OR " + SECONDARY_TENANTS_VIEW + ".totalSecondaryCount > 0 THEN 1 ELSE 0 END " + TENANTS_VIEW_DOES_TENANT_CURRENTLY_HAVE_LEASE + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_NOTES_COLUMN + " AS " + TENANTS_VIEW_NOTES + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_DATE_CREATED_COLUMN + " AS " + TENANTS_VIEW_DATE_CREATED + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_LAST_UPDATE_COLUMN + " AS " + TENANTS_VIEW_LAST_UPDATE + ", " +
                TENANT_INFO_TABLE + "." + TENANT_INFO_IS_ACTIVE_COLUMN + " AS " + TENANTS_VIEW_IS_ACTIVE + " " +
                "FROM " +
                TENANT_INFO_TABLE +
                " LEFT JOIN (" +
                " SELECT " + LEASE_PRIMARY_TENANT_ID_COLUMN + ", " +
                " COUNT(DISTINCT " + LEASE_PRIMARY_TENANT_ID_COLUMN + ")" + " AS totalPrimaryCount " +
                " FROM " + LEASE_TABLE +
                " WHERE " + LEASE_IS_ACTIVE_COLUMN + " = 1 " +
                " AND " + LEASE_END_DATE_COLUMN + " >= datetime(CURRENT_TIMESTAMP, 'localtime') " +
                " AND " + LEASE_START_DATE_COLUMN + " <= datetime(CURRENT_TIMESTAMP, 'localtime') " +
                " GROUP BY " + LEASE_PRIMARY_TENANT_ID_COLUMN +
                ") " + LEASE_TABLE + " ON " + TENANT_INFO_TABLE + "." + TENANT_INFO_ID_COLUMN_PK + " = " + LEASE_TABLE + "." + LEASE_PRIMARY_TENANT_ID_COLUMN +
                " LEFT JOIN (" +
                " SELECT " + SECONDARY_TENANTS_VIEW_TENANT_ID + ", " +
                " COUNT(DISTINCT " + SECONDARY_TENANTS_VIEW_TENANT_ID + ")" + " AS totalSecondaryCount " +
                " FROM " + SECONDARY_TENANTS_VIEW +
                " WHERE " + SECONDARY_TENANTS_VIEW_IS_ACTIVE + " = 1 " +
                " AND " + SECONDARY_TENANTS_VIEW_LEASE_END + " >= datetime(CURRENT_TIMESTAMP, 'localtime') " +
                " AND " + SECONDARY_TENANTS_VIEW_LEASE_START + " <= datetime(CURRENT_TIMESTAMP, 'localtime') " +
                " GROUP BY " + SECONDARY_TENANTS_VIEW_TENANT_ID +
                ") " + SECONDARY_TENANTS_VIEW + " ON " + TENANT_INFO_TABLE + "." + TENANT_INFO_ID_COLUMN_PK + " = " + SECONDARY_TENANTS_VIEW + "." + SECONDARY_TENANTS_VIEW_TENANT_ID +
                ";";
        db.execSQL(insert);
    }

    private void createExpenseView(SQLiteDatabase db) {
        String insert = "CREATE VIEW IF NOT EXISTS " + EXPENSES_VIEW + " AS" +
                " SELECT " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_ID_COLUMN_PK + " AS " + EXPENSES_VIEW_EXPENSE_ID + ", " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_USER_ID_COLUMN_FK + " AS " + EXPENSES_VIEW_USER_ID + ", " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_APARTMENT_ID_COLUMN_FK + " AS " + EXPENSES_VIEW_APARTMENT_ID + ", " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_LEASE_ID_COLUMN_FK + " AS " + EXPENSES_VIEW_LEASE_ID + ", " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_TENANT_ID_COLUMN_FK + " AS " + EXPENSES_VIEW_TENANT_ID + ", " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_EXPENSE_DATE_COLUMN + " AS " + EXPENSES_VIEW_EXPENSE_DATE + ", " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_AMOUNT_COLUMN + " AS " + EXPENSES_VIEW_AMOUNT + ", " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_WAS_PAID_COLUMN + " AS " + EXPENSES_VIEW_WAS_PAID + ", " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_DESCRIPTION_COLUMN + " AS " + EXPENSES_VIEW_DESCRIPTION + ", " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_TYPE_ID_COLUMN_FK + " AS " + EXPENSES_VIEW_TYPE_ID + ", " +
                TYPES_TABLE + "." + TYPES_LABEL_COLUMN + " AS " + EXPENSES_VIEW_TYPE_LABEL + ", " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_RECEIPT_PIC + " AS " + EXPENSES_VIEW_RECEIPT_PIC + ", " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_DATE_CREATED_COLUMN + " AS " + EXPENSES_VIEW_DATE_CREATED + ", " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_LAST_UPDATE_COLUMN + " AS " + EXPENSES_VIEW_LAST_UPDATE + ", " +
                EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_IS_ACTIVE_COLUMN + " AS " + EXPENSES_VIEW_IS_ACTIVE + " " +
                "FROM " +
                EXPENSE_LOG_TABLE +
                " LEFT JOIN " + TYPES_TABLE + " ON " + TYPES_TABLE + "." + TYPES_ID_COLUMN_PK+ " = " + EXPENSE_LOG_TABLE + "." + EXPENSE_LOG_TYPE_ID_COLUMN_FK +
                ";";
        db.execSQL(insert);
    }

    private void createIncomeView(SQLiteDatabase db) {
        String insert = "CREATE VIEW IF NOT EXISTS " + INCOME_VIEW + " AS" +
                " SELECT " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_ID_COLUMN_PK + " AS " + INCOME_VIEW_INCOME_ID + ", " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_USER_ID_COLUMN_FK + " AS " + INCOME_VIEW_USER_ID + ", " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_APARTMENT_ID_COLUMN_FK + " AS " + INCOME_VIEW_APARTMENT_ID + ", " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_LEASE_ID_COLUMN_FK + " AS " + INCOME_VIEW_LEASE_ID + ", " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_TENANT_ID_COLUMN_FK + " AS " + INCOME_VIEW_TENANT_ID + ", " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_PAYMENT_DATE_COLUMN + " AS " + INCOME_VIEW_INCOME_DATE + ", " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_AMOUNT_COLUMN + " AS " + INCOME_VIEW_AMOUNT + ", " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_WAS_RECEIVED_COLUMN + " AS " + INCOME_VIEW_WAS_RECEIVED + ", " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_DESCRIPTION_COLUMN + " AS " + INCOME_VIEW_DESCRIPTION + ", " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_TYPE_ID_COLUMN_FK + " AS " + INCOME_VIEW_TYPE_ID + ", " +
                TYPES_TABLE + "." + TYPES_LABEL_COLUMN + " AS " + INCOME_VIEW_TYPE_LABEL + ", " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_RECEIPT_PIC + " AS " + INCOME_VIEW_RECEIPT_PIC + ", " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_DATE_CREATED_COLUMN + " AS " + INCOME_VIEW_DATE_CREATED + ", " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_LAST_UPDATE_COLUMN + " AS " + INCOME_VIEW_LAST_UPDATE + ", " +
                PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_IS_ACTIVE_COLUMN + " AS " + INCOME_VIEW_IS_ACTIVE + " " +
                "FROM " +
                PAYMENT_LOG_TABLE +
                " LEFT JOIN " + TYPES_TABLE + " ON " + TYPES_TABLE + "." + TYPES_ID_COLUMN_PK + " = " + PAYMENT_LOG_TABLE + "." + PAYMENT_LOG_TYPE_ID_COLUMN_FK +
                ";";
        db.execSQL(insert);
    }

    private void createSecondaryTenantLeaseView(SQLiteDatabase db) {
        String insert = "CREATE VIEW IF NOT EXISTS " + SECONDARY_TENANTS_VIEW + " AS" +
                " SELECT " +
                LEASE_SECONDARY_TENANTS_TABLE + "." + LEASE_SECONDARY_TENANTS_LEASE_ID_COLUMN_FK + " AS " + SECONDARY_TENANTS_VIEW_LEASE_ID + ", " +
                LEASE_SECONDARY_TENANTS_TABLE + "." + LEASE_SECONDARY_TENANTS_TENANT_ID_COLUMN_FK + " AS " + SECONDARY_TENANTS_VIEW_TENANT_ID + ", " +
                LEASE_TABLE + "." + LEASE_START_DATE_COLUMN + " AS " + SECONDARY_TENANTS_VIEW_LEASE_START + ", " +
                LEASE_TABLE + "." + LEASE_END_DATE_COLUMN + " AS " + SECONDARY_TENANTS_VIEW_LEASE_END + ", " +
                LEASE_SECONDARY_TENANTS_TABLE + "." + LEASE_SECONDARY_TENANTS_DATE_CREATED_COLUMN + " AS " + SECONDARY_TENANTS_VIEW_DATE_CREATED + ", " +
                LEASE_SECONDARY_TENANTS_TABLE + "." + LEASE_SECONDARY_TENANTS_LAST_UPDATED_COLUMN + " AS " + SECONDARY_TENANTS_VIEW_LAST_UPDATE + ", " +
                LEASE_SECONDARY_TENANTS_TABLE + "." + LEASE_SECONDARY_TENANTS_IS_ACTIVE_COLUMN + " AS " + SECONDARY_TENANTS_VIEW_IS_ACTIVE + " " +
                "FROM " +
                LEASE_SECONDARY_TENANTS_TABLE +
                " LEFT JOIN " + LEASE_TABLE + " ON " + LEASE_TABLE + "." + LEASE_ID_COLUMN_PK + " = " + LEASE_SECONDARY_TENANTS_TABLE + "." + LEASE_SECONDARY_TENANTS_LEASE_ID_COLUMN_FK +
                ";";
        db.execSQL(insert);
    }
}
