package com.example.myalbum.EditingPhoto;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import android.app.FragmentTransaction;

import com.example.myalbum.R;

import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class PhotoEditorHandler extends Activity implements MainCallbacks{


    PhotoEditorView mPhotoEditorView;
    PhotoEditor photoEditor;
    ImageButton addEmojiButton;
    ImageButton addTextButton;
    ImageButton addBrushButton;

    FragmentTransaction ft;
    AddEmojFragment emojiFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editimage_layout);
        mPhotoEditorView = (PhotoEditorView) findViewById(R.id.photoEditorView);
        photoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();

        addEmojiButton = findViewById(R.id.addEmojiButton);
        ft = getFragmentManager().beginTransaction();

        addEmojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> emoji = PhotoEditor.getEmojis(PhotoEditorHandler.this);
                emojiFragment = AddEmojFragment.newInstance(emoji);
                ft.replace(R.id.FragmentHolder, emojiFragment);
                ft.commit();
            }
        });

    }

    @Override
    public void onMsgFromFragToMain(String sender, Bundle bundle) {

        if (sender == "EmojiFragment")
        {

            photoEditor.addEmoji(bundle.getString("ChosenEmoji"));
        }
    }
}
