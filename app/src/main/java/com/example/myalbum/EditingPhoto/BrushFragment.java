package com.example.myalbum.EditingPhoto;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.divyanshu.colorseekbar.ColorSeekBar;
import com.example.myalbum.R;

import java.util.ArrayList;

public class BrushFragment extends Fragment implements FragmentCallbacks {
    Context context = null;
    PhotoEditorHandler main;
    SeekBar brushSizeSeekBar;
    SeekBar opacitySeekBar;
    ColorSeekBar colorSeekBar;
    //SeekBar colorSeekBar;
    public static BrushFragment newInstance(int BrushSize, int Opacity, int color)
    {
        BrushFragment fragment = new BrushFragment();
        Bundle args = new Bundle();
        args.putInt("BrushSize",BrushSize);
        args.putInt("Opacity", Opacity);
        args.putInt("Color", R.color.black);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        /*LinearLayout emojiLayout = (LinearLayout) inflater.inflate(R.layout.edit_emoji_fragment_layout, null);
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
        });*/

        ConstraintLayout paintLayout = (ConstraintLayout) inflater.inflate(R.layout.edit_brush_fragment_layout, null);

        final Bundle bundle = getArguments();

        brushSizeSeekBar = paintLayout.findViewById(R.id.sbSize);
        brushSizeSeekBar.setMax(100);
        brushSizeSeekBar.setMin(5);
        brushSizeSeekBar.setProgress(bundle.getInt("BrushSize"));

        opacitySeekBar = paintLayout.findViewById(R.id.sbOpacity);
        opacitySeekBar.setMax(100);
        opacitySeekBar.setMin(5);
        opacitySeekBar.setProgress(bundle.getInt("Opacity"));

        colorSeekBar = paintLayout.findViewById(R.id.sbColor);


//        LinearGradient test = new LinearGradient(0.f, 0.f, 300.f, 0.0f,
//                new int[] { 0xFF000000, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF,
//                        0xFFFF0000, 0xFFFF00FF, 0xFFFFFF00, 0xFFFFFFFF},
//                null, TileMode.CLAMP);
//        ShapeDrawable shape = new ShapeDrawable(new RectShape());
//        shape.getPaint().setShader(test);
//
//        colorSeekBar = paintLayout.findViewById(R.id.sbColor);
//        colorSeekBar.setProgressDrawable( (Drawable)shape );
//        colorSeekBar.setProgress(bundle.getInt("Color"));

        brushSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Bundle bundle = packBundle(i, opacitySeekBar.getProgress(), colorSeekBar.getColor());
                main.onMsgFromFragToMain("BrushFragment", bundle);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        opacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 1;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Bundle bundle = packBundle(brushSizeSeekBar.getProgress(),i, colorSeekBar.getColor());
                main.onMsgFromFragToMain("BrushFragment", bundle);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        colorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                Bundle bundlle = packBundle(seekBar.getProgress(),opacitySeekBar.getProgress(), R.color.black);
//                main.onMsgFromFragToMain("BrushFragment", bundle);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

// do this for each row (ViewHolder-Pattern could be used for better performance!)

        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {
                Bundle bundle = packBundle(brushSizeSeekBar.getProgress(),opacitySeekBar.getProgress(), i);
                main.onMsgFromFragToMain("BrushFragment", bundle);
            }
        });
        return paintLayout;


    }

    @Override
    public void onMsgFromMainToFragment(Bundle bundle) {

    }

    private Bundle packBundle(int size, int opacity, int color)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("BrushSize", size);
        bundle.putInt("Opacity", opacity);
        bundle.putInt("Color",color);
        return bundle;
    }
}
