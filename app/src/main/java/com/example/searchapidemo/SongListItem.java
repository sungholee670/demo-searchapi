package com.example.searchapidemo;

import android.graphics.Bitmap;

public class SongListItem {
    private String trackName;
    private String collectionName;
    private String artistName;
    private String artworkUrl;
    private Bitmap artworkImg;
    private boolean favoriteValue;

    public SongListItem(String trackName,
                        String collectionName,
                        String artistName,
                        String artworkUrl,
                        boolean favoriteValue,
                        Bitmap artworkImg) {
        this.trackName = trackName;
        this.collectionName = collectionName;
        this.artistName = artistName;
        this.artworkImg = artworkImg;
        this.artworkUrl = artworkUrl;
        this.favoriteValue = favoriteValue;
    }

    public boolean getFavoriteValue() { return favoriteValue;}

    public void setFavoriteValue(boolean val) { favoriteValue = val;}

    public void setArtworkImg(Bitmap img) { artworkImg = img;}

    public Bitmap getArtworkImg() { return artworkImg;}

    public String getTrackName() { return trackName;}

    public String getCollectionName() { return  collectionName;}

    public String getArtistName() { return artistName;}

    public String getArtworkUrl() { return artworkUrl;}
}

