// ViewHolder code for RecyclerView
package com.example.quiz.people_quiz_add;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;

public class ViewHolder_Peoples_quiz_add extends RecyclerView.ViewHolder {
    public TextView name;
    ImageView image;
    View view;

    public ViewHolder_Peoples_quiz_add(View itemView)
    {
        super(itemView);
        name = (TextView)itemView
                .findViewById(R.id.namePeople);
        image
                = (ImageView)itemView
                .findViewById(R.id.imagePeople);
        view = itemView;
    }
}
