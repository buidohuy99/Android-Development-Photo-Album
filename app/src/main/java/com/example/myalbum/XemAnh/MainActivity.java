package com.example.gallery2;

import android.app.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

//    ViewGroup scrollViewgroup;
//    ImageView icon;
//    ImageView imageSelected;

    private ArrayList<Integer> images;
    private BitmapFactory.Options options;
    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;
    private LinearLayout thumbnailsContainer;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

//    protected  void showLargeImage(int frameID)
//    {
//        Drawable selectedLargeImage = getResources().getDrawable(largeImages[frameID], getTheme());
//        imageSelected.setBackground(selectedLargeImage);
//    }

  //  @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu,menu);
//        return true;
//    }

//    public void showMenu(View view)
//    {
//        PopupMenu menu = new PopupMenu(this, view);
//        menu.inflate(R.menu.menu);
//        menu.show();
//    }
}