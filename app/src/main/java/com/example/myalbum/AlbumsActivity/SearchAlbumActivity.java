package com.example.myalbum.AlbumsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Album;
import com.example.myalbum.R;
import com.example.myalbum.events.OnClickEvent;
import com.example.myalbum.events.OnItemClickEvent;
import com.example.myalbum.interfaces.ActivityCallBacks;
import com.example.myalbum.utilities.SearchHistoryManager;
import com.example.myalbum.utilities.UtilityFunctions;
import com.example.myalbum.utilities.UtilityGlobals;
import com.example.myalbum.utilities.UtilityListeners;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapterWrapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.myalbum.AlbumsActivity.AlbumActivity.ALBUM_TO;

public class SearchAlbumActivity extends Activity implements ActivityCallBacks {

    //Search Album Activity thread codes
    private final static int FIND_ALBUM_THREADCODE = 1;
    private final static int SET_SELECTED_THREADCODE = 2;
    private final static int RELOAD_ALBUMS_THREADCODE = 3;
    private final static int LOAD_PAGE_AGAIN_THREADCODE = 4;
    private final static int DELETE_SELECTS_THREADCODE= 5;

    //Intent codes
    private final static int SEARCH_ALBUM = 101;

    //Activity states
    private boolean isOnEdit = false;
    private int originalOrientation;
    public ActionMode actionmode;
    private GridViewItemCallBack gridViewItemCallBack;

    //Page widgets
    private AutoCompleteTextView searchBar;
    private GridView albumList;
    private Button searchButton;
    private ProgressBar loadingCir;
    private PasswordCheckDialog passwordPrompt = null;
    private Spinner SortOption;
    private Spinner SortOrder;

    private ArrayList<Album> renderAlbums;
    private ArrayList<String> hint;
    private Album navigateTo = null;

    //Adapters
    private AlbumsAdapter albumsAdapter;
    //Wrapper to section the adapter
    private StickyGridHeadersSimpleAdapterWrapper albumsWrapperAdapter;

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
    private void startMyEditMode(String titleName){
        if(isOnEdit) return;
        isOnEdit = true;
        //change action bar
        if (gridViewItemCallBack == null) {
            gridViewItemCallBack = new GridViewItemCallBack(this);
        }
        actionmode = startActionMode(gridViewItemCallBack);
        if(titleName != null) actionmode.setTitle(titleName);
    }

