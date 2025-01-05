package com.example.quiz.card;

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
import com.example.quiz.quizPreview;
import com.example.quiz.utilities.Constants;

import java.util.List;

public class event_adapter_card extends RecyclerView.Adapter<ViewHolder_Cards> {
    private List<cardQuiz> quiz;
    Context context;

    public event_adapter_card(List<cardQuiz> quiz, Context context) {
        this.quiz = quiz;
        this.context = context;

    }

    // This method creates a new ViewHolder object for each item in the RecyclerView
    @Override
    public com.example.quiz.card.ViewHolder_Cards onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item and return a new ViewHolder object
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_card, parent, false);
        return new com.example.quiz.card.ViewHolder_Cards(itemView);
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
    public void onBindViewHolder(@NonNull com.example.quiz.card.ViewHolder_Cards holder, @SuppressLint("RecyclerView") int position) {
        cardQuiz dataQuiz = quiz.get(position);
        holder.nameQuiz.setText(dataQuiz.getNameQuiz());
        holder.countQuestion.setText(dataQuiz.getCountQuestion());
        holder.typePublick.setText(dataQuiz.getTypePublick());
        holder.idQuiz.setText(dataQuiz.getIdQuiz());
        holder.play.setText(dataQuiz.getPlay());
        if (dataQuiz.getIdQuiz() != null){
            byte[] bytes = Base64.decode(dataQuiz.getImageQuiz(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.image.setImageBitmap(bitmap);
        }
        if (dataQuiz.getUserImage() != null){
            byte[] bytes = Base64.decode(dataQuiz.getUserImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.userImage.setImageBitmap(bitmap);
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), quizPreview.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.QUIZ_ID, quiz.get(position).getIdQuiz());
                view.getContext().startActivity(intent);
            }
        });
    }

    // This class defines the ViewHolder object for each item in the RecyclerView
    public static class ViewHolder_Cards extends RecyclerView.ViewHolder {
        private TextView nameQuiz;
        private TextView countQuestion;
        private TextView typePublick;
        private TextView idQuiz;

        public ViewHolder_Cards(View itemView) {
            super(itemView);
            nameQuiz = itemView.findViewById(R.id.titleQuiz);
            countQuestion = itemView.findViewById(R.id.countQuestion);
            typePublick = itemView.findViewById(R.id.namePeople);
            idQuiz = itemView.findViewById(R.id.peopleId);
        }
    }
}