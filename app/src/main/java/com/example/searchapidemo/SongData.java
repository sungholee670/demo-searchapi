package com.example.searchapidemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SongData  {

    private static final String TAG="SONG";
    public static final int MSG_UPDATE_UI = 0x1000;
    public static final int MSG_COMPELTED = 0x1001;
    public static final int MSG_UPDATE_FAVORITE_UI = 0x1002;
    public static final String jsonFileName = "favorite.json";
    public enum SongDataCategory {
        List,
        Favorite,
    };

    private SongDataCategory category;
    private Handler msgHandler;
    private SongListViewAdapter adapter;
    private ListView songListView;
    private Context context;

    public SongData(SongDataCategory category,  MainActivity activity) {
        this.category = category;
        this.msgHandler = activity.getUIHandler();

        this.adapter = new SongListViewAdapter(category, activity);
        if(category == SongDataCategory.List)
            this.songListView = (ListView)activity.findViewById(R.id.songlistview);
        else
            this.songListView = (ListView)activity.findViewById(R.id.songfavoriteview);
        this.songListView.setAdapter(this.adapter);
        this.context = activity.getApplicationContext();
    }
    public SongDataCategory getCategory() { return category;}
    public SongListViewAdapter getAdapter() {return adapter;}

    public int querySongList(String surl) {

        Log.d(TAG,"category: " + category + ",  query url: " + surl);
        String in = getJSONStringFromServer(surl);
        if(in == null) return -1;

        if(jsonParsing(in) < 0) return -1;
        return 0;
    }

    private Bitmap getbmpfromURL(String surl){
        try {
            Log.d(TAG,"Img url: " + surl);
            URL url = new URL(surl);
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setDoInput(true);
            urlcon.connect();
            InputStream in = urlcon.getInputStream();
            Bitmap mIcon = BitmapFactory.decodeStream(in);
            Log.d(TAG,"completed to download an image");
            return  mIcon;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String getJSONStringFromServer(String surl) {
        StringBuilder content = new StringBuilder();
        try {
            Log.d(TAG,"query url: " + surl );
            URL url = new URL(surl);
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setDoInput(true);
            urlcon.connect();
            InputStream in = urlcon.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
            Log.d(TAG,"completed to download json");
            return  content.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private int jsonParsing(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray songArray = jsonObject.getJSONArray("results");
            Log.d(TAG,"element size: " + songArray.length());
            for(int i=0; i<songArray.length(); i++){
                JSONObject songObject = songArray.getJSONObject(i);
                String trackName = songObject.getString("trackName");
                String collectionName = songObject.getString("collectionName");
                String artisName = songObject.getString("artistName");
                String imgUrl = songObject.getString("artworkUrl60");
                Bitmap img = getbmpfromURL(imgUrl);
                if(img == null) continue;
                ListAdapterData data = new ListAdapterData(trackName,collectionName, artisName, imgUrl, img, adapter);
                if(category == SongDataCategory.List)
                    msgHandler.obtainMessage(MSG_UPDATE_UI, data).sendToTarget();
                else
                    msgHandler.obtainMessage(MSG_UPDATE_FAVORITE_UI, data).sendToTarget();
            }

            Log.d(TAG,"completed to make a song list.");
        }
        catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    void saveFavoriteItemToFile() {
        String filename = context.getFilesDir() + this.jsonFileName;
        File files = new File(filename);

        if(files.exists()) {
            files.delete();
            Log.d(TAG,"deleted " + filename );
        }
        Log.d(TAG,"favorite size(): " + adapter.getCount());
        if(adapter.getCount() > 0) {
            JSONObject rootObj = new JSONObject();
            JSONArray songArray = new JSONArray();

            try {
                for (int i = 0; i < adapter.getCount(); i++) {
                    SongListItem item = (SongListItem) adapter.getItem(i);
                    JSONObject elm = new JSONObject();
                    elm.put("trackName", item.getTrackName());
                    elm.put("collectionName", item.getCollectionName());
                    elm.put("artistName", item.getArtistName());
                    elm.put("artworkUrl60", item.getArtworkUrl());
                    songArray.put(elm);
                }

                rootObj.put("results", songArray);
                Log.d(TAG,"saved favorite lists to Json format(" + adapter.getCount() + ")");

                filename = context.getFilesDir() + this.jsonFileName;
                FileOutputStream outputStream = new FileOutputStream(filename);
                outputStream.write(rootObj.toString().getBytes());
                outputStream.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }


    }

    Boolean isExistJson() {
        String filename = context.getFilesDir() + this.jsonFileName;
        File files = new File(filename);
        if(files.exists())
            return true;
        else
            return false;
    }

    void makeFavoriteList() {
        String filename = context.getFilesDir() + this.jsonFileName;
        File files = new File(filename);
        StringBuilder content = new StringBuilder();

        if(files.exists()) {
            Log.d(TAG,"file(" + filename +") exits.");
            try {
                FileInputStream inputStream = new FileInputStream(files);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line + "\n");
                }
                bufferedReader.close();
                jsonParsing(content.toString());
                Log.d(TAG,"completed to make a favorite list");
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }

        }
    }
}
