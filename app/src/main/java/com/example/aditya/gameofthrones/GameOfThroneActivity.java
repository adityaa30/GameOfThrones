package com.example.aditya.gameofthrones;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.SearchAutoComplete;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditya.gameofthrones.data.ThroneContract.ThroneEntry;

import java.io.InputStreamReader;
import java.net.IDN;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GameOfThroneActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Throne>> {

    private static final int THRONE_LOADER = 1;
    private static boolean loader_used = false;

    private static ThroneAdapter mAdapter;
    private ArrayList<Throne> thrones;
    private String mQuery;
    private SearchView mSearchView;
    private SearchAutoComplete mSearchAutoComplete;
    private ArrayList<String> searchOptions;

    private boolean mInternetAccess;

    private TextView mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_of_throne);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        mInternetAccess = (networkInfo != null) && networkInfo.isConnected();

        ListView listView = (ListView) findViewById(R.id.list);
        mEmptyView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyView);
        mAdapter = new ThroneAdapter(this, new ArrayList<Throne>(), mInternetAccess);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.character_name_list_item);
                Intent intent = new Intent(GameOfThroneActivity.this, CharacterActivity.class);
                long ID = fetchThroneIDFromDatabase(textView.getText().toString());
                Uri currentThroneUri = ContentUris.withAppendedId(ThroneEntry.CONTENT_URI, ID);
                intent.setData(currentThroneUri);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game_of_throne, menu);

        mSearchView = (SearchView) menu.findItem(R.id.search_container).getActionView();
        /* mSearchAutoComplete = (SearchAutoComplete) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchAutoComplete.setBackgroundColor(Color.BLUE);
        mSearchAutoComplete.setTextColor(Color.GREEN);
        mSearchAutoComplete.setDropDownBackgroundResource(android.R.color.holo_blue_light);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchOptions);
        mSearchAutoComplete.setAdapter(adapter); */
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);
                mQuery = mSearchView.getQuery().toString().trim();
                List<Throne> dbThrone = fetchThroneDataFromDatabase(mQuery);
                if(dbThrone != null){
                    mAdapter.clear();
                    mAdapter.addAll(dbThrone);
                    mAdapter.notifyDataSetChanged();
                    loadingIndicator.setVisibility(View.INVISIBLE);
                } else {
                    if(loader_used){
                        getSupportLoaderManager().restartLoader(THRONE_LOADER, null, GameOfThroneActivity.this);

                    } else {
                        getSupportLoaderManager().initLoader(THRONE_LOADER, null, GameOfThroneActivity.this);
                        loader_used = true;
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                /*mAdapter.clear();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        searchOptions = getSearchOptions(mQuery);
                    }
                });*/
                return true;
            }
        });

        return true;
    }

    private long fetchThroneIDFromDatabase(String name){
        String[] projection = new String[] {ThroneEntry._ID,
                ThroneEntry.COLUMN_THRONE_CHARACTER_NAME};
        String selection = ThroneEntry.COLUMN_THRONE_CHARACTER_NAME + "=?";
        String[] selectionArgs = new String[] {name};
        Cursor cursor = getContentResolver().query(ThroneEntry.CONTENT_URI, projection, selection, selectionArgs, null);
        if(cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToNext()) {
                return cursor.getLong(cursor.getColumnIndex(ThroneEntry._ID));
            }
        }
        return -1;
    }

    private List<Throne> fetchThroneDataFromDatabase(String requestQuery){
        String[] projection = new String[] {ThroneEntry._ID,
                ThroneEntry.COLUMN_THRONE_CHARACTER_NAME,
                ThroneEntry.COLUMN_THRONE_HOUSE,
                ThroneEntry.COLUMN_THRONE_CHARACTER_TITLE,
                ThroneEntry.COLUMN_THRONE_CHARACTER_THUMBNAIL_LINK,
                ThroneEntry.COLUMN_THRONE_LOCATIONS,
                ThroneEntry.COLUMN_THRONE_MOTHER_NAME,
                ThroneEntry.COLUMN_THRONE_FATHER_NAME
        };
        String selection = ThroneEntry.COLUMN_THRONE_CHARACTER_NAME + " LIKE ? ";
        String[] selectionArgs = new String[] {"%" + requestQuery + "%"};
        Cursor cursor = getContentResolver().query(ThroneEntry.CONTENT_URI, projection, selection, selectionArgs, null);
        if(cursor != null && cursor.getCount() > 0) {
            List<Throne> thrones = new ArrayList<>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_CHARACTER_NAME));
                String title = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_CHARACTER_TITLE));
                String thumbnailLink = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_CHARACTER_THUMBNAIL_LINK));
                String house = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_HOUSE));
                String locations = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_LOCATIONS));
                String motherName = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_MOTHER_NAME));
                String fatherName = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_FATHER_NAME));
                thrones.add(new Throne(name, house, title, thumbnailLink, locations, motherName, fatherName));
            }
            return thrones;
        }
        return null;
    }

    private ArrayList<String> getSearchOptions(String requestQuery){
        ArrayList<String> searchOptions = new ArrayList<>();
        String[] projection = new String[] {ThroneEntry._ID, ThroneEntry.COLUMN_THRONE_CHARACTER_NAME};
        String selection = ThroneEntry.COLUMN_THRONE_CHARACTER_NAME + " MATCH LIKE ?";
        String[] selectionArgs = new String[] {"%" + requestQuery + "%"};
        Cursor cursor = getContentResolver().query(ThroneEntry.CONTENT_URI, projection, selection, selectionArgs, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                String name = cursor.getString(cursor.getColumnIndex(ThroneEntry.COLUMN_THRONE_CHARACTER_NAME));
                searchOptions.add(name);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return searchOptions;

    }

    @NonNull
    @Override
    public Loader<List<Throne>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new ThroneLoader(this, mQuery);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Throne>> loader, List<Throne> thrones) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.INVISIBLE);
        mAdapter.clear();
        if(thrones != null && !thrones.isEmpty()){
            if(!checkIfThroneAlreadyExist(thrones)){
                addThroneListToDatabase(thrones);
            }
            mAdapter.addAll(thrones);
        } else {
            Toast.makeText(GameOfThroneActivity.this, "No character found for the given search!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkIfThroneAlreadyExist(List<Throne> thrones){
        for (int i = 0; i < thrones.size(); i++){
            Throne currentThrone = thrones.get(i);
            if (fetchThroneIDFromDatabase(currentThrone.getCharacterName()) == -1){
                return false;
            }
        }
        return true;
    }

    private void addThroneListToDatabase(List<Throne> thrones) {
        for(int i = 0; i < thrones.size(); i++){
            Throne throne = thrones.get(i);
            ContentValues values = new ContentValues();
            values.put(ThroneEntry.COLUMN_THRONE_CHARACTER_NAME, throne.getCharacterName());
            values.put(ThroneEntry.COLUMN_THRONE_CHARACTER_TITLE, throne.getCharacterTitle());
            values.put(ThroneEntry.COLUMN_THRONE_HOUSE, throne.getHouse());
            values.put(ThroneEntry.COLUMN_THRONE_CHARACTER_THUMBNAIL_LINK, throne.getThumbnailLink());
            values.put(ThroneEntry.COLUMN_THRONE_LOCATIONS, throne.getLocations());
            values.put(ThroneEntry.COLUMN_THRONE_MOTHER_NAME, throne.getMotherName());
            values.put(ThroneEntry.COLUMN_THRONE_FATHER_NAME, throne.getFatherName());
            getContentResolver().insert(ThroneEntry.CONTENT_URI, values);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Throne>> loader) {
        mAdapter.clear();
    }
}
