package com.example.user.myapplication; /**
 * Created by user on 2018-01-13.
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class MyApplication extends Application {

    public ArrayList<SongListViewItem> SongList;
    Context tempContext;


    public MyApplication(Context context) {
        tempContext = context;
        SongList = new ArrayList<SongListViewItem>();
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    public void loadData()
    {
        SongList = fetchAllSongs();
    }

    private ArrayList<SongListViewItem> fetchAllSongs() {
        ArrayList<SongListViewItem> songs = new ArrayList<>();
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";
        Cursor cursor = tempContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
        );

        if(cursor == null)
            return songs;

        while(cursor.moveToNext()){
            String albumCoverPath = null;
            String albumID = cursor.getString(5);
            Cursor albumCursor = tempContext.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                    MediaStore.Audio.Albums._ID+" =? ",
                    new String[]{albumID},
                    null
            );

            if(albumCursor.moveToFirst()){
                albumCoverPath = albumCursor.getString(0);
            }

            SongListViewItem s = new SongListViewItem(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3),
                    cursor.getString(4),
                    albumCoverPath
            );

            Log.i("SONG","hi");
            Log.i("SONG", s.id);
            Log.i("SONG", s.title);
            Log.i("SONG", s.artist);
            Log.i("SONG", Long.toString(s.duration));
            Log.i("SONG", s.data);

            songs.add(s);
            albumCursor.close();
        }
        cursor.close();
        return songs;
    }



    public ArrayList<SongListViewItem> getSongList() {
        return SongList;
    }
}