package com.example.myalbum.AlbumsActivity;

import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.myalbum.DAO.DatabaseHandler;
import com.example.myalbum.DTOs.Album;
import com.example.myalbum.utilities.UtilityGlobals;

import java.util.Comparator;
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

    public static class SortByName implements Comparator<Album>
    {

        @Override
        public int compare(Album album, Album t1) {
            return album.getAlbumName().compareTo(t1.getAlbumName());
        }
    }

    public static class SortByImageCount implements Comparator<Album>
    {
        Activity context;

        public SortByImageCount(Activity c) {
            context = c;
        }

        @Override
        public int compare(Album album, Album t1) {
            int count1 = DatabaseHandler.getInstance(context).getNumberOfImages(album.getId());
            int count2 = DatabaseHandler.getInstance(context).getNumberOfImages(t1.getId());

            return count1 - count2;
        }
    }

    public static class SortByDate implements Comparator<Album> {

        @Override
        public int compare(Album o1, Album o2) {
            String o1DateStr = o1.getDate();
            String o2DateStr = o2.getDate();
            long o1Days, o2Days;
            try{
                o1Days = UtilityGlobals.globalSDF.parse(o1DateStr).getTime();
                o1Days = (o1Days / (1000 * 60 * 60 *24)) % 7;
            } catch(Exception e) {
                o1Days = -1;
            }
            try{
                o2Days = UtilityGlobals.globalSDF.parse(o2DateStr).getTime();
                o2Days =(o2Days / (1000 * 60 * 60 *24)) % 7;
            } catch(Exception e) {
                o2Days = -1;
            }
            if(o1Days < o2Days)
                return  -1;
            else if (o1Days > o2Days)
                return 1;
            return 0;
        }
    }

    public static class TwoFieldSort implements Comparator<Album> {

        private Comparator<Album> comparatorOne;
        private Comparator<Album> comparatorTwo;
        private boolean orderOneAscending = true;
        private boolean orderTwoAscending = true;

        public TwoFieldSort(Comparator<Album> one, Comparator<Album> another) {
            this.comparatorOne = one;
            this.comparatorTwo = another;
        }

        public TwoFieldSort(Comparator<Album> one, boolean orderOneAscending, Comparator<Album> another, boolean orderTwoAscending) {
            this.comparatorOne = one;
            this.comparatorTwo = another;
            this.orderOneAscending = orderOneAscending;
            this.orderTwoAscending = orderTwoAscending;
        }

        @Override
        public int compare(Album o1, Album o2) {
            int comparatorOneResult, comparatorTwoResult;
            if(orderOneAscending)
                comparatorOneResult = comparatorOne.compare(o1, o2);
            else
                comparatorOneResult = comparatorOne.compare(o2, o1);

            if(orderTwoAscending)
                comparatorTwoResult = comparatorTwo.compare(o1, o2);
            else
                comparatorTwoResult = comparatorTwo.compare(o2, o1);

            if (comparatorOneResult == 0)
                return comparatorTwoResult;
            else
                return comparatorOneResult;
        }
    }
}
