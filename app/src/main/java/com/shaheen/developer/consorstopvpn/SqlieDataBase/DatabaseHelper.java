package com.shaheen.developer.consorstopvpn.SqlieDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.shaheen.developer.consorstopvpn.Models.AppData;
import com.shaheen.developer.consorstopvpn.Models.PInfo;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "CensorStop_db";

    public static final String TABLE_APP_LIST = "Application_List";
    public static final String TABLE_LAST_TIME_DATA = "Last_Time";

    public static final String LAST_TIME_DATA_TIME = "time";

    public static final String APP_LIST_ID = "id";
    public static final String APP_LIST_APPNAME = "appname";
    public static final String APP_LIST_PNAME = "pname";
    public static final String APP_LIST_VERSIONNANME = "versionName";
    public static final String APP_LIST_VERSIONCODE = "versionCode";

    private int id;
    private String note;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE_APP_LIST =
            "CREATE TABLE " + TABLE_APP_LIST + "("
                    + APP_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + APP_LIST_APPNAME + " TEXT,"
                    + APP_LIST_PNAME + " TEXT,"
                    + APP_LIST_VERSIONNANME + " TEXT,"
                    + APP_LIST_VERSIONCODE + " INTEGER"
                    + ")";


    // Create table SQL query
    public static final String CREATE_TABLE_LAST_TIME_DATA =
            "CREATE TABLE " + TABLE_LAST_TIME_DATA + "("
                    + LAST_TIME_DATA_TIME + " TEXT"
                    + ")";

    /*// Create table SQL query
    public static final String CREATE_TABLE_LAST_TIME_DATA =
            "CREATE TABLE " + TABLE_LAST_TIME_DATA + "("
                   // + APP_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + LAST_TIME_DATA_TIME + " TEXT,"
                    + ")";*/


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(CREATE_TABLE_APP_LIST);
        //db.execSQL(CREATE_TABLE_LAST_TIME_DATA);

        String _CREATE_TABLE_LAST_TIME_DATA = "CREATE TABLE " + TABLE_LAST_TIME_DATA + "("
                + LAST_TIME_DATA_TIME  +" TEXT" + ")";

        db.execSQL(_CREATE_TABLE_LAST_TIME_DATA);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAST_TIME_DATA);
        // Create tables again
        onCreate(db);
    }

    public long InsertApp(String appname, String pname, String vname, int vcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APP_LIST_APPNAME, appname);
        values.put(APP_LIST_PNAME, pname);
        values.put(APP_LIST_VERSIONNANME, vname);
        values.put(APP_LIST_VERSIONCODE, vcode);
        long id = db.insert(TABLE_APP_LIST, null, values);
        db.close();
        return id;
    }

    public long InsertAppData(String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LAST_TIME_DATA_TIME, time);
        long id = db.insert(TABLE_LAST_TIME_DATA, null, values);
        db.close();
        return id;
    }

    public ArrayList<PInfo> getAllAPPs() {
        ArrayList<PInfo> notes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_APP_LIST + " ORDER BY " +
                APP_LIST_ID + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PInfo note = new PInfo();
                note.setId(cursor.getString(cursor.getColumnIndex(APP_LIST_ID)));
                note.setAppname(cursor.getString(cursor.getColumnIndex(APP_LIST_APPNAME)));
                note.setPname(cursor.getString(cursor.getColumnIndex(APP_LIST_PNAME)));
                note.setVersionName(cursor.getString(cursor.getColumnIndex(APP_LIST_VERSIONNANME)));
                note.setVersionCode(cursor.getInt(cursor.getColumnIndex(APP_LIST_VERSIONCODE)));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        db.close();

        return notes;
    }

    public ArrayList<AppData> getAllAppData() {
        ArrayList<AppData> notes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LAST_TIME_DATA;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                AppData note = new AppData();
                note.setTime(cursor.getString(cursor.getColumnIndex(LAST_TIME_DATA_TIME)));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        db.close();

        return notes;
    }

    public void deleteNote(PInfo note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_APP_LIST, APP_LIST_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }

    public void DeleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM TABLE_LAST_TIME_DATA"); //delete all rows in a table
        db.close();
    }


}