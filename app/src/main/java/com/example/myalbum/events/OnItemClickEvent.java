package com.example.myalbum.events;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class OnItemClickEvent implements ListView.OnItemClickListener {
    private List<ListView.OnItemClickListener> listeners = null;

    //Register Listener
    public void register(ListView.OnItemClickListener listener){
        if(listeners == null) listeners = new ArrayList<ListView.OnItemClickListener>();
        listeners.add(listener);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for(ListView.OnItemClickListener x : listeners) {
            x.onItemClick(parent, view, position, id);
        }
    }
}
