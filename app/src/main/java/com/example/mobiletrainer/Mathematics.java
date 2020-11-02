package com.example.mobiletrainer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.Size;

public class Mathematics {

    /**Класс для математического преобразования данных*/



    public static float[] processData(ArrayList<double[]> data, int N0){
        /**Функция обработки данных*/

        // компоненты датичков
        double[] x = new double[data.size()];
        double[] y = new double[data.size()];
        double[] z = new double[data.size()];

        for(int i=0; i< data.size(); i++){
            double[] xyz = data.get(i);
            x[i] = xyz[0];                      // вытаскиваем из массива все х
            y[i] = xyz[1];                      // вытаскиваем из массива все y
            z[i] = xyz[2];                      // вытаскиваем из массива все z
        }

        x = interpolate(x, N0);                 // интерполируем х
        y = interpolate(y, N0);                 // интерполируем y
        z = interpolate(z, N0);                 // интерполируем z

        x = FFT(x);                             // Фурье от х
        y = FFT(y);                             // Фурье от y
        z = FFT(z);                             // Фурье от z

        double maxValue  = findMaxValue(x,y,z);

        x = normaliziation(x, maxValue);        // Нормируем на максимальное значение х
        y = normaliziation(y, maxValue);        // Нормируем на максимальное значение y
        z = normaliziation(z, maxValue);        // Нормируем на максимальное значение z

        // оставляем только первые 15 значенй FFT
        x = cutArray(x,15);
        y = cutArray(y, 15);
        z = cutArray(z, 15);

        float fx[] = new float[x.length];
        float fy[] = new float[y.length];
        float fz[] = new float[z.length];

        for(int i = 0; i < x.length; i++){
            fx[i] = (float)x[i];
            fy[i] = (float)y[i];
            fz[i] = (float)z[i];
        }

        return (ArrayUtils.addAll(ArrayUtils.addAll(fx,fy),fz));

    }

    public static void analyze(ArrayList<double[]> data, int N0 , int sensor, int typeOfExercise){
        if(data.size()==0){return;}

        double[] x = new double[data.size()];
        double[] y = new double[data.size()];
        double[] z = new double[data.size()];

        for(int i=0; i< data.size(); i++){
            double[] xyz = data.get(i);
            x[i] = xyz[0];                      // вытаскиваем из массива все х
            y[i] = xyz[1];                      // вытаскиваем из массива все y
            z[i] = xyz[2];                      // вытаскиваем из массива все z
        }

        //Записываем данные в CSV (без обработки)
        CSV.writeToCsv(x,y,z,sensor,typeOfExercise,false);

        x = interpolate(x, N0);                 // интерполируем х
        y = interpolate(y, N0);                 // интерполируем y
        z = interpolate(z, N0);                 // интерполируем z

        x = FFT(x);                             // Фурье от х
        y = FFT(y);                             // Фурье от y
        z = FFT(z);                             // Фурье от z

        double maxValue  = findMaxValue(x,y,z); // Максимальная амплутуда гармоники

        x = normaliziation(x, maxValue);        // Нормируем на максимальное значение х
        y = normaliziation(y, maxValue);        // Нормируем на максимальное значение y
        z = normaliziation(z, maxValue);        // Нормируем на максимальное значение z

        // оставляем только первые 15 значенй FFT
        x = cutArray(x,15);
        y = cutArray(y, 15);
        z = cutArray(z, 15);

        //Записываем данные в CSV (с обработкой)
        CSV.writeToCsv(x,y,z,sensor,typeOfExercise,true);
    }


    /**Обрезает массив */
    public static double[] cutArray(double[] array, int new_size){
        int size = array.length;
        if(new_size >= size){return array;}

        double[] cut = new double[new_size];
        System.arraycopy(array, 0, cut, 0, new_size);
        return cut;
    }

    /**Находит максимальное абсолютное значение в 3 векторах Х,Y,Z и выводит его
     * */
    public static double findMaxValue(@NotNull double[] X, @NotNull double[] Y, @NotNull double[] Z){
        double maxValue = 0;

        for(int i=0; i< X.length; i++){

            if(Math.abs(X[i]) > maxValue){
                maxValue = Math.abs(X[i]);
            }

            if(Math.abs(Y[i]) > maxValue){
                maxValue = Math.abs(Y[i]);
            }

            if(Math.abs(Z[i]) > maxValue) {
                maxValue = Math.abs(Z[i]);
            }
        }

        return maxValue;
    }

    /**Нормирует вектор Х на максимальное значение. Возвращает нормированный вектор*/
    public static double[] normaliziation(@NotNull double[] X, double maxValue){
        if(maxValue ==0){maxValue=1;}
        for(int i =0 ; i < X.length; i++){
            X[i] = X[i]/maxValue;
        }
        return X;
    }

    /**Интерполяция. Функции Акимы были выбраны по причине хорошей стабильности,
     * в отличие от кубических сплайнов*/

    public static double[] interpolate(@NotNull double[] Y, @Size(min = 1) int count){
        AkimaSplineInterpolator interpolator = new AkimaSplineInterpolator();

        double[] x = new double[Y.length]; // Старые отсчёты
        double[] x_new = new double[count]; // Новые отсчёты

        for(int i = 0; i < Y.length; i++){
            x[i] = i;
        }

        for(int i = 0; i < count; i++){
            x_new[i] = ( ( (double)(Y.length-1) ) / ( (double)(count-1) ) ) * i;
        }

        PolynomialSplineFunction function = interpolator.interpolate(x,Y); // находим функцию по старым значениям

        double[] y_new = new double[count]; // новые значения

        for (int i = 0; i < count-1; i++){
            y_new[i] = function.value(x_new[i]); //вычисляем новые значения в точках x_new
        }

        return y_new;
    }

    /** Быстрое преобрзование Фурье. Для преноса информации из временной области в чстотную*/
    public static double[] FFT(double[] inputArray){

        double[] tempConversion = new double[inputArray.length];

        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        try {
            Complex[] complex = transformer.transform(inputArray, TransformType.FORWARD);

            for (int i = 0; i < complex.length; i++) {
                double rr = (complex[i].getReal());
                double ri = (complex[i].getImaginary());

                tempConversion[i] = Math.sqrt((rr * rr) + (ri * ri));
            }

        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return tempConversion;
    }

}
