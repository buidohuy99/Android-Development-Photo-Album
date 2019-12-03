package com.example.myalbum.AlbumsActivity;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.R;
import com.example.myalbum.XemAnh.CustomAdapterViewPager;
import com.example.myalbum.XemAnh.ViewImageActivity;

import java.util.List;

public class SlideshowActivity extends Activity {

    int idAlbum;
    private ViewPager viewPager;
    private CustomAdapterViewPager customAdapterViewPager;
    private Button continueSlide;
    private LinearLayout header;
    private LinearLayout screen;
    int next = 0;
    Handler myHandler = new Handler();
    boolean isRunning;
    List<Image> listImage;


    public boolean onNavigateUp(){
        finish();
        return true;
    }

    void getData()
    {
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getBundleExtra("AlbumActivity");

        if(myBundle != null)
            idAlbum = myBundle.getInt("IDAlbum");

        listImage = DatabaseHandler.getInstance(this).getAllImageOfAlbum(idAlbum);

    }

    void init()
    {
        viewPager = (ViewPager) findViewById(R.id.view_page_slideshow);
        continueSlide = (Button) findViewById(R.id.continueSlide);
        header = (LinearLayout) findViewById(R.id.header);
        screen = (LinearLayout) findViewById(R.id.MainSlideShow);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_show);
        init();
        getData();

        customAdapterViewPager = new CustomAdapterViewPager(this,listImage);
        viewPager.setAdapter(customAdapterViewPager);
        isRunning = true;
        viewPager.setCurrentItem(0);

        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SlideshowActivity.this, "onCreate", Toast.LENGTH_SHORT).show();
            }
        });
        continueSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRunning == false)
                {
                    continueSlide.setText("PAUSE");

                    isRunning = true;
                }
                else
                {
                    continueSlide.setText("CONTINUE");

                    isRunning = false;
                }
            }
        });

//        viewPager.setFocusable(false);
//        viewPager.setClickable(false);

        onStart();

    }



    public void onStart() {
        super.onStart();
        Thread myBackgroundThread = new Thread( backgroundTask, "SlideShow" );
        myBackgroundThread.start();

    }
    private Runnable foregroundRunnable = new Runnable() {
        @Override
        public void run() {
            try {

                    if(!viewPager.arrowScroll(View.FOCUS_RIGHT))
                        viewPager.setCurrentItem(0);

            } catch (Exception e) {
                Log.e("<<foregroundTask>>", e.getMessage());
            }
        }
    }; // foregroundTask

    private Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {
// busy work goes here...
            try {
                while(true)
                {
                    Thread.sleep(2000);
                    while(!isRunning);  
                    myHandler.post(foregroundRunnable);

                }

            } catch (InterruptedException e) {
                Log.e("<<foregroundTask>>", e.getMessage());
            }
        }// run
    };// backgroundTask


}

