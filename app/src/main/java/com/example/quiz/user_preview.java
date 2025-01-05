package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class user_preview extends AppCompatActivity {
    TextView name, email, quizCount, quizNumber, followers, following, noText;
    RoundedImageView profilePhoto;
    Button subscribe;
    ImageView noImage, back_image_click;
    private DocumentReference documentReference;
    private List<dataQuiz> events;
    public event_adapter_quiz adapter;
    RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerFrameLayout;
    String userId = "";
    private PreferenceManager preferenceManager;
    String followings = "";
    int count_followers = 0;
    int count_followers_user = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preview);
        preferenceManager = new PreferenceManager(getApplicationContext());
        userId = getIntent().getSerializableExtra(Constants.KEY_USER_ID).toString();
        shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmer();
        List<dataQuiz> list = new ArrayList<>();
        getData();
        recyclerView = (RecyclerView) findViewById(R.id.question_view);
        recyclerView.setAdapter(adapter);
        noText = (TextView) findViewById(R.id.noText);
        noImage = (ImageView) findViewById(R.id.noImage);
        back_image_click = (ImageView) findViewById(R.id.back_image_click);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        quizCount = (TextView) findViewById(R.id.quizCount);
        followers = (TextView) findViewById(R.id.followers);
        quizNumber = (TextView) findViewById(R.id.quizNumber);
        following = (TextView) findViewById(R.id.following);
        profilePhoto = (RoundedImageView) findViewById(R.id.profilePhoto);
        subscribe = (Button) findViewById(R.id.editProfile);
        back_image_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name.setText(documentSnapshot.getString(Constants.KEY_NAME));
                email.setText("@" + documentSnapshot.getString(Constants.NICK_NAME));
                byte[] bytes = Base64.decode(documentSnapshot.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePhoto.setImageBitmap(bitmap);
                quizCount.setText(documentSnapshot.get(Constants.COUNT).toString());
                quizNumber.setText(documentSnapshot.get(Constants.COUNT).toString() + " квиз-(ов)");
                followers.setText(documentSnapshot.get(Constants.FOLLOWERS).toString());
                following.setText(documentSnapshot.get(Constants.FOLLOWINGS).toString());
            }
        });
        FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
        documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                followings = documentSnapshot.getString(Constants.FOLLOWINGS_ID);
                count_followers = Integer.parseInt(documentSnapshot.getString(Constants.FOLLOWINGS));
                if (documentSnapshot.getString(Constants.FOLLOWINGS_ID).contains(userId)){
                    subscribe.setBackgroundResource(R.drawable.newbgbuttonsupport);
                    subscribe.setTextColor(Color.parseColor("#B139E5"));
                    subscribe.setText("Отписаться");
                }
            }
        });
        FirebaseFirestore databaseSubscribe = FirebaseFirestore.getInstance();
        documentReference = databaseSubscribe.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                count_followers_user = Integer.parseInt(documentSnapshot.getString(Constants.FOLLOWINGS));
            }
        });
        subscribe.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                if (subscribe.getText().toString().equals("Подписаться")){
                    FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
                    documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID));
                    documentReference.update(Constants.FOLLOWINGS_ID, followings + userId);
                    if (count_followers > 0){
                        documentReference.update(Constants.FOLLOWERS, String.valueOf(count_followers));
                    } else {
                        documentReference.update(Constants.FOLLOWERS, String.valueOf(count_followers + 1));
                    }
                    subscribe.setBackgroundResource(R.drawable.newbgbuttonsupport);
                    subscribe.setTextColor(Color.parseColor("#B139E5"));
                    subscribe.setText("Отписаться");
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                            .document(userId);
                    if (count_followers_user > 0){
                        documentReference.update(Constants.FOLLOWINGS, String.valueOf(count_followers_user));
                    } else {
                        documentReference.update(Constants.FOLLOWINGS, String.valueOf(count_followers_user + 1));
                    }
                    subscribe.setBackgroundResource(R.drawable.newbgbuttonsupport);
                    subscribe.setTextColor(Color.parseColor("#B139E5"));
                    subscribe.setText("Отписаться");
                } else if (subscribe.getText().toString().equals("Отписаться")) {
                    FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
                    documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID));
                    documentReference.update(Constants.FOLLOWINGS_ID, followings.replace(userId, ""));
                    if (count_followers_user > 0){
                        documentReference.update(Constants.FOLLOWINGS, String.valueOf(count_followers_user - 1));
                    } else {
                        documentReference.update(Constants.FOLLOWINGS, String.valueOf(count_followers_user));
                    }
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                            .document(userId);
                    if (count_followers > 0){
                        documentReference.update(Constants.FOLLOWERS, String.valueOf(count_followers - 1));
                    } else {
                        documentReference.update(Constants.FOLLOWERS, String.valueOf(count_followers));
                    }
                    subscribe.setBackgroundResource(R.drawable.newbgbuttonround);
                    subscribe.setTextColor(Color.parseColor("#FFFFFF"));
                    subscribe.setText("Подписаться");
                }
            }
        });
    }
    private void getData()
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_QUIZ)
                .whereEqualTo(Constants.KEY_USER_ID, userId)
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
                                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider)
                            );
                            FirebaseFirestore databaseCount = FirebaseFirestore.getInstance();
                            documentReference = databaseCount.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(userId);
                            documentReference.update(Constants.COUNT, events.size());
                            recyclerView.addItemDecoration(divider);
                            noImage.setVisibility(View.INVISIBLE);
                            noText.setVisibility(View.INVISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                            adapter = new event_adapter_quiz(events, getApplicationContext());
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
    }
}