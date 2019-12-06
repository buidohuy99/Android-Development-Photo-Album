package com.example.myalbum.AlbumsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NavUtils;

import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Album;
import com.example.myalbum.R;
import com.example.myalbum.events.OnClickEvent;
import com.example.myalbum.events.OnItemClickEvent;
import com.example.myalbum.utilities.UtilityListeners;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.example.myalbum.AlbumsActivity.AlbumActivity.ALBUM_TO;

public class SearchAlbumActivity extends Activity {

    //Search Album Activity thread codes
    private final static int FIND_ALBUM_THREADCODE = 1;
    private final static int SET_SELECTED_THREADCODE = 2;
    private final static int RELOAD_ALBUMS_THREADCODE = 3;
    private final static int DELETE_SELECTS_THREADCODE= 5;

    //Intent codes
    private final static int SEARCH_ALBUM = 101;
    private final static int SEE_IMAGES = 102;

    //Activity states
    private boolean isOnEdit = false;
    private boolean activityHindered = false;
    private String previousSearch = null;
    public ActionMode actionmode;
    private GridViewItemCallBack gridViewItemCallBack;

    //Page widgets
    private AutoCompleteTextView searchBar;
    private GridView albumList;
    private Button searchButton;
    private ProgressBar loadingCir;

    private ArrayList<Album> renderAlbums;
    private ArrayList<String> hint;

    //Adapters
    private AlbumsAdapter albumsAdapter;

    //Events
    private OnClickEvent searchButton_OnClick = new OnClickEvent();
    private OnItemClickEvent albumList_OnItemClick = new OnItemClickEvent();

    //Handlers
    private IncomingHandler updateHandler = new IncomingHandler(this);

    //Album logic
    private ArrayList<Album> findAlbumByName(String name){
        if(name == null) return null;
        List<Album> result = DatabaseHandler.getInstance(this).findAlbumByName(name);
        return (ArrayList<Album>)result;
    }

