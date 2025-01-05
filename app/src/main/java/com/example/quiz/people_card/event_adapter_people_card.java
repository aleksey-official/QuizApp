package com.example.quiz.people_card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;
import com.example.quiz.card.Cards;
import com.example.quiz.people.People;
import com.example.quiz.people.ViewHolder_Peoples;
import com.example.quiz.user_preview;
import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class event_adapter_people_card extends RecyclerView.Adapter<ViewHolder_People_Cards> {
    private List<peopleCard> quiz;
    Context context;
    private DocumentReference documentReference;
    private PreferenceManager preferenceManager;
    String followings = "";
    int count_followers = 0;
    int count_followers_user = 0;

    public event_adapter_people_card(List<peopleCard> quiz, Context context) {
        this.quiz = quiz;
        this.context = context;

    }

    // This method creates a new ViewHolder object for each item in the RecyclerView
    @Override
    public com.example.quiz.people_card.ViewHolder_People_Cards onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item and return a new ViewHolder object
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_people_card, parent, false);
        return new com.example.quiz.people_card.ViewHolder_People_Cards(itemView);
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
    public void onBindViewHolder(@NonNull com.example.quiz.people_card.ViewHolder_People_Cards holder, @SuppressLint("RecyclerView") int position) {
        preferenceManager = new PreferenceManager(context);
        peopleCard dataQuiz = quiz.get(position);
        holder.name.setText(dataQuiz.getName());
        holder.nick.setText("@" + dataQuiz.getNick());
        holder.id.setText(dataQuiz.getId());
        if (dataQuiz.getImage() != null){
            byte[] bytes = Base64.decode(dataQuiz.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.image.setImageBitmap(bitmap);
        }
        if (preferenceManager.getString(Constants.FOLLOWINGS_ID).contains(dataQuiz.getId())){
            holder.subscribes.setBackgroundResource(R.drawable.newbgbuttonsupport);
            holder.subscribes.setTextColor(Color.parseColor("#B139E5"));
            holder.subscribes.setText("Отписаться");
        }
        holder.subscribes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore databaseUserSubscribe = FirebaseFirestore.getInstance();
                documentReference = databaseUserSubscribe.collection(Constants.KEY_COLLECTION_USERS)
                        .document(preferenceManager.getString(Constants.KEY_USER_ID));
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        followings = documentSnapshot.getString(Constants.FOLLOWINGS_ID);
                        count_followers = Integer.parseInt(documentSnapshot.getString(Constants.FOLLOWINGS));
                    }
                });
                FirebaseFirestore databaseSubscribe = FirebaseFirestore.getInstance();
                documentReference = databaseSubscribe.collection(Constants.KEY_COLLECTION_USERS)
                        .document(preferenceManager.getString(Constants.KEY_USER_ID));
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        count_followers_user = Integer.parseInt(documentSnapshot.getString(Constants.FOLLOWINGS));
                    }
                });
                if (holder.subscribes.getText().toString().equals("Подписаться")){
                    FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
                    documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID));
                    documentReference.update(Constants.FOLLOWINGS_ID, preferenceManager.getString(Constants.FOLLOWINGS_ID) + dataQuiz.getId());
                    if (count_followers > 0){
                        documentReference.update(Constants.FOLLOWERS, String.valueOf(Integer.parseInt(preferenceManager.getString(Constants.FOLLOWERS))));
                    } else {
                        documentReference.update(Constants.FOLLOWERS, String.valueOf(Integer.parseInt(preferenceManager.getString(Constants.FOLLOWERS)) + 1));
                    }
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID));
                    if (count_followers_user > 0){
                        documentReference.update(Constants.FOLLOWINGS, String.valueOf(count_followers_user));
                    } else {
                        documentReference.update(Constants.FOLLOWINGS, String.valueOf(count_followers_user + 1));
                    }
                    holder.subscribes.setBackgroundResource(R.drawable.newbgbuttonsupport);
                    holder.subscribes.setTextColor(Color.parseColor("#B139E5"));
                    holder.subscribes.setText("Отписаться");
                } else {
                    FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
                    documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID));
                    documentReference.update(Constants.FOLLOWINGS_ID, followings.replace(preferenceManager.getString(Constants.KEY_USER_ID), ""));
                    if (count_followers_user > 0){
                        documentReference.update(Constants.FOLLOWINGS, String.valueOf(count_followers_user - 1));
                    } else {
                        documentReference.update(Constants.FOLLOWINGS, String.valueOf(count_followers_user));
                    }
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID));
                    if (count_followers > 0){
                        documentReference.update(Constants.FOLLOWERS, String.valueOf(count_followers - 1));
                    } else {
                        documentReference.update(Constants.FOLLOWERS, String.valueOf(count_followers));
                    }
                    holder.subscribes.setBackgroundResource(R.drawable.newbgbuttonround);
                    holder.subscribes.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.subscribes.setText("Подписаться");
                }
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), user_preview.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.KEY_USER_ID, quiz.get(position).getId().toString());
                view.getContext().startActivity(intent);
            }
        });
    }

    public void filterList(ArrayList<peopleCard> filteredList) {
        quiz = filteredList;
        notifyDataSetChanged();
    }

    // This class defines the ViewHolder object for each item in the RecyclerView
    public static class ViewHolder_People_Cards extends RecyclerView.ViewHolder {

        public ViewHolder_People_Cards(View itemView) {
            super(itemView);
        }
    }
}