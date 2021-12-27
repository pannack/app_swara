package com.example.swara_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.swara_app.Fragments.HomeFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MusicPlay extends AppCompatActivity {
    ImageView musicControl, MusicImage, next, prev, forward, backward;
    String url, Musicname, from;
    String songImage;
    public static boolean isMplaying = false;
    public static boolean initialState = true;
    TextView MusicName;
    public static MediaPlayer mplay;
    Song music;
    ArrayList<Song> musiclist;
    ArrayList<Song> fetchedmusiclist;
    private ProgressDialog progressDialog;
    private Query mDatabase;
    private double startTime = 0;
    private double finalTime = 0;
    private int forwardTime = 10000;
    private int backwardTime = 10000;
    private Handler myHandler = new Handler();
    private SeekBar seekbar;
    Song recent;
    LocalDatabaseHandler localDB;
    int position;
    private ImageView goback;
    ConstraintLayout progress, content;
    private TextView startText, endText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        musicControl = findViewById(R.id.imageView4);
        MusicImage = findViewById(R.id.music_image);
        next = findViewById(R.id.playnext);
        prev = findViewById(R.id.playprevious);
        MusicName = findViewById(R.id.Musicname);
        MusicName.setSelected(true);
        seekbar = findViewById(R.id.seekbar);
        forward = findViewById(R.id.forwardFive);
        backward = findViewById(R.id.backwardFive);
        startText = findViewById(R.id.start);
        endText = findViewById(R.id.end);
        progress = findViewById(R.id.progressLayout);
        content = findViewById(R.id.contentLayout);
        progress.setVisibility(View.GONE);
        goback = findViewById(R.id.back);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlay.super.onBackPressed();
            }
        });


//        Receive Intent data
        from = getIntent().getStringExtra("from");
        position = getIntent().getIntExtra("Position", 0);
        recent = getIntent().getParcelableExtra("recentSong");
        musiclist = getIntent().getParcelableArrayListExtra("songsList");

        localDB = new LocalDatabaseHandler(getApplicationContext());
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        if (isMplaying)
            musicControl.setImageResource(R.drawable.group_34);
        else
            musicControl.setImageResource(R.drawable.pausemusic);

        if(musiclist == null)
            musiclist = fetchedmusiclist;

        if(recent != null)
            music = recent;
        else
            music = musiclist.get(position);

        if(initialState){
            if(isMplaying)
                mplay.stop();
            fetchSongDetails();
            new Player().execute();
            initialState = false;
            isMplaying = true;
        }
        else {
            Picasso.get().load(recent.getSongImage()).error(R.drawable.not_found).fit().centerCrop().into(MusicImage);
            MusicName.setText(recent.getSongName());
            updateSeekbar();
        }

//        if(from == null || from.isEmpty()) {
//            if (isMplaying == true) {
//                mplay.stop();
//                isMplaying = false;
//            }
//            fetchSongDetails();
//        }
//        else
//        {
//            Picasso.get().load(recent.getSongImage()).into(MusicImage);
//            MusicName.setText(recent.getSongName());
//            if(mplay == null){
//                music = recent;
//                if(!MusicPlay.isMplaying){
//                    new Player().execute();}
//            }
//            else
//                updateSeekbar();
//            if(isMplaying)
//            {
//                musicControl.setImageResource(R.drawable.group_34);
//            }
//            else {
//                musicControl.setImageResource(R.drawable.pausemusic);
//            }
//        }

//        db = new LocalDatabaseHandler(getApplicationContext());
//        if(db.checkIfSongLiked(music))
//            likeSong.setImageResource(R.drawable.dislike);
//        else
//            likeSong.setImageResource(R.drawable.like);


//        Controlling pause and play button
        musicControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mplay.isPlaying()) {
                    musicControl.setImageResource(R.drawable.pausemusic);
                    isMplaying = false;
                    mplay.pause();
                }
                else
                {
                    if(initialState){
                        if(isMplaying)
                            mplay.stop();
                        fetchSongDetails();
                        new Player().execute();
                        initialState = false;
                        isMplaying = true;
                    }else{
                    musicControl.setImageResource(R.drawable.group_34);
                    isMplaying = true;
                    mplay.start();}
                }
            }
        });

