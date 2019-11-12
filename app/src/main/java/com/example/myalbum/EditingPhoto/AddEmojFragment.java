package com.example.myalbum.EditingPhoto;

import android.content.Context;
import android.os.Bundle;

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myalbum.AlbumsActivity.AlbumActivity;
import com.example.myalbum.R;

import java.util.ArrayList;
import java.util.List;

public class AddEmojFragment extends Fragment implements FragmentCallbacks{

    Context context = null;
    PhotoEditorHandler main;
    public static AddEmojFragment newInstance(ArrayList<String> EmojiList)
    {
        AddEmojFragment fragment = new AddEmojFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("EmojiList", EmojiList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            context = getActivity();
            main = (PhotoEditorHandler) getActivity();
        }
        catch (IllegalStateException e)
        {
            throw new IllegalStateException( "MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        LinearLayout emojiLayout = (LinearLayout) inflater.inflate(R.layout.edit_emoji_fragment_layout, null);
        GridView EmojiGridView = emojiLayout.findViewById(R.id.EmojiList);

        final Bundle bundle = getArguments();
        final ArrayList<String> emojiList = bundle.getStringArrayList("EmojiList");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, emojiList);
        EmojiGridView.setAdapter(adapter);

        EmojiGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Bundle SendBundle = new Bundle();
                SendBundle.putString("ChosenEmoji",emojiList.get(position));
                main.onMsgFromFragToMain("EmojiFragment", SendBundle);
            }
        });
// do this for each row (ViewHolder-Pattern could be used for better performance!)
        return emojiLayout;

    }

    @Override
    public void onMsgFromMainToFragment(Bundle bundle) {

    }
}
