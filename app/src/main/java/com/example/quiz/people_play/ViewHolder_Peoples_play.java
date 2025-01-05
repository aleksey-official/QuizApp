// ViewHolder code for RecyclerView
package com.example.quiz.people_play;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;

public class ViewHolder_Peoples_play extends RecyclerView.ViewHolder {
    public TextView count, name, score, id;
    ImageView image;
    ConstraintLayout panel;
    View view;

    public ViewHolder_Peoples_play(View itemView)
    {
        super(itemView);
        count = (TextView)itemView
                .findViewById(R.id.number);
        name = (TextView)itemView
                .findViewById(R.id.userName);
        score = (TextView)itemView
                .findViewById(R.id.score);
        id = (TextView)itemView
                .findViewById(R.id.id);
        image
                = (ImageView)itemView
                .findViewById(R.id.userPhoto);
        panel = (ConstraintLayout)itemView
                .findViewById(R.id.panel);
        view = itemView;
    }
}
