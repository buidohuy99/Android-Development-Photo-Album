package com.example.myalbum.utilities;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class UtilityFunctions {
    static public void clearCurrentFocus(Activity activity) {
        //Unfocus current focus
        View current = activity.getCurrentFocus();
        if (current != null) current.clearFocus();
    }
    static public void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    static public int getOrientation(Activity context){
        // the TOP of the device points to [0:North, 1:West, 2:South, 3:East]
        Display display = ((WindowManager) context.getApplication()
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();

        display.getRotation();
        return display.getRotation();
    }
}
