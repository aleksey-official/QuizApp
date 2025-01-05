package com.example.quiz.solo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;
import com.example.quiz.quiz_preview.ViewHolder_quizPreview;
import com.example.quiz.quiz_preview.preview;

import java.util.List;

public class event_adapter_solo extends RecyclerView.Adapter<ViewHolder_solo> {
    private List<solo> quiz;
    Context context;

    public event_adapter_solo(List<solo> quiz, Context context) {
        this.quiz = quiz;
        this.context = context;

    }

    // This method creates a new ViewHolder object for each item in the RecyclerView
    @Override
    public com.example.quiz.solo.ViewHolder_solo onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item and return a new ViewHolder object
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_solo, parent, false);
        return new com.example.quiz.solo.ViewHolder_solo(itemView);
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
    public void onBindViewHolder(@NonNull com.example.quiz.solo.ViewHolder_solo holder, int position) {
        solo dataQuiz = quiz.get(position);
        holder.type.setText(dataQuiz.getType());
        holder.question.setText(dataQuiz.getQuestion());
        if (dataQuiz.getScore().split(";")[1].equals("uncorrect")){
            holder.score.setBackgroundResource(R.drawable.quizredbutton);
        }
        holder.score.setText(dataQuiz.getScore().split(";")[0]);
        if (dataQuiz.getImage() != null){
            byte[] bytes = Base64.decode(dataQuiz.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.image.setImageBitmap(bitmap);
        }
    }
}