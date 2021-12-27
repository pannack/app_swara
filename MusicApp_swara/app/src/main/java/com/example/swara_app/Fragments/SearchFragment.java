package com.example.swara_app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swara_app.MusicPlay;
import com.example.swara_app.StartMusic;
import com.example.swara_app.R;
import com.example.swara_app.Adapters.ListAdapter;
import com.example.swara_app.Song;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements ListAdapter.onSingleMusicClickListener {
    RecyclerView listRecycler;
    RecyclerView.Adapter listadapter;
    ArrayList<Song> musiclist;
    TextView query_view;
    private Query mDatabase;
    EditText searchView;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        listRecycler = view.findViewById(R.id.musiclistRecycler);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Songs");
        searchView = view.findViewById(R.id.search);
        query_view = view.findViewById(R.id.query_text);
        listRecycler.setHasFixedSize(true);
        musiclist = new ArrayList<>();
        listRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recycler();

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence!= null && charSequence.length() >= 1){
                    query_view.setText(String.format("%s%s", getResources().getString(R.string.results), charSequence.toString()));
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Songs").orderByChild("songName").startAt(charSequence.toString().toUpperCase()).endAt(charSequence.toString().toLowerCase() + "\uf8ff");
                    recycler();
                }else{
                    query_view.setText(R.string.all);
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Songs");
                    recycler();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return  view;
    }

    private void performSearch() {
        searchView.clearFocus();
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Songs");
        recycler();

    }

    public void recycler() {
        musiclist.clear();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Song music = ds.getValue(Song.class);
                    musiclist.add(music);
                }
                listadapter = new ListAdapter(musiclist, SearchFragment.this, getActivity());
                listRecycler.setAdapter(listadapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               Log.d("Database error", databaseError.toString());

            }
        });
    }

    @Override
    public void onSingleMusicClick(int position) {

        StartMusic startMusic = new StartMusic(position, musiclist, getActivity());
        MusicPlay.initialState = true;
        startMusic.startNewPlayer();

//        LocalDatabaseHandler localDB = new LocalDatabaseHandler(getActivity());
//        localDB.addRecentMusic(musiclist.get(position), position);
//        Intent music = new Intent(getActivity(), MusicPlay.class);
//        music.putExtra("Position", position);
////        music.putExtra("songUrl", musiclist.get(position));
//        music.putExtra("songImage", musiclist.get(position).getSongImage());
//        music.putParcelableArrayListExtra("songsList", musiclist);
//        startActivity(music);

    }
}
