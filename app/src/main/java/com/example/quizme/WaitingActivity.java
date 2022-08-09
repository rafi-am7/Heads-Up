package com.example.quizme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.quizme.databinding.ActivityWaitingBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class WaitingActivity extends AppCompatActivity {
    ActivityWaitingBinding binding;
    private RewardedAd mRewardedAd;
    private String uid;
    private FirebaseFirestore database;
    private int correctAnswers = 0;
    private int totalPoint=0, quizPoint=-0, rewardPoint=0;
    private int index = 0;
    //   int POINTS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWaitingBinding.inflate(getLayoutInflater());
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseFirestore.getInstance();


        setContentView(binding.getRoot());
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        database.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {

                    Toast.makeText(WaitingActivity.this,"failed",Toast.LENGTH_SHORT).show();
                }
                if(value!=null)
                {
                    User user = value.toObject(User.class);//getData(User.class);
                    index =  user.getIndex();
                    correctAnswers = user.getCorrectAnswer();
                    totalPoint = user.getTotalPoints();
                    Toast.makeText(WaitingActivity.this,""+totalPoint,Toast.LENGTH_SHORT).show();
                    quizPoint = user.getQuizPoints();
                    rewardPoint = user.getRewardPoints();
                    binding.totalPointTV.setText("Total Points: "+totalPoint);
                    binding.quizPointsTV.setText("Quiz Points: "+quizPoint);
                    binding.rewardPointsTV.setText("Reward Points: "+rewardPoint);


                    //Toast.makeText(WaitingActivity.this, "Current data:" + user.getIndex(), Toast.LENGTH_SHORT).show();
                }

            }
        });



      /*  AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("reward", loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d("reward", "Ad was loaded.");
                    }
                });*/
        showAds();

        int isCorrect = getIntent().getIntExtra("isCorrect", 0);
        if(isCorrect==1)
        {
            binding.correctAnsTV.setText("Correct Answer");
            binding.correctAnsTV.setTextColor(getResources().getColor( R.color.color_right));
        }
        else
        {
            binding.correctAnsTV.setText("Wrong Answer");
            binding.correctAnsTV.setTextColor(getResources().getColor( R.color.color_wrong));
        }


        //get correct answer from database
        //get total points from database
        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WaitingActivity.this,QuizActivity.class));
            }
        });

        // long points =  * POINTS;

        // binding.score.setText(String.format("%d/%d", correctAnswers, totalQuestions));
        //45214binding.earnedCoins.setText(String.valueOf(points));

        /*        FirebaseFirestore database = FirebaseFirestore.getInstance();*/

        /*database.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .update("coins", FieldValue.increment(points));*/

/*        binding.restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WaitingActivity.this, QuizActivity.class));
                finishAffinity();*/
    }
    void showAds(){
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("reward", loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d("reward", "Ad was loaded.");
                    }
                });

        if (mRewardedAd != null) {
            Activity activityContext = WaitingActivity.this;

            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d("rewardAd", "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    Toast.makeText(getApplicationContext(),"Ad loaded",Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d("rewardAd", "The rewarded ad wasn't ready yet.");
            Toast.makeText(this,"Ad not ready",Toast.LENGTH_SHORT).show();
            //showAds();
        }
    }



}
