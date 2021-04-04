package com.example.searchapidemo;

import android.graphics.Bitmap;

class ListAdapterData {
    public String trackName;
    public String collectionName;
    public String artistName;
    public String artworkUrl;
    public Bitmap img;
    public SongListViewAdapter adapter;

    public ListAdapterData(String trackName,
                           String collectionName,
                           String artistName,
                           String artworkUrl,
                           Bitmap img,
                           SongListViewAdapter adapter) {
        this.artistName = artistName;
        this.collectionName = collectionName;
        this.artworkUrl = artworkUrl;
        this.img = img;
        this.trackName = trackName;
        this.adapter = adapter;
    }
}
