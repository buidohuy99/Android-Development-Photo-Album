package com.example.albumhome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AlbumArrayAdapter extends ArrayAdapter<Album> {
    private static class ViewHolder {
        ImageView avatar;
        TextView name;
        TextView num;
    }

    public AlbumArrayAdapter(Context view, ArrayList<Album> list)
    {
        super(view, R.layout.album_row, list);

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Album album = getItem(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.album_row, parent, false);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.Avatar);
            viewHolder.name = (TextView) convertView.findViewById(R.id.Name);
            viewHolder.num = (TextView) convertView.findViewById(R.id.Number);
            convertView.setTag(viewHolder);
        }
        else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.avatar.setImageResource(album.avatar);
        viewHolder.name.setText(album.name);
        viewHolder.num.setText(Integer.toString(album.numPhoto));
        return convertView;
    };
}
