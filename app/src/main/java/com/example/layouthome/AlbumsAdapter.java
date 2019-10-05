package com.example.layouthome;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class AlbumsAdapter extends BaseAdapter {

    //Create a separate display Album from the true amount of Albums
    List<Album> displayAlbums;

    //Context of current Activity/ Fragment
    Context currentContext;
    //XML Layout to inflate per album
    int layoutToInflate;

    AlbumsAdapter(Context context, List<Album> displayAlbums, int resource){
        this.displayAlbums = new ArrayList<Album>();
        this.displayAlbums.addAll(displayAlbums);
        currentContext = context;
        layoutToInflate = resource;
    }

    @Override
    public int getCount() {
        return displayAlbums.size();
    }

    @Override
    public Album getItem(int position) {
        return displayAlbums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Create a wrapper for holding all Views inside an Album Row
    class AlbumRowViewsHolder {
        ImageView albumImage;
        TextView albumName;
        TextView imagesNumber;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Stores all Views in this row (at position)
        AlbumRowViewsHolder thisRowViews;

        //View representing current Row
        View currentRow;

        //check if there is existing scrap album row View not in use/ outside of screen
        //convertView is just a Row/View that moves outside of the screen
        //Reusing it reduces the overhead of creating new Views all the time
        if(convertView == null) {
            //If there is none, use the inflater of current Activity
            LayoutInflater inflater = ((Activity) currentContext).getLayoutInflater();
            //... to create a new view of a row from .xml file
            currentRow = inflater.inflate(R.layout.albumlist_row,null);

            //Create a View Holder to hold all Views from currentRow View/ViewGroup
            thisRowViews = new AlbumRowViewsHolder();
            //Get all Views from the newly-created row for changing contents
            thisRowViews.albumImage = currentRow.findViewById(R.id.albumImage);
            thisRowViews.albumName = currentRow.findViewById(R.id.albumName);
            thisRowViews.imagesNumber = currentRow.findViewById(R.id.imagesAmount);
            //Associate the row with the View Holder object, containing Views inside it
            currentRow.setTag(thisRowViews);
        }else {
            //If there is an existing view, set currentRow to it
            //and get the Views inside the row to change contents
            currentRow = convertView;
            thisRowViews = (AlbumRowViewsHolder)convertView.getTag();
        }
        //Add contents to the Views inside the Row or
        //Update the contents of the Views inside found scrap Row
        Album album = displayAlbums.get(position);
        thisRowViews.albumName.setText(album.getAlbumName());
        thisRowViews.imagesNumber.setText(album.getImagesNumber().toString());
        if(album.getImagesNumber()-1<0)
            thisRowViews.albumImage.setBackgroundColor(Color.parseColor("#ededeb"));
        else
            thisRowViews.albumImage.setImageResource(album.getImage(album.getImagesNumber()-1));

        //Return the Row for display
        return currentRow;
    }
}
