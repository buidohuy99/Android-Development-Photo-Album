package com.example.gallery2;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends Activity {

    ViewGroup scrollViewgroup;
    ImageView icon;
    ImageView imageSelected;

    Integer[] thumnails = {
            R.drawable.small_01,
            R.drawable.small_02,
            R.drawable.small_03,
            R.drawable.small_04,
            R.drawable.small_05,
            R.drawable.small_06
    };

    Integer[] largeImages = {
            R.drawable.large_01,
            R.drawable.large_02,
            R.drawable.large_03,
            R.drawable.large_04,
            R.drawable.large_05,
            R.drawable.large_06
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageSelected = (ImageView) findViewById(R.id.imageView);
        scrollViewgroup = (ViewGroup) findViewById(R.id.viewGroup);

        for(int i=0; i< thumnails.length ; i++)
        {
            final View singleFrame = getLayoutInflater().inflate(R.layout.item_thumbnails,null);
            singleFrame.setId(i);

            ImageView icon = (ImageView) singleFrame.findViewById(R.id.icon);
            icon.setImageResource(thumnails[i]);

            scrollViewgroup.addView(singleFrame);

            singleFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLargeImage(singleFrame.getId());
                }
            });
        }

    }
    protected  void showLargeImage(int frameID)
    {
        Drawable selectedLargeImage = getResources().getDrawable(largeImages[frameID], getTheme());
        imageSelected.setBackground(selectedLargeImage);
    }
}