package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.questionAdd.Events;
import com.example.quiz.questionAdd.data;
import com.example.quiz.questionAdd.event_adapter;
import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class quizQuestion extends AppCompatActivity {
    Button addQuestion, finishButton;
    public event_adapter adapter;
    private List<data> events;
    RecyclerView recyclerView;
    public ImageView NoEventImage;
    public TextView NoEventText;
    private PreferenceManager preferenceManager;
    private DocumentReference documentReference;
    public int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_question);
        preferenceManager = new PreferenceManager(getApplicationContext());
        preferenceManager.putString(Constants.QUIZ_ID, getIntent().getSerializableExtra(Constants.QUIZ_ID).toString());
        addQuestion = (Button) findViewById(R.id.play_solo);
        NoEventImage = (ImageView) findViewById(R.id.noImage5);
        NoEventText = (TextView) findViewById(R.id.noText5);
        finishButton = (Button) findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (events.size() > 0){
                    try {
                        preferenceManager = new PreferenceManager(getApplicationContext());
                        finishButton.setText("Создание...");
                        FirebaseFirestore databaseCount = FirebaseFirestore.getInstance();
                        documentReference = databaseCount.collection(Constants.KEY_COLLECTION_QUIZ)
                                .document(preferenceManager.getString(Constants.QUIZ_ID));
                        documentReference.update(Constants.COUNT, preferenceManager.getString(Constants.COUNT_QUESTION));
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        Dialog dialog = new Dialog(quizQuestion.this);
                        dialog.setContentView(R.layout.correctlayout);
                        TextView textInfo = dialog.findViewById(R.id.textinfo);
                        textInfo.setText("Квиз успешно создан");
                        Button save = dialog.findViewById(R.id.saveQuestion);
                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(false);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                        dialog.show();                    } catch (Exception e){
                        Toast.makeText(quizQuestion.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(quizQuestion.this, "Добавьте вопрос", Toast.LENGTH_SHORT).show();
                }
            }
        });
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog dialog = new BottomSheetDialog(quizQuestion.this);
                dialog.setContentView((R.layout.questiontype));
                ImageButton quiz, true_or_false, text, checkbox;
                quiz = (ImageButton) dialog.findViewById(R.id.quizQuestion);
                true_or_false = (ImageButton) dialog.findViewById(R.id.trueorfalseQuestion);
                text = (ImageButton) dialog.findViewById(R.id.textQuestion);
                checkbox = (ImageButton) dialog.findViewById(R.id.checkboxQuestion);
                try {
                    true_or_false.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), True_Or_False.class);
                            intent.putExtra(Constants.QUIZ_ID, getIntent().getSerializableExtra(Constants.QUIZ_ID));
                            intent.putExtra("id", "false");
                            startActivity(intent);
                            finish();
                        }
                    });
                    quiz.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), Quiz.class);
                            intent.putExtra(Constants.QUIZ_ID, getIntent().getSerializableExtra(Constants.QUIZ_ID));
                            intent.putExtra("id", "false");
                            startActivity(intent);
                            finish();
                        }
                    });
                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), typingQuiz.class);
                            intent.putExtra(Constants.QUIZ_ID, getIntent().getSerializableExtra(Constants.QUIZ_ID));
                            intent.putExtra("id", "false");
                            startActivity(intent);
                            finish();
                        }
                    });
                    checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), checkboxQuiz.class);
                            intent.putExtra(Constants.QUIZ_ID, getIntent().getSerializableExtra(Constants.QUIZ_ID));
                            intent.putExtra("id", "false");
                            startActivity(intent);
                            finish();
                        }
                    });
                } catch (Exception e)
                {
                    Toast.makeText(quizQuestion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                dialog.show();
            }
        });
        List<data> list = new ArrayList<>();
        getData();
        recyclerView = (RecyclerView)findViewById(R.id.questionView);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }
    private void getData()
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_QUESTION)
                .whereEqualTo(Constants.QUIZ_ID, preferenceManager.getString(Constants.QUIZ_ID))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null)
                    {
                        events = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult())
                        {
                            Events data = new Events();
                            data.type = queryDocumentSnapshot.getString(Constants.TYPE);
                            data.textQuestion = queryDocumentSnapshot.getString(Constants.QUESTION);
                            data.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            data.id = queryDocumentSnapshot.getId();
                            events.add(new data(data.type, data.textQuestion, data.image, data.id));
                        }
                        if (events.size() > 0) {
                            adapter = new event_adapter(events, getApplicationContext());
                            DividerItemDecoration divider = new DividerItemDecoration(
                                    recyclerView.getContext(), DividerItemDecoration.VERTICAL
                            );
                            divider.setDrawable(
                                    ContextCompat.getDrawable(getBaseContext(), R.drawable.divider)
                            );

                            recyclerView.addItemDecoration(divider);
                            recyclerView.setAdapter(adapter);
                            NoEventText.setVisibility(View.INVISIBLE);
                            NoEventImage.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            showErrorMessage();
                        }
                    }
                    else
                    {
                        showErrorMessage();
                    }
                });
    }

    @Override
    public void onBackPressed() {

    }

    public void countAdd(){
        count++;
    }

    private void showErrorMessage() {
        NoEventText.setVisibility(View.VISIBLE);
        NoEventImage.setVisibility(View.VISIBLE);
    }
}