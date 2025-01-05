package com.example.quiz;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;

public class userProfile extends AppCompatActivity {
    RoundedImageView profileImage;
    TextView name;
    EditText phone, email, nameText, nick, birthday;
    String encodedImage;
    Button save, exit;
    private PreferenceManager preferenceManager;
    private DocumentReference documentReference;
    private int mYear, mMonth, mDay;
    private FirebaseAuth mAuth;
    ImageButton back;
    CheckBox check;
    boolean correct = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        profileImage = (RoundedImageView) findViewById(R.id.profilePhoto);
        back = (ImageButton) findViewById(R.id.back);
        name = (TextView) findViewById(R.id.usernameText);
        phone = (EditText) findViewById(R.id.nameQuiz2);
        email = (EditText) findViewById(R.id.nameQuiz3);
        nameText = (EditText) findViewById(R.id.nameText);
        nick = (EditText) findViewById(R.id.nickText2);
        birthday = (EditText) findViewById(R.id.birthday);
        check = (CheckBox) findViewById(R.id.checkBox2);
        save = (Button) findViewById(R.id.play_solo2);
        exit = (Button) findViewById(R.id.play_solo3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance ();
                mYear = calendar.get ( Calendar.YEAR );
                mMonth = calendar.get ( Calendar.MONTH );
                mDay = calendar.get ( Calendar.DAY_OF_MONTH );
                DatePickerDialog datePickerDialog = new DatePickerDialog ( userProfile.this, new DatePickerDialog.OnDateSetListener () {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        birthday.setText ( dayOfMonth + "." + (month + 1) + "." + year );
                    }
                }, mYear, mMonth, mDay );
                datePickerDialog.show();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
                documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                        .document(preferenceManager.getString(Constants.KEY_USER_ID));
                if (check.isChecked()){
                    documentReference.update(Constants.PUBLICK, "true");
                    preferenceManager.putString(Constants.PUBLICK, "true");
                } else {
                    documentReference.update(Constants.PUBLICK, "false");
                    preferenceManager.putString(Constants.PUBLICK, "false");
                }
            }
        });
        nick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (nick.getText().toString().contains(" ")){
                    nick.setError("Ник не должен содержать пробелы");
                } else {
                    try {
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        database.collection(Constants.KEY_COLLECTION_USERS)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null)
                                    {
                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult())
                                        {
                                            if (queryDocumentSnapshot.getString(Constants.NICK_NAME).equals(nick.getText().toString()) && !queryDocumentSnapshot.getString(Constants.NICK_NAME).equals(preferenceManager.getString(Constants.NICK_NAME))){
                                                nick.setError("Это имя пользователя уже занято");
                                                correct = false;
                                            } else {
                                                correct = true;
                                            }
                                        }
                                    }
                                });
                    } catch (Exception e){

                    }
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone.getText().toString().trim().isEmpty()){
                    phone.setError("Введите номер телефона");
                } else if (email.getText().toString().trim().isEmpty()){
                    email.setError("Введите почту");
                } else if (nameText.getText().toString().trim().isEmpty()){
                    nameText.setError("Введите имя");
                } else if (nick.getText().toString().trim().isEmpty()){
                    nick.setError("Введите ник");
                } else if (correct == false){
                    nick.setError("Это имя пользователя уже занято");
                } else if (birthday.getText().toString().trim().isEmpty()){
                    birthday.setError("Введите день рождения");
                } else {
                    save.setEnabled(false);
                    save.setText("Сохранение...");
                    FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
                    documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID));
                    documentReference.update(Constants.KEY_USER_PHONE, phone.getText().toString());
                    documentReference.update(Constants.KEY_EMAIL, email.getText().toString());
                    documentReference.update(Constants.KEY_NAME, nameText.getText().toString());
                    documentReference.update(Constants.NICK_NAME, nick.getText().toString());
                    documentReference.update(Constants.BIRTHDAY, birthday.getText().toString());
                    preferenceManager.putString(Constants.KEY_USER_PHONE, phone.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL, email.getText().toString());
                    preferenceManager.putString(Constants.KEY_NAME, nameText.getText().toString());
                    preferenceManager.putString(Constants.NICK_NAME, nick.getText().toString());
                    preferenceManager.putString(Constants.BIRTHDAY, birthday.getText().toString());
                    save.setEnabled(true);
                    save.setText("Сохранить");
                    Dialog dialog = new Dialog(userProfile.this);
                    dialog.setContentView(R.layout.correctlayout);
                    TextView textInfo = dialog.findViewById(R.id.textinfo);
                    textInfo.setText("Данные успешно сохранены");
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
                    dialog.show();

                }
            }
        });
        loadUserData();
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(userProfile.this);
                dialog.setContentView(R.layout.exitlayout);
                Button cancel, exit;
                cancel = dialog.findViewById(R.id.saveQuestion2);
                exit = dialog.findViewById(R.id.saveQuestion);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                }); exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signOut();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                dialog.show();
            }
        });
    }

    private void signOut()
    {
        mAuth.signOut();
        preferenceManager.clear();
        Intent intent = new Intent(getApplicationContext(), login.class);
        startActivity(intent);
    }

    private void  showToast(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String encodeImage(Bitmap bitmap)
    {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void loadUserData()
    {
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        profileImage.setImageBitmap(bitmap);
        phone.setText(preferenceManager.getString(Constants.KEY_USER_PHONE));
        name.setText(preferenceManager.getString(Constants.KEY_NAME));
        nameText.setText(preferenceManager.getString(Constants.KEY_NAME));
        email.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        birthday.setText(preferenceManager.getString(Constants.BIRTHDAY));
        nick.setText(preferenceManager.getString(Constants.NICK_NAME));
        if (preferenceManager.getString(Constants.PUBLICK).equals("true")){
            check.setChecked(true);
        } else {
            check.setChecked(false);
        }
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
                            profileImage.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                            preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(preferenceManager.getString(Constants.KEY_USER_ID));
                            documentReference.update(Constants.KEY_IMAGE, encodedImage);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> pickFile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri fileUri = result.getData().getData();
                    }
                }
            }
    );
}