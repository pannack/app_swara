package com.example.swara_app.UserAuthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swara_app.R;
import com.example.swara_app.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Register extends AppCompatActivity {
    private static int SPLASH_VAR = 5000;
    private FirebaseAuth fbAuth;
    private EditText username, Email, Password, cpassword;
    private Button register;
    private TextView login;
    private String email, password, confirmPassword, Name;
    ProgressDialog registerDialog;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.name);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        cpassword = findViewById(R.id.cpassword);
        register = findViewById(R.id.register);
        fbAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.loginbutton);
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

     register.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             registerDialog = new ProgressDialog(Register.this);
             registerDialog.setCancelable(false);
             registerDialog.setMessage("Registering! Please wait");
//             registerDialog.show();
             register.setVisibility(View.INVISIBLE);
             progressBar.setVisibility(View.VISIBLE);
             if (!validateForm()) {
                 Toast.makeText(getApplicationContext(), "Please provide necessary fields", Toast.LENGTH_SHORT).show();
                 if (progressBar.getVisibility() == View.VISIBLE){
                     register.setVisibility(View.VISIBLE);
                     progressBar.setVisibility(View.GONE);
                     registerDialog.cancel();}
                 return;
             }

             fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     if(!task.isSuccessful())
                     {
                         if (progressBar.getVisibility() == View.VISIBLE){
                             register.setVisibility(View.VISIBLE);
                             progressBar.setVisibility(View.GONE);
                             registerDialog.cancel();}
                         Toast.makeText(getApplicationContext(), "Registration failed! Try again", Toast.LENGTH_SHORT).show();
                     }
                     else {
                         if (progressBar.getVisibility() == View.VISIBLE){
                             register.setVisibility(View.VISIBLE);
                             progressBar.setVisibility(View.GONE);
                             registerDialog.cancel();}
                         Toast.makeText(getApplicationContext(), "Registration successful!!", Toast.LENGTH_SHORT).show();
                         addUserToFirebase();
                     }
                 }
             });
         }

     });

     login.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             startActivity(new Intent(Register.this, login.class));
         }
     });
    }

    private void addUserToFirebase() {
        User user = new User(Name, email, false);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(fbAuth.getCurrentUser().getUid());
          if(ref != null) {
              ref.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()) {
                          fbAuth.signOut();
                          startActivity(new Intent(Register.this, login.class));
                          finish();
//                          updateUser(ref);
                      }
                  }
              }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                  }
              });
          }
    }

//    private void updateUser(DatabaseReference reference) {
//        DatabaseReference ref = reference;
//        if(ref != null) {
//            ref.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    userData = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
//                    User user = dataSnapshot.getValue(User.class);
//                    SharedPreferences.Editor editor = userData.edit();
//                    editor.putString(Username, user.getName());
//                    editor.putString(UserEmail, user.getEmail());
//                    editor.putBoolean(subscribe, false);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }

    private boolean validateForm() {
        boolean valid = true;

        Name = username.getText().toString();
        if (TextUtils.isEmpty(Name)) {
            username.setError("Name is required.");
            valid = false;
        } else {
            username.setError(null);
        }

        email = Email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Email.setError("Email is required.");
            valid = false;
        } else {
            Email.setError(null);
        }

        password = Password.getText().toString();
        confirmPassword = cpassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Password.setError("Required.");
            valid = false;
        } else {
            if(password.length() < 6){
                Password.setError("Atleast 6 characters required");
                valid = false;
            }
            else{
                if(!password.equals(confirmPassword)){
                    valid = false;
                    cpassword.setError("Password didn't match");
                }
                else
                    Password.setError(null);
            }
        }
        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        checkIfNetworkAvailable();
    }

//    private void checkIfNetworkAvailable() {
//        boolean haveConnectedWifi = false;
//        boolean haveConnectedMobile = false;
//
//        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        AvailableNetworkInfo mobile = cm.get;
//    }
}
