// ViewHolder code for RecyclerView
package com.example.quiz.people_card;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;

public class ViewHolder_People_Cards extends RecyclerView.ViewHolder {
    public TextView name, nick, id;
    public Button subscribes;
    public ImageView image;
    public View view;

    public ViewHolder_People_Cards(View itemView)
    {
        super(itemView);
        name = (TextView)itemView
                .findViewById(R.id.userName);
        nick = (TextView)itemView
                .findViewById(R.id.userNick);
        image
                = (ImageView)itemView
                .findViewById(R.id.userPhoto);
        id
                = (TextView)itemView
                .findViewById(R.id.userId);
        subscribes
                = (Button)itemView
                .findViewById(R.id.userSubscribe);
        view = itemView;
    }
}
