package com.example.myalbum.XemAnh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.R;

import java.util.List;

public class CustomAdapterViewPager extends PagerAdapter {

    Context context;
    private List<Image> imgList;
    private LayoutInflater layoutInflater;

    public CustomAdapterViewPager(Context context, List<Image> imgList)
    {
        this.context=context;
        this.imgList=imgList;
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imgDisplay;
        layoutInflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View viewLayout = layoutInflater.inflate(R.layout.fragment_page,container,false);
        imgDisplay=(ImageView)viewLayout.findViewById(R.id.image22);
        String path;
        path = imgList.get(position).getUrlHinh();

        Glide.with(context).load(path)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(imgDisplay);
        container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
