package com.example.aditya.gameofthrones.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.aditya.gameofthrones.data.ThroneContract.ThroneEntry;

public class ThroneDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "throneDatabase.db";
    public static final int DATABASE_VERSIION = 1;

    public ThroneDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSIION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_FIXTURES_TABLE = "CREATE TABLE " + ThroneEntry.TABLE_NAME + "("
                + ThroneEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ThroneEntry.COLUMN_THRONE_CHARACTER_NAME + " TEXT,"
                + ThroneEntry.COLUMN_THRONE_HOUSE + " TEXT,"
                + ThroneEntry.COLUMN_THRONE_CHARACTER_TITLE + " TEXT,"
                + ThroneEntry.COLUMN_THRONE_CHARACTER_THUMBNAIL_LINK + " TEXT,"
                + ThroneEntry.COLUMN_THRONE_LOCATIONS + " TEXT,"
                + ThroneEntry.COLUMN_THRONE_MOTHER_NAME + " TEXT,"
                + ThroneEntry.COLUMN_THRONE_FATHER_NAME + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_FIXTURES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
