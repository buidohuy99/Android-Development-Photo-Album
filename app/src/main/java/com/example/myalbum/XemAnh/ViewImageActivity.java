package com.example.myalbum.XemAnh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
}
