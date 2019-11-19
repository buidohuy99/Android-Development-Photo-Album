package com.example.myalbum.AlbumsActivity;


import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
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
    private static final int MOVE_IMAGE = 90;
    public static final String BACK_ALBUM ="BackAlbum";
    public static final String ALBUM_TO ="Album";
    public static final String[] filePathColumn = { MediaStore.Images.Media.DATA };


    //List các đối tượng
    GridView gridView;
    Button button;
    ProgressBar progressBar ;

    //Biến
    List<Image> list;
    ImageAdapter adapter;
    String nameAlbum;
    List<Uri> uriList = new ArrayList<Uri>();

    int IDAlbum;
    int IDAlbumtoMove;

    public void init() {
        gridView = findViewById(R.id.gridview);
        button = findViewById(R.id.add);
        progressBar = (ProgressBar) findViewById(R.id.myBarCir);
        progressBar.setVisibility(View.INVISIBLE);

    }

    public void getData() {
        //Get data from HomeActivity
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getBundleExtra(ALBUM_TO);

        if (myBundle != null)
        {
            IDAlbum = myBundle.getInt("IDAlbum");
            nameAlbum = myBundle.getString("nameAlbum") + String.valueOf(IDAlbum);
            list = DatabaseHandler.getInstance(AlbumActivity.this).getAllImageOfAlbum(IDAlbum);
        }

        myBundle = callingIntent.getBundleExtra(BACK_ALBUM);

        if(myBundle!= null)
        {
            IDAlbum = myBundle.getInt("IDAlbum");
            nameAlbum = DatabaseHandler.getInstance(AlbumActivity.this).getAlbum(IDAlbum).getAlbumName();
            list = DatabaseHandler.getInstance(AlbumActivity.this).getAllImageOfAlbum(IDAlbum);
        }

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

        //Sét sự kiện click ảnh
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent newActivity = new Intent(AlbumActivity.this, ViewImageActivity.class);

                Bundle myData = new Bundle();
                myData.putInt("IDAlbum", IDAlbum);
                myData.putInt("IDImage", list.get(i).getPos());

                newActivity.putExtras(myData);
                startActivity(newActivity);
            }
        });

        //Sét sự kiện click ảnh lâu
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog DeleteDialog = new AlertDialog.Builder(AlbumActivity.this)
                        .setTitle("Bạn muốn xóa ảnh này?\n")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Image deleted", Toast.LENGTH_LONG).show();

                                removeImage( position);

                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), list.get(position).getUrlHinh(), Toast.LENGTH_LONG).show();

                                dialog.dismiss();
                            }
                        })
//                        .setNegativeButton("Move", new DialogInterface.OnClickListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                IDAlbumtoMove = position;
//                                Intent newActivity = new Intent(AlbumActivity.this, MoveCopyImageActivity.class);
//
//                                Bundle myData = new Bundle();
//
//                                newActivity.putExtras(myData);
//                                startActivityForResult(newActivity, MOVE_IMAGE);
//                            }
//                        })
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
        new DeleteImageTask().execute(position);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

    }
    protected void onResume()
    {
        super.onResume();
        List<Image> tempListImage =  DatabaseHandler.getInstance(AlbumActivity.this).getAllImageOfAlbum(IDAlbum);
        if(list.size() != tempListImage.size())
        {
            list.clear();
            for(int i = 0; i< tempListImage.size(); i++)
                list.add(tempListImage.get(i));
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "ON RESUME", Toast.LENGTH_LONG).show();

//        adapter.notifyDataSetChanged();
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
                uriList.clear();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    uriList.add(uri);
                }

                new UploadImage().execute(uriList);
            }
            else
            {
                Uri imageUri = data.getData();

                Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Image newImage =new Image(picturePath, IDAlbum, list.size());
                list.add(newImage);
                DatabaseHandler.getInstance(AlbumActivity.this).addImage(newImage.getUrlHinh(),newImage.getPos(),newImage.getIdAlbum());
            }
        }
