package com.example.user.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class SingingActivity extends AppCompatActivity {
    ArrayList<SongListViewItem> songs;
    MediaPlayer mp;
    SeekBar seekbar;
    Thread seekbarthread = null;
    TextView totaltime, currenttime, title, artist;
    boolean ispaused;
    int position, width;
    ImageView albumArt;
    ImageView playbtn, stopbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singing);
        Intent intent = getIntent();

        MyApplication myApp = new MyApplication(this);
        myApp.loadData();
        songs = myApp.getSongList();
        position = intent.getIntExtra("position", 1);

        seekbar = findViewById(R.id.seekBar);
        currenttime = findViewById(R.id.singing_time_current);
        totaltime = findViewById(R.id.singing_time_total);
        title = findViewById(R.id.singing_title);
        artist = findViewById(R.id.singing_artist);
        ApplyFonts(this,title);
        ApplyFonts(this,artist);
        ApplyFonts(this,currenttime);
        ApplyFonts(this,totaltime);
        playbtn = findViewById(R.id.singing_play_button);
        stopbtn = findViewById(R.id.singing_stop_button);




        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;

        mp = new MediaPlayer();

        mp.setLooping(false);
        ispaused = false;

        preparesong(position);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer m){
                if(!mp.isLooping()){
                    mp.stop();
                    position = position+1;
                    if(position==songs.size()){
                        position = 0;
                    }
                    preparesong(position);
                    ispaused = false;
                    mp.start();
                }


            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBarChangeListener());

        playbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mp.isPlaying()){
                    mp.pause();
                    ispaused = true;
                    playbtn.setSelected(false);
                }
                else{
                    ispaused = false;
                    mp.start();
                    if(seekbarthread==null){
                        playbtn.setSelected(true);
                        Thread seekbarthread = new seekbarThread();
                        seekbarthread.start();
                    }
                }
            }
        });

        stopbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mp.isPlaying() || ispaused){
                    mp.stop();
                    try{
                        mp.prepare();
                    }catch(IllegalStateException e){
                        e.printStackTrace();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    mp.seekTo(0);

                    playbtn.setSelected(false);
                }
            }
        });


    }


    public void preparesong(int position){
        SongListViewItem song = songs.get(position);
        String path = song.data;
        String albumArtPath = song.albumCover;
        String title_name = song.title;
        String artist_name = song.artist;

        boolean islooping = mp.isLooping();

        mp.reset();
        try {
            mp.setDataSource(path);
            mp.prepare();
            mp.setLooping(islooping);
        } catch(Exception e) {
            e.printStackTrace();
        }

        int duration = mp.getDuration();
        seekbar.setMax(duration);

        totaltime.setText(strtime(duration));
        currenttime.setText("0:00");
        title.setText(title_name);
        artist.setText(artist_name);


    }



    class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
            if(fromUser) {
                mp.seekTo(progress);
            }
            currenttime.setText(strtime(mp.getCurrentPosition()));
        }

        public void onStartTrackingTouch(SeekBar arg0) {

        }

        public void onStopTrackingTouch(SeekBar arg0) {

        }
    }


    private class seekbarThread extends Thread{
        private static final String TAG = "seekbarThread";

        public seekbarThread(){

        }
        public void run(){
            while(true){
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                seekbar.setProgress(mp.getCurrentPosition());
            }
        }
    }

    public String strtime(int duration){
        int min = (int) Math.floor(duration/(1000*60));
        int sec = (int) Math.floor((duration-min*1000*60)/1000);
        String s="";
        if(sec<10){
            s = "0";
        }
        return Integer.toString(min)+":"+s+Integer.toString(sec);
    }
    //폰트 적용
    public static void ApplyFonts(Context ct, TextView tv){
        Typeface face=Typeface.createFromAsset(ct.getAssets(),"fonts/BMHANNA_11yrs_ttf.mp3");
        tv.setTypeface(face);
    }

}
