package com.example.quiz.people_quiz_add;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;
import com.example.quiz.people.People;
import com.example.quiz.people.ViewHolder_Peoples;
import com.example.quiz.user_preview;
import com.example.quiz.utilities.Constants;

import java.util.List;

public class event_adapter_people_quiz_add extends RecyclerView.Adapter<ViewHolder_Peoples_quiz_add> {
    private List<peopleQuizAdd> quiz;
    Context context;

    public event_adapter_people_quiz_add(List<peopleQuizAdd> quiz, Context context) {
        this.quiz = quiz;
        this.context = context;

    }

    // This method creates a new ViewHolder object for each item in the RecyclerView
    @Override
    public com.example.quiz.people_quiz_add.ViewHolder_Peoples_quiz_add onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item and return a new ViewHolder object
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_people_quiz_add, parent, false);
        return new com.example.quiz.people_quiz_add.ViewHolder_Peoples_quiz_add(itemView);
    }

    // This method returns the total
    // number of items in the data set
    @Override
    public int getItemCount() {
        return quiz.size();
    }

    // This method binds the data to the ViewHolder object
    // for each item in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull com.example.quiz.people_quiz_add.ViewHolder_Peoples_quiz_add holder, @SuppressLint("RecyclerView") int position) {
        peopleQuizAdd dataQuiz = quiz.get(position);
        holder.name.setText(dataQuiz.getName());
        if (dataQuiz.getImage() != null){
            byte[] bytes = Base64.decode(dataQuiz.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.image.setImageBitmap(bitmap);
        }
    }
}