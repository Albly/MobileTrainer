package com.example.mobiletrainer.Fragments;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.example.mobiletrainer.Classifier;
import com.example.mobiletrainer.Constants;
import com.example.mobiletrainer.Mathematics;
import com.example.mobiletrainer.R;
import com.example.mobiletrainer.RecyclerCards.Callback;
import com.example.mobiletrainer.RecyclerCards.Card;
import com.example.mobiletrainer.RecyclerCards.RVAdapter;
import com.example.mobiletrainer.Utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.mobiletrainer.Constants.NETWORK_FILE;

/**Фрагмент для тестирования сети. Выводит карточки с количеством выполненных упражнений*/
public class FragmentCards extends FragmentSensorSmart  {

    //Классификатор
    Classifier classifier;
    //переключатель для начала тренирвоки
    Switch sw_isTraining;

    //Объекты для карточек
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
        //TODO: Реализовать сохрание данных карточек
       // Card.saveCards(cards);
        super.onStop();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        View parent = inflater.inflate(R.layout.fragment_with_cards, container, false);

        //инициализация View
        recyclerView = (RecyclerView)parent.findViewById(R.id.rv);
        registerForContextMenu(recyclerView);
        sw_isTraining = parent.findViewById(R.id.sw_isTrain);

        //Инициализация классификатора
        classifier = new Classifier(Utils.assetFilePath(getContext(),NETWORK_FILE));

        //Иницализация управления recyclerView
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        init();

        adapter = new RVAdapter(cards);
        adapter.setCurrentContext(getContext());
        recyclerView.setAdapter(adapter);

        return parent;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // выполянем реализацию суперкласса
        super.onSensorChanged(sensorEvent);

        //Проверяем наличие данных на акселерометре
        if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            //Проверяем состояние выключателя "начать тренировку"
            if(sw_isTraining.isChecked()){
                // Условие начала упражнения: Упраженение еще не начато (данные не пишутся в лист)
                // И зафиксированно превышение амплитуды интегральным фильтром
                if(!isWriting && findGrow()){
                    //Флаг, что данные пишутся
                    isWriting = true;
                    //воспроизвести звук начала упражнения
                    soundStart();
                }

                // Проверяем условие конца упражнения: Данные пишутся в лист и
                // зафиксирована стабилизация амплутуды интегральным фильтром
                if(isWriting && findRow()){
                    // Флаг, что данные больше не пушутся
                    isWriting = false;
                    //воспроизводим звук конца упражнения
                    soundEnd();

                    // Обрабатываем записанные данные и помещаем в переменные
                    float[] linear = Mathematics.processData(linearList,N0);
                    float[] gravity = Mathematics.processData(gravityList,N0);
                    float[] gyroscope = Mathematics.processData(gyroList,N0);

                    // Конкатенируем данные в один вектор
                    float[] data = ArrayUtils.addAll(ArrayUtils.addAll(linear,gravity),gyroscope);

                    // Посылаем на классификатор
                    int result = classifier.predict(data);

                    // Применяем результат на интерфейсе
                    updateUIwithResult(result);
                    // Очищаем массивы и фильтры
                    clearArrays();

                }

            }
        }
    }

    public void updateUIwithResult(int result){
        /**Применяет результат классификации к интерфейсу*/
        //TODO: Use strings res instead of Strings

        // Сравниваем результат классификации и увеличиваем количество упражнения
        switch (result){
            case -1: {
                // не распознано
                break;
            }

            case Constants.HAND_ROTATION:{
                increaseExercise("Вращение рукуами");
                break;
            }
            case Constants.LUNGE:{
                increaseExercise("Выпады");
                break;
            }

            case Constants.PUSH_UPS:{
                increaseExercise("Отжимания");
                break;
            }
            case Constants.HAND_LIFTS:{
                increaseExercise("Подъёмы рук");
                break;
            }
            case  Constants.PRESS:{
                increaseExercise("Пресс");
                break;
            }

            case Constants.SQUATTING:{
                increaseExercise("Приседания");
                break;
            }

            case Constants.JUMPING:{
                increaseExercise("Прыжок");
                break;
            }


        }
    }


    public void soundEnd(){
        /**Воспроизведение звука конца упражнения в отдельном потоке*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.kok);
                mp.start();
            }
        }).start();
    }

    public void soundStart(){
        /**Воспроизведение звука начала упражнения в отдельном потоке*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.kek);
                mp.start();
            }
        }).start();
    }

    public boolean onContextItemSelected(MenuItem item) {
        /**Обработка путнков меню после долгого нажания на карточку*/
        // Обрабатывает удаление карточки
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
        /**Загрузка или создание списка с карточками*/
        //TODO: Реализовать загрузку
        if(cards==null) {
           // if (Card.loadCards() == null) {
                cards = new ArrayList<>();

           // } else {
             //   cards = Card.loadCards();
           // }
        }
    }


    public void increaseExercise(String title){
        /**Увеличивает количество повторений упражнения по его названию*/

        //Находим нужную карточку
        for(Card card : cards){
            if(card.getTitle().equals(title)){
                //увеличиваем количество повторений
                card.increaseProgress();
                //обновляем интерфейс
                adapter.notifyItemChanged(card.getPosition());
                return;
            }
        }
        // Если нужной карточки не оказалось, создаем её
        addCard(title, 1, 20);
    }

    public void addCard(String title_exercise, int progress, int goal){
        /**Добавляет новую карточку по названию,
         *  кол-ву сделанных уражнений и кол-ву требуемых повторений*/
        //TODO: Реализовать сохранение данных новых карточек

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
        /**Удаляет карточку на указанной позиции, обновляет UI*/

        cards.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position,cards.size());
    }

}
