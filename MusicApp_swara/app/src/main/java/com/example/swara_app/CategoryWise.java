package com.example.swara_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.swara_app.Adapters.ListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryWise extends AppCompatActivity implements ListAdapter.onSingleMusicClickListener{
    RecyclerView catTecycler;
    private DatabaseReference mDatabase;
    RecyclerView.Adapter listadapter;
    ArrayList<Song> musiclist;
    String gener;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_wise);

        catTecycler = findViewById(R.id.categoryRecycler);
        gener = getIntent().getStringExtra("geners");
        toolbar = findViewById(R.id.Toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(gener);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showRecyclerView();

    }

    private void showRecyclerView() {
        catTecycler.setHasFixedSize(true);
        musiclist = new ArrayList<>();
        catTecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Songs");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Song music = ds.getValue(Song.class);
                    if (gener.equals(music.getCategory()))
                        musiclist.add(music);
                }
                listadapter = new ListAdapter(musiclist, CategoryWise.this, getApplicationContext());
                catTecycler.setAdapter(listadapter);
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
