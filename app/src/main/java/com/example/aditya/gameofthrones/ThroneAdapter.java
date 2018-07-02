package com.example.aditya.gameofthrones;

import android.content.Context;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ThroneAdapter extends ArrayAdapter<Throne> {

    private boolean mInternetAccess = false;

    public ThroneAdapter(Context context, ArrayList<Throne> thrones, boolean internetAccess){
        super(context, 0, thrones);
        mInternetAccess = internetAccess;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listViewItem = convertView;
        if( listViewItem == null){
            listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.throne_list_item, parent, false);
        }

        Throne currentThrone = getItem(position);

        ImageView thumbnail = (ImageView) listViewItem.findViewById(R.id.thumbnail_list_item);
        String thumbnailLink = currentThrone.getThumbnailLink();
        if(!thumbnailLink.equals("") && mInternetAccess){
            Picasso.get().load(thumbnailLink).into(thumbnail);
        } else {
            thumbnail.setImageResource(R.drawable.default_pic);
        }

        TextView characterName = (TextView) listViewItem.findViewById(R.id.character_name_list_item);
        characterName.setText(currentThrone.getCharacterName());

        TextView house = (TextView) listViewItem.findViewById(R.id.house_name_list_item);
        house.setText(currentThrone.getHouse());

        TextView characterTitle = (TextView) listViewItem.findViewById(R.id.character_title_name_list_item);
        characterTitle.setText(currentThrone.getCharacterTitle());

        return listViewItem;
    }
}
