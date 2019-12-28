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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.load.engine.Resource;
import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.EditingPhoto.PhotoEditorHandler;
import com.example.myalbum.R;
import com.example.myalbum.XemAnh.ViewImageActivity;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlbumActivity extends Activity {

    private static final int PICK_IMAGE = 100;
    private static final int MOVE_IMAGE = 90;
    public static final String BACK_ALBUM = "BackAlbum";
    public static final String ALBUM_TO = "Album";
    public static final String[] filePathColumn = {MediaStore.Images.Media.DATA};
    private static final String IMAGE_DIRECTORY_NAME = "VLEMONN";
    static final int CAPTURE_IMAGE_REQUEST = 1;
    List<Integer> positionsList = new ArrayList<>();
    List<Integer> checkedSelection = new ArrayList<>();
    boolean deleting;
    ActionMode modeGrid;
    File photoFile = null;
    Uri photoURI;


    String mCurrentPhotoPath;


    //List các đối tượng
    GridView gridView;
    Button button;
    ProgressBar progressBar;

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

        if (IDAlbum == -1)
            button.setVisibility(View.GONE);

        for (int i = 0; i < list.size(); i++)
            checkedSelection.add(-1);
        deleting = false;
    }

    public void getData() {
        //Get data from HomeActivity
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getBundleExtra(ALBUM_TO);

        if (myBundle != null) {
            IDAlbum = myBundle.getInt("IDAlbum");
            nameAlbum = myBundle.getString("nameAlbum");
            list = DatabaseHandler.getInstance(AlbumActivity.this).getAllImageOfAlbum(IDAlbum);
        }

        myBundle = callingIntent.getBundleExtra(BACK_ALBUM);

        if (myBundle != null) {
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

        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new MultiChoiceModeListener());

        if(IDAlbum != -1)
        {

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
        }
        else
        {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final int index = i;
                    AlertDialog DeleteDialog = new AlertDialog.Builder(AlbumActivity.this)
                            .setTitle("Do you want restore this image?\n")
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    Image image = DatabaseHandler.getInstance(AlbumActivity.this).getImageAt(IDAlbum, index);
//                                    new Restore().execute(index, image.getOldIDAlbum());
//                                    Toast.makeText(AlbumActivity.this, String.valueOf(image.getPos())+ "+" + String.valueOf(image.getOldIDAlbum()), Toast.LENGTH_LONG).show();

                                }
                            })
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Image image = DatabaseHandler.getInstance(AlbumActivity.this).getImageAt(IDAlbum, index);
                                    new Restore().execute(index, image.getOldIDAlbum());
//                                    Toast.makeText(AlbumActivity.this, String.valueOf(image.getPos()), Toast.LENGTH_LONG).show();
                                }
                            })
                            .create();
                    DeleteDialog.show();

                }


            });
        }



