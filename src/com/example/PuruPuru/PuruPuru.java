package com.example.PuruPuru;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class PuruPuru extends Activity
{
    public static final int MENU_SELECT_A = 0;
    public static final int MENU_SELECT_B = 1;

    private MainView mainView;
    private SensorManager sensorManager;
    private boolean isAccSensor = false;

    private boolean useAccelerometer = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainView = (MainView)this.findViewById(R.layout.main);
        setContentView(R.layout.main);
        mainView = (MainView)findViewById(R.id.MainView01);

        useAccelerometer = Setting.useAccelerometer(this);
        if (useAccelerometer){
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (!useAccelerometer) {
            return;
        }
        
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor : sensors) {
            if( sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                sensorManager.registerListener( mainView, sensor, SensorManager.SENSOR_DELAY_UI);
                isAccSensor = false;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isAccSensor){
            sensorManager.unregisterListener(mainView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SELECT_A, 0, getText(R.string.finish).toString()).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        menu.add(0, MENU_SELECT_B, 0, getText(R.string.settings).toString()).setIcon(android.R.drawable.ic_menu_manage);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SELECT_A:
                finish();
                return true;

            case MENU_SELECT_B:
                Intent it =new Intent();
                it.setClass(PuruPuru.this, Setting.class);
                startActivity(it);
                return true;

        }
        return false;
    }
}
