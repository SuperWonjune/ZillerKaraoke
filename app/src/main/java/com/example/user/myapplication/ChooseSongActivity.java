package com.example.user.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ChooseSongActivity extends AppCompatActivity {
    ArrayList<SongListViewItem> SongList;
    ListView listview;
    TextView activity_title;
    ImageView back_button;

    public void onListBtnClick(int position) {

    }

    public class MusicAdapter extends BaseAdapter {
        private Context mContext;
        MusicAdapter(Context c){
            mContext = c;
        }

        @Override
        public int getCount() {
            int i= 1;
            return SongList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.song_listitem, viewGroup, false);
            }
            TextView title = convertView.findViewById(R.id.song_title);
            TextView artist = convertView.findViewById(R.id.song_artist);
            ImageView albumart = convertView.findViewById(R.id.song_album_image);

            SongListViewItem s = SongList.get(i);

            title.setText(s.title);
            artist.setText(s.artist);

            ApplyFonts(mContext,title);
            ApplyFonts(mContext,artist);


            Bitmap b;

            if(s.albumCover != null){
                BitmapFactory.Options bfo = new BitmapFactory.Options();
                bfo.inSampleSize = 4;
                b = BitmapFactory.decodeFile(s.albumCover, bfo);
            } else {
                b = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_play);
            }
            b = Bitmap.createScaledBitmap(b, 200, 200, true);
            albumart.setImageBitmap(b);

            return convertView;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_song);

        back_button=(ImageView)findViewById(R.id.choose_song_back_button);
        activity_title=(TextView)findViewById(R.id.choose_song_title);
        ApplyFonts(this,activity_title);

        MyApplication myApp = new MyApplication(this);
        myApp.loadData();
        SongList = myApp.getSongList();

        listview = findViewById(R.id.choose_song_listview);
        listview.setAdapter(new MusicAdapter(this));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                SongListViewItem s = SongList.get(position);
                String path = s.data;
                Intent intent = new Intent(ChooseSongActivity.this, SingingActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChooseSongActivity.this,MainActivity.class);
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
