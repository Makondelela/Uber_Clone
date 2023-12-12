package com.mutshinya.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;

import Models.Users;


public class SignUp extends AppCompatActivity {


    String firstName,lastName,userName, email, password;

    private EditText Email,name,surname,username;
    public FirebaseDatabase database;
    public DatabaseReference myRef;
    private EditText pass,confPass;
    private FirebaseAuth mAuth;
    private CardView progressCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        Email = findViewById(R.id.email);
        name = findViewById(R.id.inputName);
        surname = findViewById(R.id.inputLastName);
        username = findViewById(R.id.inputUserName);
        pass = findViewById(R.id.pass);
        confPass = findViewById(R.id.confirmPass);
        Button register1 = findViewById(R.id.register1);
        TextView signIn = findViewById(R.id.signIn);
        progressCardView = findViewById(R.id.cardV2);
        progressCardView.setVisibility(View.GONE);

        signIn.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this,SignIn.class);
            startActivity(intent);
        });

        register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressCardView.setVisibility(View.VISIBLE);
                firstName = name.getText().toString().trim();
                lastName = surname.getText().toString().trim();
                userName = username.getText().toString().trim();
                email = Email.getText().toString().trim();
                password = pass.getText().toString().trim();
                String txt_conf = confPass.getText().toString().trim();


                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || firstName.isEmpty() || lastName.isEmpty()
                        || userName.isEmpty()) {
                    progressCardView.setVisibility(View.GONE);
                    Toast.makeText(SignUp.this, "Empty credentials", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    progressCardView.setVisibility(View.GONE);
                    Toast.makeText(SignUp.this, "Password is too short", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(txt_conf)) {
                    progressCardView.setVisibility(View.GONE);
                    Toast.makeText(SignUp.this, "Password does not match" + password + "confir  " + txt_conf, Toast.LENGTH_SHORT).show();
                } else if (userName.length() < 6) {
                    progressCardView.setVisibility(View.GONE);
                    Toast.makeText(SignUp.this, "userName is too short", Toast.LENGTH_SHORT).show();

                } else {


                    registerUser(email,password);
                    saveUserData();

                }

            }
        });

    }

    public void registerUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Toast.makeText(SignUp.this, "Authentication successful",
                        Toast.LENGTH_SHORT).show();
                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userName)
                        .build();

                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                firebaseUser.updateProfile(userProfileChangeRequest);

                startActivity(new Intent(SignUp.this,MapsActivity.class));
                progressCardView.setVisibility(View.GONE);

            } else {
                progressCardView.setVisibility(View.GONE);
                // If sign in fails, display a message to the user.
                if(Objects.requireNonNull(task.getException()).toString().contains("The email address is badly formatted")){
                    Toast.makeText(SignUp.this, "Authentication failed,\nCheck your email address and try again",
                            Toast.LENGTH_SHORT).show();
                }else if(task.getException().toString().contains("A network error")){
                    Toast.makeText(SignUp.this, "Network error,\nCheck your internet connection and try again",
                            Toast.LENGTH_SHORT).show();

                }else if(task.getException().toString().contains("Collision")){
                    Toast.makeText(SignUp.this, "Email already in use ,\nreset password",
                            Toast.LENGTH_SHORT).show();
                } else{

                    Toast.makeText(SignUp.this, "Authentication failed."+task.getException(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    public  void saveUserData(){

        String passW  = "Hidden";

        Users users = new Users(firstName,lastName,userName, email,passW);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Models");

        myRef.child(userName).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Email.setText("");
                name.setText("");
                surname.setText("");
                username.setText("");
                name.setText("");
                pass.setText("");
                confPass.setText("");
            }
        });

    }
}
