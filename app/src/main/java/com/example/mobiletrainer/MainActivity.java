package com.example.mobiletrainer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.mobiletrainer.Fragments.FragmentCallback;
import com.example.mobiletrainer.Fragments.FragmentMenu;
import com.example.mobiletrainer.Fragments.FragmentTestNet;
import com.example.mobiletrainer.Fragments.FragmentTestSensors;
import com.example.mobiletrainer.Fragments.FragmentWriteData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements FragmentCallback {

    /**Основная активность приложения
     * Осуществляет переключение между фаргментами
     * Сохранение и загрузку состояний
     * Проверку разрешений*/


    FragmentMenu fragmentMenu;
    FragmentTestSensors fragmentTestSensors;
    FragmentWriteData fragmentWriteData;
    FragmentTestNet fragmentTestNet;

    FragmentTransaction transaction;

    SharedPreferences preferences;

    BottomNavigationView bottomNavigationView;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomAppBar);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.page_1:{
                        replaceFragment(fragmentMenu, false);
                        break;
                    }
                    case R.id.page_2:{
                        replaceFragment(fragmentTestSensors,false);
                        break;
                    }
                    case  R.id.page_3:{
                        replaceFragment(fragmentTestNet, false);
                        break;
                    }
                }

                return false;
            }
        });



        checkFolder();

        preferences = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        loadPreferences();

        fragmentMenu = new FragmentMenu();
        fragmentTestSensors = new FragmentTestSensors();
        fragmentWriteData = new FragmentWriteData();
        fragmentTestNet = new FragmentTestNet();

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, fragmentMenu);
        transaction.commit();

        verifyStoragePermissions(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]  {android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

    }

    @Override
    protected void onStop() {
        savePreferences();
        super.onStop();
    }

    private void loadPreferences() {
        CSV.setFileCount(preferences.getInt(Constants.APP_PREFERENCES_FILE_COUNT,0));
    }

    private void savePreferences(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constants.APP_PREFERENCES_FILE_COUNT,CSV.getFileCount());
        editor.apply();
    }

    private void resetPreferences(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Constants.APP_PREFERENCES_FILE_COUNT);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(),"Файлы будут перезаписаны", Toast.LENGTH_LONG).show();
            resetPreferences();
            CSV.setFileCount(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getPressedButton(String tag) {

        switch (tag){
            case FragmentCallback.BTN_TEST:{
                replaceFragment(fragmentTestNet);
                break;
            }
            case FragmentCallback.BTN_WRITE_DATASET:{
                replaceFragment(fragmentWriteData);
                break;
            }
            case FragmentCallback.BTN_SENSORS:{
                replaceFragment(fragmentTestSensors);
                break;
            }

  /*          case FragmentCallback.BTN_HOME:{
                replaceFragment(fragmentMenu, false);
                break;
            }

            case FragmentCallback.BTN_DASHBOARD:{
                replaceFragment(fragmentTestSensors,false);
                break;
            }

            case FragmentCallback.BTN_NOTIFICATIONS:{
                replaceFragment(fragmentTestNet, false);
                break;
            }*/

        }
    }

    public void replaceFragment(Fragment fragment){
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack){
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.container, fragment);
        if (addToBackStack){
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void checkFolder() {

        File f = new File(Environment.getExternalStorageDirectory(), Constants.APP_FOLDER_NAME);
        if (!f.exists()) {
            boolean isSuccessful = f.mkdirs();
            if(isSuccessful){
                Toast.makeText(getApplicationContext(),"Успешно создали папку :)",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Не удалось создать папку :(",Toast.LENGTH_LONG).show();
            }
        }
    }
}