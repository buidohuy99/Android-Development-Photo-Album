package com.example.myalbum;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import com.example.myalbum.events.OnClickEvent;
import com.example.myalbum.utilities.UtilityListeners;

public class HomeActivity extends Activity implements ActivityCallBacks{
    //Page widgets
    private AutoCompleteTextView searchBar;
    private ListView albumList;
    private Button searchButton;
    private Button addAlbumButton;
    private AddAlbumDialog addAlbumDialog = null;

    //Auto-completes
    private ArrayList<String> hint;

    //Albums database
    private List<Album> allAlbums;

    //Events
    private OnClickEvent addAlbumButton_OnClick = new OnClickEvent();
    private OnClickEvent searchButton_OnClick = new OnClickEvent();

    //Listeners
    //Nothing here

    //Adapters
    private AlbumsAdapter albumsAdapter;
    private ArrayAdapter<String> autoCompleteAdapter;

    //Album related logic, to be moved to different class
    private void addAlbum(String name) {
        allAlbums.add(new Album(name));
        albumsAdapter.notifyDataSetChanged();
        hint.add(name);
        autoCompleteAdapter.notifyDataSetChanged();
    }

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

        hint = new ArrayList<String>();
        for (int i = 0; i < allAlbums.size();i++)
        {
            hint.add(allAlbums.get(i).getAlbumName());
        }

        //Add adapters
            //For displaying all albums
        albumsAdapter = new AlbumsAdapter(this,
                allAlbums,
                R.layout.albumlist_row);
        albumList.setAdapter(albumsAdapter);

            //For autocomplete field
        autoCompleteAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, hint);
        searchBar.setAdapter(autoCompleteAdapter);

        //Set listeners + events

            //Add Album Button
        addAlbumButton_OnClick.register(UtilityListeners.view_OnClick_ClearFocus(HomeActivity.this));
        addAlbumButton_OnClick.register(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addAlbumDialog == null)
                    addAlbumDialog = AddAlbumDialog.newInstance(HomeActivity.this,"Add Album Name");
                addAlbumDialog.show();
            }
        });
        addAlbumButton.setOnClickListener(addAlbumButton_OnClick);

            //Search Bar
        searchBar.setOnFocusChangeListener(UtilityListeners.editText_OnFocusChange(HomeActivity.this));

            //Search Button
        searchButton_OnClick.register(UtilityListeners.view_OnClick_ClearFocus(HomeActivity.this));
        searchButton_OnClick.register( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                String find = (searchBar.getText()).toString();
                if(find.equals("")) {
                    albumsAdapter.setList(allAlbums);
                    albumsAdapter.notifyDataSetChanged();
                    return;
                }
                ArrayList<Album> result = new ArrayList<Album>();
                Album album;
                for (int i = 0; i < allAlbums.size();i++)
                {
                    album = allAlbums.get(i);
                    if (album.getAlbumName().equals(find))
                    {
                        result.add(album);
                    }
                }

                albumsAdapter.setList(result);
                albumsAdapter.notifyDataSetChanged();
            }
        });
        searchButton.setOnClickListener(searchButton_OnClick);

            //Album List
        albumList.setOnItemClickListener(UtilityListeners.listView_OnItemClick_ClearFocus(HomeActivity.this));

    }

    @Override
    public void onMessageToActivity(String source, Bundle bundle) {
        switch (source){
            case "Add Album Dialog":
                addAlbum(bundle.getString("albumName"));
                break;
        }
    }
}
