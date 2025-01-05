package com.example.quiz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
 * Use the {@link playQuiz#newInstance} factory method to
 * create an instance of this fragment.
 */
public class playQuiz extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageView imageQuestion;
    Button time, point, firstText, secondText, thirdText, fourText, nextQuestionButton;
    TextView question, select_done;
    int score = 0;
    private PreferenceManager preferenceManager;
    ConstraintLayout correctPanel, uncorrectPanel, constraintLayout;
    TextView correctPoint, uncorrectPoint;
    String pointText;
    private DocumentReference documentReference;
    CountDownTimer countDownTimer;

    public playQuiz() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment playQuiz.
     */
    // TODO: Rename and change types and number of parameters
    public static playQuiz newInstance(String param1, String param2) {
        playQuiz fragment = new playQuiz();
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
        View view = inflater.inflate(R.layout.fragment_play_quiz, container, false);
        preferenceManager = new PreferenceManager(view.getContext());
        String imageText = getArguments().getString(Constants.KEY_IMAGE);
        String timeText = getArguments().getString(Constants.TIME).split(" ")[0];
        pointText = getArguments().getString(Constants.POINT);
        String questionText = getArguments().getString(Constants.QUESTION);
        String answerText = getArguments().getString(Constants.ANSWER);
        String[] answer = getArguments().getString(Constants.ANSWER_TEXT).split(";");
        String type = getArguments().getString(Constants.TYPE);
        String creatorId = getArguments().getString(Constants.CREATOR_NAME);
        String userId;
        if (getArguments().getString(Constants.KEY_USER_ID).equals("")){
            userId = preferenceManager.getString(Constants.KEY_USER_ID);
        } else {
            userId = getArguments().getString(Constants.KEY_USER_ID);
        }
        imageQuestion = view.findViewById(R.id.imageQuestion);
        time = view.findViewById(R.id.time);
        point = view.findViewById(R.id.point);
        firstText = view.findViewById(R.id.firstText);
        secondText = view.findViewById(R.id.secondText);
        thirdText = view.findViewById(R.id.thirdText);
        fourText = view.findViewById(R.id.fourText);
        question = view.findViewById(R.id.question);
        correctPanel = view.findViewById(R.id.correctPanel);
        uncorrectPanel = view.findViewById(R.id.uncorrectPanel);
        correctPoint = view.findViewById(R.id.name_quiz3);
        uncorrectPoint = view.findViewById(R.id.name_quiz31);
        select_done = view.findViewById(R.id.select_done);
        firstText.setText(answer[0]);
        secondText.setText(answer[1]);
        thirdText.setText(answer[2]);
        fourText.setText(answer[3]);
        nextQuestionButton = view.findViewById(R.id.nextQuestionButton);
        constraintLayout = view.findViewById(R.id.constraintLayout);
        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((quiz_solo)view.getContext()).hideQuiz();
            }
        });
        time.setText(timeText);
        point.setText(pointText);
        question.setText(questionText);
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
                                ((quiz_friend)view.getContext()).hideQuiz();
                            } else {
                                ((quiz_friend_user)view.getContext()).hideQuiz();
                            }
                        }
                    }
                });
            } catch (Exception e){
                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        countDownTimer = new CountDownTimer(Integer.parseInt(timeText) * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                time.setText(seconds + " сек.");
            }

            public void onFinish() {
                firstText.setEnabled(false);
                secondText.setEnabled(false);
                thirdText.setEnabled(false);
                fourText.setEnabled(false);
                uncorrectPanel.setVisibility(View.VISIBLE);
                uncorrectPoint.setText("Время вышло");
                nextQuestionButton.setVisibility(View.VISIBLE);
                if (!type.equals("friend")){
                    constraintLayout.setVisibility(View.VISIBLE);
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
                        ((quiz_friend)view.getContext()).hideQuiz();
                    } else {
                        ((quiz_friend_user)view.getContext()).hideQuiz();
                    }
                }
            }
        }.start();
        if (!imageText.trim().isEmpty()){
            byte[] bytes = Base64.decode(imageText, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageQuestion.setImageBitmap(bitmap);
        }
        firstText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstText.setEnabled(false);
                secondText.setEnabled(false);
                thirdText.setEnabled(false);
                fourText.setEnabled(false);
                if (answerText.contains("1")){
                    firstText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    firstText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("2")){
                    secondText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    secondText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("3")){
                    thirdText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    thirdText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("4")){
                    fourText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    fourText.setBackgroundResource(R.drawable.redbutton);
                }
                if (!type.equals("friend")){
                    constraintLayout.setVisibility(View.VISIBLE);
                } else {
                    select_done.setVisibility(View.VISIBLE);
                }
                if (answerText.contains("1")){
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
                if (type.equals("friend")){
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
        secondText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!type.equals("friend")){
                    constraintLayout.setVisibility(View.VISIBLE);
                } else {
                    select_done.setVisibility(View.VISIBLE);
                }
                if (answerText.contains("1")){
                    firstText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    firstText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("2")){
                    secondText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    secondText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("3")){
                    thirdText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    thirdText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("4")){
                    fourText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    fourText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("2")){
                    firstText.setEnabled(false);
                    secondText.setEnabled(false);
                    thirdText.setEnabled(false);
                    fourText.setEnabled(false);
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
                if (type.equals("friend")){
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
        thirdText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!type.equals("friend")){
                    constraintLayout.setVisibility(View.VISIBLE);
                } else {
                    select_done.setVisibility(View.VISIBLE);
                }
                if (answerText.contains("1")){
                    firstText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    firstText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("2")){
                    secondText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    secondText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("3")){
                    thirdText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    thirdText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("4")){
                    fourText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    fourText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("3")){
                    firstText.setEnabled(false);
                    secondText.setEnabled(false);
                    thirdText.setEnabled(false);
                    fourText.setEnabled(false);
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
                if (type.equals("friend")){
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
        fourText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerText.contains("1")){
                    firstText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    firstText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("2")){
                    secondText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    secondText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("3")){
                    thirdText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    thirdText.setBackgroundResource(R.drawable.redbutton);
                }
                if (answerText.contains("4")){
                    fourText.setBackgroundResource(R.drawable.quizgreenbutton);
                } else {
                    fourText.setBackgroundResource(R.drawable.redbutton);
                }
                if (!type.equals("friend")){
                    constraintLayout.setVisibility(View.VISIBLE);
                } else {
                    select_done.setVisibility(View.VISIBLE);
                }
                if (answerText.contains("4")){
                    firstText.setEnabled(false);
                    secondText.setEnabled(false);
                    thirdText.setEnabled(false);
                    fourText.setEnabled(false);
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
                if (type.equals("friend")){
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