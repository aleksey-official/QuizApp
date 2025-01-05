package com.example.quiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.card.cardQuiz;
import com.example.quiz.people.People;
import com.example.quiz.people.Peoples;
import com.example.quiz.people.event_adapter_people;
import com.example.quiz.people_play.event_adapter_people_play;
import com.example.quiz.people_play.peoplePlay;
import com.example.quiz.people_play.peoplesPlay;
import com.example.quiz.people_quiz_add.event_adapter_people_quiz_add;
import com.example.quiz.people_quiz_add.peopleQuizAdd;
import com.example.quiz.people_quiz_add.peoplesQuizAdd;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class quiz_friend extends AppCompatActivity {
    ImageView quizImage, imageQr;
    TextView nameQuiz, countPeople, code, oneName, twoName, thirdName, onePoint, twoPoint, thirdPoint;
    RoundedImageView oneImage, twoImage, thirdImage;
    ShimmerFrameLayout shimmerFrameLayout, shimmerLayoutScore;
    RecyclerView peopleView, scoreView;
    private List<peopleQuizAdd> people;
    private List<peoplePlay> scorePlay;
    private DocumentReference documentReference;
    public event_adapter_people_quiz_add peopleAdapter;
    public event_adapter_people_play playAdapter;
    private PreferenceManager preferenceManager;
    String idText;
    String id;
    String quizId;
    Button play_with_friend, doneQr, showQr;
    FrameLayout content;
    ConstraintLayout loadUser, scoreboard, waitLayout, connectQr, finalScore, quiz;
    int position = 0;
    playTrueOrFalse fragmentTrueOrFalse;
    playQuiz fragmentQuiz;
    playCheckbox fragmentCheckbox;
    playTyping fragmentTyping;
    String creatorId;
    ScrollView scrollView;
    private List<load_quiz_question> events;
    int loadUserNumber =  0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_friend);
        events = new ArrayList<load_quiz_question>();
        preferenceManager = new PreferenceManager(getApplicationContext());
        creatorId = getIntent().getSerializableExtra(Constants.CREATOR_NAME).toString();
        quizId = getIntent().getSerializableExtra(Constants.QUIZ_ID).toString();
        quizImage = (ImageView) findViewById(R.id.imageQuiz);
        nameQuiz = (TextView) findViewById(R.id.quizName);
        code = (TextView) findViewById(R.id.welcome1253);
        imageQr = (ImageView) findViewById(R.id.imageQr);
        countPeople = (TextView) findViewById(R.id.countPeople);
        oneName = (TextView) findViewById(R.id.welcome15);
        twoName = (TextView) findViewById(R.id.welcome13);
        thirdName = (TextView) findViewById(R.id.welcome17);
        onePoint = (TextView) findViewById(R.id.welcome16);
        twoPoint = (TextView) findViewById(R.id.welcome14);
        thirdPoint = (TextView) findViewById(R.id.welcome18);
        oneImage = (RoundedImageView) findViewById(R.id.profilePhoto3);
        twoImage = (RoundedImageView) findViewById(R.id.profilePhoto2);
        thirdImage = (RoundedImageView) findViewById(R.id.profilePhoto4);
        shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmerLayout);
        shimmerLayoutScore = (ShimmerFrameLayout) findViewById(R.id.shimmerLayoutScore);
        peopleView = (RecyclerView) findViewById(R.id.question_view);
        scoreView = (RecyclerView) findViewById(R.id.score_view);
        loadUser = (ConstraintLayout) findViewById(R.id.loadUser);
        scoreboard = (ConstraintLayout) findViewById(R.id.scoreboard);
        waitLayout = (ConstraintLayout) findViewById(R.id.waitLayout);
        connectQr = (ConstraintLayout) findViewById(R.id.connectQr);
        finalScore = (ConstraintLayout) findViewById(R.id.finalScore);
        quiz = (ConstraintLayout) findViewById(R.id.quiz);
        content = (FrameLayout) findViewById(R.id.content);
        play_with_friend = (Button) findViewById(R.id.play_with_friend);
        doneQr = (Button) findViewById(R.id.doneQr);
        showQr = (Button) findViewById(R.id.showQr);
        scrollView = (ScrollView) findViewById(R.id.scrollView45);
        peopleView.setAdapter(peopleAdapter);
        scoreView.setAdapter(playAdapter);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(creatorId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                code.setText(documentSnapshot.getString(Constants.CODE));
            }
        });
        showQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectQr.setVisibility(View.VISIBLE);
                play_with_friend.setVisibility(View.GONE);
                showQr.setVisibility(View.GONE);
            }
        });
        if (preferenceManager.getString(Constants.KEY_USER_ID).equals(creatorId)){
            loadUser.setVisibility(View.VISIBLE);
        }
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(creatorId, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            imageQr.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        doneQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectQr.setVisibility(View.GONE);
                play_with_friend.setVisibility(View.VISIBLE);
                showQr.setVisibility(View.VISIBLE);
            }
        });
        if (!preferenceManager.getString(Constants.KEY_USER_ID).equals(creatorId)){
            waitLayout.setVisibility(View.VISIBLE);
            connectQr.setVisibility(View.GONE);
        }
        FirebaseFirestore databaseQuiz = FirebaseFirestore.getInstance();
        documentReference = databaseQuiz.collection(Constants.KEY_COLLECTION_QUIZ)
                .document(quizId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                nameQuiz.setText(documentSnapshot.getString(Constants.KEY_NAME));
                if (!documentSnapshot.getString(Constants.KEY_IMAGE).equals("")){
                    byte[] bytes = Base64.decode(documentSnapshot.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    quizImage.setImageBitmap(bitmap);
                }
            }
        });
                FirebaseFirestore databasePeople = FirebaseFirestore.getInstance();
                documentReference = databasePeople.collection(Constants.KEY_COLLECTION_USERS)
                        .document(creatorId);
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.getString(Constants.PLAY_WITH_FRIEND_ID).trim().isEmpty()){
                            id = value.getString(Constants.PLAY_WITH_FRIEND_ID);
                            getPeople();
                        }
                    }
                });
        play_with_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (play_with_friend.getText().toString().equals("Завершить")){
                    play_with_friend.setEnabled(false);
                    play_with_friend.setText("Завершение...");
                    FirebaseFirestore databaseQuiz = FirebaseFirestore.getInstance();
                    documentReference = databaseQuiz.collection(Constants.KEY_COLLECTION_QUIZ)
                            .document(quizId);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            documentReference.update(Constants.PLAY, String.valueOf(Integer.parseInt(documentSnapshot.getString(Constants.PLAY)) + 1));
                            play_with_friend.setEnabled(true);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    });
                } else {
                    if (people.size() > 1){
                        showQr.setVisibility(View.GONE);
                        waitLayout.setVisibility(View.GONE);
                        play_with_friend.setText("Загрузка...");
                        if (events.size() > 0){
                            FirebaseFirestore databaseCount = FirebaseFirestore.getInstance();
                            documentReference = databaseCount.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(creatorId);
                            documentReference.update(Constants.NEXT_PLAY, "true");
                            documentReference.update(Constants.COUNT_ANSWERED, "0");
                            documentReference.update(Constants.NEXT_PLAY, "");
                            nextQuestion();
                        } else {
                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(creatorId);
                            documentReference.update(Constants.COUNT_USER, people.size());
                            getData();
                        }
                    } else {
                        Toast.makeText(quiz_friend.this, "Дождитесь подключения участников", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void getPeople() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        people = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (id.contains(queryDocumentSnapshot.getId().toString())){
                                peoplesQuizAdd peoples = new peoplesQuizAdd();
                                peoples.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                peoples.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                people.add(new peopleQuizAdd(peoples.image, peoples.name));
                            }
                        }
                        if (people.size() > 0) {
                            DividerItemDecoration divider = new DividerItemDecoration(
                                    peopleView.getContext(), DividerItemDecoration.VERTICAL
                            );
                            divider.setDrawable(
                                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.dividernew)
                            );
                            countPeople.setText("Подключено " + people.size() + " участник-(а)");
                            peopleView.addItemDecoration(divider);
                            peopleView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                            peopleAdapter = new event_adapter_people_quiz_add(people, getApplicationContext());
                            peopleView.setAdapter(peopleAdapter);
                            peopleAdapter.notifyDataSetChanged();
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private void getScore() {
        try {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            scorePlay = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                if (id.contains(queryDocumentSnapshot.getId().toString())){
                                    peoplesPlay peoples = new peoplesPlay();
                                    peoples.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                    peoples.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                    peoples.score = queryDocumentSnapshot.getString(Constants.SCORE);
                                    peoples.id = queryDocumentSnapshot.getId();
                                    scorePlay.add(new peoplePlay(peoples.image, peoples.name, peoples.score, peoples.id));
                                }
                            }
                            if (scorePlay.size() > 0) {
                                try {
                                    Comparator<peoplePlay> descendingComparator = new Comparator<peoplePlay>() {
                                        @Override
                                        public int compare(peoplePlay a, peoplePlay b) {
                                            int valueA = Integer.parseInt(a.getScore());
                                            int valueB = Integer.parseInt(b.getScore());
                                            return valueB - valueA;
                                        }
                                    };
                                    LinearLayoutManager layoutManager
                                            = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                    scoreView.setLayoutManager(layoutManager);
                                    playAdapter = new event_adapter_people_play(scorePlay, getApplicationContext());
                                    scoreView.setAdapter(playAdapter);
                                    Collections.sort(scorePlay, descendingComparator);
                                    playAdapter.notifyDataSetChanged();
                                    shimmerLayoutScore.stopShimmer();
                                    shimmerLayoutScore.setVisibility(View.GONE);
                                } catch (Exception e){
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
        if (position == events.size()){
            play_with_friend.setText("Завершить");
            play_with_friend.setVisibility(View.VISIBLE);
            showScore();
        }  else if (preferenceManager.getString(Constants.KEY_USER_ID).equals(creatorId)){
            play_with_friend.setText("Дальше");
            play_with_friend.setVisibility(View.VISIBLE);
        }
        loadUser.setVisibility(View.GONE);
        scoreboard.setVisibility(View.VISIBLE);
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
        if (position == events.size()){
            play_with_friend.setText("Завершить");
            play_with_friend.setVisibility(View.VISIBLE);
            showScore();
        }  else if (preferenceManager.getString(Constants.KEY_USER_ID).equals(creatorId)){
            play_with_friend.setText("Дальше");
            play_with_friend.setVisibility(View.VISIBLE);
        }
        loadUser.setVisibility(View.GONE);
        scoreboard.setVisibility(View.VISIBLE);
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
        if (position == events.size()){
            play_with_friend.setText("Завершить");
            play_with_friend.setVisibility(View.VISIBLE);
            showScore();
        }  else if (preferenceManager.getString(Constants.KEY_USER_ID).equals(creatorId)){
            play_with_friend.setText("Дальше");
            play_with_friend.setVisibility(View.VISIBLE);
        }
        loadUser.setVisibility(View.GONE);
        scoreboard.setVisibility(View.VISIBLE);
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
        if (position == events.size()){
            play_with_friend.setText("Завершить");
            play_with_friend.setVisibility(View.VISIBLE);
            showScore();
        } else if (preferenceManager.getString(Constants.KEY_USER_ID).equals(creatorId)){
            play_with_friend.setText("Дальше");
            play_with_friend.setVisibility(View.VISIBLE);
        }
        loadUser.setVisibility(View.GONE);
        scoreboard.setVisibility(View.VISIBLE);
        if(getSupportFragmentManager().findFragmentById(R.id.content) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.content)).commit();
        }
    }

    private void showScore() {
        try {
            play_with_friend.setEnabled(false);
            play_with_friend.setText("Загружаем баллы...");
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) scrollView.getLayoutParams();
            layoutParams.topMargin = 900;
            scrollView.setLayoutParams(layoutParams);
            finalScore.setVisibility(View.VISIBLE);
            oneName.setText(String.valueOf(scorePlay.get(0).getName()));
            onePoint.setText(String.valueOf(scorePlay.get(0).getScore()));
            byte[] bytes = Base64.decode(scorePlay.get(0).getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            oneImage.setImageBitmap(bitmap);
            twoName.setText(String.valueOf(scorePlay.get(1).getName()));
            twoPoint.setText(String.valueOf(scorePlay.get(1).getScore()));
            byte[] bytesTwo = Base64.decode(scorePlay.get(1).getImage(), Base64.DEFAULT);
            Bitmap bitmapTwo = BitmapFactory.decodeByteArray(bytesTwo, 0, bytesTwo.length);
            twoImage.setImageBitmap(bitmapTwo);
            if (scorePlay.size() > 2){
                thirdName.setText(String.valueOf(scorePlay.get(2).getName()));
                thirdPoint.setText(String.valueOf(scorePlay.get(2).getScore()));
                byte[] bytesThree = Base64.decode(scorePlay.get(2).getImage(), Base64.DEFAULT);
                Bitmap bitmapThree = BitmapFactory.decodeByteArray(bytesThree, 0, bytesThree.length);
                thirdImage.setImageBitmap(bitmapThree);
            }
            play_with_friend.setEnabled(true);
            play_with_friend.setText("Завершить");
        } catch (Exception e){

        }
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
                        if (events.size() > 0){
                            FirebaseFirestore databaseCount = FirebaseFirestore.getInstance();
                            documentReference = databaseCount.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(creatorId);
                            documentReference.update(Constants.NEXT_PLAY, "true");
                            nextQuestion();
                        }
                    }
                });
    }

    private void nextQuestion() {
        try {
            content.setVisibility(View.VISIBLE);
            if (events.size() > 0){
                if (position == events.size()){
                    play_with_friend.setText("Завершить");
                } else {
                    waitLayout.setVisibility(View.GONE);
                    scoreboard.setVisibility(View.VISIBLE);
                    play_with_friend.setVisibility(View.GONE);
                    if (events.get(position).getType().equals("Квиз")){
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY_IMAGE, events.get(position).getImage());
                        bundle.putString(Constants.TIME, events.get(position).getTime());
                        bundle.putString(Constants.POINT, events.get(position).getPoint());
                        bundle.putString(Constants.QUESTION, events.get(position).getQuestion());
                        bundle.putString(Constants.ANSWER, events.get(position).getAnswer());
                        bundle.putString(Constants.ANSWER_TEXT, events.get(position).getAnswerText());
                        bundle.putString(Constants.COUNT, String.valueOf(people.size()));
                        bundle.putString(Constants.CREATOR_NAME, creatorId);
                        bundle.putString(Constants.TYPE, "friend");
                        bundle.putString(Constants.KEY_USER_ID, "");
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
                        bundle.putString(Constants.COUNT, String.valueOf(people.size()));
                        bundle.putString(Constants.CREATOR_NAME, creatorId);
                        bundle.putString(Constants.TYPE, "friend");
                        bundle.putString(Constants.KEY_USER_ID, "");
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
                        bundle.putString(Constants.COUNT, String.valueOf(people.size()));
                        bundle.putString(Constants.CREATOR_NAME, creatorId);
                        bundle.putString(Constants.TYPE, "friend");
                        bundle.putString(Constants.KEY_USER_ID, "");
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
                        bundle.putString(Constants.COUNT, String.valueOf(people.size()));
                        bundle.putString(Constants.CREATOR_NAME, creatorId);
                        bundle.putString(Constants.KEY_USER_ID, "");
                        bundle.putString(Constants.TYPE, "friend");
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

    @Override
    public void onBackPressed() {

    }
}