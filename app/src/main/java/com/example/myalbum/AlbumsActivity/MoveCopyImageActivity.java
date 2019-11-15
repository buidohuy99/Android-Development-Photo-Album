package com.example.myalbum.AlbumsActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
    public static final String BUNDLE ="BackAlbum";



    private int type;
    private int idImage;
    private int idAlbum;
    ActionBar actionBar;

    public static final String EXTRA_DATA = "EXTRA_DATA";


    public void getData() {
        //Get data from HomeActivity
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        type = myBundle.getInt("Type");
        idAlbum = myBundle.getInt("IDAlbum");
        idImage = myBundle.getByte("IDImage");



    }

    @Override
    public boolean onNavigateUp(){
        Intent newActivity = new Intent(MoveCopyImageActivity.this, AlbumActivity.class);

        Bundle myData = new Bundle();
        myData.putInt("IDAlbum", idAlbum);

        newActivity.putExtra(BUNDLE, myData);
        startActivity(newActivity);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_copy_image_layout);


        actionBar= getActionBar();
        actionBar.setHomeButtonEnabled(true);

        //Set elements
        listView = (ListView)findViewById(R.id.moveCopyAlbumList);

        getData();

        //Get all albums
        allAlbums = new ArrayList<Album>();
        allAlbums = DatabaseHandler.getInstance(MoveCopyImageActivity.this).getAllAlbums();

        albumsAdapter = new AlbumsAdapter(this,
                allAlbums,
                R.layout.albumlist_row);
        listView.setAdapter(albumsAdapter);

        //Album List

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(type == 1)
                {
                    int count = DatabaseHandler.getInstance(MoveCopyImageActivity.this).getNumberOfImagesAtAlbum(idAlbum);
                    Image image = DatabaseHandler.getInstance(MoveCopyImageActivity.this).getImageAt(idAlbum,idImage);
                    DatabaseHandler.getInstance(MoveCopyImageActivity.this).updateIDAlbumIDImage(image,i,count);

                    Intent returnIntent = new Intent();
                    Bundle myBundle = new Bundle();
                    myBundle.putInt("IDAlbum", idAlbum);
                    myBundle.putInt("IDImage", idImage);

                    returnIntent.putExtra("result",myBundle);
                    setResult(Activity.RESULT_OK,returnIntent);

                    finish();



                }
            }
        });
    }
}
