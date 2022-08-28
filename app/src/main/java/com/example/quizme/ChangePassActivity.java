package com.example.quizme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.quizme.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassActivity extends AppCompatActivity {

    private EditText oldPassET, newPassET, newPassReenterET;
    private Button updateButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);


        oldPassET = findViewById(R.id.oldPasswordHotelET);
        newPassET = findViewById(R.id.newPasswordHotelET);
        newPassReenterET = findViewById(R.id.newRentPasswordHotelET);
        updateButton = findViewById(R.id.updateUpPassHotel);
        progressBar = findViewById(R.id.marker_progress_uppass_Hotel);
        errorMessage = findViewById(R.id.message_view_Hotel_uppass);
        mAuth = FirebaseAuth.getInstance();


        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                errorMessage.setVisibility(View.GONE);

                String oldp, newp, rnewp;
                oldp = oldPassET.getText().toString();
                newp = newPassET.getText().toString();
                rnewp = newPassReenterET.getText().toString();
                if (oldp.isEmpty()) {
                    oldPassET.setError("Enter current password");
                    oldPassET.requestFocus();
                    return;
                }
                if (newp.isEmpty()) {
                    newPassET.setError("Enter new password");
                    newPassET.requestFocus();
                    return;
                }
                if (rnewp.isEmpty()) {
                    newPassReenterET.setError("Reenter new password");
                    newPassReenterET.requestFocus();
                    return;
                }
                if (newp.length() < 6) {
                    newPassET.setError("Minimum password length is 6 characters!");
                    newPassET.requestFocus();
                    return;
                }
                if (!newp.equals(rnewp)) {
                    newPassReenterET.setError("Please reenter new password correctly!");
                    newPassReenterET.requestFocus();
                    return;
                }
                if (newp.equals(oldp)) {
                    newPassET.setError("Old and new password cannot be same!");
                    newPassET.requestFocus();
                    newPassReenterET.setError("Old and new password cannot be same!");
                    newPassReenterET.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                if (true) {

                    mAuth.getCurrentUser().updatePassword(newp).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //reference.child("password").setValue(newp);
                                errorMessage.setTextColor(ContextCompat.getColor(ChangePassActivity.this, R.color.color_success));
                                errorMessage.setText("Password has changed successfully!");
                                errorMessage.setVisibility(View.VISIBLE);

                            } else {
                                errorMessage.setTextColor(ContextCompat.getColor(ChangePassActivity.this, R.color.color_error));
                                errorMessage.setText("Password change Failed!");
                                errorMessage.setVisibility(View.VISIBLE);

                            }
                        }
                    });
                } else {
                    oldPassET.setError("Wrong password!");
                    oldPassET.requestFocus();
                }



                progressBar.setVisibility(View.GONE);
                // startActivity(new Intent(HotelChangePasswordActivity.this,HotelActivity.class));


            }
        });


    }
}