    private void resetAdapters(final ArrayList<Integer> selectedAlbums, Integer selectedSize) {
        //Add adapters
        //For displaying all albums
        if(albumsAdapter == null) {
            albumsAdapter = new AlbumsAdapter(this,
                    renderAlbums,
                    R.layout.albumlist_row);
        }
        if(albumsWrapperAdapter == null) {
            albumsWrapperAdapter = new StickyGridHeadersSimpleAdapterWrapper(albumsAdapter);
        }
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
        if(selectedSize != null) albumsAdapter.setSelectedSize(selectedSize);
        albumList.setAdapter(albumsWrapperAdapter);

        //For autocomplete field
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, hint);
        searchBar.setAdapter(autoCompleteAdapter);
    }

    private void bindFunctionalities(final ArrayList<Integer> selectedAlbums, Integer selectedSize){
        //Reset adapters
        resetAdapters(selectedAlbums, selectedSize);

        passwordPrompt = PasswordCheckDialog.newInstance(SearchAlbumActivity.this, "Nhập mật khẩu xem album");
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
                    //Cannot trigger edit for system albums
                    if(renderAlbums.get((int)l).getId() < 0) return;
                    albumsAdapter.toggleSelected((int) l);
                    albumsAdapter.notifyDataSetChanged();
                    actionmode.setTitle(albumsAdapter.getSelectedCount() + " items selected");
                }
                else {

                    if(renderAlbums.get((int)l).getAlbumPassword() != null) {
                        navigateTo = renderAlbums.get((int)l);
                        passwordPrompt.setCompare(renderAlbums.get((int)l).getAlbumPassword());
                        passwordPrompt.show();
                        return;
                    }

                    Intent newActivity = new Intent(SearchAlbumActivity.this, AlbumActivity.class);

                    Bundle myData = new Bundle();
                    myData.putString("nameAlbum", renderAlbums.get((int) l).getAlbumName());
                    myData.putInt("IDAlbum", renderAlbums.get((int) l).getId());

                    newActivity.putExtra(ALBUM_TO, myData);
                    startActivity(newActivity);
                }
            }
        });
        albumList.setOnItemClickListener(albumList_OnItemClick);

        //Delete
        albumList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(isOnEdit) return false;
                startMyEditMode(null);
                albumsAdapter.toggleBlurSystemAlbums();
                String actionBarTitle = "Select items";
                //Cannot trigger edit for system albums
                if(renderAlbums.get((int)id).getId() < 0) {
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

        SortOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerValue = String.valueOf(SortOption.getSelectedItem().toString());

                if (!spinnerValue.equals("Xếp theo..."))
                {
                    SortAlbum(SortOption.getSelectedItemPosition(), SortOrder.getSelectedItemPosition());
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        SortOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerValue = String.valueOf(SortOption.getSelectedItem().toString());

                if (!spinnerValue.equals("Xếp theo..."))
                {
                    SortAlbum(SortOption.getSelectedItemPosition(), SortOrder.getSelectedItemPosition());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    //Function to process messages send to main thread from other thread
    private void handleMessage(Message msg){
        //Message from find album thread
        switch (msg.what) {
            case FIND_ALBUM_THREADCODE:
                ArrayList<Album> filtered = (ArrayList<Album>) msg.obj;
                SearchHistoryManager.getInstance().pushSearch((searchBar.getText()).toString());
                Intent newActivity = new Intent(this, SearchAlbumActivity.class);
                newActivity.putParcelableArrayListExtra("Render Info", filtered);
                newActivity.putExtra("AutoComplete", hint);
                searchBar.setText("");
                loadingCir.setVisibility(View.INVISIBLE);
                startActivityForResult(newActivity, SEARCH_ALBUM);
                break;
            case SET_SELECTED_THREADCODE:
                albumsAdapter.notifyDataSetChanged();
                loadingCir.setVisibility(View.INVISIBLE);
                break;
            case RELOAD_ALBUMS_THREADCODE:
                renderAlbums = (ArrayList<Album>) msg.obj;
                resetAdapters(null, null);
                loadingCir.setVisibility(View.INVISIBLE);
                break;
            case LOAD_PAGE_AGAIN_THREADCODE:
                renderAlbums = (ArrayList<Album>) msg.obj;
                bindFunctionalities(null, null);
                loadingCir.setVisibility(View.INVISIBLE);
                break;
            case DELETE_SELECTS_THREADCODE:
                resetAdapters(null, null);
                loadingCir.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void resolveCheckPassword(boolean state, Album album) {
        if(state) {
            Intent newActivity = new Intent(SearchAlbumActivity.this, AlbumActivity.class);

            Bundle myData = new Bundle();
            myData.putString("nameAlbum", album.getAlbumName());
            myData.putInt("IDAlbum", album.getId());

            newActivity.putExtra(ALBUM_TO, myData);
            startActivity(newActivity);
        }
    }

    //--------------------------Life-cycle--------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchalbum_layout);
        originalOrientation = UtilityFunctions.getOrientation(this);

        //Set action bar back
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        //Bind
        searchBar = findViewById(R.id.searchBar);
        searchButton = findViewById(R.id.searchButton);
        albumList = findViewById(R.id.albumList);
        loadingCir = findViewById(R.id.progress_circular);
        SortOption = findViewById(R.id.spinner1);
        SortOrder = findViewById(R.id.spinner2);

        if(UtilityFunctions.getOrientation(this) % 2 != 0) {
            albumList.setNumColumns(5);
        }
        else {
            albumList.setNumColumns(2);
        }

        if(savedInstanceState != null) {
            if(savedInstanceState.getBoolean("orientationChanged")) {
                if (savedInstanceState.getBoolean("isOnEdit"))
                    startMyEditMode(savedInstanceState.getString("editBarText"));
                hint = savedInstanceState.getStringArrayList("autocompleteHints");
                renderAlbums = savedInstanceState.getParcelableArrayList("renderAlbums");
                bindFunctionalities((ArrayList<Integer>)savedInstanceState.getSerializable("selectedState"), savedInstanceState.getInt("selectedArraySize"));
            }else{
                Thread researchAlbums =  new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Album> filtered = findAlbumByName(SearchHistoryManager.getInstance().peekSearch());
                        List<Album> allAlbums = DatabaseHandler.getInstance(SearchAlbumActivity.this).getAllAlbums();
                        //Populate hints with album name
                        for(int i = 0 ; i< allAlbums.size(); i++) {
                            hint.add(allAlbums.get(i).getAlbumName());
                        }
                        Message msg = updateHandler.obtainMessage(LOAD_PAGE_AGAIN_THREADCODE, filtered);
                        updateHandler.sendMessage(msg);
                    }
                });

                //Create new hints
                hint = new ArrayList<>();

                //Run the thread
                loadingCir.setVisibility(View.VISIBLE);
                researchAlbums.run();
            }
        }else{
            Intent received = getIntent();
            hint = received.getStringArrayListExtra("AutoComplete");
            renderAlbums = received.getParcelableArrayListExtra("Render Info");
            searchBar.setText(SearchHistoryManager.getInstance().peekSearch());
            bindFunctionalities(null, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SEARCH_ALBUM && resultCode == RESULT_OK) {
            Intent received = data;
            ArrayList<String> temp = received.getStringArrayListExtra("AutoComplete");

            searchBar.setText(SearchHistoryManager.getInstance().peekSearch());

            Thread reloadAlbums =  new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Album> filtered = findAlbumByName(SearchHistoryManager.getInstance().peekSearch());
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
        loadingCir.setVisibility(View.VISIBLE);

        boolean orientationFlag = UtilityFunctions.getOrientation(this) != originalOrientation;
        outState.putBoolean("orientationChanged", orientationFlag);
        if(orientationFlag) {
            outState.putBoolean("isOnEdit", isOnEdit);
            if (actionmode != null)
                outState.putString("editBarText", actionmode.getTitle().toString());
            outState.putInt("selectedArraySize", albumsAdapter.getSelectedCount());
            outState.putSerializable("selectedState", albumsAdapter.getSelected());
            outState.putStringArrayList("autocompleteHints", hint);
            outState.putParcelableArrayList("renderAlbums", renderAlbums);
        }

        loadingCir.setVisibility(View.INVISIBLE);
        if (actionmode != null) actionmode.finish();
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onNavigateUp() {
        SearchHistoryManager.getInstance().popSearch();
        Intent intent = getIntent();
        intent.putStringArrayListExtra("AutoComplete", hint);
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        SearchHistoryManager.getInstance().popSearch();
        Intent intent = getIntent();
        intent.putStringArrayListExtra("AutoComplete", hint);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    //-----------------------------Implements-------------------

    @Override
    public void onMessageToActivity(String Source, Bundle bundle) {
        switch (Source) {
            case UtilityGlobals.PASSWORD_CHECK_DIALOG:
                if (navigateTo != null) {
                    resolveCheckPassword(bundle.getBoolean("passwordMatch"), navigateTo);
                    navigateTo = null;
                }
                break;
        }
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

    private class SortByImageCount implements Comparator<Album>
    {

        @Override
        public int compare(Album album, Album t1) {
            int count1 = DatabaseHandler.getInstance(SearchAlbumActivity.this).getNumberOfImages(album.getId());
            int count2 = DatabaseHandler.getInstance(SearchAlbumActivity.this).getNumberOfImages(t1.getId());

            return count1 - count2;
        }
    }

    private void SortAlbum(int SortOption, int Order)
    {
        if (SortOption == 1)
        {
            if (Order == 0)
            {
                Collections.sort(renderAlbums, new SortByName());
            }
            else
            {
                Collections.sort(renderAlbums, Collections.reverseOrder(new SortByName()));
            }

        }
        else
        {
            if (Order == 0)
            {
                Collections.sort(renderAlbums, new SortByImageCount());
            }
            else
            {
                Collections.sort(renderAlbums, Collections.reverseOrder(new SortByImageCount()));
            }
        }
        albumsAdapter.notifyDataSetChanged();

    }

}
