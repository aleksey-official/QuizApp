package com.example.quiz;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;

import com.example.quiz.card.Cards;
import com.example.quiz.card.cardQuiz;
import com.example.quiz.card.event_adapter_card;
import com.example.quiz.people.People;
import com.example.quiz.people.Peoples;
import com.example.quiz.people.event_adapter_people;
import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DocumentReference documentReference;
    private PreferenceManager preferenceManager;
    private List<cardQuiz> events;
    private List<People> people;
    public event_adapter_card adapter;
    public event_adapter_people peopleAdapter;
    RecyclerView myQuizView, questionView, author;
    TextView noText, noText2, noText3, all, popularAll, authorAll;
    ImageView noImage, noImage2, noImage3;
    private ShimmerFrameLayout shimmerFrameLayout, shimmerLayoutPopular, shimmerLayoutPeople;
    int count_followers = 0;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        try {
            shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);
            shimmerFrameLayout.startShimmer();
            shimmerLayoutPopular = view.findViewById(R.id.shimmerLayoutPopular);
            shimmerLayoutPopular.startShimmer();
            shimmerLayoutPeople = view.findViewById(R.id.shimmerLayoutPeople);
            shimmerLayoutPeople.startShimmer();
            noText = view.findViewById(R.id.noText);
            noImage = view.findViewById(R.id.noImage);
            noText2 = view.findViewById(R.id.noText2);
            noImage2 = view.findViewById(R.id.noImage3);
            noText3 = view.findViewById(R.id.noText3);
            noImage3 = view.findViewById(R.id.noImage2);
            all = view.findViewById(R.id.discoverAll2);
            popularAll = view.findViewById(R.id.popularAll);
            authorAll = view.findViewById(R.id.autorAll2);
            getData(view);
            myQuizView = view.findViewById(R.id.question_view);
            myQuizView.setAdapter(adapter);
            questionView = view.findViewById(R.id.questionView);
            getDataPopular(view);
            questionView.setAdapter(adapter);
            author = view.findViewById(R.id.autor);
            getPeople(view);
            author.setAdapter(adapter);
            all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), see_all.class);
                    intent.putExtra(Constants.TYPE, "all");
                    startActivity(intent);
                }
            });
            popularAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), see_all.class);
                    intent.putExtra(Constants.TYPE, "popular");
                    startActivity(intent);
                }
            });
            authorAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        preferenceManager = new PreferenceManager(view.getContext());
                        FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
                        documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                                .document(preferenceManager.getString(Constants.KEY_USER_ID));
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                preferenceManager.putString(Constants.FOLLOWINGS_ID, documentSnapshot.getString(Constants.FOLLOWINGS_ID));
                                preferenceManager.putString(Constants.FOLLOWERS, String.valueOf(Integer.parseInt(documentSnapshot.getString(Constants.FOLLOWINGS))));
                                preferenceManager.putString(Constants.QUIZ_ID, documentSnapshot.getId().toString());
                                Intent intent = new Intent(view.getContext(), see_all.class);
                                intent.putExtra(Constants.TYPE, "author");
                                startActivity(intent);
                            }
                        });
                    } catch (Exception e){
                        Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
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
                    .whereEqualTo(Constants.TYPE, "Публичный")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            events = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Cards data = new Cards();
                                data.nameQuiz = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                data.typePublick = queryDocumentSnapshot.getString(Constants.CREATOR_NAME);
                                data.countQuestion = queryDocumentSnapshot.getString(Constants.COUNT);
                                data.imageQuiz = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                data.userImage = queryDocumentSnapshot.getString(Constants.USER_IMAGE);
                                data.idQuiz = queryDocumentSnapshot.getId();
                                data.play = queryDocumentSnapshot.getString(Constants.PLAY);
                                events.add(new cardQuiz(data.nameQuiz, data.typePublick, data.countQuestion, data.imageQuiz, data.userImage, data.idQuiz, data.play));
                            }
                            if (events.size() > 0) {
                                noImage.setVisibility(View.INVISIBLE);
                                noText.setVisibility(View.INVISIBLE);
                                LinearLayoutManager layoutManager
                                        = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
                                DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
                                itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
                                myQuizView.addItemDecoration(itemDecorator);
                                adapter = new event_adapter_card(events, getContext());
                                myQuizView.setAdapter(adapter);
                                myQuizView.setLayoutManager(layoutManager);
                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                            } else {
                                noImage.setVisibility(View.VISIBLE);
                                noText.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } catch (Exception e){

        }
    }
    private void getDataPopular(View view)
    {
        try {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_QUIZ)
                    .whereEqualTo(Constants.TYPE, "Публичный")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            events = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Cards data = new Cards();
                                data.nameQuiz = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                data.typePublick = queryDocumentSnapshot.getString(Constants.CREATOR_NAME);
                                data.countQuestion = queryDocumentSnapshot.getString(Constants.COUNT);
                                data.imageQuiz = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                data.userImage = queryDocumentSnapshot.getString(Constants.USER_IMAGE);
                                data.idQuiz = queryDocumentSnapshot.getId();
                                data.play = queryDocumentSnapshot.getString(Constants.PLAY);
                                events.add(new cardQuiz(data.nameQuiz, data.typePublick, data.countQuestion, data.imageQuiz, data.userImage, data.idQuiz, data.play));
                            }
                            if (events.size() > 0) {
                                Comparator<cardQuiz> descendingComparator = new Comparator<cardQuiz>() {
                                    @Override
                                    public int compare(cardQuiz a, cardQuiz b) {
                                        int valueA = Integer.parseInt(a.getPlay());
                                        int valueB = Integer.parseInt(b.getPlay());
                                        return valueB - valueA;
                                    }
                                };
                                DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
                                itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
                                questionView.addItemDecoration(itemDecorator);
                                noImage2.setVisibility(View.INVISIBLE);
                                noText2.setVisibility(View.INVISIBLE);
                                LinearLayoutManager layoutManager
                                        = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
                                adapter = new event_adapter_card(events, getContext());
                                questionView.setAdapter(adapter);
                                questionView.setLayoutManager(layoutManager);
                                Collections.sort(events, descendingComparator);
                                adapter.notifyDataSetChanged();
                                shimmerLayoutPopular.stopShimmer();
                                shimmerLayoutPopular.setVisibility(View.GONE);
                            }
                            else {
                                noImage2.setVisibility(View.VISIBLE);
                                noImage2.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } catch (Exception e){

        }
    }
    private void getPeople(View view)
    {
        try {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.PUBLICK, "true")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            people = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Peoples peoples = new Peoples();
                                peoples.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                peoples.id = queryDocumentSnapshot.getId();
                                peoples.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                peoples.subscribes = queryDocumentSnapshot.getString(Constants.FOLLOWERS);
                                people.add(new People(peoples.image, peoples.name, peoples.id, peoples.subscribes));
                            }
                            if (people.size() > 0) {
                                Comparator<People> descendingComparator = new Comparator<People>() {
                                    @Override
                                    public int compare(People a, People b) {
                                        int valueA = Integer.parseInt(a.getSubscribes());
                                        int valueB = Integer.parseInt(b.getSubscribes());
                                        return valueB - valueA;
                                    }
                                };
                                DividerItemDecoration divider = new DividerItemDecoration(
                                        author.getContext(), DividerItemDecoration.HORIZONTAL
                                );
                                divider.setDrawable(
                                        ContextCompat.getDrawable(view.getContext(), R.drawable.divider)
                                );

                                author.addItemDecoration(divider);
                                noImage3.setVisibility(View.INVISIBLE);
                                noText3.setVisibility(View.INVISIBLE);
                                LinearLayoutManager layoutManager
                                        = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
                                peopleAdapter = new event_adapter_people(people, getContext());
                                author.setAdapter(peopleAdapter);
                                author.setLayoutManager(layoutManager);
                                Collections.sort(people, descendingComparator);
                                peopleAdapter.notifyDataSetChanged();
                                shimmerLayoutPeople.stopShimmer();
                                shimmerLayoutPeople.setVisibility(View.GONE);
                            } else {
                                noImage3.setVisibility(View.VISIBLE);
                                noImage3.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } catch (Exception e){

        }
    }
}