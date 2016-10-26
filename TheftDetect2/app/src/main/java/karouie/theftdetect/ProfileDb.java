package karouie.theftdetect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.*;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Ben on 10/17/2016.
 */

public class ProfileDb extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "TheftDetect_Profile.db";

    public static final String TABLE_PASSWORD = "table_password";
    public static final String TABLE_PASSWORD_COL1 = "password";
    public static final String SQL_DELETE_TABLE_PASSWORD = "DROP TABLE IF EXISTS " + TABLE_PASSWORD;
    public static final String SQL_CREATE_TABLE_PASSWORD = "CREATE TABLE " + TABLE_PASSWORD + " ("
            + TABLE_PASSWORD_COL1 + " TEXT PRIMARY KEY"
            + ");";

    public static final String TABLE_GPS = "table_gps";
    public static final String TABLE_GPS_COL1 = "latitude";
    public static final String TABLE_GPS_COL2 = "longitude";
    public static final String TABLE_GPS_COL3 = "date";
    public static final String TABLE_GPS_COL4 = "radius";
    public static final String SQL_DELETE_TABLE_GPS = "DROP TABLE IF EXISTS " + TABLE_GPS;
    public static final String SQL_CREATE_TABLE_GPS = "CREATE TABLE " + TABLE_GPS + " ("
            + TABLE_GPS_COL1 + " REAL, "
            + TABLE_GPS_COL2 + " REAL, "
            + TABLE_GPS_COL3 + " DATETIME, "
            + TABLE_GPS_COL4 + " REAL, "
            + "PRIMARY KEY (" + TABLE_GPS_COL1 + ", " + TABLE_GPS_COL2 + ")" //set primary key as composite of latitude and longitude
            + ");";

    public static final String TABLE_EMAILS = "table_emails";
    public static final String TABLE_EMAILS_COL1 = "email";
    public static final String SQL_DELETE_TABLE_EMAILS = "DROP TABLE IF EXISTS " + TABLE_EMAILS;
    public static final String SQL_CREATE_TABLE_EMAILS = "CREATE TABLE " + TABLE_EMAILS + " ("
            + TABLE_EMAILS_COL1 + " TEXT"
            + ");";

    public static final String TABLE_PHONES = "table_phones";
    public static final String TABLE_PHONES_COL1 = "phone";
    public static final String SQL_DELETE_TABLE_PHONES = "DROP TABLE IF EXISTS " + TABLE_PHONES;
    public static final String SQL_CREATE_TABLE_PHONES = "CREATE TABLE " + TABLE_PHONES + " ("
            + TABLE_PHONES_COL1 + " TEXT"
            + ");";

    public static final String TABLE_GPSOUTSIDE = "table_gpsOutside";
    public static final String TABLE_GPSOUTSIDE_COL1 = "latitude";
    public static final String TABLE_GPSOUTSIDE_COL2 = "longitude";
    public static final String TABLE_GPSOUTSIDE_COL3 = "date";
    public static final String SQL_DELETE_TABLE_GPSOUTSIDE = "DROP TABLE IF EXISTS " + TABLE_GPSOUTSIDE;
    public static final String SQL_CREATE_TABLE_GPSOUTSIDE = "CREATE TABLE " + TABLE_GPSOUTSIDE + " ("
            + TABLE_GPSOUTSIDE_COL1 + " REAL, "
            + TABLE_GPSOUTSIDE_COL2 + " REAL, "
            + TABLE_GPSOUTSIDE_COL3 + " DATETIME, "
            + "PRIMARY KEY (" + TABLE_GPSOUTSIDE_COL1 + ", " + TABLE_GPSOUTSIDE_COL2 + ")" //set primary key as composite of latitude and longitude
            + ");";


    public ProfileDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_PASSWORD);
        db.execSQL(SQL_CREATE_TABLE_GPS);
        db.execSQL(SQL_CREATE_TABLE_EMAILS);
        db.execSQL(SQL_CREATE_TABLE_PHONES);
        db.execSQL(SQL_CREATE_TABLE_GPSOUTSIDE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on change discard everything and start over
        db.execSQL(SQL_DELETE_TABLE_PASSWORD);
        db.execSQL(SQL_DELETE_TABLE_GPS);
        db.execSQL(SQL_DELETE_TABLE_EMAILS);
        db.execSQL(SQL_DELETE_TABLE_PHONES);
        db.execSQL(SQL_DELETE_TABLE_GPSOUTSIDE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public boolean setPassword(String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        //delete previous password
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_PASSWORD, null);
        if(res.moveToFirst()) {
            int numDeleted = db.delete(TABLE_PASSWORD, TABLE_PASSWORD_COL1 + " = ?", new String[] {res.getString(0)}); //remove all rows (should be one row here)
            if(numDeleted != 1) {
                //didn't delete 1 password, problem
                Log.e("ProfileDb.setPassword()", "did not delete previous password \"" + res.getString(0) + "\" correctly");
                return false;
            }
        } else {
            //no password to delete (first time)
            Log.d("ProfileDb.setPassword()", "first password being set...");
        }

        //add new password
        ContentValues content = new ContentValues(); // for SQL data
        content.put(TABLE_PASSWORD_COL1, password);
        long numInserted = db.insert(TABLE_PASSWORD, null, content);
        if(numInserted == -1 || numInserted > 1) {
            //deleted more than it should have or failed
            Log.e("ProfileDb.setPassword()", "did not insert new password correctly, gave: " + numInserted);
            return false;
        }
        return true;
    }

    public boolean insertGps(double latitude, double longitude, String dateTime, double radius) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(TABLE_GPS_COL1, latitude);
        content.put(TABLE_GPS_COL2, longitude);
        content.put(TABLE_GPS_COL3, dateTime);
        content.put(TABLE_GPS_COL4, radius);

        long row = db.insert(TABLE_GPS, null, content);
        if(row == -1) {
            //didn't insert correctly
            Log.e("ProfileDb.insertGps()", "did not insert gps data correctly, la:" + latitude + " lo:" + longitude + " dt:" + dateTime + " r:" + radius);
            return false;
        }
        return true;
    }

    public boolean insertEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(TABLE_EMAILS_COL1, email);

        long row = db.insert(TABLE_EMAILS, null, content);
        if(row == -1) {
            //didn't insert correctly
            Log.e("ProfileDb.insertEmail()", "did not insert email correctly, email:" + email);
            return false;
        }
        return true;
    }

    public boolean insertPhone(String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(TABLE_PHONES_COL1, phoneNumber);

        long row = db.insert(TABLE_PHONES, null, content);
        if(row == -1) {
            //didn't insert correctly
            Log.e("ProfileDb.insertPhone()", "did not insert phone number correctly, #:" + phoneNumber);
            return false;
        }
        return true;
    }

    public boolean insertGpsOutside(double latitude, double longitude, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(TABLE_GPSOUTSIDE_COL1, latitude);
        content.put(TABLE_GPSOUTSIDE_COL2, longitude);
        content.put(TABLE_GPSOUTSIDE_COL3, dateTime);

        long row = db.insert(TABLE_GPSOUTSIDE, null, content);
        if(row == -1) {
            //didn't insert correctly
            Log.e("ProfileDb.insertGpsO()", "did not insert dps data correctly, la:" + latitude + " lo:" + longitude + " dt:" + dateTime);
            return false;
        }
        return true;
    }

    public boolean updateGpsRadius(double latitude, double longitude, double newRadius) {
        SQLiteDatabase db = this.getWritableDatabase();

        //get previous data
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_GPS
                + " WHERE (" + TABLE_GPS_COL1 + " = " + latitude  + ")"
                + " AND   (" + TABLE_GPS_COL2 + " = " + longitude + ")", null);
        if(res.moveToFirst()) {
            //got the data row
            ContentValues content = new ContentValues();
            content.put(TABLE_GPS_COL1, latitude);
            content.put(TABLE_GPS_COL2, longitude);
            content.put(TABLE_GPS_COL3, res.getString(3));
            content.put(TABLE_GPS_COL4, newRadius);
            db.update(TABLE_GPS, content, TABLE_GPS_COL1 + " = ? and " + TABLE_GPS_COL2 + " = ?"
                    , new String[] { Double.toString(latitude), Double.toString(longitude) });
            return true;
        } else {
            //no gps data to change
            Log.d("ProfileDb.updateGpsR()", "no gps data found, la:" + latitude + " lo:" + longitude);
            return false;
        }
    }

    public int deleteGps(double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_GPS, TABLE_GPS_COL1 + " = ? and " + TABLE_GPS_COL2 + " = ?"
                , new String[] {Double.toString(latitude), Double.toString(longitude)});
    }

    public int deleteEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EMAILS, TABLE_EMAILS_COL1 + " = ?", new String[] {email});
    }

    public int deletePhone(String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PHONES, TABLE_PHONES_COL1 + " = ?", new String[] {phoneNumber});
    }

    public int deleteGpsOutside(double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_GPSOUTSIDE, TABLE_GPSOUTSIDE_COL1 + " = ? and " + TABLE_GPSOUTSIDE_COL2 + " = ?"
                , new String[] {Double.toString(latitude), Double.toString(longitude)});
    }

    public String getPassword() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_PASSWORD, null);
        if(res.moveToFirst()) {
            String pass = res.getString(0);
            res.close();
            return pass;
        }
        res.close();
        return "";
    }

    public ArrayList<String> getAllEmails() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_EMAILS, null);
        ArrayList<String> emails = new ArrayList<>();
        while(res.moveToNext()) {
            emails.add(res.getString(0));
        }
        res.close();

        return emails;
    }

    public ArrayList<String> getAllPhones() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_PHONES, null);
        ArrayList<String> phones = new ArrayList<>();
        while(res.moveToNext()) {
            phones.add(res.getString(0));
        }
        res.close();

        return phones;
    }

    //TODO: make class for both types so data can be returned as arraylist instead of cursor
    public Cursor getAllGps() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_GPS, null);
    }

    public Cursor getAllGpsOutside() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_GPSOUTSIDE, null);
    }
}
