package com.example.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.quiz_preview.quizPreviews;
import com.example.quiz.quiz_preview.event_adapter_quiz_view;
import com.example.quiz.quiz_preview.preview;
import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class quizPreview extends AppCompatActivity {
    TextView name, question_count, play_count, like_count, developer_name, developer_nick, description, question_count_text;
    ImageView quiz_photo, back_image, like;
    RoundedImageView developer_photo;
    Button subscribe, play_solo, play_with_friend;
    RecyclerView question_view;
    String quiz_id = "";
    private DocumentReference documentReference;
    public event_adapter_quiz_view adapter;
    private List<preview> events;
    private ShimmerFrameLayout shimmerFrameLayout;
    private PreferenceManager preferenceManager;
    String likeText = "";
    String userId = "";
    String followings = "";
    int count_followers = 0;
    int count_followers_user = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_preview);
        quiz_id = getIntent().getSerializableExtra(Constants.QUIZ_ID).toString();
        preferenceManager = new PreferenceManager(getApplicationContext());
        shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmer();
        name = (TextView) findViewById(R.id.name_quiz);
        question_count = (TextView) findViewById(R.id.count_question);
        play_count = (TextView) findViewById(R.id.count_play);
        like_count = (TextView) findViewById(R.id.count_like);
        developer_name = (TextView) findViewById(R.id.developer_name);
        developer_nick = (TextView) findViewById(R.id.developer_nick);
        description = (TextView) findViewById(R.id.description);
        question_count_text = (TextView) findViewById(R.id.count_question_text);
        quiz_photo = (ImageView) findViewById(R.id.imageQuiz);
        back_image = (ImageView) findViewById(R.id.back_image);
        like = (ImageView) findViewById(R.id.like);
        developer_photo = (RoundedImageView) findViewById(R.id.developer_image);
        subscribe = (Button) findViewById(R.id.subscribe);
        play_solo = (Button) findViewById(R.id.play_solo);
        play_with_friend = (Button) findViewById(R.id.play_with_friends);
        question_view = (RecyclerView) findViewById(R.id.question_view);
        getData();
        question_view.setAdapter(adapter);
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constants.KEY_COLLECTION_QUIZ)
                .document(quiz_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    userId = documentSnapshot.getString(Constants.KEY_USER_ID);
                    name.setText(documentSnapshot.getString(Constants.KEY_NAME));
                    question_count.setText(documentSnapshot.getString(Constants.COUNT));
                    play_count.setText(documentSnapshot.getString(Constants.PLAY));
                    like_count.setText(documentSnapshot.getString(Constants.LIKE));
                    developer_name.setText(documentSnapshot.getString(Constants.CREATOR_NAME));
                    developer_nick.setText("@" + documentSnapshot.getString(Constants.NICK_NAME));
                    description.setText(documentSnapshot.getString(Constants.DESCRIPTION));
                    question_count_text.setText(documentSnapshot.getString(Constants.COUNT) + " вопрос-(ов)");
                    if (!documentSnapshot.getString(Constants.KEY_IMAGE).toString().trim().isEmpty()){
                        byte[] bytes = Base64.decode(documentSnapshot.getString(Constants.KEY_IMAGE).toString(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        quiz_photo.setImageBitmap(bitmap);
                    }
                    if (!documentSnapshot.getString(Constants.USER_IMAGE).toString().trim().isEmpty()){
                        byte[] bytes = Base64.decode(documentSnapshot.getString(Constants.USER_IMAGE).toString(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        developer_photo.setImageBitmap(bitmap);
                    }
                    FirebaseFirestore databaseSubscribe = FirebaseFirestore.getInstance();
                    documentReference = databaseSubscribe.collection(Constants.KEY_COLLECTION_USERS)
                            .document(userId);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            count_followers_user = Integer.parseInt(documentSnapshot.getString(Constants.FOLLOWINGS));
                        }
                    });
                }
            }
        });
        FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
        documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                likeText = documentSnapshot.getString(Constants.LIKE);
                if (documentSnapshot.getString(Constants.LIKE).contains(quiz_id)){
                    like.setImageDrawable(getResources().getDrawable(R.drawable.likefill));
                    like.setTag("like");
                }
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(like.getTag()).equals("likeOutline")){
                    FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
                    documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID));
                    documentReference.update(Constants.LIKE, likeText + quiz_id);
                    FirebaseFirestore databaseLike = FirebaseFirestore.getInstance();
                    documentReference = databaseLike.collection(Constants.KEY_COLLECTION_QUIZ)
                            .document(quiz_id);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            documentReference.update(Constants.LIKE, String.valueOf(Integer.parseInt(documentSnapshot.getString(Constants.LIKE)) + 1));
                        }
                    });
                    like.setImageDrawable(getResources().getDrawable(R.drawable.likefill));
                    like.setTag("like");
                } else {
                    FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
                    documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID));
                    documentReference.update(Constants.LIKE, likeText.replace(quiz_id, ""));
                    FirebaseFirestore databaseLike = FirebaseFirestore.getInstance();
                    documentReference = databaseLike.collection(Constants.KEY_COLLECTION_QUIZ)
                            .document(quiz_id);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (Integer.parseInt(documentSnapshot.getString(Constants.LIKE)) > 0){
                                documentReference.update(Constants.LIKE, String.valueOf(Integer.parseInt(documentSnapshot.getString(Constants.LIKE)) - 1));
                            }
                        }
                    });
                    like.setImageDrawable(getResources().getDrawable(R.drawable.likeoutline));
                    like.setTag("likeOutline");
                }
            }
        });
        play_solo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(quizPreview.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView((R.layout.start_quiz_layout));
                TextView timeText = (TextView) dialog.findViewById(R.id.textsettingstatus2);
                Button cancelQuiz = (Button) dialog.findViewById(R.id.saveQuestion);
                CountDownTimer countDownTimer = new CountDownTimer(4000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        long seconds = millisUntilFinished / 1000;
                        timeText.setText("" + seconds);
                    }

                    public void onFinish() {
                        Intent intent = new Intent(getApplicationContext(), quiz_solo.class);
                        intent.putExtra(Constants.QUIZ_ID, quiz_id);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }.start();
                cancelQuiz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        countDownTimer.cancel();
                        dialog.dismiss();
                    }
                });
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });
        play_with_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                        .document(preferenceManager.getString(Constants.KEY_USER_ID));
                documentReference.update(Constants.PLAY_WITH_FRIEND_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                documentReference.update(Constants.QUIZ_ID, quiz_id);
                documentReference.update(Constants.COUNT_ANSWERED, "0");
                documentReference.update(Constants.COUNT_USER, "0");
                documentReference.update(Constants.SCORE, "0");
                Intent intent = new Intent(getApplicationContext(), quiz_friend.class);
                intent.putExtra(Constants.QUIZ_ID, quiz_id);
                intent.putExtra(Constants.CREATOR_NAME, preferenceManager.getString(Constants.KEY_USER_ID));
                startActivity(intent);
            }
        });
        FirebaseFirestore databaseUserSubscribe = FirebaseFirestore.getInstance();
        documentReference = databaseUserSubscribe.collection(Constants.KEY_COLLECTION_USERS)
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
        developer_nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), user_preview.class);
                intent.putExtra(Constants.KEY_USER_ID, userId);
                startActivity(intent);
            }
        });
    }
    private void getData()
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_QUESTION)
                .whereEqualTo(Constants.QUIZ_ID, quiz_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        events = new ArrayList<preview>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            quizPreviews quizPreviews  = new quizPreviews();
                            quizPreviews.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            quizPreviews.type = queryDocumentSnapshot.getString(Constants.TYPE);
                            quizPreviews.question = queryDocumentSnapshot.getString(Constants.QUESTION);
                            events.add(new preview(quizPreviews.image, quizPreviews.type, quizPreviews.question));
                        }
                        if (events.size() > 0) {
                            adapter = new event_adapter_quiz_view(events, getApplicationContext());
                            DividerItemDecoration divider = new DividerItemDecoration(
                                    question_view.getContext(), DividerItemDecoration.VERTICAL
                            );
                            divider.setDrawable(
                                    ContextCompat.getDrawable(getBaseContext(), R.drawable.divider)
                            );

                            question_view.addItemDecoration(divider);
                            question_view.setAdapter(adapter);
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }
}