package com.example.mobiletrainer.Fragments;

public interface FragmentCallback {

    public String BTN_TEST = "BTN_TEST";
    public String BTN_WRITE_DATASET = "BTN_WRITE_DATASET";
    String BTN_SENSORS = "BTN_SENSORS";

    void getPressedButton(String tag);
}
