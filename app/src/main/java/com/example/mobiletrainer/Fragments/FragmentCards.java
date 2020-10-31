package com.example.mobiletrainer.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobiletrainer.R;
import com.example.mobiletrainer.RecyclerCards.Callback;
import com.example.mobiletrainer.RecyclerCards.Card;
import com.example.mobiletrainer.RecyclerCards.RVAdapter;
import com.example.mobiletrainer.RecyclerCards.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentCards extends Fragment {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    public static List<Card> cards;
    RVAdapter adapter;

    Callback callback;

    public FragmentCards(){
        //Empty
    }

    /**
     * onAttach(Context context) doesn't work with API lowest than 23
     * TODO: Remake it for checking API and use (Activity) for API<23 and (Context) for API>22
     * "http://stackoverflow.com/questions/32083053/android-fragment-onattach-deprecated"
     * **/
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        if(activity instanceof Callback){
            callback = (Callback)activity;
        }else {
            throw new RuntimeException((activity.toString()+" must implement FragmentCallback"));
        }
    }


    public void onStop(){
       // Card.saveCards(cards);
        super.onStop();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View parent = inflater.inflate(R.layout.fragment_with_cards, container, false);

        recyclerView = (RecyclerView)parent.findViewById(R.id.rv);
        registerForContextMenu(recyclerView);


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                callback.ReturnCardPosition(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        init();


        adapter = new RVAdapter(cards);
        adapter.setCurrentContext(getContext());

        recyclerView.setAdapter(adapter);


        return parent;
    }

    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try{
            position = adapter.getPosition();
        }catch (Exception e){
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()){
            default: deleteCard(position);
        }
        return super.onContextItemSelected(item);
    }


    private void init(){
        if(cards==null) {
           // if (Card.loadCards() == null) {
                cards = new ArrayList<>();

           // } else {
             //   cards = Card.loadCards();
           // }
        }
    }

    public void addCard(String title_exercise, int progress, int goal){
        cards.add(new Card(title_exercise,progress,goal));

        adapter.notifyItemInserted(cards.size()-1);
        recyclerView.scrollToPosition(0);
        adapter.notifyItemInserted(cards.size()-1);

        //if(cards!=null){
        //    Card.saveCards(cards);
        //}
        //Card.sortCards(cards);
    }

    public void deleteCard(int position){
        cards.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position,cards.size());
    }

}
