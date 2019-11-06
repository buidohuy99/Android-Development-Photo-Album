package com.example.myalbum.utilities;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class UtilityListeners {

    public static View.OnClickListener view_OnClick_ClearFocus(final Activity activity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityFunctions.clearCurrentFocus(activity);
            }
        };
    }

    public static ListView.OnItemClickListener listView_OnItemClick_ClearFocus (final Activity activity){
        return new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UtilityFunctions.clearCurrentFocus(activity);
            }
        };
    }

    public static EditText.OnFocusChangeListener editText_OnFocusChange (final Activity activity) {
        return new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    UtilityFunctions.hideKeyboardFrom(activity, v);
                }
            }
        };
    }
}
