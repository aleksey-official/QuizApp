package com.example.quiz.people_play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;
import com.example.quiz.people_quiz_add.ViewHolder_Peoples_quiz_add;
import com.example.quiz.people_quiz_add.peopleQuizAdd;
import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;

import java.util.List;

public class event_adapter_people_play extends RecyclerView.Adapter<ViewHolder_Peoples_play> {
    private List<peoplePlay> quiz;
    Context context;
    private PreferenceManager preferenceManager;

    public event_adapter_people_play(List<peoplePlay> quiz, Context context) {
        this.quiz = quiz;
        this.context = context;

    }

    // This method creates a new ViewHolder object for each item in the RecyclerView
    @Override
    public com.example.quiz.people_play.ViewHolder_Peoples_play onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item and return a new ViewHolder object
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_people_score, parent, false);
        return new com.example.quiz.people_play.ViewHolder_Peoples_play(itemView);
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
    public void onBindViewHolder(@NonNull com.example.quiz.people_play.ViewHolder_Peoples_play holder, @SuppressLint("RecyclerView") int position) {
        preferenceManager = new PreferenceManager(context);
        peoplePlay dataQuiz = quiz.get(position);
        holder.name.setText(dataQuiz.getName());
        holder.count.setText(String.valueOf(holder.getAdapterPosition() + 1));
        holder.score.setText(dataQuiz.getScore());
        if (preferenceManager.getString(Constants.KEY_USER_ID).equals(dataQuiz.getId())){
            holder.panel.setBackgroundColor(Color.WHITE);
            holder.name.setTextColor(Color.BLACK);
            holder.count.setTextColor(Color.BLACK);
            holder.score.setTextColor(Color.BLACK);
        }
        if (dataQuiz.getImage() != null){
            byte[] bytes = Base64.decode(dataQuiz.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.image.setImageBitmap(bitmap);
        }
    }
    // This class defines the ViewHolder object for each item in the RecyclerView
    public static class ViewHolder_Peoples_play extends RecyclerView.ViewHolder {

        public ViewHolder_Peoples_play(View itemView) {
            super(itemView);
        }
    }
}