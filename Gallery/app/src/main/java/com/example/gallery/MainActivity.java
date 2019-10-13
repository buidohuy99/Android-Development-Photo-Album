package com.example.gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gallery.ImageAdapter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    GridView gridView;
    Button button;
    Uri imageUri;
    ArrayList<Bitmap> list = new ArrayList<Bitmap>();
    ImageAdapter adapter = new ImageAdapter(this,list);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridview);
        button = (Button)findViewById(R.id.add);
        gridView.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult( requestCode,  resultCode,  data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri=data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);

                bitmap = getResizedBitmap(bitmap);
                // for(int i=0; i<10;i++)
                list.add(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            gridView.setAdapter(adapter);
        }
    }
    static public Bitmap getResizedBitmap(Bitmap image) {
        int mSize=500;
        float  scale = (float) mSize / image.getWidth();
        int newSize = Math.round(image.getHeight() * scale);

        return Bitmap.createScaledBitmap(image, mSize, newSize, true);
    }
//    public String BitMapToString(Bitmap bitmap){
//        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
//        byte [] arr=baos.toByteArray();
//        String result=Base64.encodeToString(arr, Base64.DEFAULT);
//        return result;
//    }
//    public Bitmap StringToBitMap(String image){
//        try{
//            byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);
//            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            return bitmap;
//        }catch(Exception e){
//            e.getMessage();
//            return null;
//        }
//    }
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//
//        // Save UI state changes to the savedInstanceState.
//        // This bundle will be passed to onCreate if the process is
//        // killed and restarted.
////        ArrayList<String> listPictureString = new ArrayList<String>();
////        for(int i = 0; i < list.size(); i++)
////            listPictureString.add(BitMapToString(list.get(i)));
////        savedInstanceState.putStringArrayList("mylist", listPictureString);
//
//        // etc.
//
//        super.onSaveInstanceState(savedInstanceState);
//        ArrayList<String> listPictureString = new ArrayList<String>();
//        for(int i = 0; i < list.size(); i++)
//            listPictureString.add(BitMapToString(list.get(i)));
//        savedInstanceState.putStringArrayList("mylist", listPictureString);
//
//    }
////
////onRestoreInstanceState
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//
//        super.onRestoreInstanceState(savedInstanceState);
//
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
//
//
//        ArrayList<String> listPictureString = new ArrayList<String>();
//        listPictureString=savedInstanceState.getStringArrayList("mylist");
//        list.clear();
//        for(int i = 0; i < list.size(); i++)
//            list.add(StringToBitMap(listPictureString.get(i)));
//
//        gridView.setAdapter(new ImageAdapter(this,list));
//    }
}
