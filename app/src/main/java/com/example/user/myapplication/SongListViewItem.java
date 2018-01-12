package com.example.user.myapplication;

import java.io.Serializable;

/**
 * Created by user on 2018-01-12.
 */

public class SongListViewItem implements Serializable{
    String id, title, artist, data, albumCover;
    long duration;
    SongListViewItem (String id, String title, String artist, long duration, String data, String albumCover) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.data = data;
        this.albumCover = albumCover;
    }
}
