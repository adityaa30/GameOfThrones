package com.example.aditya.gameofthrones;

import android.database.Cursor;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;

import com.example.aditya.gameofthrones.data.ThroneContract.ThroneEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils extends AppCompatActivity{

    private static final String LOG_TAG = GameOfThroneActivity.class.getName();

    public static List<Throne> fetchThroneDataFromJson (String query){
        String requestUrl = "https://api.got.show/api/characters/bySlug/" + convertNameToSlug(query);
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making http request", e);
        }
        Throne throne = extractFeatureFromJson(jsonResponse);
        List<Throne> thrones = new ArrayList<>();
        if(throne != null){
            requestUrl = "https://api.got.show/api/characters/locations/bySlug/" + convertNameToSlug(throne.getCharacterName());
            url = createUrl(requestUrl);
            jsonResponse = null;
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making http request", e);
            }
            thrones.add(new Throne(throne.getCharacterName(), throne.getHouse(), throne.getCharacterTitle(),
                    throne.getThumbnailLink(), extractLocationFeatureFromJson(jsonResponse), throne.getMotherName(),
                    throne.getFatherName()));
        } else {
            return null;
        }
        return thrones;
    }

    private static String extractLocationFeatureFromJson(String jsonResponse) {
        if(TextUtils.isEmpty(jsonResponse)){
            return null;
        }
        String locations = "";
        try {
            JSONObject baseJsonObject = new JSONObject(jsonResponse);
            JSONObject data = baseJsonObject.getJSONArray("data").getJSONObject(0);
            JSONArray locationArray = data.optJSONArray("locations");
            if(locationArray != null){
                for (int i = 0; i < locationArray.length(); i++){
                    locations += locationArray.optString(i) + "\n";
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locations;
    }

    private static URL createUrl(String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error getting the url: " + stringUrl, e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";
        if(url == null){
            return null;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if( urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Reponse Code:" + url);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retreiving the json response from URL", e);
        } finally {
            if(inputStream != null){
                inputStream.close();
            }
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder builder = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line = br.readLine();
            while (line != null){
                builder.append(line);
                line = br.readLine();
            }
        }
        return builder.toString();
    }

    private static Throne extractFeatureFromJson(String throneJson){
        if(TextUtils.isEmpty(throneJson)){
            return null;
        }
        Throne throne = null;
        try{
            JSONObject baseJsonObject = new JSONObject(throneJson);
            JSONObject data = baseJsonObject.getJSONObject("data");
            String thumbnailLink = data.optString("imageLink");
            if(thumbnailLink == null) {
                thumbnailLink = "";
            } else {
                thumbnailLink = "https://api.got.show" + thumbnailLink;
            }
            String characterName = data.optString("name");
            if(characterName == null){
                characterName = "";
            }
            String house = data.optString("house");
            if (house == null){
                house = "";
            }
            String title = "";
            JSONArray titleJsonArray = data.optJSONArray("titles");
            if(titleJsonArray != null){
                for( int i = 0; i < titleJsonArray.length(); i++){
                    title = title + titleJsonArray.getString(i);
                }
            }
            String motherName = data.optString("mother");
            if(motherName == null){
                motherName = "";
            }
            String fatherName = data.optString("father");
            if (fatherName == null){
                fatherName = "";
            }
            throne = new Throne(characterName, house, title, thumbnailLink, motherName, fatherName);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing JSON String", e);
        }
        return throne;
    }

    private static String convertNameToSlug(String characterName){
        char[] name = characterName.toCharArray();
        String slug = "";
        for (int i = 0; i < name.length; i++){
            if(name[i] == ' '){
                slug = slug + '_';
            } else {
                slug = slug + name[i];
            }
        }
        return slug;

    }
}
