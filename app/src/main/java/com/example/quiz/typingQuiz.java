
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
import android.widget.EditText;
import android.widget.ImageView;
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

public class typingQuiz extends AppCompatActivity {
    ImageView imageQuestion, backIcon;
    EditText text;
    TextView textImage;
    Button time, point, addButton;
    EditText question;
    Uri imageUri;
    String encodedImage = "";
    private DocumentReference documentReference;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typing_quiz);
        preferenceManager = new PreferenceManager(getApplicationContext());
        imageQuestion = (ImageView) findViewById(R.id.imageQuestion);
        text = (EditText) findViewById(R.id.firstText);
        time = (Button) findViewById(R.id.time);
        point = (Button) findViewById(R.id.point);
        textImage = (TextView) findViewById(R.id.textImage);
        addButton = (Button) findViewById(R.id.addButton);
        backIcon = (ImageView) findViewById(R.id.backIcon4);
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
                } else if (text.getText().toString().trim().isEmpty()){
                    text.setError("Введите ответ");
                } else {
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    documentReference = database.collection(Constants.KEY_COLLECTION_QUIZ)
                            .document(getIntent().getSerializableExtra(Constants.QUIZ_ID).toString());
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
                final Dialog dialog = new Dialog(typingQuiz.this);
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
                final Dialog dialog = new Dialog(typingQuiz.this);
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
                                text.setText(documentSnapshot.getString(Constants.ANSWER));
                                addButton.setText("Сохранить");
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
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addQuestion(String questionId) {
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
            documentReference.update(Constants.ANSWER, text.getText().toString());
            Intent intent = new Intent(getApplicationContext(), quizQuestion.class);
            intent.putExtra(Constants.QUIZ_ID, preferenceManager.getString(Constants.QUIZ_ID));
            startActivity(intent);
            addButton.setEnabled(true);
            addButton.setText("Сохранить");
            finish();
        } else {
            addButton.setEnabled(false);
            addButton.setText("Добавление...");
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            HashMap<String, Object> questionInfo = new HashMap<>();
            questionInfo.put(Constants.KEY_IMAGE, encodedImage);
            questionInfo.put(Constants.TIME, time.getText().toString());
            questionInfo.put(Constants.POINT, point.getText().toString());
            questionInfo.put(Constants.QUESTION, question.getText().toString());
            questionInfo.put(Constants.QUIZ_ID, getIntent().getSerializableExtra(Constants.QUIZ_ID).toString());
            questionInfo.put(Constants.TYPE, "Текст");
            questionInfo.put(Constants.ANSWER, text.getText().toString());
            database.collection(Constants.KEY_COLLECTION_QUESTION)
                    .add(questionInfo)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String id = documentReference.getId();
                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            documentReference = database.collection(Constants.KEY_COLLECTION_QUIZ)
                                    .document(getIntent().getSerializableExtra(Constants.QUIZ_ID).toString());
                            if (questionId.isEmpty()){
                                documentReference.update(Constants.QUESTION_ID, id);
                            } else {
                                documentReference.update(Constants.QUESTION_ID, questionId + " " + id);
                            }
                            preferenceManager.putString(Constants.COUNT_QUESTION, String.valueOf(Integer.parseInt(preferenceManager.getString(Constants.COUNT_QUESTION)) + 1));
                            Intent intent = new Intent(getApplicationContext(), quizQuestion.class);
                            intent.putExtra(Constants.KEY_NAME, getIntent().getSerializableExtra(Constants.KEY_NAME));
                            intent.putExtra(Constants.DESCRIPTION, getIntent().getSerializableExtra(Constants.DESCRIPTION));
                            intent.putExtra(Constants.TYPE, getIntent().getSerializableExtra(Constants.TYPE));
                            intent.putExtra(Constants.KEY_IMAGE, getIntent().getSerializableExtra(Constants.KEY_IMAGE));
                            intent.putExtra(Constants.QUIZ_ID, getIntent().getSerializableExtra(Constants.QUIZ_ID));
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

    private String encodeImage(Bitmap bitmap)
    {
        int previewWidth = 300;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
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
        intent.putExtra(Constants.KEY_NAME, getIntent().getSerializableExtra(Constants.KEY_NAME));
        intent.putExtra(Constants.DESCRIPTION, getIntent().getSerializableExtra(Constants.DESCRIPTION));
        intent.putExtra(Constants.TYPE, getIntent().getSerializableExtra(Constants.TYPE));
        intent.putExtra(Constants.KEY_IMAGE, getIntent().getSerializableExtra(Constants.KEY_IMAGE));
        intent.putExtra(Constants.QUIZ_ID, getIntent().getSerializableExtra(Constants.QUIZ_ID));
        startActivity(intent);
        finish();
    }
}