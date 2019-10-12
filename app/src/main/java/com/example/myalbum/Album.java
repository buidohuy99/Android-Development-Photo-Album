package com.example.myalbum;

import java.util.ArrayList;
import java.util.List;

public class Album {
    //To change this list to be list of URI because will load from Gallery
    private List<Integer> images;
    private String albumName;
    private String albumID;

    Album(String albumName){
        this.albumName = albumName;
        this.images = new ArrayList<Integer>();
    }

    String getAlbumName() {return albumName;}
    void setAlbumName(String newName) {albumName = newName;}

    Integer getImagesNumber() {return images.size();}
    boolean addImage(Integer image, int index) {
        if( index<0 || index >= images.size()) return false;
        images.add(image,index);
        return true;
    }
    Integer getImage(int index) {return images.get(index);};
}
