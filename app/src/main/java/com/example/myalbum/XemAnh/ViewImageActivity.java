package com.example.myalbum.XemAnh;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.myalbum.AlbumsActivity.AlbumActivity;
import com.example.myalbum.AlbumsActivity.MoveCopyImageActivity;
import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.EditingPhoto.PhotoEditorHandler;
import com.example.myalbum.R;

import java.util.ArrayList;
import java.util.List;

public class ViewImageActivity extends FragmentActivity {

    public static final String BUNDLE ="BackAlbum";

    private int IDAlbum;
    private int IDImage;
    private List<Image> listImage;
    public void getData() {
        //Get data from HomeActivity
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        IDAlbum = myBundle.getInt("IDAlbum");
        IDImage = myBundle.getInt("IDImage");
        listImage = DatabaseHandler.getInstance(ViewImageActivity.this).getAllImageOfAlbum(IDAlbum);

    }
    private ArrayList<Integer> images;
    private BitmapFactory.Options options;
    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;
    private LinearLayout thumbnailsContainer;
    ActionBar actionBar;

    private final static int[] largeImages= new int[]{
            R.drawable.large_01,
            R.drawable.large_02,
            R.drawable.large_03,
            R.drawable.large_04,
            R.drawable.large_05,
            R.drawable.large_06
    };

//    Integer[] thumnails = {
//            R.drawable.small_01,
//            R.drawable.small_02,
//            R.drawable.small_03,
//            R.drawable.small_04,
//            R.drawable.small_05,
//            R.drawable.small_06
//    };
    @Override
    public boolean onNavigateUp(){
        finish();
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getData();
        actionBar= getActionBar();
        actionBar.setHomeButtonEnabled(true);

        images = new ArrayList<>();

        viewPager =(ViewPager)findViewById(R.id.ViewPager);
        thumbnailsContainer = (LinearLayout) findViewById(R.id.container);

        setImagesData();

        adapter = new ViewPagerAdapter (getSupportFragmentManager(),images);
        viewPager.setAdapter(adapter);

        inflateThumbnails();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        int id = menuItem.getItemId();
        if (id == R.id.action_settings)
        {
            Intent intent =new Intent(this, PhotoEditorHandler.class);
            startActivity(intent);
            return true;
        }
        return false;

    }

    private void inflateThumbnails() {
        for (int i = 0; i < images.size(); i++) {
            View imageLayout = getLayoutInflater().inflate(R.layout.item_thumbnails, null);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.icon);
            imageView.setOnClickListener(onChagePageClickListener(i));
            options = new BitmapFactory.Options();
            options.inSampleSize = 3;
            options.inDither = false;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), images.get(i), options );
            imageView.setImageBitmap(bitmap);
            //set to image view
            imageView.setImageBitmap(bitmap);
            //add imageview
            thumbnailsContainer.addView(imageLayout);
        }
    }

    private View.OnClickListener onChagePageClickListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(i);
            }
        };
    }

    private void setImagesData() {
        for(int i=0;i<largeImages.length;i++)
        {
            images.add(largeImages[i]);
        }
    }

}