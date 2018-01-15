package com.example.user.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ImageView plant_dog, song_list;
    GlideDrawableImageViewTarget ImageViewTarget;
    Animation move;
    TextView speech_text;
    RelativeLayout layout;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plant_dog=(ImageView)findViewById(R.id.main_plant_dog);
        speech_text=findViewById(R.id.main_speech_text);
        //식물 gif 파일
        ImageViewTarget= new GlideDrawableImageViewTarget(plant_dog);
        Glide.with(this).load(R.raw.don_normal_really_final).into(plant_dog);
        layout=findViewById(R.id.main_plant_dog_layout);

        //식물 눌렀을 때 random 으로 말들 가져오기
        plant_dog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                random_toast();
            }
        });

        //song list 가져오기
        song_list=(ImageView)findViewById(R.id.main_song_list);
        song_list.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this, ChooseSongActivity.class);
                startActivity(intent);
            }
        });

        move= AnimationUtils.loadAnimation(this,R.anim.translate);
        layout.setAnimation(move);
    }

    //폰트 적용
    public static void ApplyFonts(Context ct, TextView tv){
        Typeface face=Typeface.createFromAsset(ct.getAssets(),"fonts/BMHANNA_11yrs_ttf.mp3");
        tv.setTypeface(face);
    }

    void random_toast(){

        Random random=new Random();
        int num=random.nextInt(9);
        String speech[]={"밥 주세요","노래불러줘요","음치가 아니길 바래요","제 이름은 뭔가요","학식 그만 먹고 싶어요",
                            "어은동 기기","링딩동링딩동링디리디리딩딩딩","매드캠프가 벌써 끝나가요","앙원준띠~","술마시러가요"};

        speech_text.setText(speech[num]);
        ApplyFonts(this,speech_text);


    }

}
