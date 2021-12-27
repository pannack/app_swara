package com.example.swara_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.example.swara_app.UserAuthentication.login;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    public ArrayAdapter<String> adapter;
    private FirebaseAuth fb;

    public static final String NightMode = "NightMode" ;
    public static final String ISNIGHTMODE = "Activated";
    public static final String WALKTHROUGH = "walkthroughSeen";
    SharedPreferences sharedpreferences;

    private static int SPLASH_VAR = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fb = FirebaseAuth.getInstance();

        sharedpreferences = getSharedPreferences(NightMode, Context.MODE_PRIVATE);
        checkIfNightModeActivated();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fb.getCurrentUser() ==null) {
                    sharedpreferences = getSharedPreferences(WALKTHROUGH, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    Intent next;
                    next = new Intent(MainActivity.this, login.class);
                    editor.putBoolean(WALKTHROUGH, true);
                    editor.apply();
                    startActivity(next);
                    finish();
                }
                else {

                }
                }
            }, SPLASH_VAR);
    }

    public void checkIfNightModeActivated(){
        if (sharedpreferences.getBoolean(ISNIGHTMODE, false)){

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
