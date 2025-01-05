// ViewHolder code for RecyclerView
package com.example.quiz.card;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;

public class ViewHolder_Cards extends RecyclerView.ViewHolder {
    public TextView nameQuiz, typePublick, countQuestion, idQuiz, play;
    ImageView image, userImage;
    View view;

    public ViewHolder_Cards(View itemView)
    {
        super(itemView);
        nameQuiz = (TextView)itemView
                .findViewById(R.id.titleQuiz);
        image
                = (ImageView)itemView
                .findViewById(R.id.image);
        userImage
                = (ImageView)itemView
                .findViewById(R.id.imagePeople);
        typePublick
                = (TextView)itemView
                .findViewById(R.id.namePeople);
        countQuestion
                = (TextView)itemView
                .findViewById(R.id.countQuestion);
        idQuiz
                = (TextView)itemView
                .findViewById(R.id.peopleId);
        play
                = (TextView)itemView
                .findViewById(R.id.play);
        view = itemView;
    }
}
