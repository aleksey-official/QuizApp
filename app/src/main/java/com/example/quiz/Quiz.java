
package com.example.quiz;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class Quiz extends AppCompatActivity {
    ImageView imageQuestion, backIcon;
    CheckBox firstIcon, secondIcon, thirdIcon, fourIcon;
    TextView textImage;
    Button time, point, firstText, secondText, thirdText, fourText, addButton;
    EditText question;
    Uri imageUri;
    String encodedImage = "";
    private DocumentReference documentReference;
    String idText, saveText;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        preferenceManager = new PreferenceManager(getApplicationContext());
        idText = preferenceManager.getString(Constants.QUIZ_ID);
        try {
            saveText = getIntent().getSerializableExtra("id").toString();
        } catch (Exception e) {

        }
        imageQuestion = (ImageView) findViewById(R.id.imageQuestion);
        firstIcon = (CheckBox) findViewById(R.id.firstIcon);
        secondIcon = (CheckBox) findViewById(R.id.secondIcon);
        thirdIcon = (CheckBox) findViewById(R.id.thirdIcon);
        fourIcon = (CheckBox) findViewById(R.id.fourIcon);
        textImage = (TextView) findViewById(R.id.textImage);
        time = (Button) findViewById(R.id.time);
        point = (Button) findViewById(R.id.point);
        firstText = (Button) findViewById(R.id.firstText);
        secondText = (Button) findViewById(R.id.secondText);
        thirdText = (Button) findViewById(R.id.thirdText);
        fourText = (Button) findViewById(R.id.fourText);
        addButton = (Button) findViewById(R.id.addButton);
        backIcon = (ImageView) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (question.getText().toString().trim().isEmpty()){
                    question.setError("Введите вопрос");
                } else if (firstText.getText().toString().equals("Добавьте ответ")){
                    firstText.setError("Введите ответ");
                } else if (secondText.getText().toString().equals("Добавьте ответ")){
                    secondText.setError("Введите ответ");
                }else if (thirdText.getText().toString().equals("Добавьте ответ")){
                    thirdText.setError("Введите ответ");
                }else if (fourText.getText().toString().equals("Добавьте ответ")){
                    fourText.setError("Введите ответ");
                } else if (!firstIcon.isChecked() && secondIcon.isChecked() && thirdIcon.isChecked() && fourIcon.isChecked()){
                    Toast.makeText(Quiz.this, "Выберите хотя бы один верный варинт ответа", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    documentReference = database.collection(Constants.KEY_COLLECTION_QUIZ)
                            .document(idText);
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                addQuestion(documentSnapshot.getString(Constants.QUESTION_ID));
                            }
                        }
                    });
                }
            }
        });
        question = (EditText) findViewById(R.id.question);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Quiz.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView((R.layout.timelayout));
                Button one, two, three, four, five, six, seven, eight;
                one = dialog.findViewById(R.id.quizQuestion);
                two = dialog.findViewById(R.id.quizQuestion2);
                three = dialog.findViewById(R.id.quizQuestion3);
                four = dialog.findViewById(R.id.quizQuestion4);
                five = dialog.findViewById(R.id.quizQuestion5);
                six = dialog.findViewById(R.id.quizQuestion6);
                seven = dialog.findViewById(R.id.quizQuestion7);
                eight = dialog.findViewById(R.id.quizQuestion8);
                one.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        time.setText("5 сек.");
                        dialog.dismiss();
                    }
                });
                two.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        time.setText("10 сек.");
                        dialog.dismiss();
                    }
                });
                three.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        time.setText("20 сек.");
                        dialog.dismiss();
                    }
                });
                four.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        time.setText("30 сек.");
                        dialog.dismiss();
                    }
                });
                five.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        time.setText("45 сек.");
                        dialog.dismiss();
                    }
                });
                six.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        time.setText("60 сек.");
                        dialog.dismiss();
                    }
                });
                seven.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        time.setText("90 сек.");
                        dialog.dismiss();
                    }
                });
                eight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        time.setText("120 сек.");
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });
        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Quiz.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView((R.layout.pointlayout));
                Button one, two, three, four, five, six, seven, eight;
                one = dialog.findViewById(R.id.quizQuestion);
                two = dialog.findViewById(R.id.quizQuestion2);
                three = dialog.findViewById(R.id.quizQuestion3);
                four = dialog.findViewById(R.id.quizQuestion4);
                five = dialog.findViewById(R.id.quizQuestion5);
                six = dialog.findViewById(R.id.quizQuestion6);
                seven = dialog.findViewById(R.id.quizQuestion7);
                eight = dialog.findViewById(R.id.quizQuestion8);
                one.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        point.setText("50");
                        dialog.dismiss();
                    }
                });
                two.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        point.setText("100");
                        dialog.dismiss();
                    }
                });
                three.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        point.setText("200");
                        dialog.dismiss();
                    }
                });
                four.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        point.setText("250");
                        dialog.dismiss();
                    }
                });
                five.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        point.setText("500");
                        dialog.dismiss();
                    }
                });
                six.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        point.setText("750");
                        dialog.dismiss();
                    }
                });
                seven.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        point.setText("1000");
                        dialog.dismiss();
                    }
                });
                eight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        point.setText("2000");
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });
        imageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
        firstText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Quiz.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView((R.layout.quizlayout));
                EditText text = dialog.findViewById(R.id.textQuestionquiz);
                Switch correct = dialog.findViewById(R.id.correct);
                text.setBackground(getResources().getDrawable(R.drawable.quizgreenbutton));
                Button saveQuestion= dialog.findViewById(R.id.saveQuestion);
                if (!firstText.getText().toString().equals("Добавьте ответ")){
                    text.setText(firstText.getText().toString());
                }
                if (firstIcon.isChecked()){
                    correct.setChecked(true);
                } else {
                    correct.setChecked(false);
                }
                saveQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (text.getText().toString().trim().isEmpty()){
                            text.setError("Введите ответ");
                        } else {
                            firstText.setText(text.getText().toString());
                            if (correct.isChecked()){
                                firstIcon.setChecked(true);
                            } else {
                                firstIcon.setChecked(false);
                            }
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });
        secondText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Quiz.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView((R.layout.quizlayout));
                EditText text = dialog.findViewById(R.id.textQuestionquiz);
                Switch correct = dialog.findViewById(R.id.correct);
                text.setBackground(getResources().getDrawable(R.drawable.quizbluebutton));
                Button saveQuestion= dialog.findViewById(R.id.saveQuestion);
                if (!secondText.getText().toString().equals("Добавьте ответ")){
                    text.setText(secondText.getText().toString());
                }
                if (secondIcon.isChecked()){
                    correct.setChecked(true);
                } else {
                    correct.setChecked(false);
                }
                saveQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (text.getText().toString().trim().isEmpty()){
                            text.setError("Введите ответ");
                        } else {
                            secondText.setText(text.getText().toString());
                            if (correct.isChecked()){
                                secondIcon.setChecked(true);
                            } else {
                                secondIcon.setChecked(false);
                            }
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });
        thirdText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Quiz.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView((R.layout.quizlayout));
                EditText text = dialog.findViewById(R.id.textQuestionquiz);
                Switch correct = dialog.findViewById(R.id.correct);
                text.setBackground(getResources().getDrawable(R.drawable.quizorangebutton));
                Button saveQuestion= dialog.findViewById(R.id.saveQuestion);
                if (!thirdText.getText().toString().equals("Добавьте ответ")){
                    text.setText(thirdText.getText().toString());
                }
                if (thirdIcon.isChecked()){
                    correct.setChecked(true);
                } else {
                    correct.setChecked(false);
                }
                saveQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (text.getText().toString().trim().isEmpty()){
                            text.setError("Введите ответ");
                        } else {
                            thirdText.setText(text.getText().toString());
                            if (correct.isChecked()){
                                thirdIcon.setChecked(true);
                            } else {
                                thirdIcon.setChecked(false);
                            }
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });
        fourText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Quiz.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView((R.layout.quizlayout));
                EditText text = dialog.findViewById(R.id.textQuestionquiz);
                Switch correct = dialog.findViewById(R.id.correct);
                text.setBackground(getResources().getDrawable(R.drawable.quizredbutton));
                Button saveQuestion= dialog.findViewById(R.id.saveQuestion);
                if (!fourText.getText().toString().equals("Добавьте ответ")){
                    text.setText(fourText.getText().toString());
                }
                if (fourIcon.isChecked()){
                    correct.setChecked(true);
                } else {
                    correct.setChecked(false);
                }
                saveQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (text.getText().toString().trim().isEmpty()){
                            text.setError("Введите ответ");
                        } else {
                            fourText.setText(text.getText().toString());
                            if (correct.isChecked()){
                                fourIcon.setChecked(true);
                            } else {
                                fourIcon.setChecked(false);
                            }
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });
        try {
            if (!getIntent().getSerializableExtra("id").toString().equals("false")){
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                documentReference = database.collection(Constants.KEY_COLLECTION_QUESTION)
                        .document(getIntent().getSerializableExtra("id").toString());
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.getString(Constants.KEY_IMAGE) != null){
                                    byte[] bytes = Base64.decode(documentSnapshot.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    imageQuestion.setImageBitmap(bitmap);
                                    textImage.setVisibility(View.INVISIBLE);
                                }
                                time.setText(documentSnapshot.getString(Constants.TIME));
                                point.setText(documentSnapshot.getString(Constants.POINT));
                                question.setText(documentSnapshot.getString(Constants.QUESTION));
                                String[] answerText = documentSnapshot.getString(Constants.ANSWER_TEXT).split(";");
                                firstText.setText(answerText[0]);
                                secondText.setText(answerText[1]);
                                thirdText.setText(answerText[2]);
                                fourText.setText(answerText[3]);
                                addButton.setText("Сохранить");
                                if (documentSnapshot.getString(Constants.ANSWER).toString().contains("1")){
                                    firstIcon.setChecked(true);
                                }
                                if (documentSnapshot.getString(Constants.ANSWER).toString().contains("2")){
                                    secondIcon.setChecked(true);
                                }
                                if (documentSnapshot.getString(Constants.ANSWER).toString().contains("3")){
                                    thirdIcon.setChecked(true);
                                }
                                if (documentSnapshot.getString(Constants.ANSWER).toString().contains("4")){
                                    fourIcon.setChecked(true);
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                final Dialog dialog = new Dialog(getApplicationContext());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView((R.layout.uncorrectlayout));
                                TextView correctText = (TextView) dialog.findViewById(R.id.textinfo);
                                correctText.setText("Произошла ошибка. Проверьте подключение к интернету");
                                Button save = (Button) dialog.findViewById(R.id.saveQuestion);
                                save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                addButton.setEnabled(true);
                                addButton.setText("Добавить");
                            }
                        });
            }
        } catch (Exception e){

        }
    }

    private void addQuestion(String questionId) {
        String answer = "";
        String answerText = firstText.getText().toString() + ";" + secondText.getText().toString() + ";" + thirdText.getText().toString() + ";" + fourText.getText().toString();
        if (firstIcon.isChecked()) {
            if (answer.equals("")) {
                answer = "1";
            } else {
                answer += " 1";
            }
        }
        if (secondIcon.isChecked()) {
            if (answer.equals("")) {
                answer = "2";
            } else {
                answer += " 2";
            }
        }
        if (thirdIcon.isChecked()) {
            if (answer.equals("")) {
                answer = "3";
            } else {
                answer += " 3";
            }
        }
        if (fourIcon.isChecked()) {
            if (answer.equals("")) {
                answer = "4";
            } else {
                answer += " 4";
            }
        }
        try{
            if (!getIntent().getSerializableExtra("id").toString().equals("false")){
                addButton.setEnabled(false);
                addButton.setText("Сохранение...");
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                documentReference = database.collection(Constants.KEY_COLLECTION_QUESTION)
                        .document(getIntent().getSerializableExtra("id").toString());
                documentReference.update(Constants.KEY_IMAGE, encodedImage);
                documentReference.update(Constants.TIME, time.getText().toString());
                documentReference.update(Constants.POINT, point.getText().toString());
                documentReference.update(Constants.QUESTION, question.getText().toString());
                documentReference.update(Constants.ANSWER, answer);
                documentReference.update(Constants.ANSWER_TEXT, answerText);
                Intent intent = new Intent(getApplicationContext(), quizQuestion.class);
                intent.putExtra(Constants.QUIZ_ID, idText);
                startActivity(intent);
                addButton.setEnabled(true);
                addButton.setText("Сохранить");
                finish();
            } else {
                addButton.setText("Добавление...");
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                HashMap<String, Object> questionInfo = new HashMap<>();
                questionInfo.put(Constants.KEY_IMAGE, encodedImage);
                questionInfo.put(Constants.TIME, time.getText().toString());
                questionInfo.put(Constants.POINT, point.getText().toString());
                questionInfo.put(Constants.QUESTION, question.getText().toString());
                questionInfo.put(Constants.QUIZ_ID, idText);
                questionInfo.put(Constants.TYPE, "Квиз");
                questionInfo.put(Constants.ANSWER, answer);
                questionInfo.put(Constants.ANSWER_TEXT, answerText);
                database.collection(Constants.KEY_COLLECTION_QUESTION)
                        .add(questionInfo)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                String id = documentReference.getId();
                                FirebaseFirestore database = FirebaseFirestore.getInstance();
                                documentReference = database.collection(Constants.KEY_COLLECTION_QUIZ)
                                        .document(idText);
                                if (questionId.isEmpty()) {
                                    documentReference.update(Constants.QUESTION_ID, id);
                                } else {
                                    documentReference.update(Constants.QUESTION_ID, questionId + " " + id);
                                }
                                preferenceManager.putString(Constants.COUNT_QUESTION, String.valueOf(Integer.parseInt(preferenceManager.getString(Constants.COUNT_QUESTION)) + 1));
                                Intent intent = new Intent(getApplicationContext(), quizQuestion.class);
                                intent.putExtra(Constants.QUIZ_ID, idText);
                                startActivity(intent);
                                finish();
                                addButton.setEnabled(true);
                                addButton.setText("Добавить");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                final Dialog dialog = new Dialog(getApplicationContext());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView((R.layout.uncorrectlayout));
                                TextView correctText = (TextView) dialog.findViewById(R.id.textinfo);
                                correctText.setText("Произошла ошибка. Проверьте подключение к интернету");
                                Button save = (Button) dialog.findViewById(R.id.saveQuestion);
                                save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                addButton.setEnabled(true);
                                addButton.setText("Добавить");
                            }
                        });
            }
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeImage(Bitmap bitmap)
    {
        int previewWidth = 300;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null) {
                        imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imageQuestion.setImageBitmap(bitmap);
                            textImage.setVisibility(View.INVISIBLE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), quizQuestion.class);
        intent.putExtra(Constants.QUIZ_ID, idText);
        startActivity(intent);
        finish();
    }
}