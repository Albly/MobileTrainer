package com.example.mobiletrainer.Fragments;

public interface FragmentCallback {

    String BTN_TEST = "BTN_TEST";
    String BTN_WRITE_DATASET = "BTN_WRITE_DATASET";
    String BTN_SENSORS = "BTN_SENSORS";

    String BTN_HOME = "BTN_HOME";
    String BTN_DASHBOARD = "BTN_DASHBOARD";
    String BTN_NOTIFICATIONS = "BTN_NOTIFICATIONS";

    void getPressedButton(String tag);
}
