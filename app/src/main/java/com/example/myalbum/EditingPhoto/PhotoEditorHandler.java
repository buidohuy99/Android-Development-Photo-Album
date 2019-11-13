package com.example.myalbum.EditingPhoto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import android.app.FragmentTransaction;


import com.example.myalbum.R;

import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

class BrushInfo
{
    int size = 20;
    int opacity= 100;
    int color = R.color.black;
}

public class PhotoEditorHandler extends Activity implements MainCallbacks{


    PhotoEditorView mPhotoEditorView;
    PhotoEditor photoEditor;
    ImageButton addEmojiButton;
    ImageButton addTextButton;
    ImageButton addBrushButton;

    FragmentTransaction ft;
    AddEmojFragment emojiFragment;
    BrushFragment brushFragment;
    BrushInfo brushInfo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editimage_layout);
        mPhotoEditorView = (PhotoEditorView) findViewById(R.id.photoEditorView);
        photoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();

        addEmojiButton = findViewById(R.id.addEmojiButton);
        addBrushButton = findViewById(R.id.addBrushButton);
        brushInfo = new BrushInfo();

        ArrayList<String> emoji = PhotoEditor.getEmojis(PhotoEditorHandler.this);
        emojiFragment = AddEmojFragment.newInstance(emoji);



        addEmojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoEditor.setBrushDrawingMode(false);
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.FragmentHolder, emojiFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        addBrushButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                ft = getFragmentManager().beginTransaction();
                brushFragment = BrushFragment.newInstance(brushInfo.size, brushInfo.opacity, brushInfo.color);
                photoEditor.setBrushDrawingMode(true);
                photoEditor.setBrushSize(brushInfo.size);
                photoEditor.setOpacity(brushInfo.opacity);
                photoEditor.setBrushColor(brushInfo.color);
                ft.replace(R.id.FragmentHolder, brushFragment);
                ft.addToBackStack(null);
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

        if (sender == "BrushFragment")
        {

            photoEditor.setBrushSize((float)bundle.getInt("BrushSize"));
            brushInfo.size = bundle.getInt("BrushSize");

            photoEditor.setOpacity(bundle.getInt("Opacity"));
            brushInfo.opacity = bundle.getInt("Opacity");

            photoEditor.setBrushColor(bundle.getInt("Color"));
            brushInfo.color= bundle.getInt("Color");

        }
    }
}
