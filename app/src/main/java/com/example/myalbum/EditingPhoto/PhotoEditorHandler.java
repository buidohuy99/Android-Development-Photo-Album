package com.example.myalbum.EditingPhoto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;

import android.app.FragmentTransaction;
import android.widget.LinearLayout;


import androidx.constraintlayout.widget.ConstraintLayout;

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

class InputText
{
    String text="hello there";
    int color = R.color.black;
}

public class PhotoEditorHandler extends Activity implements MainCallbacks{


    PhotoEditorView mPhotoEditorView;
    PhotoEditor photoEditor;
    ImageButton closeFragmentButton;

    HorizontalScrollView editBar;
    ConstraintLayout navigateBar;
    ConstraintLayout fragmentWindow;

    ImageButton addEmojiButton;
    ImageButton addTextButton;
    ImageButton addBrushButton;

    BrushInfo brushInfo;
    InputText inputText;

    FragmentTransaction ft;
    Fragment currentFragment;

    AddEmojFragment emojiFragment;
    BrushFragment brushFragment;
    AddTextFragment textFragment;

    boolean isEditingText = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editimage_layout);
        mPhotoEditorView = (PhotoEditorView) findViewById(R.id.photoEditorView);
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
        closeFragmentButton = findViewById(R.id.CloseFragmentButton);
        editBar = findViewById(R.id.editBar);
        navigateBar = findViewById(R.id.navigateBar);
        fragmentWindow = findViewById(R.id.FragmentWindow);

        addEmojiButton = findViewById(R.id.addEmojiButton);
        addBrushButton = findViewById(R.id.addBrushButton);
        addTextButton = findViewById(R.id.addTextButton);

        brushInfo = new BrushInfo();
        inputText = new InputText();

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
            inputText.text = bundle.getString("inputText");
            inputText.color = bundle.getInt("Color");
            isEditingText = false;
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


}
