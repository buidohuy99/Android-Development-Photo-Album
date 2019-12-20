package com.example.myalbum.EditingPhoto;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myalbum.R;

import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FilterAdapter extends ArrayAdapter<String> {
    Context context;
    Integer[] avatar;
    String[] description;
    PhotoFilter[] filterOptions;


    public FilterAdapter (Context context, int layoutToBeInflated, Integer[] a, String[] d, PhotoFilter[] f)
    {
        super(context,layoutToBeInflated,d);
        this.context=context;
        this.avatar=a;
        this.description= d;
        this.filterOptions = f;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.edit_filter_adapter_layout, null);

        // Lookup view for data population
        ImageView avatarView = (ImageView) row.findViewById(R.id.avatar);
        TextView descriptionView = (TextView) row.findViewById(R.id.description);

        // Populate the data into the template view using the data object
        avatarView.setImageResource(avatar[position]);
        descriptionView.setText(description[position]);
        // Return the completed view to render on screen
        return (row);
    }

}
