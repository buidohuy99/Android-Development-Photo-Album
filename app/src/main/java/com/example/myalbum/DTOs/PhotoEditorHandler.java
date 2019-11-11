package com.example.myalbum.DTOs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.myalbum.R;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class PhotoEditorHandler extends Activity {

    PhotoEditorView mPhotoEditorView;
    PhotoEditor photoEditor;
    ImageButton addEmojiButton;
    ImageButton addTextButton;
    ImageButton addBrushButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editimage_layout);
        mPhotoEditorView = (PhotoEditorView) findViewById(R.id.photoEditorView);
        photoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();

    }
}
