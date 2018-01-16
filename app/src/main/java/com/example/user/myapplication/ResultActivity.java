package com.example.user.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ResultActivity extends AppCompatActivity {
    ImageView homebtn, restartbtn;
    int heart_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        homebtn=findViewById(R.id.result_home);
        restartbtn=findViewById(R.id.result_restart);

        Intent intent=getIntent();
        heart_num=intent.getIntExtra("heart_num",heart_num);
        Log.v("heart_num",String.valueOf(heart_num));
        heart_show(heart_num);

        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ResultActivity.this,MainActivity.class);
                intent.putExtra("heart_num",heart_num);
                startActivity(intent);
            }
        });

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

}
