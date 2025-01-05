package com.example.quiz;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.Result;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Join#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Join extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PERMISSION_REQUEST_CODE = 5;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button connect;
    EditText code;
    ProgressBar progBar;
    private DocumentReference documentReference;
    private PreferenceManager preferenceManager;
    private CodeScanner mCodeScanner;

    public Join() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Join.
     */
    // TODO: Rename and change types and number of parameters
    public static Join newInstance(String param1, String param2) {
        Join fragment = new Join();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_join, container, false);
        try {
            preferenceManager = new PreferenceManager(view.getContext());
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            }
            final Activity activity = getActivity();
            code = view.findViewById(R.id.nameQuiz);
            connect = view.findViewById(R.id.play_solo);
            progBar = view.findViewById(R.id.progBar);
            CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
            mCodeScanner = new CodeScanner(activity, scannerView);
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FirebaseFirestore databaseConnect = FirebaseFirestore.getInstance();
                            documentReference = databaseConnect.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(result.getText().toString());
                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    code.setText(documentSnapshot.getString(Constants.CODE));
                                }
                            });
                        }
                    });
                }
            });
            mCodeScanner.startPreview();
            connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (code.getText().toString().trim().isEmpty()){
                        code.setError("Введите код подключения");
                    } else if (code.getText().toString().length() != 6){
                        code.setError("Введите верный код");
                    } else {
                        try {
                            progBar.setVisibility(View.VISIBLE);
                            connect.setVisibility(View.GONE);
                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            database.collection(Constants.KEY_COLLECTION_USERS)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult() != null)
                                        {
                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult())
                                            {
                                                if (queryDocumentSnapshot.getString(Constants.CODE).equals(String.valueOf(code.getText().toString()))){
                                                    FirebaseFirestore databaseConnect = FirebaseFirestore.getInstance();
                                                    documentReference = databaseConnect.collection(Constants.KEY_COLLECTION_USERS)
                                                            .document(queryDocumentSnapshot.getId());
                                                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            documentReference.update(Constants.PLAY_WITH_FRIEND_ID, documentSnapshot.getString(Constants.PLAY_WITH_FRIEND_ID) + preferenceManager.getString(Constants.KEY_USER_ID));
                                                            if (documentSnapshot.getString(Constants.QUIZ_ID).equals("")){
                                                                code.setError("По этому коду игра не запущена");
                                                            } else {
                                                                FirebaseFirestore database = FirebaseFirestore.getInstance();
                                                                documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(preferenceManager.getString(Constants.KEY_USER_ID));
                                                                documentReference.update(Constants.SCORE, "0");
                                                                FirebaseFirestore databaseUser = FirebaseFirestore.getInstance();
                                                                documentReference = databaseUser.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(documentSnapshot.getId());
                                                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        documentReference.update(Constants.PLAY_WITH_FRIEND_ID, documentSnapshot.getString(Constants.PLAY_WITH_FRIEND_ID));
                                                                    }
                                                                });
                                                                documentReference.update(Constants.SCORE, "0");
                                                                Intent intent = new Intent(getActivity(), quiz_friend_user.class);
                                                                intent.putExtra(Constants.CREATOR_NAME, documentSnapshot.getId());
                                                                intent.putExtra(Constants.QUIZ_ID, documentSnapshot.getString(Constants.QUIZ_ID));
                                                                intent.putExtra(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                                    break;
                                                } else {
                                                    progBar.setVisibility(View.GONE);
                                                    connect.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                    });
                        } catch (Exception e){

                        }
                    }
                }
            });
        } catch (Exception e){

        }
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Предоставьте доступ к камере для того, чтобы отсканировать QR-код", Toast.LENGTH_SHORT).show();
        }
    }
}