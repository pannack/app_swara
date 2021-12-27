package com.example.swara_app;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class StartMusic {
    int position;
    ArrayList<Song> musiclist;
    Context activity;

    public StartMusic(int position, ArrayList<Song> musiclist, Context activity) {
        this.position = position;
        this.musiclist = musiclist;
        this.activity = activity;
    }

    public void startNewPlayer(){
        MusicPlay.initialState = true;
        LocalDatabaseHandler localDB = new LocalDatabaseHandler(activity);
        Song playing = musiclist.get(position);
        localDB.addRecentMusic(playing, position);
        Intent musicActivity = new Intent(activity, MusicPlay.class);
        musicActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        musicActivity.putExtra(Intent.)
        musicActivity.putExtra("Position", position);
        musicActivity.putExtra("songImage", playing.getSongImage());
        musicActivity.putParcelableArrayListExtra("songsList", musiclist);

//        Notification notification = new Notification.Builder(activity).setContentTitle("Now playing").setContentText(playing.getSongName())
//                .setSmallIcon(R.drawable.app_logo).build();
//        notification.notify();
        activity.startActivity(musicActivity);
    }
}
