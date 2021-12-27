package com.example.swara_app.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import com.example.swara_app.ContactUs;
import com.example.swara_app.R;
import com.example.swara_app.UserAuthentication.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.swara_app.MainActivity.ISNIGHTMODE;
import static com.example.swara_app.MainActivity.NightMode;
import static com.example.swara_app.HomePage.bottomNav;
import static com.example.swara_app.UserAuthentication.login.PREFERENCE_NAME;
import static com.example.swara_app.UserAuthentication.login.UserEmail;
import static com.example.swara_app.UserAuthentication.login.Username;
import static com.example.swara_app.UserAuthentication.login.subscribe;


public class SettingFragment extends Fragment {
    public static String name="";
    Button b;

    private TextView  email, UserIntro;
    Button share, rate, contact,Logout;
    private SwitchCompat Subscribe;
    FirebaseAuth fbAuth;
    FirebaseUser user;
    SharedPreferences userData;
    SharedPreferences.Editor editor;
    AlertDialog.Builder builder;
    private DatabaseReference ref;
    SharedPreferences sharedpreferences;
    SwitchCompat darkTheme;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        fbAuth = FirebaseAuth.getInstance();
        share = view.findViewById(R.id.Shareapp);
        rate = view.findViewById(R.id.rateourapp);
        contact = view.findViewById(R.id.contactus);
        Subscribe = view.findViewById(R.id.switchCompat);
        email =  view.findViewById(R.id.usermail);
        user = fbAuth.getCurrentUser();
        Logout = view.findViewById(R.id.logout);
        UserIntro = view.findViewById(R.id.userIntro);
        darkTheme = view.findViewById(R.id.darkModeSwitch);

        sharedpreferences = getActivity().getSharedPreferences(NightMode, Context.MODE_PRIVATE);
        checkIfNightModeActivated();

        darkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (darkTheme.isChecked()){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    bottomNav.setSelectedItemId(R.id.nav_home);
                    saveNightMode(true);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    bottomNav.setSelectedItemId(R.id.nav_home);
                    saveNightMode(false);
                }
//                getActivity().recreate();
//                new SettingFragment();
            }
        });

        if(user != null){
            ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("subscribe");
            userData = getActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            email.setText(userData.getString(UserEmail,null));

            if(userData.getBoolean(subscribe, true))
                Subscribe.setChecked(true);
            else
                Subscribe.setChecked(false);
            editor = userData.edit();

            Logout.setText("Log Out");
            UserIntro.setText(userData.getString(Username, "Hello User"));
        }
        else {
            email.setText("Please Login");
            Logout.setText("Log In");
            UserIntro.setText("Hello User");
        }

        if(share != null) {
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey, download Swara Music app! Visit www.webtechy.online to download the app");
                    startActivity(shareIntent);
                }
            });
        }

        if(rate != null) {
            rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                    Intent market = new Intent(Intent.ACTION_VIEW, uri);
                    market.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(market);
                    }catch (ActivityNotFoundException e)
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/details?id=" + getContext().getPackageName())));
                    }


                }
            });
        }

        if(contact != null)
        {
            contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent next = new Intent(getActivity(), ContactUs.class);
                    startActivity(next);
                }
            });
        }

        if(Subscribe != null)
        {
            Subscribe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if(user == null){
                        showAlertDialog();
                        Subscribe.setChecked(false);
                    }
                    else {
                        if (Subscribe.isChecked()){
                            editor.putBoolean(subscribe, true);
                            ref.setValue(true);}
                        else {
                            editor.putBoolean(subscribe, false);
                            ref.setValue(false);
                        }
                        editor.apply();
                    }
                }
            });
        }

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fbAuth.getCurrentUser() != null)
                    showLogoutDialog();
                else {
                    Intent next = new Intent(getActivity(), login.class);
                    startActivity(next);
                }
            }
        });

        return  view;
    }

    private void saveNightMode(boolean mode) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(ISNIGHTMODE, mode);
        editor.apply();
    }

    public boolean checkIfNightModeActivated(){
        if (sharedpreferences.getBoolean(ISNIGHTMODE, false)){
            darkTheme.setChecked(true);
            return false;
        }
        else{
            darkTheme.setChecked(false);
            return true;}
    }

    private void showAlertDialog() {
        builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.image_icon);
        builder.setMessage("Please Login to continue");
        builder.setTitle("Swara");
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent next = new Intent(getActivity(), login.class);
                startActivity(next);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showLogoutDialog() {
        builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.image_icon);
        builder.setMessage("Do you really want to Logout?");
        builder.setTitle("Swara");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fbAuth.signOut();
                editor.clear();
                Intent next = new Intent(getActivity(), login.class);
                startActivity(next);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
