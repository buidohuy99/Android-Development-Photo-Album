package com.example.myalbum.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myalbum.DTOs.Album;
import com.example.myalbum.DTOs.Image;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    //Database
    private static final String DATABASE_NAME = "Gallery";
    private static final int DATABASE_VERSION = 1;

    //Các table
        //Table album
    private static final String TABLE_NAME = "ALBUM";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";
        //Các table khác viết sau đây
        //...

    private static final String TABLE_IMAGE = "IMAGE";
    private static final String KEY_ID_IMAGE = "id";
    private static final String ID_ALBUM = "id_album";
    private static final String IMAGE = "image";
    //Singleton
    private static DatabaseHandler databaseObject = null;

    public static DatabaseHandler getInstance(Context context){
        if(databaseObject == null) {
            databaseObject = new DatabaseHandler(context);
        }
        return databaseObject;

    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //end Singleton

    //
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Mai mốt có nhiều table thì nhó copy 2 dòng này
        //Sửa lại các biến cần thiết để tạo bảng mới
        String create_albums_table = String.format("CREATE TABLE %s" +
                "(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT)", TABLE_NAME, KEY_ID, KEY_NAME, KEY_DATE);
        db.execSQL(create_albums_table);

        //Tạo  bảng danh sách các hình ảnh
        String create_images_table = String.format("CREATE TABLE %s" +
                "(%s INTEGER PRIMARY KEY, %s INTEGER, %s TEXT)", TABLE_IMAGE, KEY_ID_IMAGE, ID_ALBUM, IMAGE);
        db.execSQL(create_images_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_albums_table = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(drop_albums_table);

        String drop_images_table = String.format("DROP TABLE IF EXISTS %s", TABLE_IMAGE);
        db.execSQL(drop_images_table);

        onCreate(db);
    }

    public void addAlbum(Album album) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,album.getId());
        values.put(KEY_NAME, album.getAlbumName());
        values.put(KEY_DATE, album.getDate());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Album getAlbum(int albumId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, KEY_ID + " = ?", new String[] { String.valueOf(albumId) },null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
        Album album = new Album(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
        return album;
    }

    public List<Album> getAllAlbums() {
        List<Album>  albumList = new ArrayList<Album>();
        String query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false) {
            Album album = new Album(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            albumList.add(album);
            cursor.moveToNext();
        }
        return albumList;
    }

    public void updateAlbum(Album album) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, album.getAlbumName());
        values.put(KEY_DATE, album.getDate());

        db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[] { String.valueOf(album.getId()) });
        db.close();
    }

    public void deleteAlbum(int albumId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?", new String[] { String.valueOf(albumId) });
        db.close();
    }
    public void addImage(String image, int albumId ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_ALBUM , albumId);
        values.put(IMAGE, image);

        db.insert(TABLE_IMAGE, null, values);
        db.close();


    }

    public List<Image> getAllImageOfAlbum(int albumID) {

        List<Image> listImage = new ArrayList<Image>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM IMAGE WHERE id_album=? ", new String[]{String.valueOf(albumID)});

        cursor.moveToFirst();


        while(cursor.isAfterLast() == false) {
            String url = cursor.getString(2);
            listImage.add(new Image(url));
            cursor.moveToNext();
        }
        return listImage;
    }
}
