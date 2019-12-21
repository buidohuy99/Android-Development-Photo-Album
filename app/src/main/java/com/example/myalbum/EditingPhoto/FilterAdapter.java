package com.example.myalbum.EditingPhoto;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myalbum.R;

import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    String[] description;
    Integer[] avatar;

    //set listener interface so that others can attach listener to this adapter
    private Listener listener;
    interface Listener {
        void onClick(int position);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }
    }

    public FilterAdapter(String[] d, Integer[] a)
    {
        description = d;
        avatar = a;
    }
    @NonNull
    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.edit_filter_adapter_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterAdapter.ViewHolder holder, final int position) {
        View view = holder.view;

        ImageView imageView = (ImageView)view.findViewById(R.id.avatar);
        imageView.setImageResource(avatar[position]);

        TextView textView = (TextView)view.findViewById(R.id.description);
        textView.setText(description[position]);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                {
                    listener.onClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return description.length;
    }


}
