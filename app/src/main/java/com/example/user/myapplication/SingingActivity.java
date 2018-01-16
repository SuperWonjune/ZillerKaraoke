package com.example.user.myapplication;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class SingingActivity extends AppCompatActivity {
    ArrayList<SongListViewItem> songs;
    MediaPlayer mp;
    SeekBar seekbar;
    Thread seekbarthread = null;
    TextView totaltime, currenttime, title, artist,lyric;
    boolean ispaused;
    int position, width;
    ImageView albumArt;
    ImageView playbtn, stopbtn;
    ImageView plant_dog, note_rainbow1, note_rainbow2;
    int heart_num;

    // 음 높이 표시 관련
    TextView pitchText;
    TextView noteText;

    // 음성 input 계산 관련
    //

    /* 옥타브
    Great     -   1
    Small     -   2
    One-lined -   3
    Two-lined -   4
    Three-lined - 5
    Four-lined -  6

    A	55.00	110.00	220.00	440.00	880.00	1760.00
    C	65.41	130.81	261.63	523.25	1046.50	2093.00
    */

    float octav1_C_hz = 65.41f;
    int current_Octav = 0;

    //--------------------------------------------
    // 악보 기록 관련


    // 현재 녹음되고 있는 음의 높이
    private float currentPitch = 0;

    // 악보에서 현재 시간을 나타내는 파란색 막대의 위치
    private float xPositionOnNote = 0;
    private float yPositionOnNote = 50;

    private float xLengthInterval = 0;

    // 악보 정보가 그려질 parent layout의 변수
    private float xLengthOfParentLayout = 0;
    private float yLengthOfParentLayout = 0;

    // 타이머 관련 변수

    // get in ms
    private int score_time_duration = 10000;

    private Handler handler = new Handler();
    private Handler handler1 = new Handler();
    private Handler handler2 = new Handler();
    private Handler handler3 = new Handler();
    private int timer_fps = 30;

    private ImageView scoreBar;
    private RelativeLayout scoreLayout;

    private List<ImageView> bluePixelList = new ArrayList<>();

    GlideDrawableImageViewTarget ImageViewTarget;



    //--------------------------------------------

    // 가사 출력 관련 변수
    private StringBuilder lyricString = new StringBuilder();


    //--------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singing);
        Intent intent = getIntent();
        heart_num=intent.getIntExtra("heart_num",heart_num);
       // heart_show(heart_num);

        MyApplication myApp = new MyApplication(this);
        myApp.loadData();
        songs = myApp.getSongList();
        position = intent.getIntExtra("position", 1);

        seekbar = findViewById(R.id.singing_seekBar);
        currenttime = findViewById(R.id.singing_time_current);
        totaltime = findViewById(R.id.singing_time_total);
        title = findViewById(R.id.singing_title);
        artist = findViewById(R.id.singing_artist);
        ApplyFonts(this, title);
        ApplyFonts(this, artist);
        ApplyFonts(this, currenttime);
        ApplyFonts(this, totaltime);
        playbtn = findViewById(R.id.singing_play_button);
        stopbtn = findViewById(R.id.singing_stop_button);

        // 레이아웃에서 변수 받아오기
        pitchText = findViewById(R.id.pitchText);
        noteText = findViewById(R.id.codeText);
        scoreBar = findViewById(R.id.currentTimeBar);
        scoreLayout = findViewById(R.id.singing_note_background);
        lyric = findViewById(R.id.singing_lyrics);
        lyric.setMovementMethod(new ScrollingMovementMethod());

        ApplyFonts(this, lyric);

        //식물 gif 파일
        plant_dog = findViewById(R.id.main_plant_dog);
        ImageViewTarget = new GlideDrawableImageViewTarget(plant_dog);
        Glide.with(this).load(R.raw.dog_satisfied_full).into(plant_dog);

//        note_rainbow1 = findViewById(R.id.main_note_rainbow);
//        ImageViewTarget = new GlideDrawableImageViewTarget(note_rainbow1);
//        Glide.with(this).load(R.raw.note_rainbow).into(note_rainbow1);
//
//        note_rainbow2 = findViewById(R.id.main_note_rainbow2);
//        ImageViewTarget = new GlideDrawableImageViewTarget(note_rainbow2);
//        Glide.with(this).load(R.raw.note_rainbow).into(note_rainbow2);


        // 음악 재생
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;

        mp = new MediaPlayer();

        mp.setLooping(false);
        ispaused = false;

        preparesong(position);

        //시작 시 3초 후 자동재생
        music_play();

        //노래 완곡 시
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer m) {
                Toast.makeText(getApplicationContext(), "수고했어요!", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(SingingActivity.this, ResultActivity.class);
                startActivity(intent);

            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBarChangeListener());

        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop_show();
            }
        });


        // 2018.1.15 김원준 추가

        // 로컬 파일에서 가사 파일 받아와서 TextView에 표시

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("lyricExample.txt"),"euc-kr"));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                lyricString.append(mLine);
                lyricString.append('\n');
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error reading file!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
            lyric.setText((CharSequence) lyricString);
        }


        // 2018.1.13 김원준 추가

        // 목소리 인식 라이브러리 추가
        AudioDispatcher dispatcher =
                AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e){
                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {

                    // 현재 음 높이에 따른 악보 추가
                    @Override
                    public void run() {
                        processPitch(pitchInHz);
                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();


        // process bar 움직임을 위한 layout 길이의 측정
        // 레이아웃 pixel 측정을 위해 observe가 끝나고 실행
        scoreLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                scoreLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // relativeLayout x,y  길이 측정
                xLengthOfParentLayout = scoreLayout.getWidth(); //height is ready
                yLengthOfParentLayout = scoreLayout.getHeight();


                // 한번이동할때의 x 간격
                xLengthInterval = xLengthOfParentLayout / (( timer_fps) * (score_time_duration/1000));

                // 악보 녹음 등록
                handler.postDelayed(recordOnScore, 1000);
            }
        });



    }

    @Override
    protected void onStop() {
        super.onStop();
        stop_show();
    }

    // Pitch 정보를 받아와 현재 음 계산
    public void processPitch(float pitchInHz) {

        // 전역변수에 저장
        currentPitch = pitchInHz;

        // 몇옥타브인지 계산
        current_Octav = get_Octav(pitchInHz);

        float pitchInHz_divided = pitchInHz / (float)Math.pow(2, current_Octav-1);
        String note_code = "";

        if(pitchInHz_divided >= 65.41 && pitchInHz_divided < 73.42) {
            //C
            note_code = "C";
        }
        else if(pitchInHz_divided >= 73.42 && pitchInHz_divided < 82.41) {
            //D
            note_code = "D";
        }
        else if(pitchInHz_divided >= 82.41 && pitchInHz_divided < 87.31) {
            //E
            note_code = "E";
        }
        else if(pitchInHz_divided >= 87.31 && pitchInHz_divided < 98) {
            //F
            note_code = "F";
        }
        else if(pitchInHz_divided >= 98 && pitchInHz_divided < 110) {
            //G
            note_code = "G";
        }
        else if(pitchInHz_divided >= 110 && pitchInHz_divided < 123.47) {
            //A
            note_code = "A";
        }
        else if(pitchInHz_divided >= 123.47 && pitchInHz_divided < 130.81) {
            //B
            note_code = "B";
        }

        // 목소리 hz Display
        pitchText.setText("" + pitchInHz);

        int display_Octav = current_Octav + 1;

        noteText.setText(note_code + display_Octav);

    }

    // 음의 옥타브를 계산
    private int get_Octav(float pitchInHz) {
        int ret_Octav = 0;

        for (int i = 7; i > 0; i--) {
            if (pitchInHz < Math.pow(2,i-1) * octav1_C_hz) {
                ret_Octav = i;
            }
        }

        return ret_Octav-1;
    }


    // handler로 화면상의 process bar 지속 이동 구현
    private Runnable recordOnScore = new Runnable() {
        @Override
        public void run() {
            recordInputOnScore();

            // 함수 timer 딜레이
            handler.postDelayed(this,1000 / timer_fps);
        }
    };

    // Handler 타이머의 호출에 따라 일정시간동안 호출
    private void recordInputOnScore() {

        // progress bar 계속해서 오른쪽으로 이동시킴
        xPositionOnNote = xPositionOnNote + xLengthInterval;
        scoreBar.setX(xPositionOnNote);


        // 현재 bar의 위치, 제일 오른쪽으로 가면 다시 제일 왼쪽으로
        // 여태 그린 픽셀도 초기화
        if (xPositionOnNote >= xLengthOfParentLayout) {
            // 위치 초기화
            xPositionOnNote = 0;

            // 픽셀 모두 삭제
            bluePixelInitialize();
        }


        // y Location 현재 노트 위치에 따라 기록
        yPositionOnNote = yLengthOfParentLayout - currentPitch;

        // progress bar 위치에 노트 기록
        AddNoteOnScore();

    }

    private void bluePixelInitialize() {

        for (int i=0; i<bluePixelList.size(); i++) {
            // 픽셀 안보이게 설정
            bluePixelList.get(i).setVisibility(View.GONE);
        }

        // 배열 모든 요소 삭제
        bluePixelList.clear();
    }

    private void AddNoteOnScore(){
        // 상대 레이아웃 경로 설정
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.singing_note_background);
        // 파란색 픽셀 추가
        ImageView currentLocationPixel = new ImageView(this);
        // 픽셀 리스트에 추가
        bluePixelList.add(currentLocationPixel);
        currentLocationPixel.setImageResource(R.drawable.blue_pixel);
        rl.addView(currentLocationPixel);

        // 픽셀 크기 재설정
        currentLocationPixel.getLayoutParams().height = 15;
        currentLocationPixel.getLayoutParams().width = 15;
        currentLocationPixel.requestLayout();

        // 픽셀의 위치 설정
        currentLocationPixel.setX(xPositionOnNote);
        currentLocationPixel.setY(yPositionOnNote);

    }


    // to stop Handler
    //handler.removeCallback(runnable)


    // 김원준 추가 함수 종료
    // 나머진 너희들에게 맡긴다



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
    //노래 시작 시간 계산
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
    //정지 시 정지 및 알림창 띄우기
    void stop_show(){
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("경고!!");
        builder.setMessage("정지 할 시, 하트가 하나 소진돼요! " +
                "그래도 나갈래요?");
        builder.setNegativeButton("계속 부를래요",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"잘 생각했어요!",Toast.LENGTH_LONG).show();

                    }
                });
        builder.setPositiveButton("그만할래요",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(SingingActivity.this, ResultActivity.class);
                        heart_num--;
                        intent.putExtra("heart_num",heart_num);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"너무해요!",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();

    }
    //시작 시 3초 후 노래 자동재생
    void music_play() {
        final RelativeLayout count_layout=findViewById(R.id.singing_count_layout);
        final ImageView count1=(ImageView) findViewById(R.id.singing_num1);
        final ImageView count2=(ImageView) findViewById(R.id.singing_num2);
        final ImageView count3=(ImageView) findViewById(R.id.singing_num3);
        final Animation move= AnimationUtils.loadAnimation(this,R.anim.bigtosmall);
        final Animation move2= AnimationUtils.loadAnimation(this,R.anim.bigtosmall);
        final Animation move3= AnimationUtils.loadAnimation(this,R.anim.bigtosmall);
        count3.setAnimation(move);

        handler1.postDelayed(new Runnable(){
            @Override
            public void run() {
                //handler1.removeCallbacksAndMessages(null);
                handler1.removeCallbacks(this);
                count2.setAnimation(move2);
            }
        },1000);

        handler2.postDelayed(new Runnable(){
            @Override
            public void run() {
                handler2.removeCallbacks(this);
                count1.setAnimation(move3);
            }
        },2000);
        handler3.postDelayed(new Runnable(){
            public void run(){
                handler3.removeCallbacks(this);
                count_layout.setVisibility(View.INVISIBLE);
            }
        },3000);


        //count.setImageResource(R.drawable.anim);
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ispaused = false;
                mp.start();
                if (seekbarthread == null) {
                    playbtn.setSelected(true);
                    Thread seekbarthread = new seekbarThread();
                    seekbarthread.start();
                }
            }
        };
        handler.sendEmptyMessageDelayed(0,3000);
    }
    //노래 끝날 시 다음 곡 자동 재생
    void music_play_next(){
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
    }
    //실행 버튼 누를 시 노래 시작
    void music_play_button(){
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
    }

}
