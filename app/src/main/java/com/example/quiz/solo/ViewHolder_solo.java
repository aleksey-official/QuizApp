// ViewHolder code for RecyclerView
package com.example.quiz.solo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;

public class ViewHolder_solo extends RecyclerView.ViewHolder {
    public TextView type, question, score;
    ImageView image;
    View view;

    public ViewHolder_solo(View itemView)
    {
        super(itemView);
        score = (TextView)itemView
                .findViewById(R.id.namePeople);
        type = (TextView)itemView
                .findViewById(R.id.titleQuiz);
        image
                = (ImageView)itemView
                .findViewById(R.id.image);
        question
                = (TextView)itemView
                .findViewById(R.id.countQuestion);
        view = itemView;
    }
}
