package com.example.quizme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.quizme.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

       auth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging in...");
        try {
            if (sharedPreferences.contains("Login")) {
                String loginStatus = sharedPreferences.getString("Login", "0");
                if(loginStatus.equals("1"))
                {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }


            }


        }catch (Exception e){

        }


        binding.logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInUser();
            }
        });

        binding.signUpButtonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });
        binding.forgetPassButtonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }
    public void logInUser() {
        String email, password, memType;
        email = binding.userEmailLogIn.getText().toString();
        password = binding.userPasswordLogIn.getText().toString();
        if (email.isEmpty()) {
            binding.userEmailLogIn.setError("Enter email!");
            binding.userEmailLogIn.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.userEmailLogIn.setError("Enter valid email!");
            binding.userEmailLogIn.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            binding.userPasswordLogIn.setError("Enter password!");
            binding.userPasswordLogIn.requestFocus();
            return;
        }
        //binding.markerProgressLogIn.setVisibility(View.VISIBLE);
        //mAuth.confirmPasswordReset("hellw",password);
        dialog.show();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {

                    if (!auth.getCurrentUser().isEmailVerified()) {
                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    binding.errorMessageTextLogin.setText("Account not verified. Verification link sent to your email.");
                                else
                                    binding.errorMessageTextLogin.setText("Error! Please try again!");
                            }
                        });
                        binding.errorMessageTextLogin.setText("Account not verified. Verification link sent to your email.");
                        binding.errorMessageTextLogin.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.color_error));
                        binding.errorMessageTextLogin.setVisibility(View.VISIBLE);
                        //binding.markerProgressLogIn.setVisibility(View.GONE);
                        return;

                    }
                    /*Share preference*/
                    editor.putString("Login", "1");
                    editor.commit();


                    //progressBar.setVisibility(View.GONE);
                    binding.errorMessageTextLogin.setText("Log in successful!");
                    binding.errorMessageTextLogin.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.color_success));
                    binding.errorMessageTextLogin.setVisibility(View.VISIBLE);
                    if(auth.getCurrentUser() != null) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                } else {
                    //progressBar.setVisibility(View.GONE);
                    binding.errorMessageTextLogin.setText("Wrong Email or Password!");
                    binding.errorMessageTextLogin.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.color_error));
                    binding.errorMessageTextLogin.setVisibility(View.VISIBLE);
                }
               // binding.markerProgressLogIn.setVisibility(View.GONE);


            }
        });

    }



}