package com.example.myalbum.XemAnh;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.R;

import java.util.List;

public class ViewImageActivity extends Activity {
    private int IDAlbum;
    private int IDImage;
    private List<Image> listImage;
    private ViewPager viewPager;
    private CustomAdapterViewPager customAdapterViewPager;
    private LinearLayout thumbnailsContainer;
    //private BitmapFactory.Options options;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager)findViewById(R.id.view_page);

        getData();

        customAdapterViewPager = new CustomAdapterViewPager(this,listImage);

        viewPager.setAdapter(customAdapterViewPager);
        viewPager.setCurrentItem(IDImage);

        thumbnailsContainer = (LinearLayout) findViewById(R.id.container);

        inflateThumbnails();

    }

    private void inflateThumbnails() {
        for (int i = 0; i < listImage.size(); i++) {
            View imageLayout = getLayoutInflater().inflate(R.layout.item_thumbnails, null);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.thumbnail);
            imageView.setOnClickListener(onChangePageClickListener(i));

            String filepath = listImage.get(i).getUrlHinh();

            Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(filepath),64,64);
            //set to image view
            imageView.setImageBitmap(bitmap);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
}
