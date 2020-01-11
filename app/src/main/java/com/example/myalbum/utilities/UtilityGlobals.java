package com.example.myalbum.utilities;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;

//Save global variables for identification between components
public class UtilityGlobals {

    public static final String ADD_ALBUM_DIALOG = "Add Album Dialog";
    public static final String PASSWORD_CHECK_DIALOG = "Check password";

    public static final int TRASH_ALBUM = -1;
    public static final int FAVORITE_ALBUM = -2;

    public static final String globalDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final SimpleDateFormat globalSDF = new SimpleDateFormat(globalDateFormat);

}
