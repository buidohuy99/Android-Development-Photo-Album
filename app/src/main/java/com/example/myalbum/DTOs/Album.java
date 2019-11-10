package com.example.myalbum.DTOs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Album class
public class Album implements Serializable {
    //To change this list to be list of URI because will load from Gallery
    private List<Integer> images;
    private String albumName;
    private int albumID;
    private String albumDate;
    private static int count=0;
    //created date

    public Album(String albumName){
        this.albumName = albumName;
        //nhớ đổi "Integer thành Byte"
        this.albumID = count;
        count++;
        this.images = new ArrayList<Integer>();
    }

    public Album(int id, String name, String date){
        super();
        albumID = id;
        albumName = name;
        albumDate = date;
        //nhớ đổi "Integer thành Byte"
        this.images = new ArrayList<Integer>();
    }

    public Album(String name, String date){
        super();
        albumName = name;
        albumDate = date;
        //nhớ đổi "Integer thành Byte"
        this.images = new ArrayList<Integer>();
    }

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


    //Album's one image
    public boolean addImage(Integer image, int index) {
        if( index<0 || index >= images.size()) return false;
        images.add(image,index);
        return true;
    }
    public Integer getImage(int index) {return images.get(index);};

    //Album's global properties
    public Integer getImagesNumber() {
        return images.size();
    }
}
