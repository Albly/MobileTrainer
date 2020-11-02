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
    /**Класс с карточками, которые выводятся в recycler view*/

    //название упражнения
    String title_exercise;
    //Сколько сделано повторений
    int progress;
    //Цель
    int goal;
    //Позиция карточки
    int position = 0;

    //Конструктор
    public Card(String title_exercise, int progress, int goal){
        this.title_exercise = title_exercise;
        this.progress = progress;
        this.goal = goal;
    }

    /**Getters and setters*/

    public String getTitle(){
        return this.title_exercise;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public int getPosition(){
        return this.position;
    }

    public void increaseProgress(){
        // увеличить количество сделанных повторений
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
        //TODO: Strings --->> res string
        return "Осталось сделать: " + String.valueOf(this.goal - this.progress);
    }

    String getTitle_exercise(){return this.title_exercise;}

}
