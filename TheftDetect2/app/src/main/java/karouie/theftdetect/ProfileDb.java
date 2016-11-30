package karouie.theftdetect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

    public static final String TABLE_TRIALTIME = "table_trialtime";
    public static final String TABLE_TRIALTIME_COL1 = "trialtime";
    public static final String SQL_DELETE_TABLE_TRIALTIME = "DROP TABLE IF EXISTS " + TABLE_TRIALTIME;
    public static final String SQL_CREATE_TABLE_TRIALTIME = "CREATE TABLE " + TABLE_TRIALTIME + " ("
            + TABLE_TRIALTIME_COL1 + " INTEGER"
            + ");";

    public static final String TABLE_TRIALRUN = "table_trialrun";
    public static final String TABLE_TRIALRUN_COL1 = "trialrun";
    public static final String SQL_DELETE_TABLE_TRIALRUN = "DROP TABLE IF EXISTS " + TABLE_TRIALRUN;
    public static final String SQL_CREATE_TABLE_TRIALRUN = "CREATE TABLE " + TABLE_TRIALRUN + " ("
            + TABLE_TRIALRUN_COL1 + " INTEGER"
            + ");";

    public static final String TABLE_DEFAULTRADIUS = "table_defaultradius";
    public static final String TABLE_DEFAULTRADIUS_COL1 = "radius";
    public static final String SQL_DELETE_TABLE_DEFAULTRADIUS = "DROP TABLE IF EXISTS " + TABLE_DEFAULTRADIUS;
    public static final String SQL_CREATE_TABLE_DEFAULTRADIUS = "CREATE TABLE " + TABLE_DEFAULTRADIUS + " ("
            + TABLE_DEFAULTRADIUS_COL1 + " REAL"
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
            + TABLE_GPS_COL3 + " INTEGER, " //2007-01-01 10:00:00
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
            + TABLE_GPSOUTSIDE_COL3 + " INTEGER, "
            + "PRIMARY KEY (" + TABLE_GPSOUTSIDE_COL1 + ", " + TABLE_GPSOUTSIDE_COL2 + ")" //set primary key as composite of latitude and longitude
            + ");";


    public ProfileDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_PASSWORD);
        db.execSQL(SQL_CREATE_TABLE_TRIALTIME);
        db.execSQL(SQL_CREATE_TABLE_TRIALRUN);
        db.execSQL(SQL_CREATE_TABLE_DEFAULTRADIUS);
        db.execSQL(SQL_CREATE_TABLE_GPS);
        db.execSQL(SQL_CREATE_TABLE_EMAILS);
        db.execSQL(SQL_CREATE_TABLE_PHONES);
        db.execSQL(SQL_CREATE_TABLE_GPSOUTSIDE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on change discard everything and start over
        db.execSQL(SQL_DELETE_TABLE_PASSWORD);
        db.execSQL(SQL_DELETE_TABLE_TRIALTIME);
        db.execSQL(SQL_DELETE_TABLE_TRIALRUN);
        db.execSQL(SQL_DELETE_TABLE_DEFAULTRADIUS);
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

    public boolean setTrialTime(long trialStartTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        //delete previous time
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_TRIALTIME, null);
        if(res.moveToFirst()) {
            int numDeleted = db.delete(TABLE_TRIALTIME, TABLE_TRIALTIME_COL1 + " = ?", new String[] {res.getString(0)}); //remove all rows (should be one row here)
            if(numDeleted != 1) {
                //didn't delete 1 password, problem
                Log.e("ProfileDb.setTrialT()", "did not delete previous time \"" + res.getString(0) + "\" correctly");
                return false;
            }
        } else {
            //no password to delete (first time)
            Log.d("ProfileDb.setTrialT()", "first trial time being set...");
        }

        ContentValues content = new ContentValues();
        content.put(TABLE_TRIALTIME_COL1, trialStartTime);
        long row = db.insert(TABLE_TRIALTIME, null, content);
        if(row  == -1 || row > 1) {
            Log.e("ProfileDb.setTrialT()", "did not insert new time correctly, gave: " + row);
            return false;
        }
        return true;
    }

    public boolean setTrialRun(boolean running) {
        SQLiteDatabase db = this.getWritableDatabase();
        //delete previous time
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_TRIALRUN, null);
        if(res.moveToFirst()) {
            int numDeleted = db.delete(TABLE_TRIALRUN, TABLE_TRIALRUN_COL1 + " = ?", new String[] {res.getString(0)}); //remove all rows (should be one row here)
            if(numDeleted != 1) {
                //didn't delete 1 password, problem
                Log.e("ProfileDb.setTrialR()", "did not delete previous run \"" + res.getString(0) + "\" correctly");
                return false;
            }
        } else {
            //no password to delete (first time)
            Log.d("ProfileDb.setTrialR()", "first time trial run being set...");
        }

        ContentValues content = new ContentValues();
        if(running) {
            content.put(TABLE_TRIALRUN_COL1, 1);
        } else {
            content.put(TABLE_TRIALRUN_COL1, 0);
        }
        long row = db.insert(TABLE_TRIALRUN, null, content);
        if(row  == -1 || row > 1) {
            Log.e("ProfileDb.setTrialT()", "did not insert new run correctly, gave: " + row);
            return false;
        }
        return true;
    }

    public boolean setDefaultRadius(double radius) {
        SQLiteDatabase db = this.getWritableDatabase();
        //delete previous time
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_DEFAULTRADIUS, null);
        if(res.moveToFirst()) {
            int numDeleted = db.delete(TABLE_DEFAULTRADIUS, TABLE_DEFAULTRADIUS_COL1 + " = ?", new String[] {res.getString(0)}); //remove all rows (should be one row here)
            if(numDeleted != 1) {
                //didn't delete 1 password, problem
                Log.e("ProfileDb.setDefaultR()", "did not delete previous radius \"" + res.getString(0) + "\" correctly");
                return false;
            }
        } else {
            //no password to delete (first time)
            Log.d("ProfileDb.setDefaultR()", "first time radius being set...");
        }

        ContentValues content = new ContentValues();
        content.put(TABLE_DEFAULTRADIUS_COL1, radius);
        long row = db.insert(TABLE_DEFAULTRADIUS, null, content);
        if(row  == -1 || row > 1) {
            Log.e("ProfileDb.setDefaultT()", "did not insert new radius correctly, gave: " + row);
            return false;
        }
        return true;
    }

    public boolean insertGps(GpsData data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(TABLE_GPS_COL1, data.latitude);
        content.put(TABLE_GPS_COL2, data.longitude);
        content.put(TABLE_GPS_COL3, data.dateTime);
        content.put(TABLE_GPS_COL4, data.radius);

        ArrayList<GpsData> closePoints = getGPSWithin(data.latitude, data.latitude, data.longitude, data.longitude);
        if(closePoints.size() > 0) {
            Log.e("ProfileDb.insertGps()", "lat/lng pair already exists, la:" + data.latitude + " lo:" + data.longitude + " dt:" + data.dateTime + " r:" + data.radius);
            Log.e("ProfileDb.insertGps()", "" + closePoints.get(0));
            return false;
        }

        long row = db.insert(TABLE_GPS, null, content);
        if(row == -1) {
            //didn't insert correctly
            Log.e("ProfileDb.insertGps()", "did not insert gps data correctly, la:" + data.latitude + " lo:" + data.longitude + " dt:" + data.dateTime + " r:" + data.radius);
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

    public boolean insertGpsOutside(GpsData data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(TABLE_GPSOUTSIDE_COL1, data.latitude);
        content.put(TABLE_GPSOUTSIDE_COL2, data.longitude);
        content.put(TABLE_GPSOUTSIDE_COL3, data.dateTime);

        ArrayList<GpsData> closePoints = getGPSOutsideWithin(data.latitude, data.latitude, data.longitude, data.longitude);
        if(closePoints.size() > 0) {
            Log.e("ProfileDb.insertGpsO()", "lat/lng pair already exists, la:" + data.latitude + " lo:" + data.longitude + " dt:" + data.dateTime + " r:" + data.radius);
            return false;
        }

        long row = db.insert(TABLE_GPSOUTSIDE, null, content);
        if(row == -1) {
            //didn't insert correctly
            Log.e("ProfileDb.insertGpsO()", "did not insert dps data correctly, la:" + data.latitude + " lo:" + data.longitude + " dt:" + data.dateTime);
            return false;
        }
        return true;
    }

    public boolean updateGpsRadius(double latitude, double longitude, double newRadius) {
        SQLiteDatabase db = this.getWritableDatabase();

        //get previous data
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_GPS
                + " WHERE (" + TABLE_GPS_COL1 + " = " + latitude + ")"
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

    public int deleteAllGpsOutside() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_GPSOUTSIDE, null, null);
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

    public long getTrialTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_TRIALTIME, null);
        if(res.moveToFirst()) {
            long pass = res.getLong(0);
            res.close();
            return pass;
        }
        res.close();
        return 0L;
    }

    public boolean getTrialRun() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_TRIALRUN, null);
        if(res.moveToFirst()) {
            boolean pass;
            if(res.getInt(0) == 0) {
                pass = false;
            } else {
                pass = true;
            }
            res.close();
            return pass;
        }
        res.close();
        return true;
    }

    public double getDefaultRadius() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_DEFAULTRADIUS, null);
        if(res.moveToFirst()) {
            double pass = res.getDouble(0);
            res.close();
            return pass;
        }
        res.close();
        return 0D;
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
    public ArrayList<GpsData> getAllGps() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_GPS, null);
        ArrayList<GpsData> points = new ArrayList<>();
        while(res.moveToNext()) {
            // 1 latitude, 2 longitude, 3 datetime, 4 radius
            points.add(new GpsData(res.getDouble(0), res.getDouble(1), res.getLong(2), res.getDouble(3)));
        }

        res.close();
        return points;
    }

    public ArrayList<GpsData> getAllGpsOutside() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_GPSOUTSIDE, null);
        ArrayList<GpsData> points = new ArrayList<>();
        while(res.moveToNext()) {
            // 1 latitude, 2 longitude, 3 datetime, radius = 0
            points.add(new GpsData(res.getDouble(0), res.getDouble(1), res.getLong(2), 0));
        }

        res.close();
        return points;
    }

    public ArrayList<GpsData> getGPSWithin(double startLat, double endLat, double startLong, double endLong) {
        double lat1 = Math.min(startLat, endLat);
        double lat2 = Math.max(startLat, endLat);
        double long1 = Math.min(startLong, endLong);
        double long2 = Math.max(startLong, endLong);
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GpsData> points = new ArrayList<>();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_GPS
                + " WHERE (" + TABLE_GPS_COL1 + " BETWEEN " + lat1 + " and " + lat2 + ")"
                + " and (" + TABLE_GPS_COL2 + " BETWEEN " + long1 + " and " + long2 + ")"
                , null);
        while(res.moveToNext()) {
            // 1 latitude, 2 longitude, 3 datetime, 4 radius
            points.add(new GpsData(res.getDouble(0), res.getDouble(1), res.getLong(2), res.getDouble(3)));
        }

        res.close();
        return points;
    }

    public ArrayList<GpsData> getGPSOutsideWithin(double startLat, double endLat, double startLong, double endLong) {
        double lat1 = Math.min(startLat, endLat);
        double lat2 = Math.max(startLat, endLat);
        double long1 = Math.min(startLong, endLong);
        double long2 = Math.max(startLong, endLong);
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GpsData> points = new ArrayList<>();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_GPSOUTSIDE
                + " WHERE (" + TABLE_GPSOUTSIDE_COL1 + " BETWEEN " + lat1 + " and " + lat2 + ")"
                + " and (" + TABLE_GPSOUTSIDE_COL2 + " BETWEEN " + long1 + " and " + long2 + ")"
                , null);
        while(res.moveToNext()) {
            // 1 latitude, 2 longitude, 3 datetime, radius = 0
            points.add(new GpsData(res.getDouble(0), res.getDouble(1), res.getLong(2), 0));
        }

        res.close();
        return points;
    }
}
