package com.example.user.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ResultActivity extends AppCompatActivity {
    ImageView homebtn, restartbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        homebtn=findViewById(R.id.result_home);
        restartbtn=findViewById(R.id.result_restart);
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ResultActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        restartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ResultActivity.this,ChooseSongActivity.class);
                startActivity(intent);
            }
        });
    }
}
