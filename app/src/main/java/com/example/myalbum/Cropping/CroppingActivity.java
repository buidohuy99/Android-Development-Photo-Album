package com.example.myalbum.Cropping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropping);


        Intent myCallerIntent = getIntent();
        Bundle myBundle = myCallerIntent.getExtras();
        IDAlbum = myBundle.getInt("IDAlbum");
        IDImage = myBundle.getInt("IDImage");
        context = this;

        image = DatabaseHandler.getInstance(CroppingActivity.this).getImageAt(IDAlbum,IDImage);

        mCropView = (CropImageView) findViewById(R.id.cropImageView);
        File file = new File(image.getUrlHinh());
        imageUri = Uri.fromFile(file);

        mCropView.startLoad(

                imageUri,

                new LoadCallback() {
                    @Override
                    public void onError(Throwable e) { }

                    @Override
                    public void onSuccess() {}
                });

        mCropView.setCropMode(CropImageView.CropMode.FREE);



    }
}
