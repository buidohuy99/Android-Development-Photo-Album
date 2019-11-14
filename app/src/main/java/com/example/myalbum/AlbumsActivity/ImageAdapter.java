package com.example.myalbum.AlbumsActivity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.R;

import java.util.List;



public class ImageAdapter extends BaseAdapter {
    Context context;
    List<Image> picture;
    public ImageAdapter(Context mainActivity, List<Image> list) {
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
            imageView.setPadding(1, 1, 3, 1);
        } else {
            imageView = (ImageView) view;
        }

        Glide.with(context).load(picture.get(i).getUrlHinh())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(imageView);
        imageView.setId(i);

        return imageView;

    }//getView



}

