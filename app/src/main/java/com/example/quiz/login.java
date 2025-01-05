package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class login extends AppCompatAGithctivity {
    EditText emailText, pasText;
    TextView forgotpas, regBtn;
    Button logBtn;
    ProgressBar progressBar;
    private PreferenceManager preferenceManager;
    private FirebaseAuth mAuth;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        preferenceManager = new PreferenceManager(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN))
        {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null)
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        emailText = (EditText) findViewById(R.id.nameQuiz);
        pasText = (EditText) findViewById(R.id.pasText);
        forgotpas = (TextView) findViewById(R.id.forgotPas);
        forgotpas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailText.getText().toString().equals("")){
                    Toast.makeText(login.this, "Введите почту, нажмите на эту кнопку и мы отправим письмо с подтверждением", Toast.LENGTH_SHORT).show();
                } else if (Patterns.EMAIL_ADDRESS.matcher(emailText.getText().toString()).matches()){
                    mAuth.sendPasswordResetEmail(emailText.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    emailText.setText(null);
                                    Toast.makeText(login.this, "Письмо отправлено на Вашу почту", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    emailText.setText(null);
                                    Toast.makeText(login.this, "Произошла ошибка. Повторите попытку", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        regBtn = (TextView) findViewById(R.id.logBtn);
        logBtn = (Button) findViewById(R.id.play_solo);
        progressBar = (ProgressBar) findViewById(R.id.progBar);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidSignInDetails())
                {
                    signIn();
                }
            }
        });
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this, phonereg.class));
            }
        });
    }

    private void signIn()
    {
        loading(true);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(emailText.getText().toString(), pasText.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        database.collection(Constants.KEY_COLLECTION_USERS)
                                .whereEqualTo(Constants.KEY_EMAIL, emailText.getText().toString())
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0)
                                    {
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                                        preferenceManager.putString(Constants.BIRTHDAY, documentSnapshot.getString(Constants.BIRTHDAY));
                                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                                        preferenceManager.putString(Constants.KEY_USER_PHONE, documentSnapshot.getString(Constants.KEY_USER_PHONE));
                                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                                        preferenceManager.putString(Constants.NICK_NAME, documentSnapshot.getString(Constants.NICK_NAME));
                                        if (documentSnapshot.getString(Constants.PUBLICK).equals("true")){
                                            preferenceManager.putString(Constants.PUBLICK, "true");
                                        } else {
                                            preferenceManager.putString(Constants.PUBLICK, "false");
                                        }
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }else
                                    {
                                        loading(false);
                                        emailText.setText("");
                                        pasText.setText("");
                                        showToast("Неверная почта или пароль");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading(false);
                        emailText.setText("");
                        pasText.setText("");
                        showToast("Неверная почта или пароль");
                    }
                });
    }

    private void loading(Boolean isLoading)
    {
        if (isLoading)
        {
            logBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else
        {
            logBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void  showToast(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails()
    {
        if (emailText.getText().toString().trim().isEmpty())
        {
            showToast("Введите почту");
            emailText.setError("Введите почту");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(emailText.getText().toString()).matches())
        {
            showToast("Введите верный адрес почты");
            emailText.setError("Введите верный адрес почты");
            return false;
        }else if (pasText.getText().toString().trim().isEmpty()) {
            showToast("Введите пароль");
            pasText.setError("Введите пароль");
            return false;
        }else {
            return true;
        }
    }
}