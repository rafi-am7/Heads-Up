package com.example.quizme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.example.quizme.databinding.ActivityResultBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding binding;
    int POINTS = 10;
    private String uid ;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int correctAnswers = getIntent().getIntExtra("correct", 0);
        int totalQuestions = getIntent().getIntExtra("total", 0);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseFirestore.getInstance();

        long points = correctAnswers * POINTS;

        //binding.score.setText(String.format("%d/%d", correctAnswers, totalQuestions));
        binding.earnedCoins.setText(String.valueOf(points));


        database.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .update("points", FieldValue.increment(points));

        binding.restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultActivity.this, MainActivity.class));
                finishAffinity();
            }
        });


    }
}