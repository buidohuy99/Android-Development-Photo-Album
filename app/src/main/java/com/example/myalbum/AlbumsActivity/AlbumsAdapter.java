package com.example.myalbum.AlbumsActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Album;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.R;

import java.util.List;

public class AlbumsAdapter extends BaseAdapter {

    //Create a separate display Album from the true amount of Albums
    List<Album> displayAlbums;

    //Context of current Activity/ Fragment
    Context currentContext;
    //XML Layout to inflate per album
    int layoutToInflate;

    AlbumsAdapter(Context context, List<Album> displayAlbums, int resource){
        this.displayAlbums = displayAlbums;
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
            thisRowViews.imagesNumber = currentRow.findViewById(R.id.imagesNumber);
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
        //Get number of images
        Integer numberOfImages = DatabaseHandler.getInstance(currentContext).getNumberOfImages(album.getId());
        thisRowViews.imagesNumber.setText(numberOfImages.toString());
        //Set album thumbnail
        if(numberOfImages - 1<0)
            thisRowViews.albumImage.setBackgroundColor(Color.parseColor("#ededeb"));
        else {
            Image latestImage = DatabaseHandler.getInstance(currentContext).getImageAt(album.getId(), 0);
            Glide.with(thisRowViews.albumImage.getContext()).load(latestImage.getUrlHinh())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(thisRowViews.albumImage);
        }

        //Return the Row for display
        return currentRow;
    }

    public void setList(List<Album> a)
    {
        displayAlbums = a;
    }
}
