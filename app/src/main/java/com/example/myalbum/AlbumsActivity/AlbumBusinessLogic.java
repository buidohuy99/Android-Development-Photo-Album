package com.example.myalbum.AlbumsActivity;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Album;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlbumBusinessLogic {
    public static int findSmallestMissingAlbumID(List<Album> allAlbums){
        int N = allAlbums.size();
        Set<Integer> set = new HashSet<>();
        for (Album a : allAlbums)
            set.add(a.getId());

        for (int i = 0; i < N ; i++) {
            if (!set.contains(i)) return i;
        }
        return N;
    }
}
