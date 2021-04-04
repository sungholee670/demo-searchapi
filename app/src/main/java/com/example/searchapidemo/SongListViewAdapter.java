package com.example.searchapidemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SongListViewAdapter extends BaseAdapter implements View.OnClickListener {

    private static final String TAG ="SONG";


    public interface ListBtnClickListener {
        void onListBtnClick(int position);
    }

    private ArrayList<SongListItem> songList = new ArrayList<SongListItem>();
    private ListBtnClickListener btnClickListener;
    private SongData.SongDataCategory category;

    public SongListViewAdapter(SongData.SongDataCategory category, ListBtnClickListener btnClickListener) {
        this.btnClickListener = btnClickListener;
        this.category = category;
    }

    @Override
    public synchronized int getCount() { return songList.size();}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();
        ImageView artWorkImageView;
        TextView trackNameTextView;
        TextView collectionTextView;
        TextView artistTextView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(category ==  SongData.SongDataCategory.List) {
                convertView = inflater.inflate(R.layout.listview_element, parent, false);
            }
            else {
                convertView = inflater.inflate(R.layout.listview_favorite, parent, false);
            }
        }

        if(category ==  SongData.SongDataCategory.List) {
            artWorkImageView = (ImageView) convertView.findViewById(R.id.imageView_artwork);
            trackNameTextView = (TextView) convertView.findViewById(R.id.textView_trackName);
            collectionTextView = (TextView) convertView.findViewById(R.id.textView_collectionName);
            artistTextView = (TextView) convertView.findViewById(R.id.textView_artistName);
        }
        else {
            artWorkImageView = (ImageView) convertView.findViewById(R.id.imageView_favorite_artwork);
            trackNameTextView = (TextView) convertView.findViewById(R.id.textView_favorite_trackName);
            collectionTextView = (TextView) convertView.findViewById(R.id.textView_favorite_collectionName);
            artistTextView = (TextView) convertView.findViewById(R.id.textView_favorite_artistName);
        }
        SongListItem item = songList.get(position);
        artWorkImageView.setImageBitmap(item.getArtworkImg());
        trackNameTextView.setText(item.getTrackName());
        collectionTextView.setText(item.getCollectionName());
        artistTextView.setText(item.getArtistName());

        if(category == SongData.SongDataCategory.List) {
            ImageButton buttonFavorite = (ImageButton) convertView.findViewById(R.id.button_favorite);
            if (item.getFavoriteValue() == false) {
                buttonFavorite.setBackgroundResource(R.drawable.star_uncheck);
            } else {
                buttonFavorite.setBackgroundResource(R.drawable.star_check);
            }
            buttonFavorite.setTag(position);
            buttonFavorite.setOnClickListener(this);
        }

        Log.d(TAG,"idx: " + position + ", trackName: " + item.getTrackName());
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return this.songList.get(position);
    }

    @Override
    public void onClick(View v) {
        if(btnClickListener != null) {
            int pos = (int)v.getTag();
            SongListItem item = songList.get(pos);
            boolean val = !(item.getFavoriteValue());
            item.setFavoriteValue(val);
            if(item.getFavoriteValue() == false) {
                v.setBackgroundResource(R.drawable.star_uncheck);
            }
            else {
                v.setBackgroundResource(R.drawable.star_check);
            }
            btnClickListener.onListBtnClick(pos);
        }
    }

    public synchronized  void addItem(String trackName,
                                      String collectionName,
                                      String artisName,
                                      String artworkUrl,
                                      boolean favoriteValue,
                                      Bitmap img) {
        SongListItem item = new SongListItem(trackName, collectionName, artisName, artworkUrl,favoriteValue,img);
        songList.add(item);
    }

    public boolean isExistItem(ListAdapterData item) {
        for(int i=0; i<songList.size(); i++) {
            SongListItem elm = (SongListItem)songList.get(i);
            Log.d(TAG,"getTrackName: " + elm.getTrackName() + " trackName:" + item.trackName);
            if(elm.getTrackName().equals(item.trackName)  &&
                    elm.getArtistName().equals(item.artistName)  &&
                    elm.getCollectionName().equals(item.collectionName)) {
                Log.d(TAG,"item has already exist.");
                return true;
            }
        }
        return false;
    }

    public void addItem(SongListItem item) {
        for(int i=0; i<songList.size(); i++) {
            SongListItem elm = (SongListItem)songList.get(i);
            if(elm.getTrackName().equals(item.getTrackName())  &&
                elm.getArtistName().equals(item.getArtistName()) &&
                elm.getCollectionName().equals(item.getCollectionName())) {
                Log.d(TAG,"item has already exist.");
                return;
            }
        }

        songList.add(item);
    }

    public void removeItem(SongListItem item) {
        for(int i=0; i<songList.size(); i++) {
            SongListItem elm = (SongListItem)songList.get(i);
            if(elm.getTrackName().equals(item.getTrackName())  &&
                    elm.getArtistName().equals(item.getArtistName())  &&
                    elm.getCollectionName().equals(item.getCollectionName())) {
                Log.d(TAG,"founded the same item in list");
                songList.remove(i);
                return;
            }
        }
    }


}
