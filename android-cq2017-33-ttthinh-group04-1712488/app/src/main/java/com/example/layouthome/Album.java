package com.example.layouthome;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private List<Integer> images;
    private String albumName;

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

