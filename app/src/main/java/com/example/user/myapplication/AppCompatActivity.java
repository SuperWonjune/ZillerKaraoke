package com.example.user.myapplication;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by user on 2018-01-12.
 */

public class AppCompatActivity  extends Activity
{
    private static Typeface typeface=null;
    public void setContentView(int layoutResID){
        super.setContentView(layoutResID);
        if(AppCompatActivity.typeface==null) {
            AppCompatActivity.typeface = Typeface.createFromAsset(this.getAssets(), "fonts/BMHANNA_11yrs_ttf.mp3");
        }
        ViewGroup root=(ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
    }

    void setGlobalFont(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
                ((TextView)child).setTypeface(typeface);
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup)child);
        }
    }

}
