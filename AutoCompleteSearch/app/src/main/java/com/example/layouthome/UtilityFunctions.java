package com.example.layouthome;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UtilityFunctions {
    static void clearCurrentFocus(Activity activity) {
        //Unfocus current focus
        View current = activity.getCurrentFocus();
        if (current != null) current.clearFocus();
    }
    static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
