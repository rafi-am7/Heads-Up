package com.example.quizme.AdminPanelActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.quizme.Classes.AdminProperties;
import com.example.quizme.Classes.Quiz;
import com.example.quizme.R;
import com.example.quizme.Classes.User;
import com.example.quizme.SignupActivity;
import com.example.quizme.databinding.ActivityInsertQuizBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class InsertQuizActivity extends AppCompatActivity {
    int index=0;
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

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterQuiz();
            }
        });



    }

    private void enterQuiz() {


        question = binding.qustionAdminET.getText().toString();
        option1 = binding.option1AdminET.getText().toString();
        option2 = binding.option2AdminET.getText().toString();
        option3 = binding.option3AdminET.getText().toString();
        option4 = binding.option4AdminET.getText().toString();
        String ansStr = binding.optionCorrectET.getText().toString();

        if (question.isEmpty()) {
            binding.qustionAdminET.setError("Enter question!");
            binding.qustionAdminET.requestFocus();
            return;
        }
        if (option1.isEmpty()) {
            binding.option1AdminET.setError("Enter option1!");
            binding.option1AdminET.requestFocus();
            return;
        }
        if (option2.isEmpty()) {
            binding.option2AdminET.setError("Enter option2!");
            binding.option2AdminET.requestFocus();
            return;
        }
        if (option3.isEmpty()) {
            binding.option3AdminET.setError("Enter option3!");
            binding.option3AdminET.requestFocus();
            return;
        }
        if (option4.isEmpty()) {
            binding.option4AdminET.setError("Enter option4!");
            binding.option4AdminET.requestFocus();
            return;
        }
        if (ansStr.isEmpty()) {
            binding.optionCorrectET.setError("Enter Correct Option!");
            binding.optionCorrectET.requestFocus();
            return;
        }

        correctAns = Integer.parseInt(ansStr);

        if(!(correctAns>=1 && correctAns<=4))
        {
            binding.optionCorrectET.setError("Range (1 to 4)");
            binding.optionCorrectET.requestFocus();
            return;
        }
        index++;
        Quiz quiz = new Quiz(index,question,option1,option2,option3,option4,correctAns);

        database
                .collection("quizzes")
                .document(""+index)
                .set(quiz).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            database
                                    .collection("AdminProperties")
                                    .document("hup").update("index", index);
                            Toast.makeText(InsertQuizActivity.this,"Quiz Inserted!",Toast.LENGTH_SHORT).show();
                            binding.qustionAdminET.getText().clear();
                            binding.option1AdminET.getText().clear();
                            binding.option2AdminET.getText().clear();
                            binding.option3AdminET.getText().clear();
                            binding.option4AdminET.getText().clear();
                            binding.optionCorrectET.getText().clear();
                        } else {

                        }
                    }
                });
    }
}