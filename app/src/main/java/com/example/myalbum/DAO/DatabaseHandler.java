package com.example.myalbum.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.example.myalbum.DTOs.Album;
import com.example.myalbum.DTOs.Image;
import com.example.myalbum.utilities.UtilityGlobals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class DatabaseHandler extends SQLiteOpenHelper {

    //Database update
    private static final int ADD_PASSWORD_FIELD = 2;

    //Database
    private static final String DATABASE_NAME = "Gallery";
    private static final int DATABASE_VERSION = 2;

    //Các table
    //Table album
    private static final String TABLE_ALBUM = "ALBUM";
    private static final String AlBUM_ID = "id";
    private static final String ALBUM_NAME = "name";
    private static final String ALBUM_DATE = "date";
    private static final String ALBUM_PASS = "password";
    //Các table khác viết sau đây
    //...

    private static final String TABLE_IMAGE = "IMAGE";
    private static final String KEY_ID_IMAGE = "id";
    private static final String ID_ALBUM = "id_album";
    private static final String IMAGE = "image";
    private static final String ID_OLDALBUM = "id_oldAlbum";

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
                "(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT)", TABLE_ALBUM, AlBUM_ID, ALBUM_NAME, ALBUM_DATE, ALBUM_PASS);
        db.execSQL(create_albums_table);

        //Tạo  bảng danh sách các hình ảnh
        String create_images_table = String.format("CREATE TABLE %s" +
                "(%s INTEGER , %s INTEGER, %s TEXT, %s INTEGER, " +
                "PRIMARY KEY (%s, %s))", TABLE_IMAGE, KEY_ID_IMAGE, ID_ALBUM, IMAGE, ID_OLDALBUM, KEY_ID_IMAGE, ID_ALBUM);
        db.execSQL(create_images_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < ADD_PASSWORD_FIELD){
            String add_album_password= String.format("ALTER TABLE %s ADD COLUMN %s TEXT",TABLE_ALBUM, ALBUM_PASS);
            db.execSQL(add_album_password);
        }
    }

    public void initSpecialAlbums(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        //Trash : -1
        ContentValues values = new ContentValues();
        values.put(AlBUM_ID, UtilityGlobals.TRASH_ALBUM);
        values.put(ALBUM_NAME, "Thùng rác");

        db.insert(TABLE_ALBUM, null, values);

        //Favorite : -2
        values.put(AlBUM_ID, UtilityGlobals.FAVORITE_ALBUM);
        values.put(ALBUM_NAME, "Yêu thích");

        db.insert(TABLE_ALBUM, null, values);

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void addAlbum(Album album) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(AlBUM_ID, album.getId());
        values.put(ALBUM_NAME, album.getAlbumName());
        values.put(ALBUM_DATE, album.getDate());
        values.put(ALBUM_PASS, album.getAlbumPassword());

        db.insert(TABLE_ALBUM, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public Album getAlbum(int albumId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ALBUM, null, AlBUM_ID + " = ?", new String[]{String.valueOf(albumId)}, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Album album = new Album(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));

        cursor.close();
        db.close();
        return album;
    }

    public List<Album> findAlbumByName(String albumName) {
        List<Album> albumList = new ArrayList<Album>();
        String query = String.format("SELECT * FROM %s " +
                "WHERE INSTR(LOWER(%s), LOWER(\"%s\")) > 0 AND %s >= 0 ",TABLE_ALBUM, ALBUM_NAME, albumName, AlBUM_ID);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            Album album = new Album(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            albumList.add(album);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return albumList;
    }

    public List<Album> getAllAlbums() {
        List<Album> albumList = new ArrayList<Album>();
        String query = "SELECT * FROM " + TABLE_ALBUM;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            Album album = new Album(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            albumList.add(album);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
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
                        "WHERE %s = %d ", TABLE_IMAGE, AlBUM_ID, albumId);
        db.execSQL(sqlDeleteAllImages);
        db.close();
    }

    public void deleteAlbums(Integer[] albumIDs) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        String listString = Arrays.toString(albumIDs).replaceAll("\\[|\\]|\\s", "");
        String sqlDeleteAllAlbums = String.format(
                "DELETE FROM %s " +
                        "WHERE %s IN (%s) ", TABLE_ALBUM, AlBUM_ID, listString);
        db.execSQL(sqlDeleteAllAlbums);
        String sqlDeleteAllImages = String.format(
                "DELETE FROM %s " +
                        "WHERE %s IN (%s) ", TABLE_IMAGE, ID_ALBUM, listString);
        db.execSQL(sqlDeleteAllImages);
        db.setTransactionSuccessful();
        db.endTransaction();
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
    public void addImageWithOldIDAlbum(String image, int idImage, int albumId, int idOldAlbum) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_IMAGE, idImage);
        values.put(ID_ALBUM, albumId);
        values.put(IMAGE, image);
        values.put(ID_OLDALBUM, idOldAlbum);

        db.insert(TABLE_IMAGE, null, values);
        db.close();
    }

    public void addListImageWithOldIDAlbum(List<String> image,int idImage, int albumId, int idOldAlbum) {
        for(int i = 0 ; i < image.size(); i++)
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_ID_IMAGE, idImage + i);
            values.put(ID_ALBUM, albumId);
            values.put(IMAGE, image.get(i));
            values.put(ID_OLDALBUM, idOldAlbum);

            db.insert(TABLE_IMAGE, null, values);
            db.close();
        }

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
        int oldIDAlbum = answer.getInt(3);
        Image thumbnail = new Image(imageUrl, albumID, imageID, oldIDAlbum);
        answer.close();

        db.close();
        return thumbnail;

    }

    public int getNumberOfImages(int albumID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = String.format(
                "SELECT COUNT(%s) " +
                        "FROM %s " +
                        "WHERE %s = %d ",KEY_ID_IMAGE, TABLE_IMAGE, ID_ALBUM, albumID);
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
    public void deleteListImage(int albumID, List<Integer> imageID) {
        for(int i = 0; i < imageID.size(); i++)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_IMAGE, ID_ALBUM + " = ? AND " + KEY_ID_IMAGE + " = ?", new String[]{String.valueOf(albumID), String.valueOf(imageID.get(i))});
            String sqlDeleteAllImages = String.format(
                    "DELETE FROM %s " +
                            "WHERE %s = %d AND %s = %d", TABLE_IMAGE, ID_ALBUM, albumID, KEY_ID_IMAGE, imageID.get(i));
            db.execSQL(sqlDeleteAllImages);
            db.close();
        }

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
