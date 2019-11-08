package com.example.myalbum.events;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class OnClickEvent implements View.OnClickListener{
    private List<View.OnClickListener> listeners = null;

    //Register Listener
    public void register(View.OnClickListener listener){
        if(listeners == null) listeners = new ArrayList<View.OnClickListener>();
        listeners.add(listener);
    }

    @Override
    public void onClick(View v) {
        for(View.OnClickListener x : listeners) {
            x.onClick(v);
        }
    }
}
