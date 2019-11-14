package com.example.myalbum.AlbumsActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Album;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.R;
import com.example.myalbum.events.OnItemClickEvent;
import com.example.myalbum.utilities.UtilityListeners;

import java.util.ArrayList;
import java.util.List;

public class MoveCopyImageActivity extends Activity {

    private List<Album> allAlbums;
    private AlbumsAdapter albumsAdapter;
    private ListView listView;

    private OnItemClickEvent albumList_OnItemClick = new OnItemClickEvent();

    private int type;
    int id;
    private List<Image> listImage ;


    public void getData() {
        //Get data from HomeActivity
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
//        type = myBundle.getInt("Type");
//        listView = myBundle.getParcelableArrayList("Mylist");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_copy_image_layout);
        //Set elements
        listView = (ListView)findViewById(R.id.moveCopyAlbumList);

        //Get all albums
        allAlbums = new ArrayList<Album>();
        allAlbums = DatabaseHandler.getInstance(MoveCopyImageActivity.this).getAllAlbums();

        albumsAdapter = new AlbumsAdapter(this,
                allAlbums,
                R.layout.albumlist_row);
        listView.setAdapter(albumsAdapter);

        //Album List
        albumList_OnItemClick.register(UtilityListeners.listView_OnItemClick_ClearFocus(MoveCopyImageActivity.this));
        albumList_OnItemClick.register(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        listView.setOnItemClickListener(albumList_OnItemClick);
    }
}
