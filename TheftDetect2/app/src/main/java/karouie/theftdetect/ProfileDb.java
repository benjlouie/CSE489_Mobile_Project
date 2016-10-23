package karouie.theftdetect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.*;
import android.provider.Settings;

/**
 * Created by Ben on 10/17/2016.
 */

//TODO: go through this and make sure it's ok
public class ProfileDb extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TheftDetect_Profile.db";
    public static final String TABLE_NAME = "profile_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "MARKS";

    //TODO: fix with actual fields
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_2 + " TEXT, "
            + COL_3 + " INTEGER"
            + ");";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public ProfileDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public boolean insertData(String name, int marks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues(); // for SQL data
        content.put(COL_2, name);
        content.put(COL_3, marks);
        //inserts into row
        long row = db.insert(TABLE_NAME, null, content);
        if(row == -1) {
            //didn't insert correctly
            return false;
        }
        return true;
    }

    public boolean updateData(int id, String name, int marks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL_1, id);
        content.put(COL_2, name);
        content.put(COL_3, marks);
        db.update(TABLE_NAME, content, COL_1 + " = ?", new String[] { Integer.toString(id) });//update based on id
        return true;
    }

    public Integer deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //returns number of rows deleted
        return db.delete(TABLE_NAME, COL_1 + " = ?", new String[] {Integer.toString(id)});
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }
}
