package com.example.aditya.gameofthrones.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ThroneContract {

    public static final String CONTENT_AUTHORITY = "com.example.aditya.gameofthrones" ;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_THRONES = "throneTable";

    public static final class ThroneEntry implements BaseColumns {

        public static final String TABLE_NAME = "throneTable";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_THRONE_CHARACTER_NAME = "characterName";
        public static final String COLUMN_THRONE_HOUSE = "house";
        public static final String COLUMN_THRONE_CHARACTER_TITLE = "characterTitle";
        public static final String COLUMN_THRONE_CHARACTER_THUMBNAIL_LINK = "thumbnailLink";
        public static final String COLUMN_THRONE_LOCATIONS = "locations";
        public static final String COLUMN_THRONE_MOTHER_NAME = "motherName";
        public static final String COLUMN_THRONE_FATHER_NAME = "fatherName";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_THRONES);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_THRONES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_THRONES;

    }
}
