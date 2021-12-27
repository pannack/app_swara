package com.example.swara_app;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {

    String songImage;
    String songName;
    String songUrl;
    String category;
    String artist, movieName;
    String duration;

    public Song() {
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Song(String songImage, String songName, String songUrl) {
        this.songImage = songImage;
        this.songName = songName;
        this.songUrl = songUrl;
    }

    public Song(String songImage, String songName, String songUrl, String artist, String duration) {
        this.songImage = songImage;
        this.songName = songName;
        this.songUrl = songUrl;
        this.artist = artist;
        this.duration = duration;
    }

    public Song(String category, String songImage, String songName, String songUrl, String artist, String movieName) {
        this.songImage = songImage;
        this.songName = songName;
        this.songUrl = songUrl;
        this.category = category;
        this.artist = artist;
        this.movieName = movieName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public Song(String songImage, String songName) {
        this.songImage = songImage;
        this.songName = songName;
    }

    protected Song(Parcel in) {
        songImage = in.readString();
        songName = in.readString();
        songUrl = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getCategory() {
        return category;
    }

    public void setCategory(String songPosition) {
        this.category = songPosition;
    }

    public String getSongImage() {
        return songImage;
    }

    public void setSongImage(String songImage) {
        this.songImage = songImage;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(songImage);
        parcel.writeString(songName);
        parcel.writeString(songUrl);
    }
}
