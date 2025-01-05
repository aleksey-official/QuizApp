// ViewHolder code for RecyclerView
package com.example.quiz.people;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;

public class ViewHolder_Peoples extends RecyclerView.ViewHolder {
    public TextView name, id, subscribes;
    ImageView image;
    View view;

    public ViewHolder_Peoples(View itemView)
    {
        super(itemView);
        name = (TextView)itemView
                .findViewById(R.id.namePeople);
        image
                = (ImageView)itemView
                .findViewById(R.id.imagePeople);
        id
                = (TextView)itemView
                .findViewById(R.id.peopleId);
        subscribes
                = (TextView)itemView
                .findViewById(R.id.subscribes);
        view = itemView;
    }
}
