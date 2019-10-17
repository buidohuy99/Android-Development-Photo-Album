package com.example.gallery;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    TextView text;
    GridView gridView;
    Button button;
    Uri imageUri;
    ArrayList<Uri> listUri = new ArrayList<Uri>();
    ArrayList<Bitmap> list = new ArrayList<Bitmap>();
    ImageAdapter adapter = new ImageAdapter(this, list);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridview);
        button = (Button) findViewById(R.id.add);
        text = (TextView)findViewById(R.id.nameAlbum) ;
        gridView.setAdapter(adapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE);

        } else {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(gallery, PICK_IMAGE);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    listUri.add(uri);
                    list.add(getResizedBitmap(ChangeUriToBitmap(uri)));
                }
                adapter.notifyDataSetChanged();

            }
        }
    }

    public Bitmap ChangeUriToBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            //Change the path, from Uri to Bitmap
            InputStream inputStream = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(inputStream);

           } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    //Resize image
    static public Bitmap getResizedBitmap(Bitmap image) {
        int mSize=200;
        float  scale = (float) mSize / image.getWidth();
        int newSize = Math.round(image.getHeight() * scale);

        return Bitmap.createScaledBitmap(image, mSize, newSize, true);
    }
    // Hàm gọi xử lý cắt ảnh

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
