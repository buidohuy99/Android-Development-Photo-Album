package com.example.myalbum.Cropping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.R;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.LoadCallback;

import java.io.File;

public class CroppingActivity extends Activity {

    Context context;
    int IDAlbum;
    int IDImage;
    Image image;
    Uri imageUri;
    CropImageView mCropView;

    TextView option_free;
    TextView option_3_4;
    TextView option_16_9;
    TextView option_square;
    TextView option_circular;
    TextView option_fit;


    void findLayoutView()
    {
        mCropView = (CropImageView) findViewById(R.id.cropImageView);
        option_free = (TextView) findViewById(R.id.option_free);
        option_3_4 = (TextView) findViewById(R.id.option_3_4);
        option_16_9 = (TextView) findViewById(R.id.option_16_9);
        option_square = (TextView) findViewById(R.id.option_square);
        option_circular = (TextView) findViewById(R.id.option_circular);
        option_fit = (TextView) findViewById(R.id.option_fit);


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropping);

        //get bundle sent from hom_activity
        Intent myCallerIntent = getIntent();
        Bundle myBundle = myCallerIntent.getExtras();
        IDAlbum = myBundle.getInt("IDAlbum");
        IDImage = myBundle.getInt("IDImage");
        context = this;

        //GET SELECTED IMAGE
        image = DatabaseHandler.getInstance(CroppingActivity.this).getImageAt(IDAlbum,IDImage);

        //MAKE URI
        File file = new File(image.getUrlHinh());
        imageUri = Uri.fromFile(file);

        findLayoutView();

        mCropView.startLoad(

                imageUri,

                new LoadCallback() {
                    @Override
                    public void onError(Throwable e) { }

                    @Override
                    public void onSuccess() {}
                });

        //SET LISTENER
        option_free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCropMode(view);
            }
        });

        LinearLayout layout = findViewById(R.id.cropMode);
        for (int i = 0; i < layout.getChildCount();i++)
        {
            View option = layout.getChildAt(i);
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCropMode(view);
                }
            });
        }
    }

    void setCropMode(View view) {

        switch (view.getId()) {
            case R.id.option_free: {
                mCropView.setCropMode(CropImageView.CropMode.FREE);
                break;
            }

            case R.id.option_3_4: {
                mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                break;
            }

            case R.id.option_16_9: {
                mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                break;
            }

            case R.id.option_square: {
                mCropView.setCropMode(CropImageView.CropMode.SQUARE);
                break;
            }

            case R.id.option_circular: {
                mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
                break;
            }

            case R.id.option_fit: {
                mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
                break;
            }
        }
    }
}