    private void deleteSelected() {
        Thread deleteSelects = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Integer> selected = albumsAdapter.getSelected();
                if(selected.size() == 0) {
                    Message msg = updateHandler.obtainMessage(DELETE_SELECTS_THREADCODE, null);
                    updateHandler.sendMessage(msg);
                    return;
                }
                //update autoComplete
                //Remove display
                ArrayList<Album> toDelete = new ArrayList<>();
                for(int i = 0 ; i < selected.size(); i++) {
                    Album temp = renderAlbums.get(selected.get(i));
                    toDelete.add(temp);
                    selected.set(i, temp.getId());
                };
                Integer[] chosenIds = selected.toArray(new Integer[0]);
                for(int i = 0 ; i < toDelete.size(); i++) {
                    hint.remove(toDelete.get(i).getAlbumName());
                    renderAlbums.remove(toDelete.get(i));
                };
                //Remove database
                DatabaseHandler.getInstance(SearchAlbumActivity.this).deleteAlbums(chosenIds);

                Message msg = updateHandler.obtainMessage(DELETE_SELECTS_THREADCODE);
                updateHandler.sendMessage(msg);
            }
        });

        loadingCir.setVisibility(View.VISIBLE);
        deleteSelects.run();
    }

    //------------------------Misc ver 2 ---------------------
    private void startMyEditMode(){
        if(isOnEdit) return;
        isOnEdit = true;
        //change action bar
        if (gridViewItemCallBack == null) {
            gridViewItemCallBack = new GridViewItemCallBack(this);
        }
        actionmode = startActionMode(gridViewItemCallBack);
        actionmode.setTitle("Select albums");
    }

    private void resetAdapters(final ArrayList<Integer> selectedAlbums) {
        //Add adapters
        //For displaying all albums
        albumsAdapter = new AlbumsAdapter(this,
                renderAlbums,
                R.layout.albumlist_row);
        if(selectedAlbums != null) {
            //Create thread for setting selected
            Thread setSelectedThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    albumsAdapter.setSelected(selectedAlbums);
                    Message msg = updateHandler.obtainMessage(SET_SELECTED_THREADCODE);
                    updateHandler.sendMessage(msg);
                }
            });

            loadingCir.setVisibility(View.VISIBLE);
            setSelectedThread.run();
        }
        albumList.setAdapter(albumsAdapter);

        //For autocomplete field
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, hint);
        searchBar.setAdapter(autoCompleteAdapter);
    }

    private void bindFunctionalities(final ArrayList<Integer> selectedAlbums){
        //Reset adapters
        resetAdapters(selectedAlbums);
        //Set listeners + events
        //Search Bar
        searchBar.setOnFocusChangeListener(UtilityListeners.editText_OnFocusChange(this));

        //Search Button
        searchButton_OnClick.register(UtilityListeners.view_OnClick_ClearFocus(this));
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
                        ArrayList<Album> filtered = findAlbumByName(find);
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
        albumList_OnItemClick.register(UtilityListeners.listView_OnItemClick_ClearFocus(this));
        albumList_OnItemClick.register(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(isOnEdit){
                    albumsAdapter.toggleSelected((int) l);
                    albumsAdapter.notifyDataSetChanged();
                }
                else {
                    Intent newActivity = new Intent(SearchAlbumActivity.this, AlbumActivity.class);

                    Bundle myData = new Bundle();
                    myData.putString("nameAlbum", renderAlbums.get((int) l).getAlbumName());
                    myData.putInt("IDAlbum", renderAlbums.get((int) l).getId());

                    newActivity.putExtra(ALBUM_TO, myData);
                    activityHindered = true;
                    startActivityForResult(newActivity, SEE_IMAGES);
                }
            }
        });
        albumList.setOnItemClickListener(albumList_OnItemClick);

        //Delete
        albumList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(isOnEdit) return false;
                startMyEditMode();
                albumsAdapter.toggleSelected((int)id);
                albumsAdapter.notifyDataSetChanged();
                return true;
            }
        });

    }

    //Function to process messages send to main thread from other thread
    private void handleMessage(Message msg){
        //Message from find album thread
        switch (msg.what) {
            case FIND_ALBUM_THREADCODE:
                ArrayList<Album> filtered = (ArrayList<Album>) msg.obj;
                Intent newActivity = new Intent(this, SearchAlbumActivity.class);
                newActivity.putParcelableArrayListExtra("Render Info", filtered);
                newActivity.putExtra("AutoComplete", hint);
                newActivity.putExtra("Search Word", (searchBar.getText()).toString());
                loadingCir.setVisibility(View.INVISIBLE);
                activityHindered = true;
                startActivityForResult(newActivity, SEARCH_ALBUM);
                break;
            case SET_SELECTED_THREADCODE:
                albumsAdapter.notifyDataSetChanged();
                loadingCir.setVisibility(View.INVISIBLE);
                break;
            case RELOAD_ALBUMS_THREADCODE:
                renderAlbums = (ArrayList<Album>) msg.obj;
                resetAdapters(null);
                loadingCir.setVisibility(View.INVISIBLE);
                break;
            case DELETE_SELECTS_THREADCODE:
                resetAdapters(null);
                loadingCir.setVisibility(View.INVISIBLE);
                break;
        }
    }

    //--------------------------Life-cycle--------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchalbum_layout);

        //Set action bar back
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        //Bind
        searchBar = findViewById(R.id.searchBar);
        searchButton = findViewById(R.id.searchButton);
        albumList = findViewById(R.id.albumList);
        loadingCir = findViewById(R.id.progress_circular);

        if(savedInstanceState != null) {
            if (savedInstanceState.getBoolean("isOnEdit"))
                startMyEditMode();
            hint = savedInstanceState.getStringArrayList("autocompleteHints");
            renderAlbums = savedInstanceState.getParcelableArrayList("renderAlbums");
            bindFunctionalities((ArrayList<Integer>)savedInstanceState.getSerializable("selectedState"));
        }else{
            Intent received = getIntent();
            hint = received.getStringArrayListExtra("AutoComplete");
            renderAlbums = received.getParcelableArrayListExtra("Render Info");
            previousSearch = received.getStringExtra("Search Word");
            bindFunctionalities(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SEARCH_ALBUM && resultCode == RESULT_OK) {
            Intent received = data;
            ArrayList<String> temp = received.getStringArrayListExtra("AutoComplete");

            Thread reloadAlbums =  new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Album> filtered = findAlbumByName(previousSearch);
                    Message msg = updateHandler.obtainMessage(RELOAD_ALBUMS_THREADCODE, filtered);
                    updateHandler.sendMessage(msg);
                }
            });

            if(temp.size() != hint.size()) {
                //Run the thread
                hint = temp;
                loadingCir.setVisibility(View.VISIBLE);
                reloadAlbums.run();
            }
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if(!activityHindered) {
            loadingCir.setVisibility(View.VISIBLE);
            outState.putBoolean("isOnEdit", isOnEdit);
            outState.putSerializable("selectedState", albumsAdapter.getSelected());
            outState.putStringArrayList("autocompleteHints", hint);
            outState.putParcelableArrayList("renderAlbums", renderAlbums);

            loadingCir.setVisibility(View.INVISIBLE);
        }
        if (actionmode != null) actionmode.finish();
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onNavigateUp() {
        Intent intent = getIntent();
        intent.putStringArrayListExtra("AutoComplete", hint);
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }

    //-------------------------------Misc------------------------

    private class GridViewItemCallBack implements ActionMode.Callback {

        private SearchAlbumActivity currentContext;
        private AlertDialog DeleteDialog;
        private ActionMode currentMode;

        public GridViewItemCallBack(SearchAlbumActivity mainContext) {
            currentContext = mainContext;
            DeleteDialog = new AlertDialog.Builder(currentContext)
                    .setTitle("Bạn muốn xóa các mục đã lựa chọn?\n")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isOnEdit = false;
                            deleteSelected();
                            currentMode.finish();
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if(item.getItemId() == R.id.action_delete_album){
                DeleteDialog.show();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            isOnEdit = false;
            actionmode = null;
            if(albumsAdapter != null) {
                albumsAdapter.clearSelected();
                albumsAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.editbar_album, menu);
            currentMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }

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
                activity.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        activityHindered = false;
        if(albumsAdapter != null) {
            albumsAdapter.notifyDataSetChanged();
        }
    }
}
