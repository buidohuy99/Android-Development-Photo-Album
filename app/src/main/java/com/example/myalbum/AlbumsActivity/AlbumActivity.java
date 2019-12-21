package com.example.myalbum.AlbumsActivity;


import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.EditingPhoto.PhotoEditorHandler;
import com.example.myalbum.R;
import com.example.myalbum.XemAnh.ViewImageActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlbumActivity extends Activity {

    private static final int PICK_IMAGE = 100;
    private static final int MOVE_IMAGE = 90;
    public static final String BACK_ALBUM ="BackAlbum";
    public static final String ALBUM_TO ="Album";
    public static final String[] filePathColumn = { MediaStore.Images.Media.DATA };
    private static final String IMAGE_DIRECTORY_NAME = "VLEMONN";
    static final int CAPTURE_IMAGE_REQUEST = 1;


    File photoFile = null;
    Uri photoURI;


    String mCurrentPhotoPath;


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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PICK_IMAGE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(gallery, PICK_IMAGE);
                } else {
                    //do nothing
                }
                return;
            }

            case CAPTURE_IMAGE_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                }
            }
        }

    }


    public void init() {
        gridView = findViewById(R.id.gridview);
        button = findViewById(R.id.add);
        progressBar = (ProgressBar) findViewById(R.id.myBarCir);
        progressBar.setVisibility(View.INVISIBLE);

        if(IDAlbum == - 1)
            button.setVisibility(View.GONE);
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
        if(IDAlbum != -1)
        {
            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog DeleteDialog = new AlertDialog.Builder(AlbumActivity.this)
                            .setTitle("Bạn muốn xóa ảnh này?\n")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    Toast.makeText(getApplicationContext(), "Image deleted", Toast.LENGTH_LONG).show();
                                    Image image = list.get(position);
                                    Toast.makeText(getApplicationContext(), String.valueOf(IDAlbum), Toast.LENGTH_LONG).show();

                                    new MoveImage().execute(-1, position);
                                    list.remove(position);
                                    adapter.notifyDataSetChanged();

                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            })
                            .create();
                    DeleteDialog.show();
                    return true;

                }
            });
        }
        else
        {
            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog DeleteDialog = new AlertDialog.Builder(AlbumActivity.this)
                            .setTitle("Thao tác bạn muốn\n")
                            .setPositiveButton("Restore", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Image image =  DatabaseHandler.getInstance(AlbumActivity.this).getImageAt(IDAlbum, position);
                                    int oldIDAlbum = image.getOldIDAlbum();

                                    Toast.makeText(getApplicationContext(), String.valueOf(oldIDAlbum) , Toast.LENGTH_LONG).show();

                                    new MoveImage().execute(oldIDAlbum, position);
                                    list.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    removeImage(position);
                                }
                            })

                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            })
                            .create();
                    DeleteDialog.show();
                    return true;

                }
            });
        }



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
                                captureImage();
                            }
                        })
                        .create();
                AddImageDialog.show();
            }
        });
    }
    private void captureImage()
    {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        else
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                try {

                    photoFile = createImageFile();
                    Log.i("Mayank",photoFile.getAbsolutePath());
                    galleryAddPic();


                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(this,"com.example.myalbum.provider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                } catch (Exception ex) {
                    // Error occurred while creating the File
                    displayMessage(getBaseContext(),ex.getMessage().toString());
                }


            }else
            {
                displayMessage(getBaseContext(),"Nullll");
            }
        }



    }
    private void displayMessage(Context context, String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(IDAlbum == -1)
            return  false;

        getMenuInflater().inflate(R.menu.menualbum,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.action_slideshow) {

            Intent intent = new Intent(this, SlideshowActivity.class);
            Bundle myData = new Bundle();
            myData.putInt("IDAlbum", IDAlbum);
            intent.putExtra("AlbumActivity", myData);
            startActivity(intent);
            return true;
        }

        return false;

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
        if(resultCode == RESULT_OK)
        {
            if(requestCode == PICK_IMAGE)
            {
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
            else
            {
                if(requestCode == CAPTURE_IMAGE_REQUEST)
                {
                    Image newImage =new Image(mCurrentPhotoPath, IDAlbum, list.size());
                    list.add(newImage);
                    DatabaseHandler.getInstance(AlbumActivity.this).addImage(newImage.getUrlHinh(),newImage.getPos(),newImage.getIdAlbum());
                    adapter.notifyDataSetChanged();
                }
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

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
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

    private class MoveImage extends AsyncTask<Integer, Void, Void> {
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
            Integer numberOfImages = DatabaseHandler.getInstance(AlbumActivity.this).getNumberOfImages(integers[0]);

            Image image = DatabaseHandler.getInstance(AlbumActivity.this).getImageAt(IDAlbum,integers[1]);
            DatabaseHandler.getInstance(AlbumActivity.this).deleteImage(IDAlbum, integers[1]);

            for(int i=integers[1]; i<list.size(); i++)
            {
                DatabaseHandler.getInstance(AlbumActivity.this).updateIDImage(list.get(i),i);
                list.get(i).setPos(i);
            }

            DatabaseHandler.getInstance(AlbumActivity.this).addImageWithOldIDAlbum(image.getUrlHinh(),numberOfImages,integers[0], IDAlbum);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... value) {
            adapter.notifyDataSetChanged();
        }
        protected void onPostExecute(final Void unused) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

        }
    }

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

