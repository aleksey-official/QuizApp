package com.example.quiz;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quiz.adapterQuiz.Quizs;
import com.example.quiz.adapterQuiz.dataQuiz;
import com.example.quiz.adapterQuiz.event_adapter_quiz;
import com.example.quiz.people_card.event_adapter_people_card;
import com.example.quiz.people_card.peopleCard;
import com.example.quiz.people_card.peopleCards;
import com.example.quiz.utilities.Constants;
import com.example.quiz.utilities.PreferenceManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link search_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class search_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView textView, noText;
    ImageView noImage;
    private List<dataQuiz> events;
    public event_adapter_quiz adapter;
    RecyclerView recyclerView;
    private PreferenceManager preferenceManager;
    private ShimmerFrameLayout shimmerFrameLayout;
    String likeText = "";
    private DocumentReference documentReference;
    private List<peopleCard> people;
    public event_adapter_people_card peopleAdapter;
    EditText searchText;
    String sTitle;

    public search_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment search_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static search_fragment newInstance(String param1, String param2) {
        search_fragment fragment = new search_fragment();
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
        View view = inflater.inflate(R.layout.fragment_search_fragment, container, false);
        preferenceManager = new PreferenceManager(getContext());
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmer();
        textView = view.findViewById(R.id.typeText);
        noText = view.findViewById(R.id.noText4);
        noImage = view.findViewById(R.id.noImage4);
        noImage = view.findViewById(R.id.noImage4);
        searchText = view.findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
        sTitle = getArguments().getString("title");
        if (sTitle.equals("Квизы")){
            List<dataQuiz> list = new ArrayList<>();
            getData(view);
            recyclerView = view.findViewById(R.id.question_view);
            recyclerView.setAdapter(adapter);
        } else {
            List<dataQuiz> list = new ArrayList<>();
            recyclerView = view.findViewById(R.id.question_view);
            recyclerView.setAdapter(peopleAdapter);
            getAuthor(view);
        }
        textView.setText(sTitle);
        return view;
    }
    private void getData(View view)
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_QUIZ)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        events = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Quizs data = new Quizs();
                            data.nameQuiz = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            data.typePublick = queryDocumentSnapshot.getString(Constants.TYPE);
                            data.countQuestion = queryDocumentSnapshot.getString(Constants.COUNT);
                            data.imageQuiz = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            data.idQuiz = queryDocumentSnapshot.getId();
                            data.play = queryDocumentSnapshot.getString(Constants.PLAY);
                            events.add(new dataQuiz(data.nameQuiz, data.typePublick, data.countQuestion, data.imageQuiz, data.idQuiz, data.play));
                        }
                        if (events.size() > 0) {
                            noImage.setVisibility(View.INVISIBLE);
                            noText.setVisibility(View.INVISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
                            DividerItemDecoration divider = new DividerItemDecoration(
                                    recyclerView.getContext(), DividerItemDecoration.VERTICAL
                            );
                            divider.setDrawable(
                                    ContextCompat.getDrawable(view.getContext(), R.drawable.divider)
                            );
                            recyclerView.addItemDecoration(divider);
                            adapter = new event_adapter_quiz(events, getContext());
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(layoutManager);
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        } else {
                            noImage.setVisibility(View.VISIBLE);
                            noText.setVisibility(View.VISIBLE);
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private void getAuthor(View view)
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.PUBLICK, "true")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        people = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            peopleCards peoples = new peopleCards();
                            peoples.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            peoples.nick = queryDocumentSnapshot.getString(Constants.NICK_NAME);
                            peoples.id = queryDocumentSnapshot.getId();
                            peoples.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            peoples.subscribes = queryDocumentSnapshot.getString(Constants.FOLLOWERS);
                            people.add(new peopleCard(peoples.image, peoples.name, peoples.nick, peoples.id, peoples.subscribes));
                        }
                        if (people.size() > 0) {
                            Comparator<peopleCard> descendingComparator = new Comparator<peopleCard>() {
                                @Override
                                public int compare(peopleCard a, peopleCard b) {
                                    int valueA = Integer.parseInt(a.getSubscribes());
                                    int valueB = Integer.parseInt(b.getSubscribes());
                                    return valueB - valueA;
                                }
                            };
                            DividerItemDecoration divider = new DividerItemDecoration(
                                    recyclerView.getContext(), DividerItemDecoration.VERTICAL
                            );
                            divider.setDrawable(
                                    ContextCompat.getDrawable(view.getContext(), R.drawable.divider)
                            );

                            recyclerView.addItemDecoration(divider);
                            LinearLayoutManager layoutManager
                                    = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
                            peopleAdapter = new event_adapter_people_card(people, view.getContext());
                            recyclerView.setAdapter(peopleAdapter);
                            recyclerView.setLayoutManager(layoutManager);
                            Collections.sort(people, descendingComparator);
                            peopleAdapter.notifyDataSetChanged();
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private void filter(String text) {
        if (sTitle.equals("Квизы")){
            ArrayList<dataQuiz> filteredList = new ArrayList<>();
            for (dataQuiz item : events){
                if (item.getNameQuiz().toString().toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(item);
                }
            }
            adapter.filterList(filteredList);
        } else {
            ArrayList<peopleCard> filteredList = new ArrayList<>();
            for (peopleCard item : people){
                if (item.getName().toString().toLowerCase().contains(text.toLowerCase()) || item.getNick().toString().toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(item);
                }
            }
            peopleAdapter.filterList(filteredList);
        }
    }
}