package com.example.swara_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.swara_app.Fragments.HomeFragment;
import com.example.swara_app.Fragments.LibraryFragment;
import com.example.swara_app.Fragments.SearchFragment;
import com.example.swara_app.Fragments.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class HomePage extends AppCompatActivity {

    public static BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        bottomNav = findViewById(R.id.bottom_nav);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        bottomNav.setOnNavigationItemSelectedListener(navListener);

    }

    @Override
    protected void onStart() {
        if (!isOnline()){
            View context = findViewById(R.id.bottom_nav);
            Snackbar.make(context, "No internet connection", Snackbar.LENGTH_LONG).setAction("Go to Library", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new LibraryFragment()).commit();
                    bottomNav.setSelectedItemId(R.id.nav_library);
                }
            }).setAnchorView(bottomNav).show();
        }
        super.onStart();
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo !=null && networkInfo.isConnectedOrConnecting())
            return true;
        else
            return false;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selected = null;

                    switch (item.getItemId())
                    {
                        case  R.id.nav_home:
                            selected = new HomeFragment();
                            break;

                        case  R.id.nav_setting:
                            selected = new SettingFragment();
                            break;

                        case  R.id.nav_search:
                            selected = new SearchFragment();
                            break;

                        case  R.id.nav_library:
                            selected = new LibraryFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected).commit();
                    return true;
                }
            };

    @Override
    public void onBackPressed() {
        if (bottomNav.getSelectedItemId() != R.id.nav_home){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
        bottomNav.setSelectedItemId(R.id.nav_home);}
        else
            super.onBackPressed();
    }
}
