package com.example.myalbum.AlbumsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myalbum.R;

//Included for event kind of listener
import com.example.myalbum.events.OnClickEvent;
//Include interface
import com.example.myalbum.events.OnItemClickEvent;
import com.example.myalbum.interfaces.ActivityCallBacks;
//Included for utilities, check out corresponding folders for code
import com.example.myalbum.utilities.UtilityGlobals;
import com.example.myalbum.utilities.UtilityListeners;

//Import DTOs needed
import com.example.myalbum.DTOs.Album;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//test
import com.example.myalbum.DAO.DatabaseHandler;

import org.w3c.dom.Text;
//end test

public class HomeActivity extends Activity implements ActivityCallBacks {

    //------------------------------------------------Variables--------------------------------------

    //HomeActivity's Globals
    private final static int FIND_ALBUM_THREADCODE = 1;
    public static final String ALBUM_TO ="Album";


    //Page widgets
    private AutoCompleteTextView searchBar;
    private ListView albumList;
    private Button searchButton;
    private Button addAlbumButton;
    private ProgressBar loadingCir;
    private AddAlbumDialog addAlbumDialog = null;

    //Auto-complete source
    private ArrayList<String> hint;

    //Albums database
    private List<Album> allAlbums;

    //Events
    private OnClickEvent addAlbumButton_OnClick = new OnClickEvent();
    private OnClickEvent searchButton_OnClick = new OnClickEvent();
    private OnItemClickEvent albumList_OnItemClick = new OnItemClickEvent();

    //Listeners
    //Nothing here

    //Handlers
    private IncomingHandler updateHandler = new IncomingHandler(HomeActivity.this);

    //Adapters
    private AlbumsAdapter albumsAdapter;

    //---------------------------------------Functions-----------------------------------

    //Album related logic (to be moved to different class)
        //Add album
    private void addAlbum(String name) {
        Album album = new Album(name);
        //Add to database
        int nextID = AlbumBusinessLogic.findSmallestMissingAlbumID(allAlbums);
        album.setId(nextID);
        DatabaseHandler.getInstance(HomeActivity.this).addAlbum(album);
        //Display album
        allAlbums.add(album);
        albumsAdapter.notifyDataSetChanged();
        //Update autocomplete
        hint.add(name);
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, hint);
        searchBar.setAdapter(autoCompleteAdapter);
        albumList.smoothScrollToPosition(albumsAdapter.getCount()-1);
    }

    private void removeAlbum(int position) {
        int albumID = allAlbums.get(position).getId();
        String albumName = allAlbums.get(position).getAlbumName();
        //update autoComplete
        hint.remove(albumName);
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, hint);
        searchBar.setAdapter(autoCompleteAdapter);
        //Remove display
        allAlbums.remove(position);
        albumsAdapter.notifyDataSetChanged();
        //Remove database
        DatabaseHandler.getInstance(HomeActivity.this).deleteAlbum(albumID);
        albumList.smoothScrollToPosition(albumsAdapter.getCount()-1);
    }

        //Find album by name
    private ArrayList<Album> findAlbumByName(List<Album> source, String name){
        if(name == null) return null;
        ArrayList<Album> result = new ArrayList<Album>();
        Album album;
        for (int i = 0; i < source.size();i++)
        {
            album = source.get(i);
            if (album.getAlbumName().equals(name))
            {
                result.add(album);
            }
        }

        return result;
    }

    //Function to process messages send to main thread from other thread
    private void handleMessage(Message msg){
        //Message from find album thread
        if(msg.what == FIND_ALBUM_THREADCODE) {
            ArrayList<Album> filtered = (ArrayList<Album>) msg.obj;
            ArrayList<Album> source = (ArrayList<Album>) allAlbums;
            Intent newActivity = new Intent(this, SearchAlbumActivity.class);
            newActivity.putExtra("Source", source);
            newActivity.putExtra("Render Info", filtered);
            newActivity.putExtra("AutoComplete", hint);
            loadingCir.setVisibility(View.INVISIBLE);
            startActivity(newActivity);
        }
    }

    //-------------------------------------------Life-cycle------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toast.makeText(getApplicationContext(),"ONCREATE", Toast.LENGTH_LONG).show();


        //Bind
        searchBar = findViewById(R.id.searchBar);
        searchButton = findViewById(R.id.searchButton);
        addAlbumButton = findViewById(R.id.addAlbumButton);
        albumList = findViewById(R.id.albumList);
        loadingCir = findViewById(R.id.progress_circular);

        //Get all albums
        allAlbums = new ArrayList<Album>();
        allAlbums = DatabaseHandler.getInstance(HomeActivity.this).getAllAlbums();

        hint = new ArrayList<String>();

        //Populate hints with album name
        for(int i = 0 ; i< allAlbums.size(); i++) {
            hint.add(allAlbums.get(i).getAlbumName());
        }

        //Add adapters
            //For displaying all albums
        albumsAdapter = new AlbumsAdapter(this,
                allAlbums,
                R.layout.albumlist_row);
        albumList.setAdapter(albumsAdapter);

            //For autocomplete field
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(this,
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
                //Get input
                final String find = (searchBar.getText()).toString();
                if(find.equals("")) {
                    return;
                }

                //Create thread for searching
                Thread findThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Album> filtered = findAlbumByName(allAlbums, find);
                        Message msg = updateHandler.obtainMessage(FIND_ALBUM_THREADCODE, filtered);
                        updateHandler.sendMessage(msg);
                    }
                });

                //Run the thread
                loadingCir.setVisibility(View.VISIBLE);
                findThread.run();
            }
        });
        searchButton.setOnClickListener(searchButton_OnClick);

            //Album List
        albumList_OnItemClick.register(UtilityListeners.listView_OnItemClick_ClearFocus(HomeActivity.this));
        albumList_OnItemClick.register(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent newActivity = new Intent(HomeActivity.this, AlbumActivity.class);

                Bundle myData = new Bundle();
                myData.putString("nameAlbum", allAlbums.get(i).getAlbumName());
                myData.putInt("IDAlbum", allAlbums.get(i).getId());

                newActivity.putExtra(ALBUM_TO, myData);
                startActivity(newActivity);
            }
        });
        albumList.setOnItemClickListener(albumList_OnItemClick);

            //Delete
        albumList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog DeleteDialog = new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Bạn muốn xóa album này?\n")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeAlbum(position);
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                DeleteDialog.show();
                return true;
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //-------------------------------------Interfaces Implementations---------------------------------

    @Override
    public void onMessageToActivity(String source, Bundle bundle) {
        switch (source){
            case UtilityGlobals.ADD_ALBUM_DIALOG:
                addAlbum(bundle.getString("albumName"));
                break;
        }
    }

    //---------------------------------Utilities for this activity-----------------------

    //Wrapper class for Handler
    private static class IncomingHandler extends Handler {
        private final WeakReference<HomeActivity> mActivity;

        IncomingHandler(HomeActivity homeActivity) {
            mActivity = new WeakReference<HomeActivity>(homeActivity);
        }
        @Override
        public void handleMessage(Message msg)
        {
            HomeActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        albumsAdapter.notifyDataSetChanged();
    }

}
