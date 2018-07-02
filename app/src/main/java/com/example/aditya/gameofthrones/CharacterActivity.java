package com.example.aditya.gameofthrones;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aditya.gameofthrones.data.ThroneContract.ThroneEntry;
import com.squareup.picasso.Picasso;


public class CharacterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private Uri mCurrentThroneUri;

    private TextView mCharacterName;
    private TextView mHouse;
    private TextView mCharacterTitle;
    private ImageView mThumbnail;
    private TextView mLocations;
    private TextView mMotherName;
    private TextView mFatherName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        Intent intent = getIntent();
        mCurrentThroneUri = intent.getData();

        mCharacterName = (TextView) findViewById(R.id.character_activity_character_name);
        mHouse = (TextView) findViewById(R.id.character_activity_house);
        mCharacterTitle = (TextView) findViewById(R.id.character_activity_character_title);
        mThumbnail = (ImageView) findViewById(R.id.character_activity_thumbnail);
        mLocations = (TextView) findViewById(R.id.character_activity_locations);
        mMotherName = (TextView) findViewById(R.id.character_activity_mother_name);
        mFatherName = (TextView) findViewById(R.id.character_activity_father_name);

        getSupportLoaderManager().initLoader(3, null, this);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = new String[] {
                ThroneEntry._ID,
                ThroneEntry.COLUMN_THRONE_CHARACTER_NAME,
                ThroneEntry.COLUMN_THRONE_HOUSE,
                ThroneEntry.COLUMN_THRONE_CHARACTER_TITLE,
                ThroneEntry.COLUMN_THRONE_CHARACTER_THUMBNAIL_LINK,
                ThroneEntry.COLUMN_THRONE_LOCATIONS,
                ThroneEntry.COLUMN_THRONE_MOTHER_NAME,
                ThroneEntry.COLUMN_THRONE_FATHER_NAME
        };

        return new CursorLoader(this, mCurrentThroneUri, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if(cursor.getCount() < 1 || cursor == null) { return; }

        if(cursor.moveToFirst()) {
            String characterName = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_CHARACTER_NAME));
            String house = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_HOUSE));
            String characterTitle = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_CHARACTER_TITLE));
            String thumbnailLink = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_CHARACTER_THUMBNAIL_LINK));
            String locations = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_LOCATIONS));
            String motherName = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_MOTHER_NAME));
            String fatherName = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_FATHER_NAME));


            mCharacterName.setText(characterName);
            mHouse.setText(house);
            mCharacterTitle.setText(characterTitle);
            if (!thumbnailLink.equals("")) {
                Picasso.get().load(thumbnailLink).into(mThumbnail);
            } else {
                mThumbnail.setImageResource(R.drawable.default_pic);
            }
            mLocations.setText(locations);
            mMotherName.setText(motherName);
            mFatherName.setText(fatherName);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCharacterName.setText("");
        mHouse.setText("");
        mCharacterTitle.setText("");
        mThumbnail.setImageResource(R.drawable.default_pic);
        mLocations.setText("");
        mMotherName.setText("");
        mFatherName.setText("");
    }
}
