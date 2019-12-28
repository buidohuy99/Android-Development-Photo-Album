package com.example.myalbum.AlbumsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.myalbum.R;

//Included for event kind of listener
import com.example.myalbum.events.OnClickEvent;
//Include interface
import com.example.myalbum.events.OnItemClickEvent;
import com.example.myalbum.interfaces.ActivityCallBacks;
//Included for utilities, check out corresponding folders for code
import com.example.myalbum.utilities.HeaderGridView;
import com.example.myalbum.utilities.SearchHistoryManager;
import com.example.myalbum.utilities.UtilityGlobals;
import com.example.myalbum.utilities.UtilityListeners;

//Import DTOs needed
import com.example.myalbum.DTOs.Album;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//test
import com.example.myalbum.DAO.DatabaseHandler;
//end test

public class HomeActivity extends Activity implements ActivityCallBacks {

    //------------------------------------------------Variables--------------------------------------

    //HomeActivity's Globals
    private final static int FIND_ALBUM_THREADCODE = 1;
    private final static int LOAD_ALL_ALBUM_THREADCODE = 2;
    private final static int SET_SELECTED_THREADCODE = 3;
    private final static int RELOAD_ALL_ALBUM_THREADCODE = 4;
    private final static int DELETE_SELECTS_THREADCODE= 5;

    //Intent codes
    private final static int SEARCH_ALBUM = 101;

    public static final String ALBUM_TO ="Album";


    //HomeActivity states
    private boolean isOnEdit = false;
    private boolean activityHindered = false;
    public ActionMode actionmode;
    private GridViewItemCallBack gridViewItemCallBack;

    //Page widgets
    private AutoCompleteTextView searchBar;
    private HeaderGridView albumList;
    private Button searchButton;
    private Button addAlbumButton;
    private ProgressBar loadingCir;
    private AddAlbumDialog addAlbumDialog = null;
    private PasswordCheckDialog passwordPrompt = null;
    private Spinner SortOption;
    private Spinner SortBy;

    //Auto-complete source
    private ArrayList<String> hint;

    //Albums database
    private List<Album> allAlbums;
    private Album navigateTo;

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
    private void addAlbum(String name, String password) {
        Album album = new Album(name, password);
        new AddAlbum().execute(album);
    }

    //Find album by name
    private ArrayList<Album> findAlbumByName(String name){
        if(name == null) return null;
        List<Album> result = DatabaseHandler.getInstance(this).findAlbumByName(name);
        return (ArrayList<Album>)result;
    }

    private void resetAdapters(final ArrayList<Integer> selectedAlbums, boolean setBlur, Integer selectedSize){
        //Add adapters
        //For displaying all albums
        albumsAdapter = new AlbumsAdapter(this,
                allAlbums,
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
        albumsAdapter.setBlurSystemAlbumsState(setBlur);
        if(selectedSize != null) albumsAdapter.setSelectedSize(selectedSize);
        albumList.setAdapter(albumsAdapter);

        //For autocomplete field
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, hint);
        searchBar.setAdapter(autoCompleteAdapter);
    }

