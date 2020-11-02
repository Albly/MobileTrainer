package com.example.mobiletrainer.Fragments;

public interface FragmentCallback {

    /** Указатели на кнопки, которые были нажаты */

    String BTN_TEST = "BTN_TEST";
    String BTN_WRITE_DATASET = "BTN_WRITE_DATASET";
    String BTN_SENSORS = "BTN_SENSORS";


    /**Функция передеющая указатель кнопки в активность для обработки нажатия*/
    void getPressedButton(String tag);
}
