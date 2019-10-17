package com.example.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.gallery.MainActivity.getResizedBitmap;

public class ImageAdapter extends BaseAdapter {
    Context context;
    ArrayList<Bitmap> picture;
    public ImageAdapter(Context mainActivity, ArrayList<Bitmap> list) {
        this.context= mainActivity;
        this.picture=list;
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

        ImageView imageView;
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
        } else {
            imageView = (ImageView) view;
        }

        imageView.setImageBitmap(picture.get(i));
        imageView.setId(i);

        return imageView;

    }//getView



}

