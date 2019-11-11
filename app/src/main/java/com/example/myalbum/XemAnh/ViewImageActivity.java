package com.example.myalbum.XemAnh;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.example.myalbum.R;

public class ViewImageActivity extends Activity {

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
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    showLargeImage(singleFrame.getId());
                }
            });
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected  void showLargeImage(int frameID)
    {
        Drawable selectedLargeImage = getResources().getDrawable(largeImages[frameID], getTheme());
        imageSelected.setBackground(selectedLargeImage);
    }

  //  @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu,menu);
//        return true;
//    }

    public void showMenu(View view)
    {
        PopupMenu menu = new PopupMenu(this, view);
        menu.inflate(R.menu.menu);
        menu.show();
    }
}