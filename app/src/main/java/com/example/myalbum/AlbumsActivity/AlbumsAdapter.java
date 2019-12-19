package com.example.myalbum.AlbumsActivity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Album;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.R;
import com.example.myalbum.utilities.SquareImageView;
import com.example.myalbum.utilities.UtilityGlobals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumsAdapter extends BaseAdapter implements Serializable {

    //Create a separate display Album from the true amount of Albums
    List<Album> displayAlbums;
    private HashMap<Integer, Boolean> selected;
    private int selectedSize = 0;
    private boolean blurSystemAlbums = false;
    private boolean prevBlurStat = false;

    //Context of current Activity/ Fragment
    Activity currentContext;
    //XML Layout to inflate per album
    int layoutToInflate;

    AlbumsAdapter(Activity context, List<Album> displayAlbums, int resource){
        this.displayAlbums = displayAlbums;
        currentContext = context;
        layoutToInflate = resource;
        selected = new HashMap<Integer, Boolean>();
    }

    public void toggleSelected(int position) {
        Boolean isSelected = selected.get(position);
        if (isSelected != null) {
            selected.put(position, !isSelected);
            if(isSelected) selectedSize--;
            else selectedSize++;
        }
        else{
            selected.put(position, true);
            selectedSize++;
        }
    }

    public void toggleBlurSystemAlbums() {
        prevBlurStat = blurSystemAlbums;
        blurSystemAlbums = !blurSystemAlbums;
    }

    public void setBlurSystemAlbumsState(boolean newVal) {
        prevBlurStat = blurSystemAlbums;
        blurSystemAlbums = newVal;
    }

    public boolean getBlurSystemAlbumsState() {
        return blurSystemAlbums;
    }

    public void setSelected(ArrayList<Integer> selectedIDs) {
        for(Integer x : selectedIDs) {
            selected.put(x, true);
        }
    }

    public void clearSelected(){
        selected.clear();
        selectedSize = 0;
    }

    public void setSelectedSize(int size){
        selectedSize = size;
    }

    public ArrayList<Integer> getSelected() {
        ArrayList<Integer> output = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : selected.entrySet()) {
            if (entry.getValue().equals(true)) {
                output.add(entry.getKey());
            }
        }
        return output;
    }

    public int getSelectedCount() {
        return selectedSize;
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
        SquareImageView albumImage;
        TextView albumName;
        TextView imagesNumber;
        SquareImageView checkedOverlay;
        View albumWhiteOverlay;
        View albumInfo;

        AlbumRowViewsHolder(View root){
            albumImage = root.findViewById(R.id.albumImage);
            albumName = root.findViewById(R.id.albumName);
            imagesNumber = root.findViewById(R.id.imagesNumber);
            checkedOverlay = root.findViewById(R.id.checkedOverlay);
            albumWhiteOverlay = root.findViewById(R.id.albumWhiteOverlay);
            albumInfo = root.findViewById(R.id.albumInfo);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Stores all Views in this row (at position)
        final AlbumRowViewsHolder thisRowViews;

        //View representing current Row
        final View currentRow;

        //check if there is existing scrap album row View not in use/ outside of screen
        //convertView is just a Row/View that moves outside of the screen
        //Reusing it reduces the overhead of creating new Views all the time
        if(convertView == null) {
            //If there is none, use the inflater of current Activity
            LayoutInflater inflater = currentContext.getLayoutInflater();
            //... to create a new view of a row from .xml file
            currentRow = inflater.inflate(R.layout.albumlist_row, null);

            //Create a View Holder to hold all Views from currentRow View/ViewGroup
            thisRowViews = new AlbumRowViewsHolder(currentRow);

            //Associate the row with the View Holder object, containing Views inside it
            currentRow.setTag(thisRowViews);
        }else {
            //If there is an existing view, set currentRow to it
            //and get the Views inside the row to change contents
            currentRow = convertView;
            thisRowViews = (AlbumRowViewsHolder)convertView.getTag();
            Glide.with(currentContext).clear(thisRowViews.albumImage);
        }

        //Set alpha in case recycled from system scrap row
        if(thisRowViews.albumWhiteOverlay.getVisibility() == View.VISIBLE)
            thisRowViews.albumWhiteOverlay.setVisibility(View.INVISIBLE);
        if(!currentRow.isEnabled()) currentRow.setEnabled(true);

        if(position == displayAlbums.size() - 1 && currentContext.getClass() == HomeActivity.class) {
            thisRowViews.albumInfo.setVisibility(View.INVISIBLE);
            thisRowViews.checkedOverlay.setVisibility(View.INVISIBLE);
            Glide.with(currentContext).load(R.drawable.add_album)
                    .placeholder(R.drawable.loading)
                    .into(thisRowViews.albumImage);
            currentRow.setBackground(null);
            currentRow.setEnabled(false);
            if(prevBlurStat != blurSystemAlbums) {
                if (blurSystemAlbums) {
                    thisRowViews.albumWhiteOverlay.setVisibility(View.VISIBLE);
                }
            }
            return currentRow;
        }

        //if reuse view without background add background
        if(currentRow.getBackground() == null) currentRow.setBackgroundResource(R.drawable.black_border);
        if(thisRowViews.albumInfo.getVisibility() == View.INVISIBLE) thisRowViews.albumInfo.setVisibility(View.VISIBLE);
        //Add contents to the Views inside the Row or
        //Update the contents of the Views inside found scrap Row
        Album album = displayAlbums.get(position);
        //Set selected overlay
        Boolean selectState = selected.get(position);
        thisRowViews.checkedOverlay.setVisibility(selectState != null && selectState ? View.VISIBLE : View.INVISIBLE);
        //Set album name
        thisRowViews.albumName.setText(album.getAlbumName());
        //Get number of images
        Integer numberOfImages = DatabaseHandler.getInstance(currentContext).getNumberOfImages(album.getId());
        thisRowViews.imagesNumber.setText(numberOfImages.toString());
        //Set album thumbnail
        if(album.getId() < 0){
            if(prevBlurStat != blurSystemAlbums) {
                if (blurSystemAlbums)
                    thisRowViews.albumWhiteOverlay.setVisibility(View.VISIBLE);
            }
            switch (album.getId()) {
                case UtilityGlobals.TRASH_ALBUM:
                    Glide.with(currentContext).load(R.drawable.trash_album)
                            .placeholder(R.drawable.loading)
                            .into(thisRowViews.albumImage);
                    break;
                case UtilityGlobals.FAVORITE_ALBUM:
                    Glide.with(currentContext).load(R.drawable.favorite_album)
                            .placeholder(R.drawable.loading)
                            .into(thisRowViews.albumImage);
                    break;
            }
        }
        else if(numberOfImages - 1 < 0)
            Glide.with(currentContext).load(R.drawable.nopictures)
                    .placeholder(R.drawable.loading)
                    .into(thisRowViews.albumImage);
        else {
            Image latestImage = DatabaseHandler.getInstance(currentContext).getImageAt(album.getId(), numberOfImages-1);
            Glide.with(currentContext).load(latestImage.getUrlHinh())
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
