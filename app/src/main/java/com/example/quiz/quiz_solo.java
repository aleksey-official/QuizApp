package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.adapterQuiz.dataQuiz;
import com.example.quiz.adapterQuiz.event_adapter_quiz;
import com.example.quiz.quiz_preview.event_adapter_quiz_view;
import com.example.quiz.quiz_preview.preview;
import com.example.quiz.quiz_preview.quizPreviews;
import com.example.quiz.solo.event_adapter_solo;
import com.example.quiz.solo.solo;
import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class quiz_solo extends AppCompatActivity {
    ImageView logo, medal;
    TextView loadText, getText, pointText, plusPoint;
    ProgressBar loadBar;
    String quizId;
    String questionId;
    Button nextBtn;
    private DocumentReference documentReference;
    private List<load_quiz_question> events;
    int position = 0;
    String score = "0";
    playTrueOrFalse fragmentTrueOrFalse;
    playQuiz fragmentQuiz;
    playCheckbox fragmentCheckbox;
    playTyping fragmentTyping;
    private PreferenceManager preferenceManager;
    ConstraintLayout scorePanel;
    private List<solo> solos;
    public event_adapter_solo adapter;
    RecyclerView recyclerView;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_solo);
        recyclerView = (RecyclerView) findViewById(R.id.question_view);
        recyclerView.setAdapter(adapter);
        solos = new ArrayList<>();
        preferenceManager = new PreferenceManager(getApplicationContext());
        quizId = getIntent().getSerializableExtra(Constants.QUIZ_ID).toString();
        logo = (ImageView) findViewById(R.id.imageView10);
        medal = (ImageView) findViewById(R.id.imageView12);
        loadText = (TextView) findViewById(R.id.loadingText);
        getText = (TextView) findViewById(R.id.pointGettext);
        pointText = (TextView) findViewById(R.id.name_quiz2);
        plusPoint = (TextView) findViewById(R.id.name_quiz3);
        loadBar = (ProgressBar) findViewById(R.id.loadingBar);
        nextBtn = (Button) findViewById(R.id.nextQuestionButton2);
        scorePanel = (ConstraintLayout) findViewById(R.id.scorePanel);
        view = (View) findViewById(R.id.view);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constants.KEY_COLLECTION_QUIZ)
                .document(quizId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                questionId = documentSnapshot.getString(Constants.QUESTION_ID);
            }
        });
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1500);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nextBtn.getText().toString().equals("Завершить")){
                    nextQuestion();
                } else {
                    nextBtn.setEnabled(false);
                    nextBtn.setText("Завершение...");
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    documentReference = database.collection(Constants.KEY_COLLECTION_QUIZ)
                            .document(quizId);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            documentReference.update(Constants.PLAY, String.valueOf(Integer.parseInt(documentSnapshot.getString(Constants.PLAY)) + 1));
                            nextBtn.setEnabled(true);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    });
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
                        if (events.size() > 0){
                            nextQuestion();
                        }
                    }
                });
    }

    private void nextQuestion() {
        try {
            if (position == 0){
                logo.setVisibility(View.GONE);
                loadText.setVisibility(View.GONE);
                loadBar.setVisibility(View.GONE);
            }
            if (events.size() > 0){
                if (position == events.size()){
                    nextBtn.setVisibility(View.GONE);
                } else {
                    nextBtn.setVisibility(View.GONE);
                    if (events.get(position).getType().equals("Квиз")){
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY_IMAGE, events.get(position).getImage());
                        bundle.putString(Constants.TIME, events.get(position).getTime());
                        bundle.putString(Constants.POINT, events.get(position).getPoint());
                        bundle.putString(Constants.QUESTION, events.get(position).getQuestion());
                        bundle.putString(Constants.ANSWER, events.get(position).getAnswer());
                        bundle.putString(Constants.ANSWER_TEXT, events.get(position).getAnswerText());
                        bundle.putString(Constants.COUNT, String.valueOf(1));
                        bundle.putString(Constants.CREATOR_NAME, "you");
                        bundle.putString(Constants.TYPE, "solo");
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
                        bundle.putString(Constants.COUNT, String.valueOf(1));
                        bundle.putString(Constants.CREATOR_NAME, "you");
                        bundle.putString(Constants.TYPE, "solo");
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
                        bundle.putString(Constants.COUNT, String.valueOf(1));
                        bundle.putString(Constants.CREATOR_NAME, "you");
                        bundle.putString(Constants.TYPE, "solo");
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
                        bundle.putString(Constants.COUNT, String.valueOf(1));
                        bundle.putString(Constants.CREATOR_NAME, "you");
                        bundle.putString(Constants.TYPE, "solo");
                        bundle.putString(Constants.KEY_USER_ID, "");
                        fragmentTyping = new playTyping();
                        fragmentTyping.setArguments(bundle);
                        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
                        fragmentTransaction.replace(R.id.content, fragmentTyping,"");
                        fragmentTransaction.commit();
                    }
                }
            }
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void hideTrueOrFalse(){
        if(getSupportFragmentManager().findFragmentById(R.id.content) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.content)).commit();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragmentTrueOrFalse)
                .commit();
        nextBtn.setVisibility(View.VISIBLE);
        getText.setVisibility(View.VISIBLE);
        pointText.setVisibility(View.VISIBLE);
        scorePanel.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        if (Integer.parseInt(preferenceManager.getString(Constants.SCORE)) != 0){
            solos.add(new solo(events.get(position).image, events.get(position).type, events.get(position).question, preferenceManager.getString(Constants.SCORE) + ";correct"));
        } else {
            solos.add(new solo(events.get(position).image, events.get(position).type, events.get(position).question, preferenceManager.getString(Constants.SCORE) + ";uncorrect"));
        }
        DividerItemDecoration divider = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL
        );
        divider.setDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.dividernewquiz)
        );
        recyclerView.addItemDecoration(divider);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new event_adapter_solo(solos, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        position += 1;
        try {
            score = String.valueOf(Integer.parseInt(score) + Integer.parseInt(String.valueOf(preferenceManager.getString(Constants.SCORE))));
            if (position == events.size()) {
                getText.setText("Итоговый счёт:");
                pointText.setText(score);
                plusPoint.setText("+" + String.valueOf(preferenceManager.getString(Constants.SCORE)));
                nextBtn.setText("Завершить");
            } else {
                pointText.setText(score);
                plusPoint.setText("+" + String.valueOf(preferenceManager.getString(Constants.SCORE)));
            }
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void hideQuiz(){
        if(getSupportFragmentManager().findFragmentById(R.id.content) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.content)).commit();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragmentQuiz)
                .commit();
        if (Integer.parseInt(preferenceManager.getString(Constants.SCORE)) != 0){
            solos.add(new solo(events.get(position).image, events.get(position).type, events.get(position).question, preferenceManager.getString(Constants.SCORE) + ";correct"));
        } else {
            solos.add(new solo(events.get(position).image, events.get(position).type, events.get(position).question, preferenceManager.getString(Constants.SCORE) + ";uncorrect"));
        }
        DividerItemDecoration divider = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL
        );
        divider.setDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.dividernewquiz)
        );
        recyclerView.addItemDecoration(divider);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new event_adapter_solo(solos, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        position += 1;
        nextBtn.setVisibility(View.VISIBLE);
        getText.setVisibility(View.VISIBLE);
        pointText.setVisibility(View.VISIBLE);
        scorePanel.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        try {
            score = String.valueOf(Integer.parseInt(score) + Integer.parseInt(String.valueOf(preferenceManager.getString(Constants.SCORE))));
            if (position == events.size()) {
                getText.setText("Итоговый счёт:");
                pointText.setText(score);
                plusPoint.setText("+" + String.valueOf(preferenceManager.getString(Constants.SCORE)));
                nextBtn.setText("Завершить");
            } else {
                pointText.setText(score);
                plusPoint.setText("+" + String.valueOf(preferenceManager.getString(Constants.SCORE)));
            }
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void hideCheckbox(){
        if(getSupportFragmentManager().findFragmentById(R.id.content) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.content)).commit();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragmentCheckbox)
                .commit();
        if (Integer.parseInt(preferenceManager.getString(Constants.SCORE)) != 0){
            solos.add(new solo(events.get(position).image, events.get(position).type, events.get(position).question, preferenceManager.getString(Constants.SCORE) + ";correct"));
        } else {
            solos.add(new solo(events.get(position).image, events.get(position).type, events.get(position).question, preferenceManager.getString(Constants.SCORE) + ";uncorrect"));
        }
        DividerItemDecoration divider = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL
        );
        divider.setDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.dividernewquiz)
        );
        recyclerView.addItemDecoration(divider);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new event_adapter_solo(solos, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        position += 1;
        nextBtn.setVisibility(View.VISIBLE);
        getText.setVisibility(View.VISIBLE);
        pointText.setVisibility(View.VISIBLE);
        scorePanel.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        try {
            score = String.valueOf(Integer.parseInt(score) + Integer.parseInt(String.valueOf(preferenceManager.getString(Constants.SCORE))));
            if (position == events.size()) {
                getText.setText("Итоговый счёт:");
                pointText.setText(score);
                plusPoint.setText("+" + String.valueOf(preferenceManager.getString(Constants.SCORE)));
                nextBtn.setText("Завершить");
            } else {
                pointText.setText(score);
                plusPoint.setText("+" + String.valueOf(preferenceManager.getString(Constants.SCORE)));
            }
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void hideTyping(){
        if(getSupportFragmentManager().findFragmentById(R.id.content) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.content)).commit();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragmentTyping)
                .commit();
        if (Integer.parseInt(preferenceManager.getString(Constants.SCORE)) != 0){
            solos.add(new solo(events.get(position).image, events.get(position).type, events.get(position).question, preferenceManager.getString(Constants.SCORE) + ";correct"));
        } else {
            solos.add(new solo(events.get(position).image, events.get(position).type, events.get(position).question, preferenceManager.getString(Constants.SCORE) + ";uncorrect"));
        }
        DividerItemDecoration divider = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL
        );
        divider.setDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.dividernewquiz)
        );
        recyclerView.addItemDecoration(divider);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new event_adapter_solo(solos, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        position += 1;
        nextBtn.setVisibility(View.VISIBLE);
        getText.setVisibility(View.VISIBLE);
        pointText.setVisibility(View.VISIBLE);
        scorePanel.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        try {
            score = String.valueOf(Integer.parseInt(score) + Integer.parseInt(String.valueOf(preferenceManager.getString(Constants.SCORE))));
            if (position == events.size()) {
                getText.setText("Итоговый счёт:");
                pointText.setText(score);
                medal.setVisibility(View.VISIBLE);
                plusPoint.setText("+" + String.valueOf(preferenceManager.getString(Constants.SCORE)));
                nextBtn.setText("Завершить");
            } else {
                pointText.setText(score);
                plusPoint.setText("+" + String.valueOf(preferenceManager.getString(Constants.SCORE)));
            }
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

    }
}