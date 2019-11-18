package com.example.myalbum.XemAnh;

import android.app.Activity;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.myalbum.AlbumsActivity.AlbumActivity;
import com.example.myalbum.AlbumsActivity.MoveCopyImageActivity;
import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Album;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.EditingPhoto.PhotoEditorHandler;
import com.example.myalbum.R;

import java.nio.ByteBuffer;
import java.util.List;

public class ViewImageActivity extends Activity {
    private int IDAlbum;
    private int IDImage;
    private List<Image> listImage;
    private ViewPager viewPager;
    private CustomAdapterViewPager customAdapterViewPager;
    private LinearLayout thumbnailsContainer;
    private BitmapFactory.Options options;

    private void getData()
    {
        //Get data from HomeActivity
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();

        IDAlbum = myBundle.getInt("IDAlbum");
        IDImage = myBundle.getInt("IDImage");
        listImage = DatabaseHandler.getInstance(ViewImageActivity.this).getAllImageOfAlbum(IDAlbum);
    }

    @Override
    public boolean onNavigateUp(){
        finish();
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager)findViewById(R.id.view_page);
        getData();
        //actionBar= getActionBar();
        //actionBar.setHomeButtonEnabled(true);

        getData();

        Album album =DatabaseHandler.getInstance(this).getAlbum(IDAlbum);
        this.setTitle(album.getAlbumName());

        customAdapterViewPager = new CustomAdapterViewPager(this,listImage);

        viewPager.setAdapter(customAdapterViewPager);
        viewPager.setCurrentItem(IDImage);

        thumbnailsContainer = (LinearLayout) findViewById(R.id.container);

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
        if (id == R.id.action_edit)
        {
            Intent intent =new Intent(this, PhotoEditorHandler.class);
            startActivity(intent);
            return true;
        }
        return false;

    }

    private void inflateThumbnails() {
        for (int i = 0; i < listImage.size(); i++) {
            View imageLayout = getLayoutInflater().inflate(R.layout.item_thumbnails, null);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.thumbnail);
            imageView.setOnClickListener(onChangePageClickListener(i));


//            Image imgtemp = DatabaseHandler.getInstance(this).getImageAt(IDAlbum,i);
//
//            Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imgtemp.getUrlHinh()),64,64);
            Glide.with(this).load(listImage.get(i).getUrlHinh()).placeholder(R.drawable.loading).error(R.drawable.error).into(imageView);
            //set to image view
            //imageView.setImageBitmap(bitmap);
            //add imageview
            thumbnailsContainer.addView(imageLayout);
        }
    }

    private View.OnClickListener onChangePageClickListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(i);
            }
        };
    }
}
