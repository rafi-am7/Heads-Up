package com.example.quizme.AdminPanelActivities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.quizme.Classes.AdminProperties;
import com.example.quizme.Classes.User;
import com.example.quizme.databinding.ActivityEditBioAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class EditBioAdminActivity extends AppCompatActivity {
    private ActivityEditBioAdminBinding binding;
    String uid;
    FirebaseFirestore database;
    int adminRule;
    int pointsPerAd, pointsPerQuiz;
    String homeScreenText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBioAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Toast.makeText(EditBioAdminActivity.this,"failed",Toast.LENGTH_SHORT).show();
                }
                if(value!=null)
                {
                    User user = value.toObject(User.class);//getData(User.class);
                    adminRule = user.getAdminRole();

                    //Toast.makeText(WaitingActivity.this, "Current data:" + user.getIndex(), Toast.LENGTH_SHORT).show();
                }

            }
        });



        database.collection("AdminProperties").document("hup").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Toast.makeText(EditBioAdminActivity.this,"failed",Toast.LENGTH_SHORT).show();
                }
                if(value!=null)
                {
                    AdminProperties adminProperties = value.toObject(AdminProperties.class);//getData(User.class);
                    if(adminRule>0)
                    {
                        pointsPerAd = adminProperties.getPointsPerAdd();
                        pointsPerQuiz = adminProperties.getPointsPerQuiz();
                        homeScreenText = adminProperties.getHomeScreenText();
                    }//Toast.makeText(WaitingActivity.this, "Current data:" + user.getIndex(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.homeTextAdminET.setText(homeScreenText);
        binding.pointsPerAdAdminET.setText(""+pointsPerAd);
        binding.pointsPerQuizAdminET.setText(""+pointsPerQuiz);

        binding.updateHomeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });





    }
}