package com.example.myalbum.AlbumsActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.core.app.NavUtils;

import com.example.myalbum.DTOs.Album;
import com.example.myalbum.R;
import com.example.myalbum.events.OnClickEvent;
import com.example.myalbum.events.OnItemClickEvent;
import com.example.myalbum.utilities.UtilityListeners;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SearchAlbumActivity extends Activity {
    //Page widgets
    private AutoCompleteTextView searchBar;
    private ListView albumList;
    private Button searchButton;
    private ProgressBar loadingCir;

    private ArrayList<Album> allAlbums;
    private ArrayList<Album> renderAlbums;
    private ArrayList<String> hint;

    //Adapters
    private AlbumsAdapter albumsAdapter;
    private ArrayAdapter<String> autoCompleteAdapter;

    //Events
    private OnClickEvent searchButton_OnClick = new OnClickEvent();
    private OnItemClickEvent albumList_OnItemClick = new OnItemClickEvent();

    //Handlers
    private static class IncomingHandler extends Handler {
        private final WeakReference<SearchAlbumActivity> mActivity;

        IncomingHandler(SearchAlbumActivity searchAlbumActivity) {
            mActivity = new WeakReference<SearchAlbumActivity>(searchAlbumActivity);
        }
        @Override
        public void handleMessage(Message msg)
        {
            SearchAlbumActivity activity = mActivity.get();
            if (activity != null) {
                    ArrayList<Album> filtered = (ArrayList<Album>) msg.obj;
                    ArrayList<Album> source = (ArrayList<Album>) activity.allAlbums;
                    Intent newActivity = new Intent(activity, SearchAlbumActivity.class);
                    newActivity.putExtra("Source", source);
                    newActivity.putExtra("Render Info", filtered);
                    newActivity.putExtra("AutoComplete", activity.hint);
                    activity.loadingCir.setVisibility(View.INVISIBLE);
                    activity.startActivity(newActivity);
            }
        }
    }
    private IncomingHandler updateHandler = new IncomingHandler(this);

    //Album logic
    private ArrayList<Album> findAlbumByName(ArrayList<Album> source, String name){
        if(name == null) return null;
        ArrayList<Album> result = new ArrayList<Album>();
        Album album;
        for (int i = 0; i < allAlbums.size();i++)
        {
            album = allAlbums.get(i);
            if (album.getAlbumName().equals(name))
            {
                result.add(album);
            }
        }

        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchalbum_layout);

        //Set action bar back
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        Intent received = getIntent();
        allAlbums = (ArrayList<Album>) received.getSerializableExtra("Source");
        hint = (ArrayList<String>) received.getSerializableExtra("AutoComplete");
        renderAlbums = (ArrayList<Album>) received.getSerializableExtra("Render Info");

        //Bind
        searchBar = findViewById(R.id.searchBar);
        searchButton = findViewById(R.id.searchButton);
        albumList = findViewById(R.id.albumList);
        loadingCir = findViewById(R.id.progress_circular);

        //Add adapters
        //For displaying all albums
        albumsAdapter = new AlbumsAdapter(this,
                renderAlbums,
                R.layout.albumlist_row);
        albumList.setAdapter(albumsAdapter);

        //For autocomplete field
        autoCompleteAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, hint);
        searchBar.setAdapter(autoCompleteAdapter);

        //Set listeners + events
            //Search Bar
            searchBar.setOnFocusChangeListener(UtilityListeners.editText_OnFocusChange(this));

            //Search Button
            searchButton_OnClick.register(UtilityListeners.view_OnClick_ClearFocus(this));
            searchButton_OnClick.register( new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String find = (searchBar.getText()).toString();
                    if(find.equals("")) {
                        return;
                    }

                    Thread findThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = updateHandler.obtainMessage(0, findAlbumByName(allAlbums, find));
                            updateHandler.sendMessage(msg);
                        }
                    });

                    loadingCir.setVisibility(View.VISIBLE);
                    findThread.run();
                }
            });
            searchButton.setOnClickListener(searchButton_OnClick);

        //Album List
            albumList_OnItemClick.register(UtilityListeners.listView_OnItemClick_ClearFocus(SearchAlbumActivity.this));
            albumList_OnItemClick.register(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent newActivity = new Intent(SearchAlbumActivity.this, AlbumActivity.class);

                    Bundle myData = new Bundle();
                    myData.putString("nameAlbum", renderAlbums.get(i).getAlbumName());
                    myData.putInt("IDAlbum", renderAlbums.get(i).getId());

                    newActivity.putExtras(myData);
                    startActivity(newActivity);
                }
            });

            albumList.setOnItemClickListener(albumList_OnItemClick);

    }//onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getParentActivityIntent() == null) {
                    Log.i("SearchActivity",

                            "Fix Manifest to indicate the parentActivityName");

                    onBackPressed();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
