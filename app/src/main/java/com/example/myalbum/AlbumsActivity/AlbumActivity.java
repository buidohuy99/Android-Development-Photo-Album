package com.example.myalbum.AlbumsActivity;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends Activity {

    private static final int PICK_IMAGE = 100;
    TextView text;
    GridView gridView;
    Button button;

    ArrayList<Uri> listUri = new ArrayList<Uri>();
    ArrayList<Bitmap> list = new ArrayList<Bitmap>();
    ImageAdapter adapter = new ImageAdapter(this, list);
    Handler myHandler = new Handler();
    ProgressBar progressBar ;
    int shouldRun;

    int n;
    int IDAlbum;

    List<byte[]> imageByte = new ArrayList<byte[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagesofalbum);

        //Get data from HomeActivity
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        text = findViewById(R.id.nameAlbum) ;
        IDAlbum= myBundle.getInt("IDAlbum");
        text.setText(myBundle.getString("nameAlbum") + String.valueOf(IDAlbum));


        imageByte= DatabaseHandler.getInstance(AlbumActivity.this).getAllImageOfAlbum(IDAlbum);

        for(int i = 0; i< imageByte.size();i++)
            list.add(BitmapFactory.decodeByteArray(imageByte.get(i), 0, imageByte.get(i).length));

        adapter.notifyDataSetChanged();

        gridView = findViewById(R.id.gridview);
        button =  findViewById(R.id.add);
        progressBar =  findViewById(R.id.myBarCir);
        progressBar.setVisibility(View.INVISIBLE);
        gridView.setAdapter(adapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                n=0;
                openGallery();
            }
        });
    }

    //Mở thư viện ảnh
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
                listUri.clear();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    listUri.add(uri);

                }
                shouldRun = 1;
                button.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                Thread myBackgroundThread = new Thread( backgroundTask, "backAlias1" );
                myBackgroundThread.start();

            }
        }

    }
    private Runnable foregroundRunnable = new Runnable() {
        @Override
        public void run() {
            try {

                if(n == (listUri.size() -1) || listUri.size() ==1) {
                    button.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    adapter.notifyDataSetChanged();
                }
                adapter.notifyDataSetChanged();

            } catch (Exception e) {
                Log.e("<<foregroundTask>>", e.getMessage());
            }
        }
    }; // foregroundTask
    private Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {

            for ( n = 0; n < listUri.size(); n++) {

                Bitmap temp =ChangeUriToBitmap(listUri.get(n));


                DatabaseHandler.getInstance(AlbumActivity.this).addImage(bitmapToByte(temp),IDAlbum);
                list.add(getResizedBitmap(temp));
                myHandler.post(foregroundRunnable);
            }
        }// run
    };// backgroundTask


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

    public static byte[] bitmapToByte(Bitmap image) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

}

