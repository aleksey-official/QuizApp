package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quiz.adapterQuiz.Quizs;
import com.example.quiz.adapterQuiz.dataQuiz;
import com.example.quiz.adapterQuiz.event_adapter_quiz;
import com.example.quiz.card.cardQuiz;
import com.example.quiz.card.event_adapter_card;
import com.example.quiz.people.People;
import com.example.quiz.people.Peoples;
import com.example.quiz.people.event_adapter_people;
import com.example.quiz.people_card.event_adapter_people_card;
import com.example.quiz.people_card.peopleCard;
import com.example.quiz.people_card.peopleCards;
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

public class see_all extends AppCompatActivity {
    TextView typeText;
    String type;
    ImageView back, search;
    private List<dataQuiz> events;
    public event_adapter_quiz adapter;
    RecyclerView recyclerView;
    private PreferenceManager preferenceManager;
    private ShimmerFrameLayout shimmerFrameLayout;
    String likeText = "";
    private DocumentReference documentReference;
    private List<peopleCard> people;
    public event_adapter_people_card peopleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all);
        type = getIntent().getSerializableExtra(Constants.TYPE).toString();
        preferenceManager = new PreferenceManager(getApplicationContext());
        shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmer();
        back = (ImageView) findViewById(R.id.imageView8);
        search = (ImageView) findViewById(R.id.imageButton2);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), search.class);
                intent.putExtra(Constants.TYPE, type);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        List<dataQuiz> list = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.question_view);
        recyclerView.setAdapter(adapter);
        typeText = (TextView) findViewById(R.id.typeText);
        if (type.equals("all")){
            getData();
            typeText.setText("Все квизы");
        } else if (type.equals("popular")){
            getData();
            typeText.setText("Популярные квизы");
        } else if (type.equals("author")){
            getAuthor();
            typeText.setText("Популярные авторы");
        }
    }
    private void getData()
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_QUIZ)
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
                            Comparator<dataQuiz> descendingComparator = new Comparator<dataQuiz>() {
                                @Override
                                public int compare(dataQuiz a, dataQuiz b) {
                                    int valueA = Integer.parseInt(a.getPlay());
                                    int valueB = Integer.parseInt(b.getPlay());
                                    return valueB - valueA;
                                }
                            };
                            DividerItemDecoration divider = new DividerItemDecoration(
                                    getApplicationContext(), DividerItemDecoration.VERTICAL
                            );
                            divider.setDrawable(
                                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider)
                            );

                            recyclerView.addItemDecoration(divider);
                            LinearLayoutManager layoutManager
                                    = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            adapter = new event_adapter_quiz(events, getApplicationContext());
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(layoutManager);
                            if (type.equals("popular")){
                                Collections.sort(events, descendingComparator);
                            }
                            adapter.notifyDataSetChanged();
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private void getAuthor()
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.PUBLICK, "true")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        people = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            peopleCards peoples = new peopleCards();
                            peoples.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            peoples.nick = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            peoples.id = queryDocumentSnapshot.getId();
                            peoples.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            peoples.subscribes = queryDocumentSnapshot.getString(Constants.FOLLOWERS);
                            people.add(new peopleCard(peoples.image, peoples.name, peoples.nick, peoples.id, peoples.subscribes));
                        }
                        if (people.size() > 0) {
                            Comparator<peopleCard> descendingComparator = new Comparator<peopleCard>() {
                                @Override
                                public int compare(peopleCard a, peopleCard b) {
                                    int valueA = Integer.parseInt(a.getSubscribes());
                                    int valueB = Integer.parseInt(b.getSubscribes());
                                    return valueB - valueA;
                                }
                            };
                            DividerItemDecoration divider = new DividerItemDecoration(
                                    recyclerView.getContext(), DividerItemDecoration.VERTICAL
                            );
                            divider.setDrawable(
                                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider)
                            );

                            recyclerView.addItemDecoration(divider);
                            LinearLayoutManager layoutManager
                                    = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            peopleAdapter = new event_adapter_people_card(people, getApplicationContext());
                            recyclerView.setAdapter(peopleAdapter);
                            recyclerView.setLayoutManager(layoutManager);
                            Collections.sort(people, descendingComparator);
                            peopleAdapter.notifyDataSetChanged();
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }
}