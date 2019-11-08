package com.example.myalbum.DTOs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Album class
public class Album implements Serializable {
    //To change this list to be list of URI because will load from Gallery
    private List<Integer> images;
    private String albumName;
    private String albumID;

    public Album(String albumName){
        this.albumName = albumName;
        this.images = new ArrayList<Integer>();
    }

    //Album name
    public String getAlbumName() {return albumName;}
    public void setAlbumName(String newName) {albumName = newName;}

    //Album's one image
    public boolean addImage(Integer image, int index) {
        if( index<0 || index >= images.size()) return false;
        images.add(image,index);
        return true;
    }
    public Integer getImage(int index) {return images.get(index);};

    //Album's global properties
    public Integer getImagesNumber() {return images.size();}
}
