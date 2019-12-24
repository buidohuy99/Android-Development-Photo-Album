package com.example.myalbum.XemAnh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class TouchImageView extends ViewPager {
    public TouchImageView(@NonNull Context context) {
        super(context);
    }

    public TouchImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof TouchImageView) {
            return ((TouchImageView) v).canScrollHorizontallyFroyo(-dx);
        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }
    }

    private boolean canScrollHorizontallyFroyo(int direction) {
        return canScrollHorizontally(direction);
    }


}

