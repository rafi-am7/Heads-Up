package com.example.quizme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.quizme.Classes.User;
import com.example.quizme.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore database;
    ProgressDialog dialog;
    String uid;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating a new account...");


        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser();

            }
        });
        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                finish();
            }
        });


    }
    public void registerUser() {
        final String email, fullName, password,phoneNumber;
        email = binding.userEmailSU.getText().toString().trim();//trim spaces may be
        fullName = binding.userFullNameSU.getText().toString().trim();
        password = binding.userPasswordSU.getText().toString().trim();
        phoneNumber= binding.userPhoneSU.getText().toString().trim();

        if (fullName.isEmpty()) {
            binding.userFullNameSU.setError("Please Enter Full Name!");
            binding.userFullNameSU.requestFocus(); // showing automatically
            return;
        }

        if (email.isEmpty()) {
            binding.userEmailSU.setError("Please Enter Email Address!");
            binding.userEmailSU.requestFocus(); // showing automatically
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.userEmailSU.setError("Please Enter a Valid Email Address!");
            binding.userEmailSU.requestFocus(); // showing automatically
            return;
        }
        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            binding.userPhoneSU.setError("Please Enter a Valid  Phone Number!");
            binding.userPhoneSU.requestFocus(); // showing automatically
            return;
        }
        if (password.length() < 6) {
            binding.userPasswordSU.setError("Minimum Password Length is 6 !");
            binding.userPasswordSU.requestFocus();
            return;
        }

        //binding.markerProgressSignUp.setVisibility(View.VISIBLE);
        //   Toast.makeText(this,gender+" "+membertype,Toast.LENGTH_SHORT).show();
        dialog.show();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User userObj = new User( fullName,email,phoneNumber, password);
                    uid = task.getResult().getUser().getUid();

                    database
                            .collection("users")
                            .document(uid)
                            .set(userObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        dialog.dismiss();
                                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                    Toast.makeText(SignupActivity.this,"A verification link has sent to your email",Toast.LENGTH_LONG).show();
                                                else
                                                    Toast.makeText(SignupActivity.this,"Please try again later.",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        // Intent intent =new Intent(SignupActivity.this, MainActivity.class);
                                        //intent.putExtra("uid", uid);
                                        // startActivity(intent);

                                        //finish();
                                    } else {
                                        Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                    binding.userFullNameSU.getText().clear();
                    binding.userEmailSU.getText().clear();
                    binding.userPhoneSU.getText().clear();
                    binding.userPasswordSU.getText().clear();







                } else {
                    //  binding.markerProgressSignUp.setVisibility(View.GONE);
                    binding.errorMessageText.setText("You are already registered!");
                    binding.errorMessageText.setTextColor(ContextCompat.getColor(SignupActivity.this, R.color.color_wrong));
                    binding.errorMessageText.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
}