package com.example.user.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    ImageView plant_dog, song_list;
    GlideDrawableImageViewTarget ImageViewTarget;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plant_dog=(ImageView)findViewById(R.id.main_plant_dog);

        //식물 gif 파일
        ImageViewTarget= new GlideDrawableImageViewTarget(plant_dog);
        Glide.with(this).load(R.raw.plant_dog).into(plant_dog);

        //식물 눌렀을 때 random 으로 말들 가져오기
        plant_dog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();
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
    }

    //폰트 적용
    public static void ApplyFonts(Context ct, TextView tv){
        Typeface face=Typeface.createFromAsset(ct.getAssets(),"fonts/BMHANNA_11yrs_ttf.mp3");
        tv.setTypeface(face);
    }

}
