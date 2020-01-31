package com.shaheen.developer.consorstopvpn.SqlieDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.shaheen.developer.consorstopvpn.Models.AppData;

import java.util.ArrayList;
import java.util.Timer;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {



    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "CensorData";

    // Contacts table name
    private static final String TABLE_DATA = "data";

    // Contacts Table Columns names
    private static final String ID = "id";
    private static final String TIME = "time";
    private static final String TIME_ELAPSED = "elapsed_time";

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_DATA = "CREATE TABLE " + TABLE_DATA + "("
                + ID +" INTEGER PRIMARY KEY,"
                + TIME_ELAPSED +" TEXT,"
                + TIME  +" TEXT" + ")";
        db.execSQL(CREATE_TABLE_DATA);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);

        // Create tables again
        onCreate(db);
    }


    //Insert values to the table contacts
    public boolean addData(AppData data){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values=new ContentValues();

        Log.d("shani","db values......"+data.getTime());
        Log.d("shani","db values......"+data.getTime_elapsed());

        values.put(TIME, data.getTime());
        values.put(TIME_ELAPSED, data.getTime_elapsed());
        long check = db.insert(TABLE_DATA, null, values);
        Log.d("shani","check......"+check);
        db.close();
        if (check != -1){
            return true;
        }else {
            return false;
        }
    }

    public ArrayList<AppData> getAllData() {
        ArrayList<AppData> array_list = new ArrayList<AppData>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from data", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            AppData contacts = new AppData(res.getString(res.getColumnIndex(TIME)), res.getString(res.getColumnIndex(TIME_ELAPSED)));
            array_list.add(contacts);
            res.moveToNext();
        }
        return array_list;
    }

    public void DeleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM data"); //delete all rows in a table
        db.close();
    }

}