    private void bindFunctionalities(final ArrayList<Integer> selectedAlbums, boolean blur, Integer selectedSize){

        resetAdapters(selectedAlbums, blur, selectedSize);

        passwordPrompt = PasswordCheckDialog.newInstance(HomeActivity.this, "Nhập mật khẩu xem album");

        //Set listeners + events

        //Add Album Button
        addAlbumButton_OnClick.register(UtilityListeners.view_OnClick_ClearFocus(HomeActivity.this));
        addAlbumButton_OnClick.register(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addAlbumDialog == null)
                    addAlbumDialog = AddAlbumDialog.newInstance(HomeActivity.this,"Thông tin album");
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
        albumList_OnItemClick.register(UtilityListeners.listView_OnItemClick_ClearFocus(HomeActivity.this));
        albumList_OnItemClick.register(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(isOnEdit){
                //Cannot trigger edit for system albums
                if(allAlbums.get((int)l).getId() < 0 || allAlbums.size() - 1 == l) return;
                albumsAdapter.toggleSelected((int) l);
                albumsAdapter.notifyDataSetChanged();
                actionmode.setTitle(albumsAdapter.getSelectedCount() + " items selected");
            }
            else {
                if(allAlbums.size() - 1 == l) {
                    if(addAlbumDialog == null)
                        addAlbumDialog = AddAlbumDialog.newInstance(HomeActivity.this,"Thông tin album");
                    addAlbumDialog.show();
                    return;
                }

                if(allAlbums.get((int)l).getAlbumPassword() != null) {
                    navigateTo = allAlbums.get((int)l);
                    passwordPrompt.setCompare(allAlbums.get((int)l).getAlbumPassword());
                    passwordPrompt.show();
                    return;
                }

                Intent newActivity = new Intent(HomeActivity.this, AlbumActivity.class);

                Bundle myData = new Bundle();
                myData.putString("nameAlbum", allAlbums.get((int) l).getAlbumName());
                myData.putInt("IDAlbum", allAlbums.get((int) l).getId());

                newActivity.putExtra(ALBUM_TO, myData);
                activityHindered = true;
                startActivity(newActivity);
            }
            }
        });
        albumList.setOnItemClickListener(albumList_OnItemClick);

        SortOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SortAlbum(0,0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Delete
        albumList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(isOnEdit || allAlbums.size() - 1 == id) return false;
                startMyEditMode(null);
                albumsAdapter.toggleBlurSystemAlbums();
                String actionBarTitle = "Select items";
                //Cannot trigger edit for system albums
                if(allAlbums.get((int)id).getId() < 0) {
                    albumsAdapter.notifyDataSetChanged();
                    actionmode.setTitle(actionBarTitle);
                    return true;
                }
                albumsAdapter.toggleSelected((int)id);
                albumsAdapter.notifyDataSetChanged();
                actionBarTitle = albumsAdapter.getSelectedCount() + " items selected";
                actionmode.setTitle(actionBarTitle);
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
                SearchHistoryManager.getInstance().pushSearch((searchBar.getText()).toString());
                newActivity.putParcelableArrayListExtra("Render Info", filtered);
                newActivity.putStringArrayListExtra("AutoComplete", hint);
                searchBar.setText("");
                loadingCir.setVisibility(View.INVISIBLE);
                activityHindered = true;
                startActivityForResult(newActivity, SEARCH_ALBUM);
                break;
            case RELOAD_ALL_ALBUM_THREADCODE: case DELETE_SELECTS_THREADCODE:
                resetAdapters(null, false, null);
                loadingCir.setVisibility(View.INVISIBLE);
                break;
            case LOAD_ALL_ALBUM_THREADCODE:
                bindFunctionalities(null, false, null);
                loadingCir.setVisibility(View.INVISIBLE);
                break;
            case SET_SELECTED_THREADCODE:
                albumsAdapter.notifyDataSetChanged();
                loadingCir.setVisibility(View.INVISIBLE);
                break;
        }
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
                    Album temp = allAlbums.get(selected.get(i));
                    toDelete.add(temp);
                    selected.set(i, temp.getId());
                };
                Integer[] chosenIds = selected.toArray(new Integer[0]);
                for(int i = 0 ; i < toDelete.size(); i++) {
                    hint.remove(toDelete.get(i).getAlbumName());
                    allAlbums.remove(toDelete.get(i));
                };

                //Remove database
                DatabaseHandler.getInstance(HomeActivity.this).deleteAlbums(chosenIds);

                Message msg = updateHandler.obtainMessage(DELETE_SELECTS_THREADCODE);
                updateHandler.sendMessage(msg);
            }
        });

        loadingCir.setVisibility(View.VISIBLE);
        deleteSelects.run();
    }

    private void resolveCheckPassword(boolean state, Album album) {
        if(state) {
            Intent newActivity = new Intent(HomeActivity.this, AlbumActivity.class);

            Bundle myData = new Bundle();
            myData.putString("nameAlbum", album.getAlbumName());
            myData.putInt("IDAlbum", album.getId());

            newActivity.putExtra(ALBUM_TO, myData);
            activityHindered = true;
            startActivity(newActivity);
        }
    }

    private void startMyEditMode(String titleName){
        if(isOnEdit) return;
        isOnEdit = true;
        //change action bar
        if (gridViewItemCallBack == null) {
            gridViewItemCallBack = new GridViewItemCallBack(HomeActivity.this);
        }
        actionmode = startActionMode(gridViewItemCallBack);
        if(titleName != null) actionmode.setTitle(titleName);
    }

    //-------------------------------------------Life-cycle------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // This method must be called on a background thread.
                Glide.get(HomeActivity.this.getApplicationContext()).clearDiskCache();
                return null;
            }
        }.execute();

        //Create header area
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        View headerArea = this.getLayoutInflater().inflate(R.layout.albumlist_header, viewGroup, false);

        //Bind
        searchBar = headerArea.findViewById(R.id.searchBar);
        searchButton = headerArea.findViewById(R.id.searchButton);
        addAlbumButton = headerArea.findViewById(R.id.addAlbumButton);
        albumList = findViewById(R.id.albumList);
        loadingCir = findViewById(R.id.progress_circular);
        SortOption = headerArea.findViewById(R.id.spinner1);
        SortBy = headerArea.findViewById(R.id.spinner2);

        //Add header to gridview
        albumList.addHeaderView(headerArea);

        //Create thread for loading
        Thread loadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseHandler db = DatabaseHandler.getInstance(HomeActivity.this);
                db.initSpecialAlbums();
                allAlbums = db.getAllAlbums();
                //Populate hints with album name
                for(int i = 0 ; i< allAlbums.size(); i++) {
                    hint.add(allAlbums.get(i).getAlbumName());
                }
                allAlbums.add(new Album("Add album", null));

                Message msg = updateHandler.obtainMessage(LOAD_ALL_ALBUM_THREADCODE);
                updateHandler.sendMessage(msg);
            }
        });


        //Check saved state
        if(savedInstanceState != null) {
            if (savedInstanceState.getBoolean("isOnEdit"))
                startMyEditMode(savedInstanceState.getString("editBarText"));
            allAlbums = savedInstanceState.getParcelableArrayList("allAlbums");
            hint = savedInstanceState.getStringArrayList("autocompleteHints");
            ArrayList<Integer> selectedState = (ArrayList<Integer>)savedInstanceState.getSerializable("selectedState");
            Boolean blurState = savedInstanceState.getBoolean("systemAlbumsState");
            if(blurState == null) blurState = false;
            //If found no saved state
            if(allAlbums == null || hint == null){
                //Create necessary arrays
                allAlbums = new ArrayList<Album>();
                hint = new ArrayList<String>();

                //Run the thread
                loadingCir.setVisibility(View.VISIBLE);
                loadThread.run();
                return;
            }
            bindFunctionalities(selectedState, blurState, savedInstanceState.getInt("selectedArraySize"));
        }else{
            //Create necessary arrays
            allAlbums = new ArrayList<Album>();
            hint = new ArrayList<String>();

            //Run the thread
            loadingCir.setVisibility(View.VISIBLE);
            loadThread.run();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if(!activityHindered) {
            loadingCir.setVisibility(View.VISIBLE);

            outState.putBoolean("isOnEdit", isOnEdit);
            if(actionmode != null) outState.putString("editBarText", actionmode.getTitle().toString());
            outState.putParcelableArrayList("allAlbums", (ArrayList<Album>) allAlbums);
            outState.putSerializable("selectedState", albumsAdapter.getSelected());
            outState.putInt("selectedArraySize", albumsAdapter.getSelectedCount());
            outState.putStringArrayList("autocompleteHints", hint);
            outState.putBoolean("systemAlbumsState", albumsAdapter.getBlurSystemAlbumsState());

            loadingCir.setVisibility(View.INVISIBLE);
        }
        if (actionmode != null) actionmode.finish();
        super.onSaveInstanceState(outState);
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
                    allAlbums = DatabaseHandler.getInstance(HomeActivity.this).getAllAlbums();
                    allAlbums.add(new Album("Add album", null));
                    Message msg = updateHandler.obtainMessage(RELOAD_ALL_ALBUM_THREADCODE);
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

    //-------------------------------------Interfaces Implementations---------------------------------

    @Override
    public void onMessageToActivity(String source, Bundle bundle) {
        switch (source){
            case UtilityGlobals.ADD_ALBUM_DIALOG:
                addAlbum(bundle.getString("albumName"), bundle.getString("albumPassword"));
                break;
            case UtilityGlobals.PASSWORD_CHECK_DIALOG:
                if(navigateTo != null) {
                    resolveCheckPassword(bundle.getBoolean("passwordMatch"),navigateTo);
                    navigateTo = null;
                }
                break;
        }
    }

    //---------------------------------Utilities for this activity-----------------------

    private class AddAlbum extends AsyncTask<Album, Void, ArrayAdapter<String>> {

        @Override
        protected ArrayAdapter<String> doInBackground(Album... albums) {
            Album album = albums[0];
            //Add to database
            int nextID = AlbumBusinessLogic.findSmallestMissingAlbumID(allAlbums);
            album.setId(nextID);
            DatabaseHandler.getInstance(HomeActivity.this).addAlbum(album);
            //Display album
            allAlbums.add(allAlbums.size() - 1, album);

            //Update autocomplete
            hint.add(album.getAlbumName());
            ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(HomeActivity.this,
                    android.R.layout.simple_dropdown_item_1line, hint);

            return autoCompleteAdapter;
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> strings) {
            albumsAdapter.notifyDataSetChanged();
            searchBar.setAdapter(strings);
            albumList.smoothScrollByOffset(albumList.getMeasuredHeight());
        }
    }

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

    private class GridViewItemCallBack implements ActionMode.Callback {

        private HomeActivity currentContext;
        private AlertDialog DeleteDialog;
        private ActionMode currentMode;

        public GridViewItemCallBack(HomeActivity mainContext) {
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
                albumsAdapter.toggleBlurSystemAlbums();
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

    @Override
    protected void onResume()
    {
        super.onResume();
        activityHindered = false;
        if(albumsAdapter != null) {
            albumsAdapter.notifyDataSetChanged();
        }
    }

    private class SortByName implements Comparator<Album>
    {

        @Override
        public int compare(Album album, Album t1) {
            return album.getAlbumName().compareTo(t1.getAlbumName());
        }
    }

    private void SortAlbum(int SortOption, int Order)
    {
        List<Album> albums = allAlbums.subList(2, allAlbums.size() - 1);
        Collections.sort(albums, new SortByName());
        for (int i = 2; i < allAlbums.size() - 1; i++)
        {
            allAlbums.set(i, albums.get(i - 2));
        }

        albumsAdapter.notifyDataSetChanged();
    }

}
