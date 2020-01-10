package com.example.myalbum.DTOs;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.myalbum.AlbumsActivity.HomeActivity;
import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.utilities.UtilityGlobals;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//Album class
public class Album implements Parcelable {

    //To change this list to be list of URI because will load from Gallery
    private String albumName;
    private int albumID;
    private String albumDate;
    private String albumPassword;
    //created date

    public Album(String albumName, String albumPassword){
        super();
        this.albumName = albumName;
        this.albumPassword = albumPassword;
    }

    public Album(int id, String name, String date, String password){
        super();
        albumID = id;
        albumName = name;
        albumDate = date;
        albumPassword = password;
    }

    public Album(String name, String date, String albumPassword){
        super();
        albumName = name;
        albumDate = date;
        this.albumPassword = albumPassword;
    }

    protected Album(Parcel in) {
        albumID = in.readInt();
        albumName = in.readString();
        albumDate = in.readString();
        albumPassword = in.readString();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    //Album name
    public String getAlbumName() {return albumName;}
    public void setAlbumName(String newName) {albumName = newName;}

    //Album ID
    public int getId(){
        return albumID;
    }

    public void setId(int id){
        albumID=id;
    }

    //Album Date
    public String getDate(){
        return albumDate;
    }

    public void setDate(String date){
        albumDate=date;
    }
    public void setCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(UtilityGlobals.globalDateFormat);
        String formattedDate = df.format(c);
        albumDate = formattedDate;
    }

    //Album Pass
    public String getAlbumPassword() {return albumPassword;}

    public void setAlbumPassword(String pass) {albumPassword = pass;};

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(albumID);
        dest.writeString(albumName);
        dest.writeString(albumDate);
        dest.writeString(albumPassword);
    }
}
