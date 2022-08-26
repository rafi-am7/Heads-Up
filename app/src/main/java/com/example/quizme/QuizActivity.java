package com.example.quizme;
import java.io.IOException;
import java.net.InetAddress;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.quizme.Classes.AdminProperties;
import com.example.quizme.Classes.Quiz;
import com.example.quizme.Classes.User;
import com.example.quizme.databinding.ActivityQuizBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {


    private ActivityQuizBinding binding;
    private ArrayList<Quiz> questions;
    String countryName;
    private int index = 0;
    private Quiz question;
    private CountDownTimer timer;
    private FirebaseFirestore database;
    private ProgressDialog progressDialog;
    private int correctAnswers = 0;
    private RewardedAd mRewardedAd;
    private int totalPoint = 0, quizPoint = 0, rewardPoint = 0;
    private String uid;//= getIntent().getExtras().getString("uid");
    private LocationRequest locationRequest;
    private int pointPerQuiz, pointPerAd;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String locationCountry = "Heads up";
    private boolean addFlag = false;
    RequestQueue queue;
    boolean flagCountry = false;
    private Snackbar snackbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        questions = new ArrayList<>();
        database = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String catId = getIntent().getStringExtra("catId");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading quizes...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        binding.showAdsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                MobileAds.initialize(QuizActivity.this, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });
                showAds();

            }

        });
        Random random = new Random();
        final int rand = random.nextInt(12);
        database.collection("AdminProperties").document("hup").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    Toast.makeText(QuizActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
                if (value != null) {
                    AdminProperties data = value.toObject(AdminProperties.class);//getData(User.class);
                    pointPerQuiz = data.getPointsPerQuiz();
                    pointPerAd = data.getPointsPerAdd();


                }

            }
        });

        database.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    Toast.makeText(QuizActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
                if (value != null) {
                    User user = value.toObject(User.class);//getData(User.class);
                    index = user.getIndex();
                    correctAnswers = user.getCorrectAnswer();
                    totalPoint = user.getTotalPoints();
                    quizPoint = user.getQuizPoints();
                    rewardPoint = user.getRewardPoints();


                   // Toast.makeText(QuizActivity.this, "Current data:" + user.getIndex(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        database.collection("quizzes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() < 5) {
                    database.collection("quizzes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Quiz question =  snapshot.toObject(Quiz.class);
                                questions.add(question);
                            }
                            setNextQuestion();
                        }
                    });
                } else {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Quiz question = snapshot.toObject(Quiz.class);
                        questions.add(question);
                    }
                    setNextQuestion();
                }
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });



    }



    void setNextQuestion() {

        try {
            if (index <= questions.size()) {
                question = questions.get(index);
                binding.question.setText(question.getQuestion());
                binding.option1.setText(question.getOption1());
                binding.option2.setText(question.getOption2());
                binding.option3.setText(question.getOption3());
                binding.option4.setText(question.getOption4());
            } else {
                Toast.makeText(this, "Quiz ended!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("correct", correctAnswers);
                intent.putExtra("total", questions.size());
                startActivity(intent);
                finish();
            }
            progressDialog.dismiss();

        } catch (Exception e) {
            Toast.makeText(this, "Quiz ended!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("correct", correctAnswers);
            intent.putExtra("total", questions.size());
            startActivity(intent);
            finish();
        }
    }

    boolean checkAnswer(RadioButton radioButton) {
        String selectedAnswer = radioButton.getText().toString();
        if (selectedAnswer.equals(question.getAnswer())) {
            correctAnswers++;
            //push database
            // radioButton.setBackground(getResources().getDrawable(R.drawable.option_right));
            return true;
        } else {
            //showAnswer();
            //radioButton.setBackground(getResources().getDrawable(R.drawable.option_wrong));
            return false;
        }
    }

    void reset() {
        binding.option1.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option2.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option3.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option4.setBackground(getResources().getDrawable(R.drawable.option_unselected));
    }
    void localization(){

    }
    void showAds(){
        if (snackbar != null) snackbar.dismiss();
        if(!getCountry())
        {
            snackbar = Snackbar.make(findViewById(R.id.activity_quiz), "Please connect a USA VPN!", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.color_white));
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_error));
            snackbar.show();
            return;
        }
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
            Activity activityContext = QuizActivity.this;

            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d("rewardAd", "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                   // Toast.makeText(getApplicationContext(),"Ad loaded",Toast.LENGTH_SHORT).show();
                    totalPoint+=pointPerAd;
                    rewardPoint+=pointPerAd;
                    database
                            .collection("users")
                            .document(uid).update("totalPoints", totalPoint);
                    database
                            .collection("users")
                            .document(uid).update("rewardPoints", rewardPoint);
                    addFlag = true;

                }
            });
        } else {
            Log.d("rewardAd", "The rewarded ad wasn't ready yet.");
            Toast.makeText(this,"Ad not loaded",Toast.LENGTH_SHORT).show();
        }
    }
    private boolean getCountry(){

        queue = Volley.newRequestQueue(this);
        String url = "https://sobujbari.com/getIPDetails";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               // countryName = response.toString();

                try {
                    countryName="";
                    JSONObject object=new JSONObject(response);
/*                    JSONArray array=object.getJSONArray("users");
                    for(int i=0;i<array.length();i++) {
                        JSONObject object1=array.getJSONObject(i);*/
                        countryName =object.getString("country");
                        if(countryName.equals("United States")){
                            flagCountry=true;

                        if(snackbar!=null)snackbar.dismiss();
                    snackbar = Snackbar.make(findViewById(R.id.activity_quiz), "USA VPN Connected! Now see ads", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.setActionTextColor(ContextCompat.getColor(QuizActivity.this, R.color.color_white));
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(QuizActivity.this, R.color.color_success));
                    snackbar.show();
                        }
                    //Toast.makeText(QuizActivity.this,"Country: "+countryName,Toast.LENGTH_SHORT).show();

/*                        textView.setText(name);
                    }*/
                } catch (JSONException e) {
                     Toast.makeText(QuizActivity.this,"Try again",Toast.LENGTH_SHORT).show();

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error",error.toString());
            }
        });
        queue.add(request);
        if(flagCountry){
            flagCountry=false;
            return true;
        }
        return false;
       // countryName = ""+queue.getResponseDelivery();
      //  Toast.makeText(QuizActivity.this,"Country: "+queue,Toast.LENGTH_SHORT).show();

    }

    public void onClick(View view) {
        switch (view.getId()) {
/*           case R.id.quitBtn:
              // WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
               Toast.makeText(this,"Ip Address: 1",Toast.LENGTH_SHORT ).show();*/
            //   Toast.makeText(this,"Ip Address: "+ Formatter.formatIpAddress( wifiManager.getConnectionInfo().getIpAddress()),Toast.LENGTH_SHORT ).show();

            // getIp();
            //Toast.makeText(this,"Location"+locationCountry,Toast.LENGTH_SHORT).show();


            case R.id.option_1:
            case R.id.option_2:
            case R.id.option_3:
            case R.id.option_4:
                if (timer != null)
                    timer.cancel();
                //TextView selected = (TextView) view;
                //checkAnswer(selected);

                break;
            case R.id.submitBtn:
                if (snackbar != null) snackbar.dismiss();
                if(!getCountry())
                {
                    snackbar = Snackbar.make(findViewById(R.id.activity_quiz), "connect USA VPN and wait!", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.color_white));
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_error));
                    snackbar.show();
                    break;
                }
                if(addFlag==false)
                {
                    Toast.makeText(QuizActivity.this,"Please see adds for \n atleast one times!",Toast.LENGTH_SHORT).show();
                    break;
                }

                // reset();
                /*if(index <= questions.size()) {*/
                index++;
                database
                        .collection("users")
                        .document(uid).update("index", index);

                //push tabase

                //    setNextQuestion();
                int radioId = binding.radioGroup.getCheckedRadioButtonId();
                RadioButton selected = findViewById(radioId);
                Intent intent = new Intent(QuizActivity.this, WaitingActivity.class);

                if(checkAnswer(selected))
                {
                    //++ in function
                    intent.putExtra("isCorrect", 1);
                    database.collection("users").document(uid).update("correctAnswers",correctAnswers);
                    totalPoint+=pointPerQuiz;
                    quizPoint+=pointPerQuiz;

                }
                else
                {
                    intent.putExtra("isCorrect", 0);
                }
                //showAds();
                database
                        .collection("users")
                        .document(uid).update("totalPoints", totalPoint);
                database
                        .collection("users")
                        .document(uid).update("quizPoints", quizPoint);

                startActivity(intent);
                finish();
/*                } else {
                    Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                    intent.putExtra("correct", correctAnswers);
                    intent.putExtra("total", questions.size());
                    startActivity(intent);
                    //Toast.makeText(this, "Quiz Finished.", Toast.LENGTH_SHORT).show();
                }*/
                break;
        }
    }



}




/*    void showAnswer() {
        if (question.getAnswer().equals(binding.option1.getText().toString()))
            binding.option1.setBackground(getResources().getDrawable(R.drawable.option_right));
        else if (question.getAnswer().equals(binding.option2.getText().toString()))
            binding.option2.setBackground(getResources().getDrawable(R.drawable.option_right));
        else if (question.getAnswer().equals(binding.option3.getText().toString()))
            binding.option3.setBackground(getResources().getDrawable(R.drawable.option_right));
        else if (question.getAnswer().equals(binding.option4.getText().toString()))
            binding.option4.setBackground(getResources().getDrawable(R.drawable.option_right));
    }*/
/*        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(QuizActivity.this, "ca-app-pub-3940256099942544/5224354917",
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

/*
                String ip;
                try(final DatagramSocket socket = new DatagramSocket()){
                    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                    ip = socket.getLocalAddress().getHostAddress();
                    Toast.makeText()
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
*/
