package com.example.myalbum.XemAnh;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Album;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static java.lang.Integer.parseInt;


public class DetailPicture extends Activity {

    private int IDAlbum;
    private int IDImage;
    private TextView name;
    private TextView size;
    private TextView demension;
    private TextView time;
    private TextView path;
    private String detailName;
    private long detailSize;
    private int detailWidthDemension;
    private int detailHeightDemension;
    private String detailTime;
    private String detailLocation;

    public void getData()
    {
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        IDAlbum = myBundle.getInt("IDAlbum");
        IDImage = myBundle.getInt("IDImage");
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return  true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_images_layout);

        name  =(TextView)findViewById(R.id.nameImage);
        size = (TextView) findViewById(R.id.sizeImage);
        demension = (TextView) findViewById(R.id.demensionImage);
        time = (TextView) findViewById(R.id.timeImage);
        path =(TextView) findViewById(R.id.locationImage);


        getData();
        Image image = DatabaseHandler.getInstance(this).getImageAt(IDAlbum,IDImage);


        getImageDetail(image);

        name.setText("Tên: " + detailName);
        size.setText("Dung lượng: " + detailSize+"MB");
        demension.setText("Kích thước: "+ detailWidthDemension +"x" +detailHeightDemension);
        time.setText("Thời gian: "+ detailTime);
        path.setText("Đường dãn: "+ detailLocation);
    }

    private void getImageDetail(Image img) {
        String url = img.getUrlHinh();
        Uri uriImage = Uri.parse(img.getUrlHinh());
        if(url!=null) {



            try {
                ExifInterface exifInterface = new ExifInterface(uriImage.getPath());
                detailWidthDemension = parseInt (exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
                detailHeightDemension = parseInt (exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));
                detailTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File file = new File(uriImage.getPath());
        detailSize = (file.length()/ 1024)/1024;

        detailName = url.substring(url.lastIndexOf("/")+1);
        detailLocation = url;
    }
}
