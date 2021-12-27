package com.example.swara_app.UserAuthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swara_app.HomePage;
import com.example.swara_app.R;
import com.example.swara_app.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    private Button login;
    private FirebaseAuth fbAuth;
    private String email, password;
    private EditText Email, Password;
    public SharedPreferences userData;
    public static final String PREFERENCE_NAME = "UserDetails";
    public static final String Username = "UserName";
    public static final String UserEmail = "Email";
    public static final String subscribe = "Subscribed";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView skip, register;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        skip = findViewById(R.id.skip);
        register = findViewById(R.id.regsiterView);
        login = findViewById(R.id.Loginbutton);
        Email = findViewById(R.id.loginEmail);
        Password = findViewById(R.id.loginPassword);
        fbAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent homePage = new Intent(login.this, HomePage.class);
                startActivity(homePage);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerPage = new Intent(login.this, Register.class);
                startActivity(registerPage);
                finish();

            }
        });
    }

    private void signInUser() {
        login.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        if (!validateForm()) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                login.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
            Toast.makeText(getApplicationContext(), "Please provide necessary fields", Toast.LENGTH_SHORT).show();
            return;
        }
        fbAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (progressBar.getVisibility() == View.VISIBLE) {
                                login.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Successfully Logged in", Toast.LENGTH_LONG).show();
                            updateUser();
                        } else {
                            if (progressBar.getVisibility() == View.VISIBLE){
                                login.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                }
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updateUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(fbAuth.getCurrentUser().getUid());
            if(ref != null) {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        userData = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = userData.edit();
                        editor.putString(Username, user.getName());
                        editor.putString(UserEmail, user.getEmail());
                        editor.putBoolean(subscribe, user.isSubscribe());
                        editor.commit();
                        Intent i = new Intent(getApplicationContext(), HomePage.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
    }


    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = fbAuth.getCurrentUser();
        if(currentUser!=null) {
            Intent i = new Intent(getApplicationContext(), HomePage.class);
            startActivity(i);
        }
    }
    // [END on_start_check_user]

    private boolean validateForm() {
        boolean valid = true;
        email = Email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Email.setError("Email is required.");
            valid = false;
        } else {
            Email.setError(null);
        }

        password = Password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Password.setError("Required.");
            valid = false;
        } else {
            Password.setError(null);
        }
        return valid;
    }
}
