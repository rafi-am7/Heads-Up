package com.example.quizme.AdminPanelActivities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.quizme.User;
import com.example.quizme.databinding.ActivityAdminPanelBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AdminPanelActivity extends AppCompatActivity {
    private ActivityAdminPanelBinding binding;
    String uid;
    FirebaseFirestore database;
    int adminRule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminPanelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {

                    Toast.makeText(AdminPanelActivity.this,"failed",Toast.LENGTH_SHORT).show();
                }
                if(value!=null)
                {
                    User user = value.toObject(User.class);//getData(User.class);
                    adminRule = user.getAdminRole();

                    //Toast.makeText(WaitingActivity.this, "Current data:" + user.getIndex(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.insertQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adminRule>0)
                {
                    Intent intent = new Intent(AdminPanelActivity.this, InsertQuizActivity.class);
                    startActivity(intent);
                }

            }
        });
        binding.editPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adminRule>0)
                {
                    Intent intent = new Intent(AdminPanelActivity.this, EditBioAdminActivity.class);
                    startActivity(intent);

                }

            }
        });
        binding.seeWithdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adminRule>0)
                {
                    Intent intent = new Intent(AdminPanelActivity.this, WithdrawRequestAdminPanelActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}