//        else
//        {
//            if(resultCode == RESULT_OK && requestCode == MOVE_IMAGE) {
//                Bundle myBundle = data.getBundleExtra("Result");
//                int idalbum = myBundle.getInt("newIDAlbum");
//                int idimage = IDAlbumtoMove;
//
//                if (idalbum != IDAlbum)
//                {
//                    list.remove(idimage);
//                    new MoveImage().execute(idalbum, idimage);
//                }
//
//            }
//        }

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


    private class DeleteImageTask extends AsyncTask<Integer, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(AlbumActivity.this);

        String waitMsg = "Wait\nProcess is being done... ";
        protected void onPreExecute()
        {
            this.dialog.setMessage(waitMsg);
            this.dialog.setCancelable(false); //outside touch doesn't dismiss you
            this.dialog.show();
        }


        @Override
        protected Void doInBackground(Integer... integers) {
            DatabaseHandler.getInstance(AlbumActivity.this).deleteImage(IDAlbum, integers[0]);

            for(int i=integers[0]; i<list.size(); i++)
            {
                DatabaseHandler.getInstance(AlbumActivity.this).updateIDImage(list.get(i),i);
                list.get(i).setPos(i);
            }

            return null;
        }
        protected void onPostExecute(final Void unused) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

        }

    }

//    private class MoveImage extends AsyncTask<Integer, Void, Void> {
//        private final ProgressDialog dialog = new ProgressDialog(AlbumActivity.this);
//
//        String waitMsg = "Wait\nProcess is being done... ";
//        protected void onPreExecute()
//        {
//            this.dialog.setMessage(waitMsg);
//            this.dialog.setCancelable(false); //outside touch doesn't dismiss you
//            this.dialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Integer... integers) {
//            Integer numberOfImages = DatabaseHandler.getInstance(AlbumActivity.this).getNumberOfImages(integers[0]);
//
//            Image image = DatabaseHandler.getInstance(AlbumActivity.this).getImageAt(IDAlbum,integers[1]);
//            DatabaseHandler.getInstance(AlbumActivity.this).deleteImage(IDAlbum, integers[1]);
//
//            for(int i=integers[1]; i<list.size(); i++)
//            {
//                DatabaseHandler.getInstance(AlbumActivity.this).updateIDImage(list.get(i),i);
//                list.get(i).setPos(i);
//            }
//
//            DatabaseHandler.getInstance(AlbumActivity.this).addImage(image.getUrlHinh(),numberOfImages,integers[0]);
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... value) {
//            adapter.notifyDataSetChanged();
//        }
//        protected void onPostExecute(final Void unused) {
//            if (this.dialog.isShowing()) {
//                this.dialog.dismiss();
//            }
//
//        }
//    }

    private class UploadImage extends AsyncTask<List<Uri>,Void,Void>{
        private final ProgressDialog dialog = new ProgressDialog(AlbumActivity.this);
        String waitMsg = "Wait\nLoading image is being done... ";
        protected void onPreExecute() {
            this.dialog.setMessage(waitMsg);
            this.dialog.setCancelable(false); //outside touch doesn't dismiss you
            this.dialog.show();
        }
        @Override
        protected Void doInBackground(List<Uri>... lists) {
            for(int i= 0 ; i<lists[0].size(); i++)
            {
                Cursor cursor = getContentResolver().query(uriList.get(i), filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Image newImage =new Image(picturePath, IDAlbum, list.size());
                list.add(newImage);
                DatabaseHandler.getInstance(AlbumActivity.this).addImage(newImage.getUrlHinh(),newImage.getPos(),newImage.getIdAlbum());
                publishProgress();

            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... value) {
            adapter.notifyDataSetChanged();
            gridView.smoothScrollToPosition(list.size()-1);
        }
        // can use UI thread here
        protected void onPostExecute(final Void unused) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }


    }
}

