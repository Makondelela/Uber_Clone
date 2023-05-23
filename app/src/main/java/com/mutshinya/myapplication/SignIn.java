package com.mutshinya.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText edtEmail;
    private EditText edtPass;
    private TextView logError;
    private CardView cardView;

    private long pressedTime;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        auth = FirebaseAuth.getInstance();
        edtEmail = findViewById(R.id.inputEmail);
        edtPass = findViewById(R.id.inputPassword);
        Button login = findViewById(R.id.btnSignin);
        cardView = findViewById(R.id.cardV1);
        logError = findViewById(R.id.logError);
        ImageView remove = findViewById(R.id.moses);
        TextView createAccount = findViewById(R.id.textCreateNewAccount);


        cardView.setVisibility(View.INVISIBLE);


        edtPass.setOnTouchListener((view, motionEvent) -> {
            cardView.setVisibility(View.INVISIBLE);
            return false;
        });

        edtEmail.setOnTouchListener((view, motionEvent) -> {
            cardView.setVisibility(View.INVISIBLE);
            return false;
        });

        remove.setOnClickListener(view -> cardView.setVisibility(View.INVISIBLE));



        login.setOnClickListener(view -> {

            String txt_email = edtEmail.getText().toString().trim();
            String txt_pass = edtPass.getText().toString().trim();
            if(TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_pass)){
                cardView.setVisibility(View.VISIBLE);
                logError.setText("Empty credentials");
            }else{
                loginUser(txt_email, txt_pass);
            }
        });

        createAccount.setOnClickListener(view -> startActivity(new Intent(SignIn.this,SignUp.class)));
    }

    @SuppressLint("SetTextI18n")
    private void loginUser(String email, String pass) {
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser mCurrentUser = auth.getCurrentUser();
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText( SignIn.this, "Login successful", Toast.LENGTH_SHORT).show();
                        String userEmail = edtEmail.getText().toString();
                        Intent intent = new Intent(new Intent(SignIn.this,MapsActivity.class));
                        startActivity(intent);

                    } else if(Objects.requireNonNull(task.getException()).toString().contains("There is no user record")){
                        // If sign in fails, display a message to the user.
                        cardView.setVisibility(View.VISIBLE);
                        logError.setText("Email invalid, check your email and try again or Sign Up");


                    }else if(task.getException().toString().contains("The password is invalid")){
                        cardView.setVisibility(View.VISIBLE);
                        logError.setText("You have entered a wrong password ");

                    }else if(task.getException().toString().contains("due to many failed login attempts")){
                        cardView.setVisibility(View.VISIBLE);
                        logError.setText("You are Temporarily blocked due to too many login attempts try again after a few minutes");

                    }else if(task.getException().toString().contains("The email address is badly formatted")){
                        cardView.setVisibility(View.VISIBLE);
                        logError.setText("Please enter a valid email address");

                    }else if(task.getException().toString().contains("A network error")){
                        cardView.setVisibility(View.VISIBLE);
                        logError.setText("Network error, Check your internet connection and try again");
                    }else if(task.getException().toString().contains("Failed to connect to")){
                        cardView.setVisibility(View.VISIBLE);
                        logError.setText("Failed to fetch details, check your internet connection and try again");
                    }else{
                        cardView.setVisibility(View.VISIBLE);
                        logError.setText("" +task.getException());
                    }
                });
    }

    @Override
    public void onBackPressed() {


        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            //Set activity to a main activity
            Intent intent = new Intent(Intent.ACTION_MAIN);
            //Move to Home Screen
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
}