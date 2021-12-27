package com.example.swara_app.Fragments;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swara_app.Adapters.genersAdapter;
import com.example.swara_app.ArtistProfile;
import com.example.swara_app.CategoryWise;
import com.example.swara_app.LocalDatabaseHandler;
import com.example.swara_app.MusicPlay;
import com.example.swara_app.Adapters.ArtistAdapter;
import com.example.swara_app.Artists;
import com.example.swara_app.StartMusic;
import com.example.swara_app.R;
import com.example.swara_app.Adapters.RecentAdapter;
import com.example.swara_app.Adapters.TrendingAdapter;
import com.example.swara_app.Song;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.swara_app.HomePage.bottomNav;

public class HomeFragment extends Fragment implements com.example.swara_app.Adapters.genersAdapter.onGenersListener, TrendingAdapter.onTrendingListener, ArtistAdapter.onSingleArtistClickListener, RecentAdapter.OnSingleRecentItemClicked {

    public static ImageView play;
    TextView name, recentplay;
    EditText searchview;
    ConstraintLayout small_play;
    RecyclerView artistRecycler, trendingRecycler, recomendedRecycler, genersRecyclerView;
    RecyclerView.Adapter recomendedAdapter, genersAdapter, artistSongAdapter;
    Song recent;
    ArrayList<Song> artistList = new ArrayList<>();

    ArrayList<Artists> artistSongsList = new ArrayList<>();
    ArrayList<Song> trendingSongsList = new ArrayList<>();
    RecyclerView.Adapter trendingAdapter;
    private DatabaseReference mDatabase1;
    private LocalDatabaseHandler db;
    ArrayList<String> genersList = new ArrayList<>();
    ContentLoadingProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        play = view.findViewById(R.id.play);
        name = view.findViewById(R.id.name);
        small_play = view.findViewById(R.id.small_play);
        searchview = view.findViewById(R.id.searchbar);
        recentplay = view.findViewById(R.id.recentlyplayed);
        recomendedRecycler = view.findViewById(R.id.RecentlyPlayed);
        genersRecyclerView = view.findViewById(R.id.GenersRecycler);
        artistRecycler = view.findViewById(R.id.popularArtists);

        artistRecycler.setHasFixedSize(true);
        artistRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        trendingRecycler = view.findViewById(R.id.trending);

        LocalDatabaseHandler localDB = new LocalDatabaseHandler(getActivity());
        recent = localDB.getRecentMusic();

        if(recent == null)
            small_play.setVisibility(View.GONE);
        else
            small_play.setVisibility(View.VISIBLE);

