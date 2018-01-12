package com.example.user.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

public class LoadingActivity extends Activity {
    RelativeLayout layout1,layout2;
    Button btnsub;
    Animation uptodown,downtoup;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        layout1=(RelativeLayout)findViewById(R.id.loading_uplayout);
        layout2=(RelativeLayout)findViewById(R.id.loading_downlayout);

        uptodown= AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup= AnimationUtils.loadAnimation(this,R.anim.righttoleft);

        Handler handler=new Handler();

        layout2.setAnimation(downtoup);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                layout1.setAnimation(uptodown);
            }
        },1000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);



    }
}
