package com.example.quizme;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.quizme.AdminPanelActivities.AdminPanelActivity;
import com.example.quizme.Classes.AdminProperties;
import com.example.quizme.Classes.User;
import com.example.quizme.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class HomeFragment extends Fragment {

    //String uid = getActivity().getIntent().getExtras().getString("uid");
    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentHomeBinding binding;
    FirebaseFirestore database;
    String welcomeText;

    private int adminRule;
    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        database = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.collection("Random").document("tMLcP2edIs6jsB8JUxec").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                }
                if (value != null) {
                    AdminProperties data = value.toObject(AdminProperties.class);//getData(User.class);
                    welcomeText = data.getHomeScreenText();
                    if(welcomeText!=null)
                        binding.vpnTVHome.setText(welcomeText);


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
                    User user = value.toObject(User.class);//getData(User.class);
                    adminRule = user.getAdminRole();
                    if(adminRule>0)
                    {
                        binding.manageAdmin.setVisibility(View.VISIBLE);
                    }

                    //Toast.makeText(WaitingActivity.this, "Current data:" + user.getIndex(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        binding.startNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), QuizActivity.class);
                //intent.putExtra("uid",uid);
                startActivity(intent);
                getActivity().finish();

            }
        });
        binding.manageAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AdminPanelActivity.class);
                startActivity(new Intent(getContext(), AdminPanelActivity.class));
                //getActivity().finish();

            }
        });


/*        database = FirebaseFirestore.getInstance();

        final ArrayList<CategoryModel> categories = new ArrayList<>();

        final CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);*/

/*        database.collection("categories")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        categories.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            CategoryModel model = snapshot.toObject(CategoryModel.class);
                            model.setCategoryId(snapshot.getId());
                            categories.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });*/


/*
        binding.categoryList.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.categoryList.setAdapter(adapter);
*/

/*        binding.spinwheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SpinnerActivity.class));
            }
        });*/



        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}