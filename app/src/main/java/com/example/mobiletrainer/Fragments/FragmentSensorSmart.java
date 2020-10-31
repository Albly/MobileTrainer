package com.example.mobiletrainer.Fragments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;

import static com.example.mobiletrainer.Mathematics.analyze;

abstract public class FragmentSensorSmart extends FragmentSensor {

    /**Класс для более детальной работы с датчиками. Содержит интегральыне фильтры,
     * при срабатывании которых приложение записывает данные с датчиков в паямть
     * для последующих действий*/


    /** Таймер. Производит запись данных в массивы c периодом timerPeriod*/
    Timer timer;
    final int timerPeriod = 20;

    /**===========================================================================================*/

    boolean isWriting = false;
    public boolean isChecked = false;

    /** Массивы куда записываются данные во время упражнения*/

    ArrayList<double[]> accelList;
    ArrayList<double[]> gyroList;
    ArrayList<double[]> gravityList;
    ArrayList<double[]> linearList;
    ArrayList<double[]> rotationList;
    ArrayList<double[]> angleShiftList;

    /**===========================================================================================*/
    final int N0 = 128;

    /** Фильтр для анализа линейного ускорения*/
    int counter = 0;
    ArrayList<Double> linearFilter;
    final int REAL_LINER_FILTER_SIZE = 30;
    final int LINEAR_FILTER_SIZE = 50;
    final int PERCENT_OF_GROW_FILTER = 71; // ОТ 1 до 100 !!!
    final double LINEAR_GROW_THRESHOLD = 2.0;


    final int PERCENT_OF_ROW_FILTER = 83; // от 1 до 100 !!!

    /**Фильтр для анализа углового ускорения*/
    int counter2 = 0;
    double[] gyroscopePrev = new double[3];
    final int REAL_ROTATION_FILTER_SIZE = 30;
    final int ROTATION_FILTER_SIZE = 50;

    /**===========================================================================================*/


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initArrays();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isWriting) {
                            writeToList();
                        } else {
                            writeToListFilter();
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, timerPeriod);
        super.onResume();
    }

    @Override
    public void onPause() {
        timer.cancel();
        super.onPause();
    }

    void writeToList(){
        accelList.add(accelerometerComponents);
        gyroList.add(gyroscopeComponents);
        gravityList.add(gravityComponents);
        linearList.add(linearComponents);
        rotationList.add(rotationComponents);
        angleShiftList.add(angleShift);
    }

    void writeToListFilter(){
        writeToList();
        if(accelList.size() > REAL_LINER_FILTER_SIZE) {accelList.remove(0);}
        if(gyroList.size() > REAL_LINER_FILTER_SIZE) {gyroList.remove(0);}
        if(gravityList.size() > REAL_LINER_FILTER_SIZE) {gravityList.remove(0);}
        if(linearList.size() > REAL_LINER_FILTER_SIZE) {linearList.remove(0);}
        if(rotationList.size() > REAL_LINER_FILTER_SIZE) {rotationList.remove(0);}
        if(angleShiftList.size() > REAL_LINER_FILTER_SIZE){angleShiftList.remove(0);}

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        super.onSensorChanged(sensorEvent);

        /**Тут вычисляется модуль линейного ускорения, по превышению которого приложение
         * понимает, что началось движение*/

        if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            double x = sensorEvent.values[0];
            double y = sensorEvent.values[1];
            double z = sensorEvent.values[2];

            linearFilter.add(Math.sqrt( x*x + y*y + z*z ));

            if(linearFilter.size() > LINEAR_FILTER_SIZE){
                linearFilter.remove(0);
            }

        }

    }

    /**Поиск первышения порога по угловой скорости
     * Защищен от случайных шумов датчиков
     * */
    public boolean findRotationGrow(){
        for (int i = 0 ; i < 3; i++){
            double delta = Math.abs(gyroscopeComponents[i] - gyroscopePrev[i]);

            if(delta > 0.4){
                counter2++;
            }else {counter2--;}

            if(counter2 > 40){
                counter2 = 0;
                return true;
            }

            if(counter2 < 0 ){
                counter2 = 0;
            }
        }
        return false;
    }

    /**Поиск снижения порога по угловой скорости
     * Защищен от случайных шумов датчиков
     * */

    public boolean findRotationRow(){
        for (int i = 0 ; i < 3; i++){
            double delta = Math.abs(gyroscopeComponents[i] - gyroscopePrev[i]);

            if(delta < 0.08){
                counter2++;
            }else {counter2--;}

            if(counter2 > 40){
                counter2 = 0;
                return true;
            }

            if(counter2 < 0 ){
                counter2 = 0;
            }
        }
        return false;
    }

    /**Поиск первышения порога по абсолютному линейному ускорению
     * Защищен от случайных шумов датчиков
     * */
    public boolean findGrow(){
        // Если более чем 35 значений подходят по условию из 49
        for(int i = 49; i > 0;i --){
            if( linearFilter.get(i) > 2.0 ){
                counter++;
            }
            if( counter > 35 ){
                counter = 0;
                return true;
            }
        }
        counter = 0;
        return false;
    }

    /**Поиск снижения порога по абсолютному линейному ускорению
     * Защищен от случайных шумов датчиков
     * */

    public boolean findRow(){
        // Если более чем 41 значений подходят по условию из 49
        for(int i = 49; i > 0; i--){
            if( linearFilter.get(i) < 1.9){
                counter++;
            }
            if(counter > 41){
                counter = 0;
                return true;
            }
        }
        counter = 0;
        return false;
    }


    /*
    boolean findGrow(){
        for (int i = LINEAR_FILTER_SIZE; i > 0 ; i-- ){
            if(linearFilter.get(i) > LINEAR_GROW_THRESHOLD){
                counter++;
            }
            if(counter > Math.round(LINEAR_FILTER_SIZE * PERCENT_OF_GROW_FILTER / 100.0){
                return true;
            }
        }
        counter = 0;
        return false;
    }


*/

    void initArrays(){

        accelList = new ArrayList<>();
        gyroList = new ArrayList<>();
        gravityList = new ArrayList<>();
        linearList = new ArrayList<>();
        rotationList = new ArrayList<>();
        angleShiftList = new ArrayList<>();

        linearFilter = new ArrayList<>();

    }

    public void clearArrays(){

        accelList.clear();
        gyroList.clear();
        gravityList.clear();
        linearList.clear();
        rotationList.clear();
        angleShiftList.clear();

    }

}
