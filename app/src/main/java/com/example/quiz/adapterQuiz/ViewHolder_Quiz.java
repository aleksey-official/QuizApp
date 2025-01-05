// ViewHolder code for RecyclerView
package com.example.quiz.adapterQuiz;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;

public class ViewHolder_Quiz extends RecyclerView.ViewHolder {
    public TextView nameQuiz, typePublick, countQuestion, idQuiz;
    ImageView image;
    View view;

    public ViewHolder_Quiz(View itemView)
    {
        super(itemView);
        nameQuiz = (TextView)itemView
                .findViewById(R.id.titleQuiz);
        image
                = (ImageView)itemView
                .findViewById(R.id.image);
        typePublick
                = (TextView)itemView
                .findViewById(R.id.namePeople);
        countQuestion
                = (TextView)itemView
                .findViewById(R.id.countQuestion);
        idQuiz
                = (TextView)itemView
                .findViewById(R.id.peopleId);
        view = itemView;
    }
}
