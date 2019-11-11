package com.example.myalbum.EditingPhoto;

import android.content.Context;
import android.os.Bundle;

import android.app.Fragment;

import com.example.myalbum.AlbumsActivity.AlbumActivity;

import java.util.ArrayList;
import java.util.List;

public class AddEmojFragment extends Fragment implements FragmentCallbacks{

    Context context = null;
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
            PhotoEditorHandler main = (PhotoEditorHandler) getActivity();
        }
        catch (IllegalStateException e)
        {
            throw new IllegalStateException( "MainActivity must implement callbacks");
        }
    }

    @Override
    public void onMsgFromMainToFragment(Bundle bundle) {

    }
}
