package com.example.quiz.people;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;
import com.example.quiz.card.ViewHolder_Cards;
import com.example.quiz.card.cardQuiz;
import com.example.quiz.quizPreview;
import com.example.quiz.user_preview;
import com.example.quiz.utilities.Constants;

import java.util.List;

public class event_adapter_people extends RecyclerView.Adapter<ViewHolder_Peoples> {
    private List<People> quiz;
    Context context;

    public event_adapter_people(List<People> quiz, Context context) {
        this.quiz = quiz;
        this.context = context;

    }

    // This method creates a new ViewHolder object for each item in the RecyclerView
    @Override
    public com.example.quiz.people.ViewHolder_Peoples onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item and return a new ViewHolder object
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_people, parent, false);
        return new com.example.quiz.people.ViewHolder_Peoples(itemView);
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
    public void onBindViewHolder(@NonNull com.example.quiz.people.ViewHolder_Peoples holder, @SuppressLint("RecyclerView") int position) {
        People dataQuiz = quiz.get(position);
        holder.name.setText(dataQuiz.getName());
        holder.id.setText(dataQuiz.getId());
        if (dataQuiz.getImage() != null){
            byte[] bytes = Base64.decode(dataQuiz.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.image.setImageBitmap(bitmap);
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), user_preview.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.KEY_USER_ID, quiz.get(position).getId().toString());
                view.getContext().startActivity(intent);
            }
        });
    }

    // This class defines the ViewHolder object for each item in the RecyclerView
    public static class ViewHolder_Peoples extends RecyclerView.ViewHolder {

        public ViewHolder_Peoples(View itemView) {
            super(itemView);
        }
    }
}