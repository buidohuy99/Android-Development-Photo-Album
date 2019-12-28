package com.example.myalbum.AlbumsActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class ImageAdapter extends BaseAdapter {
    Context context;
    List<Image> picture;


    public ImageAdapter(Context mainActivity, List<Image> list) {
        this.context = mainActivity;
        this.picture = list;
    }


    @Override
    public int getCount() {
        return picture.size();
    }

    @Override
    public Object getItem(int i) {
        return picture.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final ImageView imageView;
        CheckableLayout checkableLayout;

// if possible, reuse (convertView) image already held in cache
        if (view == null) {
// no previous version of thumbnail held in the scrapview holder
// define entry in res/values/dimens.xml for grid height,width in dips
// <dimen name="gridview_size">100dp</dimen>
// setLayoutParams will do conversion to physical pixels
            imageView = new ImageView(context);
            int gridsize = context.getResources().getDimensionPixelOffset(R.dimen.gridview_size);
            imageView.setLayoutParams(new GridView.LayoutParams(gridsize, gridsize));
//imageView.setLayoutParams(new GridView.LayoutParams(100, 100));//NOT a good practice
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);

            checkableLayout = new CheckableLayout(context);
            checkableLayout.addView(imageView);
        } else {
            checkableLayout = (CheckableLayout) view;
            imageView = (ImageView) checkableLayout.getChildAt(0);
        }

        Glide.with(context).load(picture.get(i).getUrlHinh())
                .placeholder(R.drawable.loading)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.error)
                .into(imageView);


        imageView.setId(i);

        return checkableLayout;


    }//

    public class CheckableLayout extends FrameLayout implements Checkable {
        private boolean mChecked;

        public CheckableLayout(Context context) {
            super(context);
        }

        @SuppressWarnings("deprecation")
        public void setChecked(boolean checked) {
            mChecked = checked;

            if(checked)
                setBackgroundColor(Color.RED);
            else
                setBackgroundColor(Color.TRANSPARENT);

        }

        public boolean isChecked() {
            return mChecked;
        }

        public void toggle() {
            setChecked(!mChecked);
        }

    }


}

