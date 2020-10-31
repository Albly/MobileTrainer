package com.example.mobiletrainer;
import android.hardware.Sensor;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.mobiletrainer.Constants;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSV {

    /**Класс, осуществляющий запись данных в CSV файлы**/

    static int count = 0;

    public static int getFileCount(){
        return count;
    }

    public static void setFileCount(int num){
        count = num;
    }

    public static void increment_count(){count++;}

   static String setFileName(int sensor, int typeOfExercise, boolean isProcessed){
        switch (sensor){
            case Sensor.TYPE_ACCELEROMETER:{
                return typeOfExercise + (isProcessed ? "_A_" : "_XA_") ;
            }
            case  Sensor.TYPE_LINEAR_ACCELERATION:{
                return typeOfExercise + (isProcessed ? "_L_": "_XL_") ;
            }
            case Sensor.TYPE_GRAVITY:{
                return typeOfExercise + (isProcessed ? "_g_": "_Xg_") ;
            }
            case Sensor.TYPE_GYROSCOPE:{
                return typeOfExercise + (isProcessed ? "_B_": "_XB_");
            }
            case Sensor.TYPE_GAME_ROTATION_VECTOR:{
                return typeOfExercise + (isProcessed ? "_R_": "_XR_");
            }

            case -10001: {
                return typeOfExercise + (isProcessed ? "_Z_": "_XZ_");

            }
        }
        return "NULL";
    }


    public static void writeToCsv(double[] x, double[] y, double[] z , int sensor, int typeOfExercise, boolean isProcessed) {

        String name = setFileName(sensor,typeOfExercise,isProcessed) + "("+count+")";
        //count++;

        String csv = (Environment.getExternalStorageDirectory() + "/" + Constants.APP_FOLDER_NAME+"/" + name + ".csv");
        Log.i("================",csv);


        ArrayList<String[]> strings = new ArrayList<>();
        strings.add(new String[]{"n","x","y","z","val"}); // заголовок

        // Преобразуем массив чисел в массив строк
        int size = x.length;

        strings.add(new String[]{String.valueOf(0), String.valueOf(x[0]), String.valueOf(y[0]), String.valueOf(z[0]), String.valueOf(typeOfExercise)});

        for(int i = 1; i < size ; i++) {
            strings.add(new String[]{String.valueOf(i), String.valueOf(x[i]), String.valueOf(y[i]), String.valueOf(z[i])});
        }

        try
        {
            CSVWriter  writer = new CSVWriter(new FileWriter(csv));
            writer.writeAll(strings);
            writer.close();

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
