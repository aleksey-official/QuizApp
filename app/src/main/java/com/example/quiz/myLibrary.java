package com.example.quiz;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link myLibrary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class myLibrary extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView textView, noText;
    ImageView noImage;
    private List<dataQuiz> events;
    public event_adapter_quiz adapter;
    RecyclerView recyclerView;
    private PreferenceManager preferenceManager;
    private ShimmerFrameLayout shimmerFrameLayout;
    String likeText = "";
    private DocumentReference documentReference;

    public myLibrary() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment myLibrary.
     */
    // TODO: Rename and change types and number of parameters
    public static myLibrary newInstance(String param1, String param2) {
        myLibrary fragment = new myLibrary();
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
        View view = inflater.inflate(R.layout.fragment_my_library, container, false);
        preferenceManager = new PreferenceManager(getContext());
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmer();
        textView = view.findViewById(R.id.typeText);
        noText = view.findViewById(R.id.noText4);
        noImage = view.findViewById(R.id.noImage4);
        String sTitle = getArguments().getString("title");
        if (sTitle.equals("Мои квизы")){
            List<dataQuiz> list = new ArrayList<>();
            getData(view);
            recyclerView = view.findViewById(R.id.question_view);
            recyclerView.setAdapter(adapter);
        } else {
            List<dataQuiz> list = new ArrayList<>();
            getDataLike(view);
            recyclerView = view.findViewById(R.id.question_view);
            recyclerView.setAdapter(adapter);
        }
        textView.setText(sTitle);
        return view;
    }
    private void getData(View view)
    {
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
                            noImage.setVisibility(View.INVISIBLE);
                            noText.setVisibility(View.INVISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
                            DividerItemDecoration divider = new DividerItemDecoration(
                                    recyclerView.getContext(), DividerItemDecoration.VERTICAL
                            );
                            divider.setDrawable(
                                    ContextCompat.getDrawable(view.getContext(), R.drawable.divider)
                            );
                            recyclerView.addItemDecoration(divider);
                            adapter = new event_adapter_quiz(events, getContext());
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(layoutManager);
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        } else {
                            noImage.setVisibility(View.VISIBLE);
                            noText.setVisibility(View.VISIBLE);
                            shimmerFrameLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private void getDataLike(View view)
    {

        FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
        documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                likeText = documentSnapshot.getString(Constants.LIKE);
            }
        });
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_QUIZ)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        events = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (likeText.contains(queryDocumentSnapshot.getId())){
                                Quizs data = new Quizs();
                                data.nameQuiz = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                data.typePublick = queryDocumentSnapshot.getString(Constants.TYPE);
                                data.countQuestion = queryDocumentSnapshot.getString(Constants.COUNT);
                                data.imageQuiz = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                data.idQuiz = queryDocumentSnapshot.getId();
                                data.play = queryDocumentSnapshot.getString(Constants.PLAY);
                                events.add(new dataQuiz(data.nameQuiz, data.typePublick, data.countQuestion, data.imageQuiz, data.idQuiz, data.play));
                            }
                        }
                        if (events.size() > 0) {
                            DividerItemDecoration divider = new DividerItemDecoration(
                                    recyclerView.getContext(), DividerItemDecoration.VERTICAL
                            );
                            divider.setDrawable(
                                    ContextCompat.getDrawable(view.getContext(), R.drawable.divider)
                            );

                            recyclerView.addItemDecoration(divider);
                            noImage.setVisibility(View.INVISIBLE);
                            noText.setVisibility(View.INVISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
                            adapter = new event_adapter_quiz(events, getContext());
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(layoutManager);
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        } else {
                            noImage.setVisibility(View.VISIBLE);
                            noText.setVisibility(View.VISIBLE);
                            shimmerFrameLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }
}