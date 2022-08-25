package com.example.quizme;
import java.net.InetAddress;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {


    private ActivityQuizBinding binding;
    private ArrayList<Quiz> questions;
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

        binding.quitBtn.setOnClickListener(new View.OnClickListener() {
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


                    Toast.makeText(QuizActivity.this, "Current data:" + user.getIndex(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        database.collection("quizes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() < 5) {
                    database.collection("quizes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                    Toast.makeText(getApplicationContext(),"Ad loaded",Toast.LENGTH_SHORT).show();
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

    private String GetLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            return "ERROR Obtaining IP";
        }
        return "No IP Available";
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getIp() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        Toast.makeText(this,"Ip Address: "+ Formatter.formatIpAddress( wifiManager.getConnectionInfo().getIpAddress()),Toast.LENGTH_SHORT ).show();
/*        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = cm.getAllNetworks();
        for(int i = 0; i < networks.length; i++) {*/

        // NetworkCapabilities caps = cm.getNetworkCapabilities(networks[i]);
        //Toast.makeText(this,"Network : "+i+ " "+networks[i].toString(),Toast.LENGTH_SHORT ).show();
        //Toast.makeText(this,"Vpn Capability: "+i+"Cap"+caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN),Toast.LENGTH_SHORT ).show();


/*
            Log.i(TAG, "" + i + ": " + networks[i].toString());
            Log.i(TAG, "VPN transport is: " + caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
            Log.i(TAG, "NOT_VPN capability is: " + caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN));
*/

    }
}

 /*   @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    private void grantPermission(){ 
    }
    private void isLocationEnabled(){

    }*/
/*
 */



/*    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            getLocation();
        } else {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }*/

/*
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                //if(location!=null)
                {
                    try
                    {
                        Toast.makeText(QuizActivity.this,"Location helw",Toast.LENGTH_SHORT).show();

                        Geocoder geocoder = new Geocoder(QuizActivity.this, Locale.getDefault());

                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(),location.getLongitude(),1
                        );
                        locationCountry = ""+addresses.get(0).getCountryName();
                        //Toast.makeText(QuizActivity.this,"Location"+locationCountry,Toast.LENGTH_SHORT).show();

                    }
                    catch (IOException e)
                    {
                        Toast.makeText(QuizActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }



            }
        });
    }
*/


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
    /*  void checkAvailableConnection() {
          ConnectivityManager connMgr = (ConnectivityManager) this
                  .getSystemService(Context.CONNECTIVITY_SERVICE);

          final android.net.NetworkInfo wifi = connMgr
                  .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

          final android.net.NetworkInfo mobile = connMgr
                  .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

          if (wifi.isAvailable()) {

              WifiManager myWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
              WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
              int ipAddress = myWifiInfo.getIpAddress();
              System.out.println("WiFi address is "
                      + android.text.format.Formatter.formatIpAddress(ipAddress));

          } else if (mobile.isAvailable()) {

              GetLocalIpAddress();
              Toast.makeText(this, "3G Available", Toast.LENGTH_LONG).show();
          } else {
              Toast.makeText(this, "No Network Available", Toast.LENGTH_LONG).show();
          }
      }*/
