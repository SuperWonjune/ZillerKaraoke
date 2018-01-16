package com.example.user.myapplication;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.Random;

public class ResultActivity extends AppCompatActivity {
    ImageView homebtn, restartbtn,plant_dog,score_first_digit,score_second_digit,score_100;
    int heart_num,score=50;
    GlideDrawableImageViewTarget ImageViewTarget;
    Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        homebtn=findViewById(R.id.result_home);
        restartbtn=findViewById(R.id.result_restart);
        plant_dog=findViewById(R.id.result_plant_dog);

        //SingingActivity로부터 하트 개수 가져오고 보이기
        Intent intent=getIntent();
        heart_num=intent.getIntExtra("heart_num",heart_num);
        score=intent.getIntExtra("score",score);
        Log.v("heart_num",String.valueOf(heart_num));
        heart_show(heart_num);

        //score보이기
        score_first_digit=findViewById(R.id.result_score_first_digit);
        score_second_digit=findViewById(R.id.result_score_second_digit);
        score_100=findViewById(R.id.result_score_100);
        score_show(score);

        //식물 gif 파일
        ImageViewTarget= new GlideDrawableImageViewTarget(plant_dog);

        //홈버튼 누를 시 홈으로 전환
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ResultActivity.this,MainActivity.class);
                intent.putExtra("heart_num",heart_num);
                startActivity(intent);
            }
        });

        //ChooseSong Activity로 전환
        restartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ResultActivity.this,ChooseSongActivity.class);
                intent.putExtra("heart_num",heart_num);
                startActivity(intent);
            }
        });
    }

    //하트 개수 보여주기
    void heart_show(int heart_num){
        ImageView heart1,heart2,heart3;
        heart1=findViewById(R.id.result_heart1);
        heart2=findViewById(R.id.result_heart2);
        heart3=findViewById(R.id.result_heart3);
        if(heart_num==1){
            heart1.setVisibility(View.VISIBLE);
        }
        else if(heart_num==2){
            heart1.setVisibility(View.VISIBLE);
            heart2.setVisibility(View.VISIBLE);
        }
        else if(heart_num==3){
            heart1.setVisibility(View.VISIBLE);
            heart2.setVisibility(View.VISIBLE);
            heart3.setVisibility(View.VISIBLE);
        }
        else if(heart_num==0){
            //하트 소진 시 로딩 화면으로 가서 다시 시작
            Intent intent=new Intent(ResultActivity.this, LoadingActivity.class);
            startActivity(intent);
        }
    }
    //점수 보여주기
    void score_show(final int score){
        int second_digit,first_digit;
        second_digit=score/10;
        first_digit=score%10;

        if(score==100){
            handler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    //handler1.removeCallbacksAndMessages(null);
                    handler.removeCallbacks(this);
                    score_100.setVisibility(View.VISIBLE);
                }
            },2000);
        }
        else {
            score_second_digit.setImageResource(getResources().getIdentifier("num" + String.valueOf(second_digit), "drawable", getPackageName()));
            score_first_digit.setImageResource(getResources().getIdentifier("num" + String.valueOf(first_digit), "drawable", getPackageName()));


            score_first_digit.setVisibility(View.VISIBLE);
            handler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    //handler1.removeCallbacksAndMessages(null);
                    handler.removeCallbacks(this);
                    score_second_digit.setVisibility(View.VISIBLE);
                }
            },2000);

        }

        if(0<=score&&score<50){
            Glide.with(this).load(R.raw.dog_death).into(plant_dog);
        }
        else if(50<=score&&score<60){
            Glide.with(this).load(R.raw.dog_hungry_full).into(plant_dog);
        }
        else if(60<=score&&score<70){
            Glide.with(this).load(R.raw.dog_normal_full).into(plant_dog);
        }
        else if(70<=score&&score<80){
            Glide.with(this).load(R.raw.dog_singing_full).into(plant_dog);
        }
        else if(80<=score&&score<90){
            Glide.with(this).load(R.raw.dog_surprise_full).into(plant_dog);
        }
        else if(90<=score&&score<=100){
            Glide.with(this).load(R.raw.dog_satisfied_full).into(plant_dog);
        }

    }

}
