package com.example.swara_app.Fragments;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swara_app.Adapters.ListAdapter;
import com.example.swara_app.MusicPlay;
import com.example.swara_app.StartMusic;
import com.example.swara_app.R;
import com.example.swara_app.Song;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

import static com.example.swara_app.MusicPlay.milliSecondsToTimer;


public class LibraryFragment extends Fragment implements ListAdapter.onSingleMusicClickListener{

    private ArrayList<Song> musicList;
    RecyclerView playlist;
    ListAdapter listadapter;
    ConstraintLayout notFound;
    boolean granted;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        playlist = view.findViewById(R.id.localMusics);
        musicList = new ArrayList<>();
        notFound = view.findViewById(R.id.notfound);
        notFound.setVisibility(View.GONE);
        playlist.setHasFixedSize(true);
        playlist.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        if (checkPermission()) {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
            Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getColumnIndex(BaseColumns._ID);
                        long thisId = cursor.getLong(id);

                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//                        String category = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        String image = String.valueOf(ContentUris.withAppendedId(Uri.parse("content://media/external/albumart"), thisId));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String duration = milliSecondsToTimer(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                        musicList.add(new Song(image,name, data,artist, duration));
                    } while (cursor.moveToNext());
                }
            }
//        musicList.add(new MusicListRecycler("photo", "MusicName", "source"));
            listadapter = new ListAdapter(musicList, this, getActivity());
            playlist.setAdapter(listadapter);
            if (musicList.size() < 1)
                notFound.setVisibility(View.VISIBLE);
            cursor.close();

        }
        else
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        return  view;
    }

    private boolean checkPermission() {
        granted = false;
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
           granted = true;
        }
        else{
            Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    granted = true;
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    if (permissionDeniedResponse.isPermanentlyDenied()){
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Permission denied").setMessage("Location permission is permanently denied! Go to Settings and allow permission.").setNegativeButton("Cancel", null).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
                                startActivity(intent);
                            }
                        }).show();
                    }
                    else {
                        granted = false;
                        Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();
        }
        return granted= true;
    }

    @Override
    public void onSingleMusicClick(int position) {
        MusicPlay.initialState = true;
        StartMusic startMusic = new StartMusic(position, musicList, getActivity());
        startMusic.startNewPlayer();
//        LocalDatabaseHandler localDB = new LocalDatabaseHandler(getActivity());
//        localDB.addRecentMusic(musicList.get(position), position);
//        Intent music = new Intent(getActivity(), MusicPlay.class);
//        music.putExtra("Position", position);
////        music.putExtra("songUrl", musiclist.get(position));
//        music.putExtra("songImage", musicList.get(position).getSongImage());
//        music.putParcelableArrayListExtra("songsList", musicList);
//        startActivity(music);
    }


}