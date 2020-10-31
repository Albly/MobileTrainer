package com.example.mobiletrainer;

public class DataFormat {

    /**Функции для преобрзования форматов */





    /**
     * Из массива данных делает строку с отступами для вывода в textView
     * РАЗМЕР МАССИВА = 3 !
     * */
   public static String prepareData(double[] data){
        StringBuilder res = new StringBuilder();

        for(int i =0; i<3 ; i++){
            res.append(format(data[i]))
                    .append("                       ");
        }

        return res.toString();
    }


    public static String prepareData(float[] data){
        StringBuilder res = new StringBuilder();

        for(int i =0; i<3 ; i++){
            res.append(format(data[i]))
                    .append("                       ");
        }

        return res.toString();
    }



    /**Преобразует double в строку и обрезает числа после 3 знака после запятой
     * Отрицательные значения будут показывается в скобках : ()*/
   public  static String format(double value){
        return String.format(String.format("%(.3f",value));
    }

    public  static String format(float value){
        return String.format(String.format("%(.3f",value));
    }
}
