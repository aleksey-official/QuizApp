package com.example.quiz.questionAdd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.Quiz;
import com.example.quiz.R;
import com.example.quiz.R;
import com.example.quiz.True_Or_False;
import com.example.quiz.checkboxQuiz;
import com.example.quiz.quizQuestion;
import com.example.quiz.typingQuiz;
import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class event_adapter extends RecyclerView.Adapter<ViewHolder> {

    List<data> list
            = Collections.emptyList();

    Context context;
    private DocumentReference documentReference;
    private PreferenceManager preferenceManager;

    public event_adapter(List<data> list,
                         Context context)
    {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context
                = parent.getContext();
        LayoutInflater inflater
                = LayoutInflater.from(context);
        View photoView
                = inflater
                .inflate(R.layout.item_container_event,
                        parent, false);

        ViewHolder viewHolder
                = new ViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int index = holder.getAdapterPosition();
        String positionText = String.valueOf(position + 1) + " - ";
        holder.type
                .setText(positionText + list.get(position).type);
        holder.id
                .setText(list.get(position).id);
        holder.textQuestion
                .setText(list.get(position).textQuestion);
        if (list.get(position).image != null){
            byte[] bytes = Base64.decode(list.get(position).image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.image.setImageBitmap(bitmap);
        }
        holder.delQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Удалить вопрос?");
                builder.setMessage("После удаления вопроса, его нельзя будет восстановить");
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ProgressDialog progressDialog = new ProgressDialog(view.getContext(), R.style.AppCompatAlertDialogStyle);
                        progressDialog.setTitle("Удаление вопроса");
                        progressDialog.setMessage("Пожалуйста, подождите...");
                        progressDialog.show();
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        documentReference = database.collection(Constants.KEY_COLLECTION_QUESTION)
                                .document(list.get(position).id);
                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                list.remove(position);
                                ((quizQuestion)view.getContext()).adapter.notifyItemRemoved(position);
                                ((quizQuestion)view.getContext()).adapter.notifyItemRangeChanged(position, list.size());
                                if (list.size() == 0){
                                    ((quizQuestion)view.getContext()).NoEventImage.setVisibility(View.VISIBLE);
                                    ((quizQuestion)view.getContext()).NoEventText.setVisibility(View.VISIBLE);
                                }
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                final Dialog dialog = new Dialog(view.getContext(), R.style.AppCompatAlertDialogStyle);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView((R.layout.uncorrectlayout));
                                TextView correctText = (TextView) dialog.findViewById(R.id.textinfo);
                                correctText.setText("Произошла ошибка. Проверьте подключение к интернету");
                                Button save = (Button) dialog.findViewById(R.id.saveQuestion);
                                save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                            }
                        });
                    }
                });
                builder.setNegativeButton("Нет", null);
                builder.show();
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceManager = new PreferenceManager(view.getContext());
                if (list.get(position).type.equals("Квиз")){
                    Intent intent = new Intent(context, Quiz.class);
                    intent.putExtra("id", list.get(position).id);
                    intent.putExtra(Constants.QUIZ_ID, preferenceManager.getString(Constants.QUIZ_ID));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if (list.get(position).type.equals("Правда или ложь")){
                    Intent intent = new Intent(context, True_Or_False.class);
                    intent.putExtra("id", list.get(position).id);
                    intent.putExtra(Constants.QUIZ_ID, preferenceManager.getString(Constants.QUIZ_ID));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if (list.get(position).type.equals("Текст")){
                    Intent intent = new Intent(context, typingQuiz.class);
                    intent.putExtra("id", list.get(position).id);
                    intent.putExtra(Constants.QUIZ_ID, preferenceManager.getString(Constants.QUIZ_ID));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if (list.get(position).type.equals("Несколько")){
                    Intent intent = new Intent(context, checkboxQuiz.class);
                    intent.putExtra("id", list.get(position).id);
                    intent.putExtra(Constants.QUIZ_ID, preferenceManager.getString(Constants.QUIZ_ID));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void filterList(ArrayList<data> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }
}
