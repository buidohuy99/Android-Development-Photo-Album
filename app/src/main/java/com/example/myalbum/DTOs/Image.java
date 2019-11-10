package com.example.myalbum.DTOs;

public class Image
{
    private String Url;

    public Image(String UrlHinh)
    {
        this.Url = UrlHinh;
    }

    public  String getUrlHinh(){
        return this.Url;
    }
}