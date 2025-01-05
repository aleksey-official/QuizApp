// ViewHolder code for RecyclerView
package com.example.quiz.quiz_preview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;

public class ViewHolder_quizPreview extends RecyclerView.ViewHolder {
    public TextView type, question;
    ImageView image;
    View view;

    public ViewHolder_quizPreview(View itemView)
    {
        super(itemView);
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
