package com.example.swara_app;

import android.os.Parcel;
import android.os.Parcelable;

public class Artists implements Parcelable {

    String Name;
    String Photo;
    String songName;

    public Artists() {
    }

    public Artists(String name, String photo) {
        Name = name;
        Photo = photo;
    }

    public Artists(String name) {
        Name = name;
    }

    public Artists(String name, String photo, String songName) {
        Name = name;
        Photo = photo;
        this.songName = songName;
    }

    protected Artists(Parcel in) {
        Name = in.readString();
        Photo = in.readString();
        songName = in.readString();
    }

    public static final Creator<Artists> CREATOR = new Creator<Artists>() {
        @Override
        public Artists createFromParcel(Parcel in) {
            return new Artists(in);
        }

        @Override
        public Artists[] newArray(int size) {
            return new Artists[size];
        }
    };

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Name);
        parcel.writeString(Photo);
        parcel.writeString(songName);
    }
}
