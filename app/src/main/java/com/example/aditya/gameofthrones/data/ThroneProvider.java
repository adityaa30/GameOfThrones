package com.example.aditya.gameofthrones.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.aditya.gameofthrones.data.ThroneContract.ThroneEntry;

public class ThroneProvider extends ContentProvider {

    private ThroneDbHelper mDbHelper;

    private static final int THRONES = 100;
    private static final int THRONES_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ThroneContract.CONTENT_AUTHORITY, ThroneContract.PATH_THRONES, THRONES);
        sUriMatcher.addURI(ThroneContract.CONTENT_AUTHORITY, ThroneContract.PATH_THRONES + "/#", THRONES_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ThroneDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case THRONES:
                cursor = database.query(ThroneEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case THRONES_ID:
                selection = ThroneEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ThroneEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query for uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case THRONES:
                return insertThrone(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion not possible for " + uri);
        }
    }

    private Uri insertThrone(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ThroneEntry.TABLE_NAME, null, contentValues);
        if(id == -1){ return null; }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        int match = sUriMatcher.match(uri);
        switch (match){
            case THRONES:
                rowsDeleted = database.delete(ThroneEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case THRONES_ID:
                selection = ThroneEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ThroneEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalStateException("Throne cannot be deleted for " + uri);
        }
        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case THRONES:
                return updateThrone(uri, contentValues, selection, selectionArgs);
            case THRONES_ID:
                selection = ThroneEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateThrone(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalStateException("Throne cannot be updated for " + uri);
        }
    }

    private int updateThrone(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowUpdated = database.update(ThroneEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if(rowUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case THRONES:
                return ThroneEntry.CONTENT_LIST_TYPE;
            case THRONES_ID:
                return ThroneEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown uri " + uri);
        }
    }
}
