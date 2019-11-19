package com.example.myalbum.EditingPhoto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;

import android.app.FragmentTransaction;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.R;

import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

class BrushInfo
{
    int size = 20;
    int opacity= 100;
    int color = R.color.black;
}

public class PhotoEditorHandler extends Activity implements MainCallbacks{

    static final int WRITE_PERMISSION = 101;

    Context context;
    int IDAlbum;
    int IDImage;
    Image image;


    PhotoEditorView mPhotoEditorView;
    PhotoEditor photoEditor;


    HorizontalScrollView editBar;
    ConstraintLayout navigateBar;
    ConstraintLayout fragmentWindow;

    ImageButton closeFragmentButton;
    ImageButton undoButton;
    ImageButton redoButton;
    ImageButton saveButton;

    ImageButton addEmojiButton;
    ImageButton addTextButton;
    ImageButton addBrushButton;

    BrushInfo brushInfo;

    FragmentTransaction ft;

    AddEmojFragment emojiFragment;
    BrushFragment brushFragment;
    AddTextFragment textFragment;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent myCallerIntent = getIntent();
        Bundle myBundle = myCallerIntent.getExtras();
        IDAlbum = myBundle.getInt("IDAlbum");
        IDImage = myBundle.getInt("IDImage");
        context = this;

        findLayoutView();

        image = DatabaseHandler.getInstance(PhotoEditorHandler.this).getImageAt(IDAlbum,IDImage);

        setContentView(R.layout.editimage_layout);
        mPhotoEditorView = (PhotoEditorView) findViewById(R.id.photoEditorView);

        Glide.with(this).load(image.getUrlHinh())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(mPhotoEditorView.getSource());

        photoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();

        photoEditor.setOnPhotoEditorListener(new OnPhotoEditorListener() {
            @Override
            public void onEditTextChangeListener(final View rootView, String text, int colorCode) {

                textFragment = AddTextFragment.newInstance(text,colorCode);
                addFragment(textFragment);

                textFragment.setOnTextEditorListener(new AddTextFragment.TextEditor() {

                    @Override
                    public void onDone(String inputText, int colorCode) {
                        photoEditor.editText(rootView, inputText, colorCode);
                    }
                });



                //photoEditor.editText(rootView, inputText.text, inputText.color);
            }

            @Override
            public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

            }

            @Override
            public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

            }

            @Override
            public void onStartViewChangeListener(ViewType viewType) {

            }

            @Override
            public void onStopViewChangeListener(ViewType viewType) {

            }
        });

        brushInfo = new BrushInfo();

        closeFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft = getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.FragmentHolder));
                ft.commit();
                view.setVisibility(View.INVISIBLE);
                editBar.setVisibility(View.VISIBLE);
                navigateBar.setVisibility(View.VISIBLE);
                fragmentWindow.setVisibility(View.INVISIBLE);
            }
        });


        addEmojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> emoji = PhotoEditor.getEmojis(PhotoEditorHandler.this);
                emojiFragment = AddEmojFragment.newInstance(emoji);
                addFragment(emojiFragment);
            }
        });

        addBrushButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                ft = getFragmentManager().beginTransaction();
                brushFragment = BrushFragment.newInstance(brushInfo.size, brushInfo.opacity, brushInfo.color);

                photoEditor.setBrushSize(brushInfo.size);
                photoEditor.setOpacity(brushInfo.opacity);
                photoEditor.setBrushColor(brushInfo.color);
                addFragment(brushFragment);
                //photoEditor.setBrushDrawingMode(true);

            }
        });

        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textFragment = AddTextFragment.newInstance("",R.color.black);
                addFragment(textFragment);

                textFragment.setOnTextEditorListener(new AddTextFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        photoEditor.addText(inputText,colorCode);
                    }
                });




                //photoEditor.addText(inputText.text, inputText.color);
            }
        });

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoEditor.undo();
            }
        });

        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoEditor.redo();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                checkWritePermission();

                photoEditor.saveAsFile(image.getUrlHinh(), new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        Toast.makeText(context, "Image Saved Successfully", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(context, "Failed to save Image", Toast.LENGTH_LONG).show();
                    }
                });
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

        if (sender == "TextFragment")
        {
            //do nothing
        }

    }

    private void addFragment(Fragment fragment)
    {

        if (fragment != brushFragment)
        {
            photoEditor.setBrushDrawingMode(false);
        }
        ft = getFragmentManager().beginTransaction();

        if (fragment != textFragment)
        {
            ft.replace(R.id.FragmentHolder, fragment);
            ft.commit();
            closeFragmentButton.setVisibility(View.VISIBLE);
            editBar.setVisibility(View.INVISIBLE);
            navigateBar.setVisibility(View.INVISIBLE);
            fragmentWindow.setVisibility(View.VISIBLE);
        }
        else
        {
            textFragment.show(ft,"single");
        }



    }

    void checkWritePermission()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_PERMISSION);
        }
    }


    void findLayoutView()
    {
        closeFragmentButton = findViewById(R.id.CloseFragmentButton);
        editBar = findViewById(R.id.editBar);
        navigateBar = findViewById(R.id.navigateBar);
        fragmentWindow = findViewById(R.id.FragmentWindow);
        undoButton = findViewById(R.id.UndoButton);
        redoButton = findViewById(R.id.RedoButton);
        saveButton = findViewById(R.id.buttonSave);

        addEmojiButton = findViewById(R.id.addEmojiButton);
        addBrushButton = findViewById(R.id.addBrushButton);
        addTextButton = findViewById(R.id.addTextButton);
    }

}
