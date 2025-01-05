// ViewHolder code for RecyclerView
package com.example.quiz.questionAdd;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView type, id, textQuestion;
    ImageView image, delQuestion;
    View view;

    ViewHolder(View itemView)
    {
        super(itemView);
        type = (TextView)itemView
                .findViewById(R.id.titleQuiz);
        image
                = (ImageView)itemView
                .findViewById(R.id.image);
        id
                = (TextView)itemView
                .findViewById(R.id.questionId);
        textQuestion
                = (TextView)itemView
                .findViewById(R.id.countQuestion);
        delQuestion
                = (ImageView)itemView
                .findViewById(R.id.delQuestion);
        view = itemView;
    }
}