        searchview.setVisibility(View.VISIBLE);
        if (searchview != null) {
            searchview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchview.setVisibility(View.GONE);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
                    bottomNav.setSelectedItemId(R.id.nav_search);
                }
            });
        }

        displayRecomendedSongs();
        displayTrendingSongs();
        displayArtists();
        displayGeners();


        if(MusicPlay.isMplaying == true)
            play.setImageResource(R.drawable.small_play);
        else
            play.setImageResource(R.drawable.small_pause);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MusicPlay.initialState)
                    startMusicPlayer();
                else{
                    if (MusicPlay.isMplaying){
                        play.setImageResource(R.drawable.small_pause);
                        MusicPlay.isMplaying = false;
                        MusicPlay.mplay.pause();
                    }
                    else {
                        MusicPlay.mplay.start();
                        MusicPlay.isMplaying = true;
                        play.setImageResource(R.drawable.small_play);
                    }
                }
            }
        });

        if(recent != null) {
            name.setText(recent.getSongName());
            small_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startMusicPlayer();
                }
            });
        }

        view.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey, download Beats Music app! Visit www.webtechy.online to download the app");
                startActivity(shareIntent);
            }
        });
        return  view;
    }

    private void startMusicPlayer() {
        LocalDatabaseHandler db = new LocalDatabaseHandler(getActivity());
        Intent toPlayer = new Intent(getActivity(), MusicPlay.class);
        toPlayer.putExtra("from", "HomeFragment");
        toPlayer.putExtra("recentSong", recent);
        toPlayer.putExtra("songsList", db.getRecentMusicList());
        startActivity(toPlayer);
    }

    private void displayArtists() {
        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Artists");
        if(mDatabase1 != null) {
            mDatabase1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Artists artists = ds.getValue(Artists.class);
                        artistSongsList.add(artists);
                    }
                    artistSongAdapter = new ArtistAdapter(artistSongsList, HomeFragment.this);
                    artistRecycler.setAdapter(artistSongAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            artistSongsList.add(new Artists("Artist Name", String.valueOf(R.drawable.music_logo), "Song name"));
            artistSongAdapter = new ArtistAdapter(artistSongsList, HomeFragment.this);
            artistRecycler.setAdapter(artistSongAdapter);
        }
    }

    private void displayGeners() {
        genersRecyclerView.setHasFixedSize(true);
        genersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));
        genersList.add("Kannada");
        genersList.add("Hindi");
        genersList.add("English");
        genersList.add("Tamil");
        genersList.add("Telugu");
        genersAdapter = new genersAdapter(genersList, this);
        genersRecyclerView.setAdapter(genersAdapter);
    }

    private void displayRecomendedSongs() {
        recomendedRecycler.setHasFixedSize(true);
        recomendedRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));
        db = new LocalDatabaseHandler(getActivity());
        recomendedAdapter = new RecentAdapter(db.updateList(artistList), HomeFragment.this, getActivity());
        recomendedRecycler.setAdapter(recomendedAdapter);
    }

    private void displayTrendingSongs() {
        trendingRecycler.setHasFixedSize(true);
        trendingRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("TrendingSongs");
        if(mDatabase1 != null) {
            mDatabase1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Song music = ds.getValue(Song.class);
                            trendingSongsList.add(music);
                    }
                    trendingAdapter = new TrendingAdapter(trendingSongsList, HomeFragment.this);
                    trendingRecycler.setAdapter(trendingAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        db = new LocalDatabaseHandler(getActivity());
        if(db.getRowCount().getCount() < 1){
            recentplay.setVisibility(View.GONE);
            recomendedRecycler.setVisibility(View.GONE);
        }
        else {
            recomendedRecycler.setVisibility(View.VISIBLE);
            recentplay.setVisibility(View.VISIBLE);
        }

    }



    @Override
    public void onGenersClick(int position) {
//        Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
        String songType = genersList.get(position);
        Intent geners = new Intent(getActivity(), CategoryWise.class);
        geners.putExtra("geners",songType);
        startActivity(geners);
    }

    @Override
    public void onTrendingClick(int position) {
//        startNewPlayer(position, trendingSongsList);
        StartMusic musicplayer = new StartMusic(position, trendingSongsList, getActivity());
        MusicPlay.initialState = true;
        musicplayer.startNewPlayer();


//        MusicPlay.initialState = true;
//        LocalDatabaseHandler localDB = new LocalDatabaseHandler(getActivity());
//        localDB.addRecentMusic(trendingSongsList.get(position), position);
//        Intent music = new Intent(getActivzity(), MusicPlay.class);
//        music.putExtra("Position", position);
////        music.putExtra("songUrl", musiclist.get(position));
//        music.putExtra("songImage", trendingSongsList.get(position).getSongImage());
//        music.putParcelableArrayListExtra("songsList", trendingSongsList);
//        startActivity(music);
    }

    @Override
    public void onArtistClick(int position) {
        String name = artistSongsList.get(position).getName();
        String photo = artistSongsList.get(position).getPhoto();
        Intent artist = new Intent(getActivity(), ArtistProfile.class);
        artist.putExtra("imageUrl", photo);
        artist.putExtra("artistName",name);
        startActivity(artist);
    }

    @Override
    public void onRecentMusicCLick(int position) {
        LocalDatabaseHandler localDB = new LocalDatabaseHandler(getActivity());
        ArrayList<Song> musiclist = localDB.getRecentMusicList();
        if (musiclist.get(position).getSongName().equals(recent.getSongName()))
            startMusicPlayer();
        else{
            startNewPlayer(position, musiclist);
            StartMusic musicplayer = new StartMusic(position, musiclist, getActivity());
            MusicPlay.initialState = true;
            musicplayer.startNewPlayer();
//            MusicPlay.initialState = true;
//            localDB.addRecentMusic(musiclist.get(position), position);
//            Intent musicActivity = new Intent(getActivity(), MusicPlay.class);
//            musicActivity.putExtra("Position", position);
//            musicActivity.putExtra("songImage", musiclist.get(position).getSongImage());
//            musicActivity.putParcelableArrayListExtra("songsList", musiclist);
//            startActivity(musicActivity);
        }
    }



    private void startNewPlayer(int position, ArrayList<Song> musiclist){
        MusicPlay.initialState = true;
        LocalDatabaseHandler localDB = new LocalDatabaseHandler(getActivity());
        localDB.addRecentMusic(musiclist.get(position), position);
        Intent musicActivity = new Intent(getActivity(), MusicPlay.class);
        musicActivity.putExtra("Position", position);
        musicActivity.putExtra("songImage", musiclist.get(position).getSongImage());
        musicActivity.putParcelableArrayListExtra("songsList", musiclist);
        startActivity(musicActivity);
    }
}
