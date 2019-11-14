package com.example.myalbum.AlbumsActivity;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.R;
import com.example.myalbum.XemAnh.ViewImageActivity;

import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends Activity {

    private static final int PICK_IMAGE = 100;

    //List các đối tượng
    TextView text;
    GridView gridView;
    Button button;

    //Biến
    List<Image> list;
    List<Image> listTemp = new ArrayList<Image>();
    ImageAdapter adapter;
    String nameAlbum;

    int IDAlbum;

    public void init() {
        gridView = findViewById(R.id.gridview);
        button = findViewById(R.id.add);

    }

    public void getData() {
        //Get data from HomeActivity
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        IDAlbum = myBundle.getInt("IDAlbum");
        nameAlbum = myBundle.getString("nameAlbum") + String.valueOf(IDAlbum);
        list = DatabaseHandler.getInstance(AlbumActivity.this).getAllImageOfAlbum(IDAlbum);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagesofalbum);

        //Lay du lieu
        getData();

        //Lien Ket
        init();

        //Cai đặt các đối tượng
        this.setTitle(nameAlbum);

        adapter = new ImageAdapter(this, list);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), String.valueOf(list.get(i).getPos()) + "+ " +String.valueOf(list.get(i).getIdAlbum()), Toast.LENGTH_LONG).show();
                Intent newActivity = new Intent(AlbumActivity.this, ViewImageActivity.class);

                Bundle myData = new Bundle();
                myData.putInt("IDAlbum", IDAlbum);
                myData.putInt("IDImage", list.get(i).getPos());

                newActivity.putExtras(myData);
                startActivity(newActivity);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog DeleteDialog = new AlertDialog.Builder(AlbumActivity.this)
                        .setTitle("Bạn muốn xóa ảnh này?\n")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_LONG).show();

                                removeImage( position);

                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Move", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent newActivity = new Intent(AlbumActivity.this, MoveCopyImageActivity.class);

                                Bundle myData = new Bundle();
                                myData.putInt("Type", 1);
//                                myData.putInt("IDAlbum", list.get(position).getIdAlbum());
//                                myData.putInt("IDImage", list.get(position).getPos());
//                                myData.putParcelableArrayList("Mylist", (ArrayList<? extends Parcelable>) list);
                                newActivity.putExtras(myData);
                                startActivity(newActivity);
                            }
                        })
                        .create();
                DeleteDialog.show();
                return true;

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog AddImageDialog = new AlertDialog.Builder(AlbumActivity.this)
                        .setTitle("Chọn chức năng\n")
                        .setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                openGallery();
                            }
                        })
                        .setNeutralButton("Chụp ảnh", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create();
                AddImageDialog.show();
            }
        });
    }

    private void removeImage( int position) {
        list.remove(position);
        adapter.notifyDataSetChanged();

        MyThread t2 = new MyThread(position);
        t2.start();

    }

    @Override
    protected void onPause()
    {
        super.onPause();

    }
    protected void onResume()
    {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
    @Override
    protected void onStop()
    {
        super.onStop();

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

                listTemp.clear();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    Image newImage =new Image(uri.toString(), IDAlbum, list.size());
                    list.add(newImage);
                    adapter.notifyDataSetChanged();
                    listTemp.add(newImage);

                }
                MyThreadAddImage t2 = new MyThreadAddImage();
                t2.start();

            }
            else
            {
                Uri imageUri = data.getData();
                Image newImage =new Image(imageUri.toString(), IDAlbum, list.size());
                list.add(newImage);
                DatabaseHandler.getInstance(AlbumActivity.this).addImage(imageUri.toString(),newImage.getPos(),newImage.getIdAlbum());
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getParentActivityIntent() == null) {
                    Log.i("SearchActivity",

                            "Fix Manifest to indicate the parentActivityName");

                    onBackPressed();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class MyThread extends Thread{
        private int position;
        public MyThread(int position) {
            this.position = position;
        }

        @Override
        public void run() {
            super.run();
            DatabaseHandler.getInstance(AlbumActivity.this).deleteImage(IDAlbum, position);
            for(int i=position; i<list.size(); i++)
            {
                DatabaseHandler.getInstance(AlbumActivity.this).updateImage(list.get(i),i);
                list.get(i).setPos(i);
            }
        }//run
    }//MyThread

    public class MyThreadAddImage extends Thread{
        @Override
        public void run() {
            super.run();
            for(int i=0; i<listTemp.size(); i++)
            {
                DatabaseHandler.getInstance(AlbumActivity.this).addImage(listTemp.get(i).getUrlHinh(),listTemp.get(i).getPos(),listTemp.get(i).getIdAlbum());

            }
        }//run
    }//MyThread
}

