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
    private static final String TABLE_ALBUM = "ALBUM";
    private static final String AlBUM_ID = "id";
    private static final String ALBUM_NAME = "name";
    private static final String ALBUM_DATE = "date";
    //Các table khác viết sau đây
    //...

    private static final String TABLE_IMAGE = "IMAGE";
    private static final String KEY_ID_IMAGE = "id";
    private static final String ID_ALBUM = "id_album";
    private static final String IMAGE = "image";
    //Singleton
    private static DatabaseHandler databaseObject = null;

    public static DatabaseHandler getInstance(Context context) {
        if (databaseObject == null) {
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
                "(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT)", TABLE_ALBUM, AlBUM_ID, ALBUM_NAME, ALBUM_DATE);
        db.execSQL(create_albums_table);

        //Tạo  bảng danh sách các hình ảnh
        String create_images_table = String.format("CREATE TABLE %s" +
                "(%s INTEGER , %s INTEGER, %s TEXT, " +
                "PRIMARY KEY (%s, %s))", TABLE_IMAGE, KEY_ID_IMAGE, ID_ALBUM, IMAGE, KEY_ID_IMAGE, ID_ALBUM);
        db.execSQL(create_images_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_albums_table = String.format("DROP TABLE IF EXISTS %s", TABLE_ALBUM);
        db.execSQL(drop_albums_table);

        String drop_images_table = String.format("DROP TABLE IF EXISTS %s", TABLE_IMAGE);
        db.execSQL(drop_images_table);

        onCreate(db);
    }

    public void addAlbum(Album album) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AlBUM_ID, album.getId());
        values.put(ALBUM_NAME, album.getAlbumName());
        values.put(ALBUM_DATE, album.getDate());

        db.insert(TABLE_ALBUM, null, values);
        db.close();
    }

    public Album getAlbum(int albumId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ALBUM, null, AlBUM_ID + " = ?", new String[]{String.valueOf(albumId)}, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Album album = new Album(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
        return album;
    }

    public List<Album> getAllAlbums() {
        List<Album> albumList = new ArrayList<Album>();
        String query = "SELECT * FROM " + TABLE_ALBUM;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            Album album = new Album(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            albumList.add(album);
            cursor.moveToNext();
        }
        return albumList;
    }

    public void updateAlbum(Album album) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALBUM_NAME, album.getAlbumName());
        values.put(ALBUM_DATE, album.getDate());

        db.update(TABLE_ALBUM, values, AlBUM_ID + " = ?", new String[]{String.valueOf(album.getId())});
        db.close();
    }

    public void deleteAlbum(int albumId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALBUM, AlBUM_ID + " = ?", new String[]{String.valueOf(albumId)});
        String sqlDeleteAllImages = String.format(
                "DELETE FROM %s " +
                        "WHERE %s = %d ", TABLE_IMAGE, ID_ALBUM, albumId);
        db.execSQL(sqlDeleteAllImages);
        db.close();
    }

    public void deleteAlbums(int[] albumIDs) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlDeleteAllAlbums = String.format(
                "DELETE FROM %s " +
                        "WHERE %s IN (%d) ", TABLE_ALBUM, ID_ALBUM, albumIDs);
        db.execSQL(sqlDeleteAllAlbums);
        String sqlDeleteAllImages = String.format(
                "DELETE FROM %s " +
                        "WHERE %s IN (%d) ", TABLE_IMAGE, ID_ALBUM, albumIDs);
        db.execSQL(sqlDeleteAllImages);
        db.close();
    }

    public int getNumberOfAlbums() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = String.format(
                "SELECT COUNT(*)" +
                        "FROM %s"
                , TABLE_ALBUM);
        Cursor answer = db.rawQuery(sql, null);

        answer.moveToFirst();
        int amount = answer.getInt(0);
        answer.close();

        db.close();
        return amount;
    }

    public void addImage(String image, int idImage, int albumId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_IMAGE, idImage);
        values.put(ID_ALBUM, albumId);
        values.put(IMAGE, image);

        db.insert(TABLE_IMAGE, null, values);
        db.close();
    }

    public List<Image> getAllImageOfAlbum(int albumID) {

        List<Image> listImage = new ArrayList<Image>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM IMAGE WHERE id_album=? ", new String[]{String.valueOf(albumID)});

        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            String url = cursor.getString(2);
            int pos = cursor.getInt(0);
            listImage.add(new Image(url, albumID, pos));
            cursor.moveToNext();
        }

        cursor.close();

        db.close();
        return listImage;
    }

    public Image getImageAt(int albumID, int imageID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = String.format(
                "SELECT * " +
                        "FROM %s " +
                        "WHERE %s = %d AND %s = %d "
                , TABLE_IMAGE, ID_ALBUM, albumID, KEY_ID_IMAGE, imageID);
        Cursor answer = db.rawQuery(sql, null);

        answer.moveToFirst();
        String imageUrl = answer.getString(2);
        Image thumbnail = new Image(imageUrl, albumID, imageID);
        answer.close();

        db.close();
        return thumbnail;

    }

    public int getNumberOfImages(int albumID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = String.format(
                "SELECT COUNT(*) " +
                        "FROM %s " +
                        "WHERE %s = %d ", TABLE_IMAGE, ID_ALBUM, albumID);
        Cursor answer = db.rawQuery(sql, null);
        int amount = 0;
        answer.moveToFirst();
        if (answer.isAfterLast() == false)
            amount = answer.getInt(0);
        answer.close();

        db.close();
        return amount;
    }

    public void deleteImage(int albumID, int imageID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IMAGE, ID_ALBUM + " = ? AND " + KEY_ID_IMAGE + " = ?", new String[]{String.valueOf(albumID), String.valueOf(imageID)});
        String sqlDeleteAllImages = String.format(
                "DELETE FROM %s " +
                        "WHERE %s = %d AND %s = %d", TABLE_IMAGE, ID_ALBUM, albumID, KEY_ID_IMAGE, imageID);
        db.execSQL(sqlDeleteAllImages);
        db.close();
    }

    public void deleteAllImageAt(int albumID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IMAGE, ID_ALBUM + " = ? ", new String[]{String.valueOf(albumID)});
        String sqlDeleteAllImages = String.format(
                "DELETE FROM %s " +
                        "WHERE %s = %d", TABLE_IMAGE, ID_ALBUM, albumID);
        db.execSQL(sqlDeleteAllImages);
        db.close();
    }

    public void updateIDImage(Image image, int IDImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_IMAGE, IDImage);


        db.update(TABLE_IMAGE, values, ID_ALBUM + " = ? AND " + KEY_ID_IMAGE + " = ?", new String[]{String.valueOf(image.getIdAlbum()), String.valueOf((image.getPos()))});
        db.close();
    }

    public void updateIDAlbumIDImage(Image image, int IDAlbum, int IDImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_ALBUM, IDAlbum);
        values.put(KEY_ID_IMAGE, IDImage);


        db.update(TABLE_IMAGE, values, ID_ALBUM + " = ? AND " + KEY_ID_IMAGE + " = ?", new String[]{String.valueOf(image.getIdAlbum()), String.valueOf((image.getPos()))});
        db.close();
    }

    public int getNumberOfImagesAtAlbum(int IDAlbum) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = String.format(
                "SELECT COUNT(*)" +
                        "FROM %s" +
                        "WHERE %s = &d"
                , TABLE_IMAGE, ID_ALBUM , IDAlbum);
        Cursor answer = db.rawQuery(sql, null);

        answer.moveToFirst();
        int amount = answer.getInt(0);
        answer.close();

        db.close();
        return amount;
    }

}
