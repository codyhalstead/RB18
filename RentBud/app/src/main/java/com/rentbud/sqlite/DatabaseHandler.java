package com.rentbud.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rentbud.helpers.RandomNumberGenerator;
import com.rentbud.model.User;

/**
 * Created by Cody on 12/12/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper{
    public static int DATABASE_VERSION = 1;
    public static String DB_FILE_NAME = "allData.db";
    public static final String USER_INFO_TABLE = "user_info_table";
    public static final String USER_INFO_ID_COLUMN = "_id";
    public static final String USER_INFO_NAME_COLUMN = "user_info_name";
    public static final String USER_INFO_EMAIL_COLUMN = "user_info_email";
    public static final String USER_INFO_PASSWORD_COLUMN = "user_info_password";
    public static final String USER_INFO_IS_VERIFIED_COLUMN = "user_info_is_verified";
    public static final String USER_INFO_VERIFICATION_NUMBER_COLUMN = "user_info_verification_number";

    RandomNumberGenerator verificationGenerator;

    public DatabaseHandler(Context context) {
        super(context, DB_FILE_NAME, null, DATABASE_VERSION);
        verificationGenerator = new RandomNumberGenerator();
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_INFO_NAME_COLUMN, user.getName());
        contentValues.put(USER_INFO_EMAIL_COLUMN, user.getEmail());
        contentValues.put(USER_INFO_PASSWORD_COLUMN, user.getPassword());
        contentValues.put(USER_INFO_VERIFICATION_NUMBER_COLUMN, verificationGenerator.gererateVerificationNumber(5));
        db.insert(USER_INFO_TABLE, null, contentValues);
        db.close();
    }

    public void changeVerificationNumber(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_INFO_VERIFICATION_NUMBER_COLUMN, verificationGenerator.gererateVerificationNumber(5));
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
        db.update(USER_INFO_TABLE, values, USER_INFO_ID_COLUMN + " = ?", new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public void deleteUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(USER_INFO_TABLE, USER_INFO_ID_COLUMN + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public boolean checkUser(String email) {

        // array of columns to fetch
        String[] columns = {
                USER_INFO_ID_COLUMN
        };
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
        String[] columns = {
                USER_INFO_ID_COLUMN
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = USER_INFO_EMAIL_COLUMN + " = ?" + " AND " + USER_INFO_PASSWORD_COLUMN + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(USER_INFO_TABLE, columns, selection, selectionArgs, null, null,null);
        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        return  (cursorCount > 0);
    }

    public String getUserName(String email){
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlTable = "CREATE TABLE IF NOT EXISTS " + USER_INFO_TABLE + " ( " +
                USER_INFO_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_INFO_NAME_COLUMN + " VARCHAR, " +
                USER_INFO_EMAIL_COLUMN + " VARCHAR(25), " +
                USER_INFO_PASSWORD_COLUMN + " VARCHAR(15), " +
                USER_INFO_IS_VERIFIED_COLUMN + " BOOLEAN NOT NULL DEFAULT 0, " +
                USER_INFO_VERIFICATION_NUMBER_COLUMN + " VARCHAR(5) )";
        db.execSQL(sqlTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