//        Next song button click listener
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(musiclist == null) {
//                    musiclist = fetchedmusiclist;
//                    music = recent;
////                    position = Integer.parseInt(recent.getSongPosition());
//                }
                if(musiclist != null)
                    if(position == musiclist.size() - 1)
                    position = -1;
                position = position + 1;
                mplay.stop();
                startTime = 0;
                fetchSongDetails();
                new Player().execute();
                localDB.addRecentMusic(music, position);

            }
        });

//        Previous button click listener
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(musiclist == null) {
//                    musiclist = fetchedmusiclist;
//                    music = recent;
////                    position = Integer.parseInt(recent.getSongPosition());
//                }
                if(position > 0)
                    position = position - 1;
                else
                    if (musiclist != null)
                        position = musiclist.size() - 1;
                mplay.stop();
                startTime = 0;
                fetchSongDetails();
                new Player().execute();
                localDB.addRecentMusic(music, position);
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = (int)startTime;
                if((temp+forwardTime)<=finalTime){
                    startTime = startTime + forwardTime;
                    mplay.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"You have Jumped forward 10 seconds",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Cannot jump forward",Toast.LENGTH_SHORT).show();
                }
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = (int)startTime;
                if((temp-backwardTime)>0){
                    startTime = startTime - backwardTime;
                    mplay.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"You have Jumped backward 10 seconds",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Cannot jump backward",Toast.LENGTH_SHORT).show();
                }
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                startText.setText(milliSecondsToTimer(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(MusicPlay.this, "Changed " + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
                mplay.seekTo( seekBar.getProgress());
            }
        });
    }

    public static String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }


    //    Fetching song details and performing Async Task
    private void fetchSongDetails() {
        mplay = new MediaPlayer();
        mplay.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if(musiclist != null)
            music = musiclist.get(position);
        songImage = music.getSongImage();
        url = music.getSongUrl();
        Musicname = music.getSongName();
    }
    
    

    //    Redirect to HomeFragment when back key is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (progress.getVisibility() == View.VISIBLE){
            progress.setVisibility(View.GONE);
            mplay.stop();
            isMplaying = false;
            initialState = true;
        }
        Intent homePlay = new Intent(MusicPlay.this, HomePage.class);
        homePlay.putExtra("songName", Musicname);
        startActivity(homePlay);
        finish();
        return;
    }


    //    Player Class
    class Player extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean prepared = false;
            try {
                mplay.setDataSource(url);
                mplay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        HomeFragment.play.setImageResource(R.drawable.small_pause);
//                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        isMplaying = false;
                        initialState = true;
                        musicControl.setImageResource(R.drawable.pausemusic);
                    }
                });
                mplay.prepare();
                prepared = true;

            } catch (Exception e) {
                Log.e("Exception---> ", "Error Occurred!!");
                prepared = false;
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }
            if (progress.getVisibility() == View.VISIBLE) {
                progress.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
            }
            mplay.start();
            isMplaying = true;
            Picasso.get().load(songImage).error(R.drawable.not_found).into(MusicImage);
            MusicName.setText(Musicname);
            musicControl.setImageResource(R.drawable.group_34);
            updateSeekbar();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please Wait...");
//            progressDialog.show();

            content.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
        }
    }

    private void updateSeekbar() {

        startTime = mplay.getCurrentPosition();
        finalTime = mplay.getDuration();
        startText.setText(milliSecondsToTimer(mplay.getCurrentPosition()));
        endText.setText(milliSecondsToTimer(mplay.getDuration()));
//        if(startTime == finalTime) {
//            musicControl.setImageResource(R.drawable.pausemusic);
//            mplay.stop();
//            isMplaying = false;
//            initialState = true;
//            Toast.makeText(getApplicationContext(), "Hello Ended", Toast.LENGTH_LONG).show();
//        }
//        else {
            seekbar.setMax((int) finalTime);
            seekbar.setProgress((int) startTime);
            myHandler.postDelayed(UpdateSongTime, 100);
//        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mplay.getCurrentPosition();
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        fetchSongList();
    }

    private void fetchSongList() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Songs");
        fetchedmusiclist = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Song music = ds.getValue(Song.class);
                    fetchedmusiclist.add(music);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Database error", databaseError.toString());
            }
        });
    }
}


