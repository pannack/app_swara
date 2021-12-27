package com.example.swara_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class LocalDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Cache";
    private static final String TABLE_RECENT = "recentSongs";
    private static final String SONG_NAME = "songName";
    private static final String SONG_URL = "songUrl";
//    private static final String SONG_POSITION = "songPosition";
    private static final String SONG_IMAGE_URL = "SongImageUrl";

    private static final String TABLE_LIKE = "likedSongs";
    public Context context;

    public LocalDatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_RECENT_TABLE = "CREATE TABLE " + TABLE_RECENT + "(" + SONG_NAME + " TEXT PRIMARY KEY," + SONG_URL + " TEXT," + SONG_IMAGE_URL + " TEXT" +  ")";
        sqLiteDatabase.execSQL(CREATE_RECENT_TABLE);
        String CREATE_LIKE_TABLE = "CREATE TABLE " + TABLE_LIKE + "(" + SONG_NAME + " TEXT PRIMARY KEY," + SONG_URL + " TEXT," + SONG_IMAGE_URL + " TEXT" +  ")";
        sqLiteDatabase.execSQL(CREATE_LIKE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LIKE);
        onCreate(sqLiteDatabase);
    }

    public void addRecentMusic(Song music, int position){
//        String musicPosition = String.valueOf(position);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_RECENT, new String[]{SONG_NAME, SONG_URL, SONG_IMAGE_URL}, SONG_NAME + "=?",
                new String[]{music.getSongName()}, null, null, null, null);

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            db.delete(TABLE_RECENT, SONG_NAME + " =?", new String[]{music.getSongName()});
        }
        ContentValues values = new ContentValues();
        values.put(SONG_NAME, music.getSongName());
        values.put(SONG_URL, music.getSongUrl());
        values.put(SONG_IMAGE_URL, music.getSongImage());
//        values.put(SONG_POSITION, musicPosition);
        db.insert(TABLE_RECENT, null, values);
        db.close();
        refreshTable();
    }

//    This function is need to be implemented

    private void refreshTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = getRowCount();
        int rowCount = cursor.getCount();
        if(rowCount > 10){
            for (int i=0; i < rowCount-10; i++){
                cursor.moveToFirst();
                db.delete(TABLE_RECENT, SONG_NAME + " = ?",
                        new String[] { cursor.getString(0) });
            }
        }
        cursor.close();
    }

    public Cursor getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RECENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor;
    }

    public Song getRecentMusic(){
        SQLiteDatabase db = this.getReadableDatabase();
        Song music;
        String selectQuery = "SELECT  * FROM " + TABLE_RECENT;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            music = new Song(cursor.getString(2), cursor.getString(0), cursor.getString(1));
            return music;
        }
        else {
            return null;
        }
    }

    public Song getMusic(String musicName){
        SQLiteDatabase db = this.getReadableDatabase();
        Song music;
        String selectQuery = "SELECT  * FROM " + TABLE_RECENT + " WHERE " + SONG_NAME + " = " + musicName + ";";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            music = new Song(cursor.getString(2), cursor.getString(0), cursor.getString(1));
            return music;
        }
        else {
            return null;
        }
    }

    public ArrayList<Song> updateList(ArrayList<Song> artistList) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_RECENT;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToLast())
        {
            do {
                artistList.add(new Song(cursor.getString(2), cursor.getString(0), cursor.getString(1)));
            }while (cursor.moveToPrevious());
        }
        return artistList;
    }

    public ArrayList<Song> getRecentMusicList() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_RECENT;
        ArrayList<Song> recentList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToLast();
        do
        {
            recentList.add(new Song(cursor.getString(2), cursor.getString(0), cursor.getString(1)));
        }while (cursor.moveToPrevious());
        return recentList;
    }

    public void addLikedSongs(Song music){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SONG_NAME, music.getSongName());
        values.put(SONG_URL, music.getSongUrl());
        values.put(SONG_IMAGE_URL, music.getSongImage());
        db.insert(TABLE_LIKE, null, values);
        db.close();
    }

    public void deleteLikedSongs(Song music){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LIKE, SONG_NAME+ " = ?",
                new String[] { music.getSongImage() });
    }

    public boolean checkIfSongLiked(Song music)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LIKE, new String[]{SONG_NAME}, SONG_NAME + "=?",
                new String[]{music.getSongName()}, null, null, null, null);

        if(cursor.getCount() > 0)
            return true;
        else
            return false;
    }

}
