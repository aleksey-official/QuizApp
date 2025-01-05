package com.example.quiz.adapterQuiz;

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

import java.util.ArrayList;
import java.util.List;

public class event_adapter_quiz extends RecyclerView.Adapter<ViewHolder_Quiz> {
    private List<dataQuiz> quiz;
    Context context;

    public event_adapter_quiz(List<dataQuiz> quiz, Context context) {
        this.quiz = quiz;
        this.context = context;

    }

    // This method creates a new ViewHolder object for each item in the RecyclerView
    @Override
    public com.example.quiz.adapterQuiz.ViewHolder_Quiz onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item and return a new ViewHolder object
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_quiz, parent, false);
        return new com.example.quiz.adapterQuiz.ViewHolder_Quiz(itemView);
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
    public void onBindViewHolder(@NonNull com.example.quiz.adapterQuiz.ViewHolder_Quiz holder, @SuppressLint("RecyclerView") int position) {
        dataQuiz dataQuiz = quiz.get(position);
        holder.nameQuiz.setText(dataQuiz.getNameQuiz());
        holder.countQuestion.setText(dataQuiz.getCountQuestion() + " вопрос-(ов)");
        holder.typePublick.setText(" " + dataQuiz.getTypePublick());
        holder.idQuiz.setText(dataQuiz.getIdQuiz());
        if (holder.image != null){
            byte[] bytes = Base64.decode(dataQuiz.getImageQuiz(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.image.setImageBitmap(bitmap);
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

    public void filterList(ArrayList<dataQuiz> filteredList) {
        quiz = filteredList;
        notifyDataSetChanged();
    }

    // This class defines the ViewHolder object for each item in the RecyclerView
    public static class ViewHolder_Quiz extends RecyclerView.ViewHolder {
        private TextView nameQuiz;
        private TextView countQuestion;
        private TextView typePublick;
        private TextView idQuiz;

        public ViewHolder_Quiz(View itemView) {
            super(itemView);
            nameQuiz = itemView.findViewById(R.id.titleQuiz);
            countQuestion = itemView.findViewById(R.id.countQuestion);
            typePublick = itemView.findViewById(R.id.namePeople);
            idQuiz = itemView.findViewById(R.id.peopleId);
        }
    }
}