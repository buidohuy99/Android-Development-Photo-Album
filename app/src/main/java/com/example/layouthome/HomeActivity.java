package com.example.layouthome;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity {
    //Page widgets
    private EditText searchBar;
    private ListView albumList;
    private Button searchButton;
    private Button addAlbumButton;

    //Albums
    private List<Album> allAlbums;

    //Listeners
    private View.OnClickListener view_OnClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            UtilityFunctions.clearCurrentFocus(HomeActivity.this);
        }
    };

    private ListView.OnItemClickListener listView_OnItemClick = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UtilityFunctions.clearCurrentFocus(HomeActivity.this);
        }
    };

    private EditText.OnFocusChangeListener editText_OnFocusChange = new EditText.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus){
                UtilityFunctions.hideKeyboardFrom(HomeActivity.this,v);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        //Bind
        searchBar = findViewById(R.id.searchBar);
        searchButton = findViewById(R.id.searchButton);
        addAlbumButton = findViewById(R.id.addAlbumButton);
        albumList = findViewById(R.id.albumList);

        //Tests
        allAlbums = new ArrayList<Album>();
        allAlbums.add(new Album("Test Album"));
        allAlbums.add(new Album("Test Album 2"));
        allAlbums.add(new Album("Test Album 3"));
        allAlbums.add(new Album("Test Album 4"));
        allAlbums.add(new Album("Test Album 5"));

        //Add adapter for display all albums
        AlbumsAdapter albumsAdapter = new AlbumsAdapter(this,
                allAlbums,
                R.layout.albumlist_row);
        albumList.setAdapter(albumsAdapter);

        //Set listeners
            //Set editTexts hide keyboard
        searchBar.setOnFocusChangeListener(editText_OnFocusChange);
            //Set on Clicks
        searchButton.setOnClickListener(view_OnClick);
        addAlbumButton.setOnClickListener(view_OnClick);
        albumList.setOnItemClickListener(listView_OnItemClick);
    }
}
