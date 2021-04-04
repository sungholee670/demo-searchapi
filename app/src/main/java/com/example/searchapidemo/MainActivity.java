package com.example.searchapidemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements SongListViewAdapter.ListBtnClickListener {

    public static final String url = "https://itunes.apple.com/search?term=greenday&entity=song";
    private static final String TAG = "SONG";
    private SongHandler msgHandler;
    private SongData listSong;
    private SongData favoriteSong;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msgHandler = new SongHandler();
        listSong = new SongData(SongData.SongDataCategory.List, this);
        favoriteSong = new SongData(SongData.SongDataCategory.Favorite, this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        ListView favoriteView = (ListView)findViewById(R.id.songfavoriteview);
                        ListView listView = (ListView)findViewById(R.id.songlistview);
                        switch (menuItem.getItemId()) {
                            case R.id.list_page:
                                Log.d(TAG, "List Page");
                                favoriteView.setVisibility(View.INVISIBLE) ;
                                listView.setVisibility(View.VISIBLE);
                                return true;
                            case R.id.favorite_page:
                                Log.d(TAG, "Favorite Page");
                                listView.setVisibility(View.INVISIBLE);
                                favoriteView.setVisibility(View.VISIBLE) ;
                                favoriteSong.getAdapter().notifyDataSetChanged();
                                return true;
                        }
                        return false;
                    }
                }
        );

        makeSongList();
        Log.d(TAG,"starting a query thread");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void makeSongList() {
        if(favoriteSong.isExistJson()) {
            try {
                Thread t1 = new Thread(new DataRunnable(favoriteSong));
                t1.start();
                t1.join();
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,"failed to make a favoriteSong");
            }
        }
        Thread t = new Thread(new DataRunnable(listSong));
        t.start();
    }

    public class SongHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SongData.MSG_UPDATE_UI) {
                ListAdapterData data = (ListAdapterData) msg.obj;
                if(data == null) {
                    Log.d(TAG,"object is null");
                    return;
                }
                boolean checkedFavorite = false;
                if(favoriteSong.getAdapter().getCount()>0) {
                    checkedFavorite = favoriteSong.getAdapter().isExistItem(data);
                }
                data.adapter.addItem(data.trackName, data.collectionName, data.artistName,data.artworkUrl,checkedFavorite, data.img);
                if(data.adapter.getCount() == 9)
                    data.adapter.notifyDataSetChanged();
                Log.d(TAG,"updated UI");
            }
            else if(msg.what == SongData.MSG_COMPELTED) {
                Log.d(TAG,"completed to update UI");
                SongListViewAdapter adapter = (SongListViewAdapter)msg.obj;
                adapter.notifyDataSetChanged();
            }
            else if(msg.what == SongData.MSG_UPDATE_FAVORITE_UI) {
                ListAdapterData data = (ListAdapterData) msg.obj;
                data.adapter.addItem(data.trackName, data.collectionName, data.artistName,data.artworkUrl, true, data.img);
                Log.d(TAG,"updated Favorite UI");
            }
        }
    }

    @Override
    public void onListBtnClick(int position) {
        Log.d(TAG,"position: " + position);
        SongListItem item = (SongListItem)listSong.getAdapter().getItem(position);
        if(item.getFavoriteValue() == true) {
            favoriteSong.getAdapter().addItem(item);
        }
        else {
            favoriteSong.getAdapter().removeItem(item);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        favoriteSong.saveFavoriteItemToFile();
    }

    public Handler getUIHandler() { return msgHandler;}

    class DataRunnable implements Runnable {
        private SongData data;

        DataRunnable(SongData data) {
            this.data = data;
        }
        @Override
        public void run() {
            if(data.getCategory() == SongData.SongDataCategory.List)
                data.querySongList(url);
            else
                favoriteSong.makeFavoriteList();

            msgHandler.obtainMessage(SongData.MSG_COMPELTED, data.getAdapter()).sendToTarget();
        }
    }

}