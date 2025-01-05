package com.example.quiz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quiz.adapterQuiz.Quizs;
import com.example.quiz.adapterQuiz.dataQuiz;
import com.example.quiz.adapterQuiz.event_adapter_quiz;
import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView name, email, quizCount, quizNumber, followers, following, noText;
    RoundedImageView profilePhoto;
    Button editProfile;
    ImageView noImage;
    private DocumentReference documentReference;
    private PreferenceManager preferenceManager;
    private List<dataQuiz> events;
    public event_adapter_quiz adapter;
    RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerFrameLayout;

    public Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        try {
            shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);
            shimmerFrameLayout.startShimmer();
            preferenceManager = new PreferenceManager(getContext());
            List<dataQuiz> list = new ArrayList<>();
            getData(view);
            recyclerView = view.findViewById(R.id.question_view);
            recyclerView.setAdapter(adapter);
            noText = view.findViewById(R.id.noText);
            noImage = view.findViewById(R.id.noImage);
            name = view.findViewById(R.id.name);
            email = view.findViewById(R.id.email);
            quizCount = view.findViewById(R.id.quizCount);
            followers = view.findViewById(R.id.followers);
            quizNumber = view.findViewById(R.id.quizNumber);
            following = view.findViewById(R.id.following);
            profilePhoto = view.findViewById(R.id.profilePhoto);
            editProfile = view.findViewById(R.id.editProfile);
            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), userProfile.class);
                    startActivity(intent);
                }
            });
            name.setText(preferenceManager.getString(Constants.KEY_NAME));
            email.setText("@" + preferenceManager.getString(Constants.NICK_NAME));
            byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            profilePhoto.setImageBitmap(bitmap);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID));
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    quizCount.setText(documentSnapshot.get(Constants.COUNT).toString());
                    quizNumber.setText(documentSnapshot.get(Constants.COUNT).toString() + " квиз-(ов)");
                    followers.setText(documentSnapshot.get(Constants.FOLLOWERS).toString());
                    following.setText(documentSnapshot.get(Constants.FOLLOWINGS).toString());
                }
            });
        } catch (Exception e){

        }
        return view;
    }

    private void getData(View view)
    {
        try {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_QUIZ)
                    .whereEqualTo(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            events = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Quizs data = new Quizs();
                                data.nameQuiz = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                data.typePublick = queryDocumentSnapshot.getString(Constants.TYPE);
                                data.countQuestion = queryDocumentSnapshot.getString(Constants.COUNT);
                                data.imageQuiz = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                data.idQuiz = queryDocumentSnapshot.getId();
                                data.play = queryDocumentSnapshot.getString(Constants.PLAY);
                                events.add(new dataQuiz(data.nameQuiz, data.typePublick, data.countQuestion, data.imageQuiz, data.idQuiz, data.play));
                            }
                            if (events.size() > 0) {
                                DividerItemDecoration divider = new DividerItemDecoration(
                                        recyclerView.getContext(), DividerItemDecoration.VERTICAL
                                );
                                divider.setDrawable(
                                        ContextCompat.getDrawable(view.getContext(), R.drawable.divider)
                                );
                                FirebaseFirestore databaseCount = FirebaseFirestore.getInstance();
                                documentReference = databaseCount.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(preferenceManager.getString(Constants.KEY_USER_ID));
                                documentReference.update(Constants.COUNT, events.size());
                                recyclerView.addItemDecoration(divider);
                                noImage.setVisibility(View.INVISIBLE);
                                noText.setVisibility(View.INVISIBLE);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
                                adapter = new event_adapter_quiz(events, getContext());
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(layoutManager);
                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                            }
                            else {
                                noImage.setVisibility(View.VISIBLE);
                                noText.setVisibility(View.VISIBLE);
                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                            }
                        }
                    });
        } catch (Exception e){

        }
    }
}