package com.example.quizme.AdminPanelActivities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.quizme.Classes.AdminProperties;
import com.example.quizme.Classes.Quiz;
import com.example.quizme.R;
import com.example.quizme.Classes.User;
import com.example.quizme.databinding.ActivityInsertQuizBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class InsertQuizActivity extends AppCompatActivity {
    int index;
    FirebaseFirestore database;
    int adminRule;
    ActivityInsertQuizBinding binding;
    String uid;

    String question, option1, option2, option3, option4;
    int correctAns=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_quiz);

        binding = ActivityInsertQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Toast.makeText(InsertQuizActivity.this,"failed",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(InsertQuizActivity.this,"failed",Toast.LENGTH_SHORT).show();
                }
                if(value!=null)
                {
                    AdminProperties adminProperties = value.toObject(AdminProperties.class);//getData(User.class);
                    if(adminRule>0)
                    {

                        index = adminProperties.getIndex();
                    }//Toast.makeText(WaitingActivity.this, "Current data:" + user.getIndex(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        question = binding.qustionAdminET.getText().toString();
        option1 = binding.option1AdminET.getText().toString();
        option2 = binding.option2AdminET.getText().toString();
        option3 = binding.option3AdminET.getText().toString();
        option4 = binding.option4AdminET.getText().toString();
        try {
            correctAns = Integer.parseInt(binding.optionCorrectET.getText().toString());
        }catch (Exception e){

        }

        Quiz quiz = new Quiz(question,option1,option2,option3,option4,correctAns);

    }
}