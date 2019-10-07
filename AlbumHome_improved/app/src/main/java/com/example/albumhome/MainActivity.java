package com.example.albumhome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<Album> list = new ArrayList<Album>();
        AlbumArrayAdapter adapter = new AlbumArrayAdapter(this,list);
        ListView listView = findViewById(R.id.AlbumList);
        listView.setAdapter(adapter);
        ArrayList<Album> AlbumDTO = new ArrayList<Album>();
        Album album1 = new Album("hello",5,R.drawable.avatar01);
        Album album2 = new Album("hello2",5,R.drawable.avatar02);
        Album album3 = new Album("hello3",5,R.drawable.avatar03);
        AlbumDTO.add(album1);
        AlbumDTO.add(album2);
        AlbumDTO.add(album3);
        adapter.addAll(AlbumDTO);
    }
}
