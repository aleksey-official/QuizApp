package com.example.quiz;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hbb20.CountryCodePicker;

import java.util.Calendar;

public class phonereg extends AppCompatActivity {
    TextView back;
    Button next;
    CountryCodePicker countryCodePicker;
    EditText phone, name, nick, birthday;
    private int mYear, mMonth, mDay;
    boolean correct = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonereg);
        back = (TextView) findViewById(R.id.logBtn);
        next = (Button) findViewById(R.id.play_solo);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.countryCodePicker);
        phone = (EditText) findViewById(R.id.phoneuser);
        name = (EditText) findViewById(R.id.nameText2);
        nick = (EditText) findViewById(R.id.nickText);
        birthday = (EditText) findViewById(R.id.birthday2);
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance ();
                mYear = calendar.get ( Calendar.YEAR );
                mMonth = calendar.get ( Calendar.MONTH );
                mDay = calendar.get ( Calendar.DAY_OF_MONTH );
                DatePickerDialog datePickerDialog = new DatePickerDialog ( phonereg.this, new DatePickerDialog.OnDateSetListener () {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        birthday.setText ( dayOfMonth + "." + (month + 1) + "." + year );
                    }
                }, mYear, mMonth, mDay );
                datePickerDialog.show();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                                            if (queryDocumentSnapshot.getString(Constants.NICK_NAME).equals(nick.getText().toString())){
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
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone.getText().toString().trim().isEmpty()){
                    phone.setError("Введите номер телефона");
                } else if (!Patterns.PHONE.matcher(countryCodePicker.getSelectedCountryCode() + phone.getText().toString()).matches()) {
                    phone.setError("Введи верный номер телефона");
                } else if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Введи имя");
                } else if (birthday.getText().toString().trim().isEmpty()) {
                    birthday.setError("Введи день рождения");
                } else{
                    if (correct){
                        Intent intent = new Intent(phonereg.this, registration.class);
                        intent.putExtra(Constants.KEY_USER_PHONE, countryCodePicker.getSelectedCountryCodeWithPlus() + phone.getText().toString());
                        intent.putExtra(Constants.KEY_NAME, name.getText().toString());
                        intent.putExtra(Constants.BIRTHDAY, birthday.getText().toString());
                        intent.putExtra(Constants.NICK_NAME, nick.getText().toString());
                        startActivity(intent);
                    } else {
                        nick.setError("Введите верное имя пользователя");
                    }
                }
            }
        });
    }
}