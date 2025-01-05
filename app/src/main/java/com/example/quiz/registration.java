package com.example.quiz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class registration extends AppCompatActivity {

    EditText emailText, pasText, confpasText;
    Button regBtn;
    ImageView imageProfile, back;
    ProgressBar progressBar;
    String encodedImage = "";
    private PreferenceManager preferenceManager;
    private FirebaseAuth mAuth;
    private int mYear, mMonth, mDay;
    CheckBox checkBox;
    int code = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        preferenceManager = new PreferenceManager(getApplicationContext());
        emailText = (EditText) findViewById(R.id.nameQuiz);
        pasText = (EditText) findViewById(R.id.pasText);
        confpasText = (EditText) findViewById(R.id.confpasText);
        regBtn = (Button) findViewById(R.id.play_solo);
        imageProfile = (ImageView) findViewById(R.id.image);
        progressBar = (ProgressBar) findViewById(R.id.progBar);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidSignUpDetails())
                {
                    generateCode();
                }
            }
        });
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
    }
    private void  showToast(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void generateCode(){
        loading(true);
        code = ThreadLocalRandom.current().nextInt(100000, 999999);
        try {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null)
                        {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult())
                            {
                                if (queryDocumentSnapshot.getString(Constants.CODE).equals(String.valueOf(code))){
                                    generateCode();
                                } else {
                                    signUp();
                                }
                            }
                        }
                    });
        } catch (Exception e){

        }
    }

    private void  signUp()
    {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, getIntent().getSerializableExtra(Constants.KEY_NAME));
        user.put(Constants.KEY_EMAIL, emailText.getText().toString());
        user.put(Constants.BIRTHDAY, getIntent().getSerializableExtra(Constants.BIRTHDAY));
        user.put(Constants.KEY_USER_PHONE, getIntent().getSerializableExtra(Constants.KEY_USER_PHONE));
        user.put(Constants.COUNT, "0");
        user.put(Constants.FOLLOWERS, "0");
        user.put(Constants.FOLLOWINGS, "0");
        user.put(Constants.FOLLOWINGS_ID, "");
        user.put(Constants.LIKE, "");
        user.put(Constants.PLAY_WITH_FRIEND, "");
        user.put(Constants.PLAY_WITH_FRIEND_ID, "");
        user.put(Constants.NEXT_PLAY, "");
        user.put(Constants.QUIZ_ID, "");
        user.put(Constants.SCORE, "0");
        user.put(Constants.QUIZ_ID, "");
        user.put(Constants.COUNT_USER, "");
        user.put(Constants.COUNT_ANSWERED, "1");
        user.put(Constants.CODE, String.valueOf(code));
        user.put(Constants.NICK_NAME, getIntent().getSerializableExtra(Constants.NICK_NAME));
        user.put(Constants.KEY_IMAGE, encodedImage);
        if (checkBox.isChecked()){
            user.put(Constants.PUBLICK, "true");
        } else {
            user.put(Constants.PUBLICK, "false");
        }
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    mAuth.createUserWithEmailAndPassword(emailText.getText().toString(), pasText.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    loading(false);
                                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                                    preferenceManager.putString(Constants.BIRTHDAY, getIntent().getSerializableExtra(Constants.BIRTHDAY).toString());
                                    preferenceManager.putString(Constants.KEY_NAME, getIntent().getSerializableExtra(Constants.KEY_NAME).toString());
                                    preferenceManager.putString(Constants.KEY_USER_PHONE, getIntent().getSerializableExtra(Constants.KEY_USER_PHONE).toString());
                                    preferenceManager.putString(Constants.KEY_EMAIL, emailText.getText().toString());
                                    preferenceManager.putString(Constants.NICK_NAME, getIntent().getSerializableExtra(Constants.NICK_NAME).toString());
                                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                                    if (checkBox.isChecked()){
                                        preferenceManager.putString(Constants.PUBLICK, "true");
                                    } else {
                                        preferenceManager.putString(Constants.PUBLICK, "false");
                                    }
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading(false);
                                    Toast.makeText(registration.this, "Произошла ошибка. Повтори попытку", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(exception -> {

                });
    }

    private String encodeImage(Bitmap bitmap)
    {
        int previewWidth = 150;
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
                    if (result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imageProfile.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private Boolean isValidSignUpDetails()
    {
        if (emailText.getText().toString().trim().isEmpty())
        {
            showToast("Введите почту");
            emailText.setError("Введите почту");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(emailText.getText().toString()).matches())
        {
            showToast("Введите верный адрес почты");
            emailText.setError("Введи верныйте адрес почты");
            return false;
        } else if (encodedImage.equals(""))
        {
            showToast("Добавьте фото профиля");
            return false;
        }  else if (pasText.getText().toString().trim().isEmpty()) {
            showToast("Введите пароль");
            pasText.setError("Введите пароль");
            return false;
        } else if (confpasText.getText().toString().trim().isEmpty()) {
            showToast("Повторите пароль");
            confpasText.setError("Повторите пароль");
            return false;
        } else if (!pasText.getText().toString().equals(confpasText.getText().toString())) {
            showToast("Пароли не совпадают");
            pasText.setText("");
            confpasText.setText("");
            pasText.setError("");
            confpasText.setError("");
            return false;
        } else if (pasText.length() < 6){
            pasText.setError("Пароль должен содержать не менее 6 символов");
            return false;
        } else {
            return true;
        }
    }
    private void loading(Boolean isLoading)
    {
        if (isLoading)
        {
            regBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else
        {
            regBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}