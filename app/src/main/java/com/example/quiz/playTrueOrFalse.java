package com.example.quiz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link playTrueOrFalse#newInstance} factory method to
 * create an instance of this fragment.
 */
public class playTrueOrFalse extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageView imageQuestion;
    Button time, point, trueText, falseText, nextQuestionButton;
    TextView question;
    int score = 0;
    private PreferenceManager preferenceManager;
    ConstraintLayout correctPanel, uncorrectPanel;
    TextView correctPoint, uncorrectPoint, select_done;
    String pointText;
    private DocumentReference documentReference;
    CountDownTimer countDownTimer;

    public playTrueOrFalse() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment playTrueOrFalse.
     */
    // TODO: Rename and change types and number of parameters
    public static playTrueOrFalse newInstance(String param1, String param2) {
        playTrueOrFalse fragment = new playTrueOrFalse();
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
        View view = inflater.inflate(R.layout.fragment_play_true_or_false, container, false);
        preferenceManager = new PreferenceManager(view.getContext());
        String imageText = getArguments().getString(Constants.KEY_IMAGE);
        String timeText = getArguments().getString(Constants.TIME).split(" ")[0];
        String type = getArguments().getString(Constants.TYPE);
        pointText = getArguments().getString(Constants.POINT);
        String questionText = getArguments().getString(Constants.QUESTION);
        String answerText = getArguments().getString(Constants.ANSWER);
        String creatorId = getArguments().getString(Constants.CREATOR_NAME);
        String userId;
        if (getArguments().getString(Constants.KEY_USER_ID).equals("")){
            userId = preferenceManager.getString(Constants.KEY_USER_ID);
        } else {
            userId = getArguments().getString(Constants.KEY_USER_ID);
        }
        int count = Integer.parseInt(getArguments().getString(Constants.COUNT));
        imageQuestion = view.findViewById(R.id.imageQuestion);
        time = view.findViewById(R.id.time);
        point = view.findViewById(R.id.point);
        trueText = view.findViewById(R.id.firstText);
        falseText = view.findViewById(R.id.secondText);
        question = view.findViewById(R.id.question);
        correctPanel = view.findViewById(R.id.correctPanel);
        uncorrectPanel = view.findViewById(R.id.uncorrectPanel);
        correctPoint = view.findViewById(R.id.name_quiz3);
        uncorrectPoint = view.findViewById(R.id.name_quiz31);
        select_done = view.findViewById(R.id.select_done);
        if (type.equals("friend")){
            try {
                FirebaseFirestore databasePeople = FirebaseFirestore.getInstance();
                documentReference = databasePeople.collection(Constants.KEY_COLLECTION_USERS)
                        .document(creatorId);
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (Integer.parseInt(String.valueOf(value.getString(Constants.COUNT_ANSWERED))) == Integer.parseInt(String.valueOf(value.get(Constants.COUNT_USER)))){
                            documentReference.update(Constants.COUNT_ANSWERED, "0");
                            countDownTimer.cancel();
                            if (preferenceManager.getString(Constants.KEY_USER_ID).equals(creatorId)){
                                ((quiz_friend)view.getContext()).hideTrueOrFalse();
                            } else {
                                ((quiz_friend_user)view.getContext()).hideTrueOrFalse();
                            }
                        }
                    }
                });
            } catch (Exception e){
                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        nextQuestionButton = view.findViewById(R.id.nextQuestionButton);
        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((quiz_solo)view.getContext()).hideTrueOrFalse();
            }
        });
        time.setText(timeText);
        point.setText(pointText);
        question.setText(questionText);
        countDownTimer = new CountDownTimer(Integer.parseInt(timeText) * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                time.setText(seconds + " сек.");
            }

            public void onFinish() {
                trueText.setEnabled(false);
                falseText.setEnabled(false);
                uncorrectPanel.setVisibility(View.VISIBLE);
                uncorrectPoint.setText("Время вышло");
                if (!type.equals("friend")){
                    nextQuestionButton.setVisibility(View.VISIBLE);
                } else {
                    FirebaseFirestore databasePeople = FirebaseFirestore.getInstance();
                    documentReference = databasePeople.collection(Constants.KEY_COLLECTION_USERS)
                            .document(creatorId);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            documentReference.update(Constants.COUNT_ANSWERED, String.valueOf(Integer.parseInt(documentSnapshot.getString(Constants.COUNT_ANSWERED)) + 1));
                        }
                    });
                    preferenceManager.putString(Constants.SCORE, String.valueOf(score));
                    documentReference.update(Constants.COUNT_ANSWERED, "0");
                    countDownTimer.cancel();
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(creatorId)){
                        ((quiz_friend)view.getContext()).hideTrueOrFalse();
                    } else {
                        ((quiz_friend_user)view.getContext()).hideTrueOrFalse();
                    }
                }
            }
        }.start();
        if (!imageText.equals("")){
            byte[] bytes = Base64.decode(imageText, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageQuestion.setImageBitmap(bitmap);
        }
        trueText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!type.equals("friend")){
                    nextQuestionButton.setVisibility(View.VISIBLE);
                    trueText.setEnabled(false);
                    falseText.setEnabled(false);
                    if (answerText.equals("true")){
                        countDownTimer.cancel();
                        correctPanel.setVisibility(View.VISIBLE);
                        correctPoint.setText("+" + pointText);
                        score += Integer.parseInt(pointText);
                        preferenceManager.putString(Constants.SCORE, String.valueOf(score));
                    } else {
                        countDownTimer.cancel();
                        uncorrectPanel.setVisibility(View.VISIBLE);
                        preferenceManager.putString(Constants.SCORE, "0");
                    }
                } else {
                    trueText.setEnabled(false);
                    falseText.setEnabled(false);
                    select_done.setVisibility(View.VISIBLE);
                    falseText.setBackgroundResource(R.drawable.graybutton);
                    if (answerText.equals("true")){
                        countDownTimer.cancel();
                        correctPanel.setVisibility(View.VISIBLE);
                        correctPoint.setText("+" + pointText);
                        score += Integer.parseInt(pointText);
                        preferenceManager.putString(Constants.SCORE, String.valueOf(score));
                    } else {
                        countDownTimer.cancel();
                        uncorrectPanel.setVisibility(View.VISIBLE);
                        preferenceManager.putString(Constants.SCORE, "0");
                    }
                    FirebaseFirestore databasePeople = FirebaseFirestore.getInstance();
                    documentReference = databasePeople.collection(Constants.KEY_COLLECTION_USERS)
                            .document(creatorId);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            documentReference.update(Constants.COUNT_ANSWERED, String.valueOf(Integer.parseInt(documentSnapshot.getString(Constants.COUNT_ANSWERED)) + 1));
                        }
                    });
                }
            }
        });
        falseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!type.equals("friend")){
                    nextQuestionButton.setVisibility(View.VISIBLE);
                    trueText.setEnabled(false);
                    falseText.setEnabled(false);
                    if (answerText.equals("false")){
                        countDownTimer.cancel();
                        correctPanel.setVisibility(View.VISIBLE);
                        correctPoint.setText("+" + pointText);
                        score += Integer.parseInt(pointText);
                        preferenceManager.putString(Constants.SCORE, String.valueOf(score));
                        trueText.setBackgroundResource(R.drawable.quizredbutton);
                        falseText.setBackgroundResource(R.drawable.quizgreenbutton);
                    } else {
                        countDownTimer.cancel();
                        uncorrectPanel.setVisibility(View.VISIBLE);
                        preferenceManager.putString(Constants.SCORE, "0");
                    }
                } else {
                    trueText.setEnabled(false);
                    falseText.setEnabled(false);
                    select_done.setVisibility(View.VISIBLE);
                    if (answerText.equals("false")){
                        correctPanel.setVisibility(View.VISIBLE);
                        trueText.setBackgroundResource(R.drawable.quizredbutton);
                        falseText.setBackgroundResource(R.drawable.quizgreenbutton);
                        countDownTimer.cancel();
                        score += Integer.parseInt(pointText);
                        preferenceManager.putString(Constants.SCORE, String.valueOf(score));
                    } else {
                        uncorrectPanel.setVisibility(View.VISIBLE);
                    }
                    FirebaseFirestore databasePeople = FirebaseFirestore.getInstance();
                    documentReference = databasePeople.collection(Constants.KEY_COLLECTION_USERS)
                            .document(creatorId);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            documentReference.update(Constants.COUNT_ANSWERED, String.valueOf(Integer.parseInt(documentSnapshot.getString(Constants.COUNT_ANSWERED)) + 1));
                        }
                    });
                }
            }
        });
        return view;
    }
}