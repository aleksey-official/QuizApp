package com.example.quiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.adapterQuiz.Quizs;
import com.example.quiz.adapterQuiz.dataQuiz;
import com.example.quiz.adapterQuiz.event_adapter_quiz;
import com.example.quiz.card.cardQuiz;
import com.example.quiz.people_play.event_adapter_people_play;
import com.example.quiz.people_play.peoplePlay;
import com.example.quiz.people_play.peoplesPlay;
import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class quiz_friend_user extends AppCompatActivity {
    FrameLayout content;
    private DocumentReference documentReference;
    String creatorId, quizId;
    private PreferenceManager preferenceManager;
    ConstraintLayout waitLayout, scorePanel;
    private List<load_quiz_question> events;
    playTrueOrFalse fragmentTrueOrFalse;
    playQuiz fragmentQuiz;
    playCheckbox fragmentCheckbox;
    private List<peoplePlay> score;
    public event_adapter_people_play adapter;
    playTyping fragmentTyping;
    ShimmerFrameLayout shimmerFrameLayout;
    RecyclerView recyclerView;
    int position = 0;
    int size;
    int sizeCheck = 0;
    String id, userId;
    ScrollView scrollView;
    Button play_with_friend;
    TextView oneName, twoName, thirdName, onePoint, twoPoint, thirdPoint;
    RoundedImageView oneImage, twoImage, thirdImage;
    ConstraintLayout finalScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_friend_user);
        preferenceManager = new PreferenceManager(getApplicationContext());
        events = new ArrayList<load_quiz_question>();
        shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmerLayoutScore);
        shimmerFrameLayout.startShimmer();
        recyclerView = (RecyclerView) findViewById(R.id.score_view);
        creatorId = getIntent().getSerializableExtra(Constants.CREATOR_NAME).toString();
        quizId = getIntent().getSerializableExtra(Constants.QUIZ_ID).toString();
        userId = getIntent().getSerializableExtra(Constants.KEY_USER_ID).toString();
        content = (FrameLayout) findViewById(R.id.content);
        waitLayout = (ConstraintLayout) findViewById(R.id.waitLayout);
        scorePanel = (ConstraintLayout) findViewById(R.id.scoreboard);
        oneName = (TextView) findViewById(R.id.welcome15);
        twoName = (TextView) findViewById(R.id.welcome13);
        thirdName = (TextView) findViewById(R.id.welcome17);
        onePoint = (TextView) findViewById(R.id.welcome16);
        twoPoint = (TextView) findViewById(R.id.welcome14);
        thirdPoint = (TextView) findViewById(R.id.welcome18);
        oneImage = (RoundedImageView) findViewById(R.id.profilePhoto3);
        twoImage = (RoundedImageView) findViewById(R.id.profilePhoto2);
        thirdImage = (RoundedImageView) findViewById(R.id.profilePhoto4);
        scrollView = (ScrollView) findViewById(R.id.scrollView45);
        finalScore = (ConstraintLayout) findViewById(R.id.finalScore);
        play_with_friend = (Button) findViewById(R.id.play_with_friend);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(creatorId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                id = documentSnapshot.getString(Constants.PLAY_WITH_FRIEND_ID);
            }
        });
        play_with_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play_with_friend.setEnabled(false);
                play_with_friend.setText("Завершение...");
                play_with_friend.setEnabled(true);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        getData();
        FirebaseFirestore databaseNext = FirebaseFirestore.getInstance();
        documentReference = databaseNext.collection(Constants.KEY_COLLECTION_USERS)
                .document(creatorId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.getString(Constants.NEXT_PLAY).equals("true")){
                    if (sizeCheck == 0){
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                                .document(creatorId);
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                size = Integer.parseInt(String.valueOf(documentSnapshot.get(Constants.COUNT_USER)));
                                sizeCheck = 1;
                            }
                        });
                    }
                    waitLayout.setVisibility(View.GONE);
                    FirebaseFirestore databaseCount = FirebaseFirestore.getInstance();
                    documentReference = databaseCount.collection(Constants.KEY_COLLECTION_USERS)
                            .document(creatorId);
                    documentReference.update(Constants.NEXT_PLAY, "");
                    nextQuestion();
                }
            }
        });
    }
    private void getData() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_QUESTION)
                .whereEqualTo(Constants.QUIZ_ID, quizId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        events = new ArrayList<load_quiz_question>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            load_quiz_questions quizPreviews = new load_quiz_questions();
                            quizPreviews.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            quizPreviews.type = queryDocumentSnapshot.getString(Constants.TYPE);
                            quizPreviews.question = queryDocumentSnapshot.getString(Constants.QUESTION);
                            quizPreviews.answer = queryDocumentSnapshot.getString(Constants.ANSWER);
                            quizPreviews.answerText = queryDocumentSnapshot.getString(Constants.ANSWER_TEXT);
                            quizPreviews.time = queryDocumentSnapshot.getString(Constants.TIME);
                            quizPreviews.point = queryDocumentSnapshot.getString(Constants.POINT);
                            events.add(new load_quiz_question(quizPreviews.image, quizPreviews.type, quizPreviews.question, quizPreviews.answer, quizPreviews.answerText, quizPreviews.time, quizPreviews.point));
                        }
                    }
                });
    }
    public void hideTrueOrFalse(){
        FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
        documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                documentReference.update(Constants.SCORE, String.valueOf(Integer.parseInt(documentSnapshot.getString(Constants.SCORE)) + Integer.parseInt(preferenceManager.getString(Constants.SCORE))));
            }
        });
        getScore();
        content.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        waitLayout.setVisibility(View.GONE);
        scorePanel.setVisibility(View.VISIBLE);
        if(getSupportFragmentManager().findFragmentById(R.id.content) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.content)).commit();
        }
    }

    public void hideQuiz(){
        FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
        documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                documentReference.update(Constants.SCORE, String.valueOf(Integer.parseInt(documentSnapshot.getString(Constants.SCORE)) + Integer.parseInt(preferenceManager.getString(Constants.SCORE))));
            }
        });
        getScore();
        content.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        waitLayout.setVisibility(View.GONE);
        scorePanel.setVisibility(View.VISIBLE);
        if(getSupportFragmentManager().findFragmentById(R.id.content) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.content)).commit();
        }
    }
    public void hideCheckbox(){
        FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
        documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                documentReference.update(Constants.SCORE, String.valueOf(Integer.parseInt(documentSnapshot.getString(Constants.SCORE)) + Integer.parseInt(preferenceManager.getString(Constants.SCORE))));
            }
        });
        getScore();
        content.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        waitLayout.setVisibility(View.GONE);
        scorePanel.setVisibility(View.VISIBLE);
        if(getSupportFragmentManager().findFragmentById(R.id.content) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.content)).commit();
        }
    }
    public void hideTyping(){
        FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
        documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                documentReference.update(Constants.SCORE, String.valueOf(Integer.parseInt(documentSnapshot.getString(Constants.SCORE)) + Integer.parseInt(preferenceManager.getString(Constants.SCORE))));
            }
        });
        getScore();
        content.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        waitLayout.setVisibility(View.GONE);
        scorePanel.setVisibility(View.VISIBLE);
        if(getSupportFragmentManager().findFragmentById(R.id.content) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.content)).commit();
        }
    }

    private void showScore() {
        try {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) scrollView.getLayoutParams();
            layoutParams.topMargin = 900;
            scrollView.setLayoutParams(layoutParams);
            finalScore.setVisibility(View.VISIBLE);
            oneName.setText(String.valueOf(score.get(0).getName()));
            onePoint.setText(String.valueOf(score.get(0).getScore()));
            byte[] bytes = Base64.decode(score.get(0).getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            oneImage.setImageBitmap(bitmap);
            twoName.setText(String.valueOf(score.get(1).getName()));
            twoPoint.setText(String.valueOf(score.get(1).getScore()));
            byte[] bytesTwo = Base64.decode(score.get(1).getImage(), Base64.DEFAULT);
            Bitmap bitmapTwo = BitmapFactory.decodeByteArray(bytesTwo, 0, bytesTwo.length);
            twoImage.setImageBitmap(bitmapTwo);
            if (score.size() > 2){
                thirdName.setText(String.valueOf(score.get(2).getName()));
                thirdPoint.setText(String.valueOf(score.get(2).getScore()));
                byte[] bytesThree = Base64.decode(score.get(2).getImage(), Base64.DEFAULT);
                Bitmap bitmapThree = BitmapFactory.decodeByteArray(bytesThree, 0, bytesThree.length);
                thirdImage.setImageBitmap(bitmapThree);
            }
            play_with_friend.setEnabled(true);
            play_with_friend.setText("Завершить");
        } catch (Exception e){

        }
    }

    private void nextQuestion() {
        try {
            content.setVisibility(View.VISIBLE);
            if (events.size() > 0){
                if (position == events.size()){

                } else {
                    waitLayout.setVisibility(View.GONE);
                    if (events.get(position).getType().equals("Квиз")){
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY_IMAGE, events.get(position).getImage());
                        bundle.putString(Constants.TIME, events.get(position).getTime());
                        bundle.putString(Constants.POINT, events.get(position).getPoint());
                        bundle.putString(Constants.QUESTION, events.get(position).getQuestion());
                        bundle.putString(Constants.ANSWER, events.get(position).getAnswer());
                        bundle.putString(Constants.ANSWER_TEXT, events.get(position).getAnswerText());
                        bundle.putString(Constants.COUNT, String.valueOf(size));
                        bundle.putString(Constants.CREATOR_NAME, creatorId);
                        bundle.putString(Constants.TYPE, "friend");
                        bundle.putString(Constants.KEY_USER_ID, userId);
                        fragmentQuiz = new playQuiz();
                        fragmentQuiz.setArguments(bundle);
                        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
                        fragmentTransaction.replace(R.id.content, fragmentQuiz,"");
                        fragmentTransaction.commit();
                    } else if (events.get(position).getType().equals("Несколько")){
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY_IMAGE, events.get(position).getImage());
                        bundle.putString(Constants.TIME, events.get(position).getTime());
                        bundle.putString(Constants.POINT, events.get(position).getPoint());
                        bundle.putString(Constants.QUESTION, events.get(position).getQuestion());
                        bundle.putString(Constants.ANSWER, events.get(position).getAnswer());
                        bundle.putString(Constants.ANSWER_TEXT, events.get(position).getAnswerText());
                        bundle.putString(Constants.COUNT, String.valueOf(size));
                        bundle.putString(Constants.CREATOR_NAME, creatorId);
                        bundle.putString(Constants.TYPE, "friend");
                        bundle.putString(Constants.KEY_USER_ID, userId);
                        fragmentCheckbox = new playCheckbox();
                        fragmentCheckbox.setArguments(bundle);
                        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
                        fragmentTransaction.replace(R.id.content, fragmentCheckbox,"");
                        fragmentTransaction.commit();
                    } else if (events.get(position).getType().equals("Правда или ложь")){
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY_IMAGE, events.get(position).getImage());
                        bundle.putString(Constants.TIME, events.get(position).getTime());
                        bundle.putString(Constants.POINT, events.get(position).getPoint());
                        bundle.putString(Constants.QUESTION, events.get(position).getQuestion());
                        bundle.putString(Constants.ANSWER, events.get(position).getAnswer());
                        bundle.putString(Constants.COUNT, String.valueOf(size));
                        bundle.putString(Constants.CREATOR_NAME, creatorId);
                        bundle.putString(Constants.TYPE, "friend");
                        bundle.putString(Constants.KEY_USER_ID, userId);
                        fragmentTrueOrFalse = new playTrueOrFalse();
                        fragmentTrueOrFalse.setArguments(bundle);
                        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
                        fragmentTransaction.replace(R.id.content, fragmentTrueOrFalse,"");
                        fragmentTransaction.commit();
                    } else if (events.get(position).getType().equals("Текст")){
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY_IMAGE, events.get(position).getImage());
                        bundle.putString(Constants.TIME, events.get(position).getTime());
                        bundle.putString(Constants.POINT, events.get(position).getPoint());
                        bundle.putString(Constants.QUESTION, events.get(position).getQuestion());
                        bundle.putString(Constants.ANSWER, events.get(position).getAnswer());
                        bundle.putString(Constants.COUNT, String.valueOf(size));
                        bundle.putString(Constants.CREATOR_NAME, creatorId);
                        bundle.putString(Constants.TYPE, "friend");
                        bundle.putString(Constants.KEY_USER_ID, userId);
                        fragmentTyping = new playTyping();
                        fragmentTyping.setArguments(bundle);
                        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
                        fragmentTransaction.replace(R.id.content, fragmentTyping,"");
                        fragmentTransaction.commit();
                    }
                    position += 1;
                }
            }
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void getScore()
    {
        try {
            play_with_friend.setVisibility(View.VISIBLE);
            play_with_friend.setEnabled(false);
            play_with_friend.setText("Загружаем баллы...");
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            score = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                if (id.contains(queryDocumentSnapshot.getId())){
                                    peoplesPlay data = new peoplesPlay();
                                    data.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                    data.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                    data.score = queryDocumentSnapshot.getString(Constants.SCORE);
                                    data.id = queryDocumentSnapshot.getId();
                                    score.add(new peoplePlay(data.image, data.name, data.score, data.id));
                                }
                            }
                            if (score.size() > 0) {
                                Comparator<peoplePlay> descendingComparator = new Comparator<peoplePlay>() {
                                    @Override
                                    public int compare(peoplePlay a, peoplePlay b) {
                                        int valueA = Integer.parseInt(a.getScore());
                                        int valueB = Integer.parseInt(b.getScore());
                                        return valueB - valueA;
                                    }
                                };
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                adapter = new event_adapter_people_play(score, getApplicationContext());
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(layoutManager);
                                Collections.sort(score, descendingComparator);
                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                                if (position == events.size()){
                                    showScore();
                                }
                            }
                        }
                    });
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {

    }
}