//        //Sét sự kiện click ảnh lâu
//        if (IDAlbum != -1) {
//            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
////                    AlertDialog DeleteDialog = new AlertDialog.Builder(AlbumActivity.this)
////                            .setTitle("Bạn muốn xóa ảnh này?\n")
////                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface dialog, int which) {
//////                                    Toast.makeText(getApplicationContext(), "Image deleted", Toast.LENGTH_LONG).show();
////                                    Image image = list.get(position);
////                                    Toast.makeText(getApplicationContext(), String.valueOf(IDAlbum), Toast.LENGTH_LONG).show();
////
////                                    new MoveImage().execute(-1, position);
////                                    list.remove(position);
////                                    adapter.notifyDataSetChanged();
////
////                                }
////                            })
////                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface dialog, int which) {
////
////                                    dialog.dismiss();
////                                }
////                            })
////                            .create();
////                    DeleteDialog.show();
////                    return true;
//                    gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
//                    gridView.setMultiChoiceModeListener(new MultiChoiceModeListener());
//                    return true;
//
//                }
//            });
//        } else {
//            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                    AlertDialog DeleteDialog = new AlertDialog.Builder(AlbumActivity.this)
//                            .setTitle("Thao tác bạn muốn\n")
//                            .setPositiveButton("Restore", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    Image image = DatabaseHandler.getInstance(AlbumActivity.this).getImageAt(IDAlbum, position);
//                                    int oldIDAlbum = image.getOldIDAlbum();
//
//                                    Toast.makeText(getApplicationContext(), String.valueOf(oldIDAlbum), Toast.LENGTH_LONG).show();
//
//                                    new MoveImage().execute(oldIDAlbum, position);
//                                    list.remove(position);
//                                    adapter.notifyDataSetChanged();
//                                }
//                            })
//                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    removeImage(position);
//                                }
//                            })
//
//                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    dialog.dismiss();
//                                }
//                            })
//                            .create();
//                    DeleteDialog.show();
//                    return true;
//
//                }
//            });
//        }


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

    private void captureImage() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                try {

                    photoFile = createImageFile();
                    Log.i("Mayank", photoFile.getAbsolutePath());
                    galleryAddPic();


                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(this, "com.example.myalbum.provider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                } catch (Exception ex) {
                    // Error occurred while creating the File
                    displayMessage(getBaseContext(), ex.getMessage().toString());
                }


            } else {
                displayMessage(getBaseContext(), "Null");
            }
        }


    }

    private void displayMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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
        if (IDAlbum == -1)
            return false;

        getMenuInflater().inflate(R.menu.menualbum, menu);
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

    private void removeImage(int position) {
        list.remove(position);
        adapter.notifyDataSetChanged();
        new DeleteImageTask().execute(position);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    protected void onResume() {
        super.onResume();
        positionsList.clear();
        List<Image> tempListImage =  DatabaseHandler.getInstance(AlbumActivity.this).getAllImageOfAlbum(IDAlbum);
        if(list.size() != tempListImage.size())
        {
            list.clear();
            for(int i = 0; i< tempListImage.size(); i++)
                list.add(tempListImage.get(i));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
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
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    uriList.clear();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        uriList.add(uri);
                    }

                    new UploadImage().execute(uriList);
                } else {
                    Uri imageUri = data.getData();

                    Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Image newImage = new Image(picturePath, IDAlbum, list.size());
                    list.add(newImage);
                    DatabaseHandler.getInstance(AlbumActivity.this).addImage(newImage.getUrlHinh(), newImage.getPos(), newImage.getIdAlbum());
                }
            } else {
                if (requestCode == CAPTURE_IMAGE_REQUEST) {
                    Image newImage = new Image(mCurrentPhotoPath, IDAlbum, list.size());
                    list.add(newImage);
                    DatabaseHandler.getInstance(AlbumActivity.this).addImage(newImage.getUrlHinh(), newImage.getPos(), newImage.getIdAlbum());
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

        protected void onPreExecute() {
            this.dialog.setMessage(waitMsg);
            this.dialog.setCancelable(false); //outside touch doesn't dismiss you
            this.dialog.show();
        }


        @Override
        protected Void doInBackground(Integer... integers) {
            DatabaseHandler.getInstance(AlbumActivity.this).deleteImage(IDAlbum, integers[0]);

            for (int i = integers[0]; i < list.size(); i++) {
                DatabaseHandler.getInstance(AlbumActivity.this).updateIDImage(list.get(i), i);
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


    private class Restore extends AsyncTask<Integer, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(AlbumActivity.this);

        String OK = "";
        String waitMsg = "Wait\nProcess is being done... ";

        protected void onPreExecute() {
            this.dialog.setMessage(waitMsg);
            this.dialog.setCancelable(false); //outside touch doesn't dismiss you
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            int sl = DatabaseHandler.getInstance(AlbumActivity.this).getNumberOfImages(integers[1]);

            Image image = list.get(integers[0]);
            list.remove(integers[0]);

            DatabaseHandler.getInstance(AlbumActivity.this).deleteImage(-1, integers[0]);

            List<Image> tempListImage =  DatabaseHandler.getInstance(AlbumActivity.this).getAllImageOfAlbum(IDAlbum);
            if(list.size() != tempListImage.size())
            {
                list.clear();
                for(int i = 0; i< tempListImage.size(); i++)
                    list.add(tempListImage.get(i));
            }

            for(int i = 0; i < list.size(); i++)
            {
                DatabaseHandler.getInstance(AlbumActivity.this).updateIDAlbumIDImage(list.get(i), IDAlbum, i);
                list.get(i).setPos(i);
            }

            DatabaseHandler.getInstance(AlbumActivity.this).addImage(image.getUrlHinh(), sl, integers[1]);



            publishProgress();

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... value) {
            adapter.notifyDataSetChanged();
            Toast.makeText(AlbumActivity.this, "Thành công", Toast.LENGTH_LONG).show();
        }

        protected void onPostExecute(final Void unused) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

        }
    }


    private class MoveImage extends AsyncTask<Integer, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(AlbumActivity.this);

        String OK = "";
        String waitMsg = "Wait\nProcess is being done... ";

        protected void onPreExecute() {
            this.dialog.setMessage(waitMsg);
            this.dialog.setCancelable(false); //outside touch doesn't dismiss you
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            int sl = DatabaseHandler.getInstance(AlbumActivity.this).getNumberOfImages(-1);

            List<String> URLList = new ArrayList<String>();


            for(int i=0; i < positionsList.size(); i++)
            {
                for(int j=0; j < list.size(); j++)
                {
                    if(positionsList.get(i) == list.get(j).getPos())
                    {
                        URLList.add(list.get(j).getUrlHinh());
                        list.remove(j);
                        break;
                    }

                }

            }

            if(IDAlbum != -1)
                DatabaseHandler.getInstance(AlbumActivity.this).addListImageWithOldIDAlbum(URLList,sl,-1, IDAlbum);

            DatabaseHandler.getInstance(AlbumActivity.this).deleteListImage(IDAlbum, positionsList);

            for(int i = 0; i < list.size(); i++)
            {
                DatabaseHandler.getInstance(AlbumActivity.this).updateIDAlbumIDImage(list.get(i), IDAlbum, i);
                list.get(i).setPos(i);
            }


            positionsList.clear();
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... value) {
            adapter.notifyDataSetChanged();

            modeGrid.finish();
            Toast.makeText(AlbumActivity.this, "Xóa thành công", Toast.LENGTH_LONG).show();
        }

        protected void onPostExecute(final Void unused) {
            if (this.dialog.isShowing()) {
                adapter.notifyDataSetChanged();
                this.dialog.dismiss();
            }

        }
    }

    private class UploadImage extends AsyncTask<List<Uri>, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(AlbumActivity.this);
        String waitMsg = "Wait\nLoading image is being done... ";

        protected void onPreExecute() {
            this.dialog.setMessage(waitMsg);
            this.dialog.setCancelable(false); //outside touch doesn't dismiss you
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(List<Uri>... lists) {
            for (int i = 0; i < lists[0].size(); i++) {
                Cursor cursor = getContentResolver().query(uriList.get(i), filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Image newImage = new Image(picturePath, IDAlbum, list.size());
                list.add(newImage);
                DatabaseHandler.getInstance(AlbumActivity.this).addImage(newImage.getUrlHinh(), newImage.getPos(), newImage.getIdAlbum());
                publishProgress();

            }
            checkedSelection.clear();
            for (int i = 0; i < list.size(); i++)
                checkedSelection.add(-1);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... value) {
            adapter.notifyDataSetChanged();
            gridView.smoothScrollToPosition(list.size() - 1);
        }

        // can use UI thread here
        protected void onPostExecute(final Void unused) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }


    }


    public class MultiChoiceModeListener implements
            GridView.MultiChoiceModeListener {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            positionsList.clear();
            modeGrid = mode;
            mode.setTitle("Select Items");
            mode.setSubtitle("One item selected");
            mode.getMenuInflater().inflate(R.menu.editbar_album, menu);

            return true;
        }


        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            positionsList.clear();
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete_album) {

                final AlertDialog DeleteDialog = new AlertDialog.Builder(AlbumActivity.this)
                        .setTitle("Bạn có muốn xóa các mục đã chọn?\n")
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                new MoveImage().execute(-1);
//                mode.finish();
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .create();
                DeleteDialog.show();
            }
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
            deleting = false;
            adapter.notifyDataSetChanged();

        }

        public void onItemCheckedStateChanged(ActionMode mode, int position,
                                              long id, boolean checked) {



            if (checked) {
                positionsList.add((int) id);

            } else {
                int index = 0;
                for (int i = 0; i < positionsList.size(); i++)
                    if (positionsList.get(i) == (int) id) {
                        index = i;
                        break;
                    }
                positionsList.remove(index);


            }

            int selectCount = gridView.getCheckedItemCount();
            switch (selectCount) {
                case 1:
                    mode.setSubtitle("One item selected");
                    break;
                default:
                    mode.setSubtitle("" + selectCount + " items selected");
                    break;
            }
        }


    }


}

