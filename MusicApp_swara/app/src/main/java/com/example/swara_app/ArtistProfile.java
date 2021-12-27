package com.example.swara_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swara_app.Adapters.ListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArtistProfile extends AppCompatActivity implements ListAdapter.onSingleMusicClickListener {
    RecyclerView artistSong;
    ArrayList<Song> musiclist;
    RecyclerView.Adapter listadapter;
    private DatabaseReference mDatabase;
    ImageView artistPhoto;
    TextView name;
    String artistname;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_profile);
        artistSong = findViewById(R.id.artistSongs);
        artistPhoto = findViewById(R.id.artistPromoImage);
        name = findViewById(R.id.artistPromoName);
        artistname = getIntent().getStringExtra("artistName");

        toolbar = findViewById(R.id.Toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(artistname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        name.setText(artistname);
        Picasso.get().load(getIntent().getStringExtra("imageUrl")).into(artistPhoto);
        showRecyclerView();

    }

    private void showRecyclerView() {
        artistSong.setHasFixedSize(true);
        musiclist = new ArrayList<>();
        artistSong.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Songs");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Song music = ds.getValue(Song.class);
                    if (artistname.equals(music.getArtist()))
                        musiclist.add(music);
                }
                listadapter = new ListAdapter(musiclist, ArtistProfile.this, getApplicationContext());
                artistSong.setAdapter(listadapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Something went wrong!", databaseError.toString());
            }
        });
    }

    @Override
    public void onSingleMusicClick(int position) {
        MusicPlay.initialState = true;
        StartMusic startMusic = new StartMusic(position, musiclist, getApplicationContext());
        startMusic.startNewPlayer();
//        LocalDatabaseHandler localDB = new LocalDatabaseHandler(getApplicationContext());
//        localDB.addRecentMusic(musiclist.get(position), position);
//        Intent music = new Intent(getApplicationContext(), MusicPlay.class);
//        music.putExtra("Position", position);
////        music.putExtra("songUrl", musiclist.get(position));
//        music.putExtra("songImage", musiclist.get(position).getSongImage());
//        music.putParcelableArrayListExtra("songsList", musiclist);
//        startActivity(music);
    }
}
