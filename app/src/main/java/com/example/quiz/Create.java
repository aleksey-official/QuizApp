package com.example.quiz;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Create#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Create extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EditText nameQuiz, descriptionQuiz, typeQuiz;
    Button nextQuiz;
    ImageView imageQuiz;
    TextView loadText;
    Uri imageUri;
    String encodedImage = "";
    private PreferenceManager preferenceManager;

    public Create() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Create.
     */
    // TODO: Rename and change types and number of parameters
    public static Create newInstance(String param1, String param2) {
        Create fragment = new Create();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        try {
            preferenceManager = new PreferenceManager(view.getContext());
            nameQuiz = view.findViewById(R.id.nameQuiz);
            descriptionQuiz = view.findViewById(R.id.descriptionQuiz);
            typeQuiz = view.findViewById(R.id.typeQuiz);
            nextQuiz = view.findViewById(R.id.play_solo);
            imageQuiz = view.findViewById(R.id.imageQuiz);
            loadText = view.findViewById(R.id.loadText);
            imageQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    pickImage.launch(intent);
                }
            });
            typeQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(getContext(), typeQuiz);
                    popupMenu.getMenu().add(Menu.NONE, 0, 0, "Публичный");
                    popupMenu.getMenu().add(Menu.NONE, 0, 0, "Персональный");
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            typeQuiz.setText(menuItem.getTitle());
                            return false;
                        }
                    });
                }
            });
            nextQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nameQuiz.getText().toString().trim().isEmpty()){
                        nameQuiz.setError("Введите название");
                    } else if (descriptionQuiz.getText().toString().trim().isEmpty()){
                        descriptionQuiz.setError("Введите описание");
                    } else if (typeQuiz.getText().toString().trim().isEmpty()){
                        typeQuiz.setError("Выберите тип");
                    } else {
                        nextQuiz.setEnabled(false);
                        nextQuiz.setText("Подготовка...");
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        HashMap<String, Object> question = new HashMap<>();
                        question.put(Constants.KEY_NAME, nameQuiz.getText().toString());
                        question.put(Constants.DESCRIPTION, descriptionQuiz.getText().toString());
                        question.put(Constants.TYPE, typeQuiz.getText().toString());
                        question.put(Constants.KEY_IMAGE, encodedImage);
                        question.put(Constants.USER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
                        question.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                        question.put(Constants.QUESTION_ID, "");
                        question.put(Constants.CREATOR_NAME, preferenceManager.getString(Constants.KEY_NAME));
                        question.put(Constants.NICK_NAME, preferenceManager.getString(Constants.NICK_NAME));
                        question.put(Constants.PLAY, "0");
                        question.put(Constants.LIKE, "0");
                        database.collection(Constants.KEY_COLLECTION_QUIZ)
                                .add(question)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        preferenceManager.putString(Constants.COUNT_QUESTION, "0");
                                        Intent intent = new Intent(getActivity(), quizQuestion.class);
                                        intent.putExtra(Constants.QUIZ_ID, documentReference.getId());
                                        startActivity(intent);
                                        nameQuiz.setText("");
                                        descriptionQuiz.setText("");
                                        typeQuiz.setText("");
                                        nextQuiz.setEnabled(true);
                                        nextQuiz.setText("Далее");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        nextQuiz.setEnabled(true);
                                        nextQuiz.setText("Далее");
                                        final Dialog dialog = new Dialog(getContext());
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
                }
            });
        } catch (Exception e){

        }
        return view;
    }

    private String encodeImage(Bitmap bitmap)
    {
        int previewWidth = 500;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null) {
                        imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imageQuiz.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                            loadText.setVisibility(View.INVISIBLE);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
}