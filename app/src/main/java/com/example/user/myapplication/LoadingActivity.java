package com.example.user.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class LoadingActivity extends Activity {
    RelativeLayout layout1,layout2;
    RelativeLayout LoadingMainLayout;
    Button btnsub;
    Animation uptodown,downtoup;


    // 권한 요청
    //static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    //static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 2;
    static final int MULTIPLE_PERMISSIONS = 10;

    String[] permissions= new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);
        layout1=(RelativeLayout)findViewById(R.id.loading_uplayout);
        layout2=(RelativeLayout)findViewById(R.id.loading_downlayout);

        uptodown= AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup= AnimationUtils.loadAnimation(this,R.anim.righttoleft);





        // 권한 확인
        checkPermissions();


        // 애니메이션 등록
        Handler handler=new Handler();

        layout2.setAnimation(downtoup);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                layout1.setAnimation(uptodown);
            }
        },1000);



        // 화면을 클릭하면 다음 Activity로 이동
        layout1.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent=new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
        layout2.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent=new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }


    // 권한 요청
    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    // 권한 요청 응답
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.
                } else {

                }
                return;
            }
        }
    }
}
