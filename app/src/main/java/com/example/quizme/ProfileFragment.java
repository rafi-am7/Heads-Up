package com.example.quizme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.quizme.Classes.User;
import com.example.quizme.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;


public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private FragmentProfileBinding binding;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    FirebaseAuth auth;
    FirebaseFirestore database;
    User user;
    FirebaseStorage storage;
    Uri selectedImage;
    ProgressDialog progressDialog;
    private String uid;
    private int index=0;
    private int correctAnswers=0;
    private int totalPoint=0,quizPoint=0,rewardPoint=0;
    private String email=null, userName=null, profilePicURL = null, fullName="";
    Intent intent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =FragmentProfileBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        database = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Picture");
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading Profile Picture...");
        progressDialog.setCancelable(false);


        binding.uploadPPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.uploadLL.setVisibility(View.VISIBLE);
                binding.uploadPPButton.setVisibility(View.GONE);
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImage != null) {
                    progressDialog.show();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    long time = new Date().getTime();
                    final StorageReference reference = storage.getReference().child("Profiles").child(time + "");
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath = uri.toString();
                                        HashMap<String, Object> obj = new HashMap<>();
                                        obj.put("image", filePath);
                                        database
                                                .collection("users")
                                                .document(uid).update("profile",filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressDialog.dismiss();
                                                        binding.uploadLL.setVisibility(View.GONE);
                                                        binding.uploadPPButton.setVisibility(View.VISIBLE);

                                                    }
                                                });

                                    }
                                });
                            }
                        }
                    });
                    // final StorageReference reference = storage.getReference().child("Profiles").child(uid);
                    storage.getReference().child("Profiles").child(uid).putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storage.getReference().child("Profiles").child(uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        String imageUrl = uri.toString();
                                        database
                                                .collection("users")
                                                .document(uid).update("profile", imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressDialog.dismiss();
                                                        binding.uploadLL.setVisibility(View.GONE);
                                                        binding.uploadPPButton.setVisibility(View.VISIBLE);

                                                    }
                                                });

                                        //   UserHelperClass addNewUser = new UserHelperClass(fullName, fullemail, address, password, gender, date, phone, imageUrl);

/*                                        database.getReference()
                                                .child("Users")
                                                .child(phone)
                                                .setValue(addNewUser)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(SetupProfileActivity.this, LoginActivity.class);
                                                        Toast.makeText(SetupProfileActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });*/

                                    }
                                });
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(),"No Image Selected!",Toast.LENGTH_LONG).show();

/*
                    String phone = auth.getCurrentUser().getPhoneNumber();

                    UserHelperClass addNewUser = new UserHelperClass(fullName, fullemail, address, password, gender, date, phone, "No Image");

                    database.getReference()
                            .child("Users")
                            .child(phone)
                            .setValue(addNewUser)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(SetupProfileActivity.this, LoginActivity.class);
                                    Toast.makeText(SetupProfileActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    finish();
                                }
                            });*/
                }

            }
        });


        database.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {

                    Toast.makeText(getContext(),"failed",Toast.LENGTH_SHORT).show();
                }
                if(value!=null)
                {
                    try {
                        //getData(User.class);

                        // Toast.makeText(getContext(),""+totalPoint,Toast.LENGTH_SHORT).show();

                        user = value.toObject(User.class);

                        index =  user.getIndex();
                        correctAnswers = user.getCorrectAnswer();
                        totalPoint = user.getTotalPoints();
                        quizPoint = user.getQuizPoints();
                        rewardPoint = user.getRewardPoints();
                        email = user.getEmail();
                        fullName= user.getName();
                        binding.tvName.setText(fullName);
                        binding.tvTotalPoints.setText(""+totalPoint);
                        binding.tvPlayPoints.setText(""+quizPoint);
                        binding.tvRewardPoints.setText(""+rewardPoint);
                        binding.tvEmail.setText(""+email);
                        try {
                            profilePicURL = user.getProfile();
                            if(profilePicURL!=null)
                                Glide.with(getContext())
                                        .load(profilePicURL)
                                        .into(binding.userAvatarIV);
                        }catch (Exception e)
                        {
                            Toast.makeText(getContext(),"Profile Picture Loading Failed!",Toast.LENGTH_SHORT).show();
                        }

                        //binding.userAvatarIV.setImageURI(profilePicURL);

                    }catch (Exception e)
                    {
                        Toast.makeText(getContext(),"Loading Failed!",Toast.LENGTH_SHORT).show();
                    }



                    //Toast.makeText(WaitingActivity.this, "Current data:" + user.getIndex(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.uploadLL.setVisibility(View.GONE);
                binding.uploadPPButton.setVisibility(View.VISIBLE);
                try {
                    profilePicURL = user.getProfile();
                    if(profilePicURL!=null)
                        Glide.with(getContext())
                                .load(profilePicURL)
                                .into(binding.userAvatarIV);
                }catch (Exception e)
                {
                    Toast.makeText(getContext(),"Profile Picture Loading Failed!",Toast.LENGTH_SHORT).show();
                }







            }
        });

        return binding.getRoot();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (data.getData() != null) {

                binding.userAvatarIV.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }


}