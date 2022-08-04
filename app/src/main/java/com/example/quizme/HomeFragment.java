package com.example.quizme;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizme.Classes.Data;
import com.example.quizme.databinding.ActivityQuizBinding;
import com.example.quizme.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;


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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        database = FirebaseFirestore.getInstance();
        database.collection("Random").document("tMLcP2edIs6jsB8JUxec").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                }
                if (value != null) {
                    Data data = value.toObject(Data.class);//getData(User.class);
                    welcomeText = data.getHomeScreenText();
                    if(welcomeText!=null)
                    binding.vpnTVHome.setText(welcomeText);


                }

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
        binding.startNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), QuizActivity.class);
                //intent.putExtra("uid",uid);
                startActivity(intent);
                getActivity().finish();

            }
        });


        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}