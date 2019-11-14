package com.example.myalbum.DTOs;

public class Image
{
    private String Url;
    private  int idAlbum;
    private int pos;

    public Image(String UrlHinh, int idAlbum, int pos)
    {
        this.Url = UrlHinh;
        this.idAlbum = idAlbum;
        this.pos=pos;
    }

    public  String getUrlHinh(){
        return this.Url;
    }

    public int getIdAlbum(){
        return  this.idAlbum;
    }

    public  int getPos(){
        return this.pos;
    }

    public void setPos(int newPos){
        this.pos = newPos;
    }

    public void setUrl(String newUrl){
        this.Url = newUrl;
    }

    public void setIdAlbum(int newIdAlbum)
    {
        this.idAlbum = newIdAlbum;
    }
}
