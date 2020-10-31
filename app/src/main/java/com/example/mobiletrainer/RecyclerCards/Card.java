package com.example.mobiletrainer.RecyclerCards;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.example.mobiletrainer.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Card{

    String title_exercise;
    int progress;
    int goal;

  //  public int priority;

    //public boolean isExcited;

    public static final String KEY_CARDS = "KEY_CARDS";

    public Card(String title_exercise, int progress, int goal){
        this.title_exercise = title_exercise;
        this.progress = progress;
        this.goal = goal;

       // this.color = Color.parseColor("#ff033e");
        //this.priority = 3;
       // this.isExcited = false;
    }

    public void increaseProgress(){
        this.progress+=1;
    }

    public int getProgress(){
        return this.progress;
    }

    public void setProgress(int progress){
        this.progress = progress;
    }

    public void setGoal(int goal){
        this.goal = goal;
    }

    public int getGoal(){
        return this.goal;
    }

    String getHint(){
        return "Осталось сделать: " + String.valueOf(this.goal - this.progress);
    }

    String getTitle_exercise(){return this.title_exercise;}


//    public static Comparator<Card> getCardsByPriority(){
//        Comparator<Card> comparator = new Comparator<Card>() {
//            @Override
//            public int compare(Card o1, Card o2) {
//                return o2.priority-o1.priority;
//            }
//        };
//        return comparator;
//    }


//    public static void saveCards(List<Card> list){
//
//        String savedString = new Gson().toJson(list);
//        SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
//        editor.putString(KEY_CARDS, savedString);
//        editor.apply();
//    }

//    public static List<Card> loadCards() {
//        String savedString = MainActivity.sharedPreferences.getString(KEY_CARDS, null);
//        if (savedString != null) {
//            Type type = new TypeToken<List<Card>>() {}.getType();
//            List<Card> cards = new Gson().fromJson(savedString, type);
//
//            return cards;
//        } else {
//            return null;
//        }
//    }



//    public static void sortCards(List<Card> cards){
//        Collections.sort(cards,Card.getCardsByPriority());
//    }

}
