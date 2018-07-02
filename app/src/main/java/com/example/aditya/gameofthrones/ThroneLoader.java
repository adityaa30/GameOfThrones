package com.example.aditya.gameofthrones;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ThroneLoader extends AsyncTaskLoader<List<Throne>> {

    private static final String LOG_TAG = GameOfThroneActivity.class.getName();
    private String mQuery;

    public ThroneLoader(Context context, String stringQuery){
        super(context);
        mQuery = stringQuery;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Throne> loadInBackground() {
        if(mQuery == null){
            return null;
        }
        return QueryUtils.fetchThroneDataFromJson(mQuery);
    }
}
