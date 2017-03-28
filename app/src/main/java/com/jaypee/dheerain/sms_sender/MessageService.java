package com.jaypee.dheerain.sms_sender;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Dheerain on 28-03-2017.
 */

public class MessageService extends Service implements SensorEventListener{

    String contact;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    String latitude,longitude;

    public MessageService() {
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        // Toast.makeText(getApplicationContext(), "On start shake", Toast.LENGTH_LONG).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        contact=intent.getStringExtra("number");
        Log.d("service","onstart service");
        SensorManager sManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL); // or other delay

        //    Toast.makeText(getApplicationContext(), "On start Shake service", Toast.LENGTH_LONG).show();

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent se) {
        try {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;

            if (mAccel > 35) {

                //latitude = Preferences.readString(getApplicationContext(), Preferences.LATITUDE);
                //longitude = Preferences.readString(getApplicationContext(), Preferences.LONGITUDE);

                String msg = "Please help me...!!! ";

                if (latitude != null && longitude != null)
                    msg = msg + "http://maps.google.com/?q="+latitude+","+longitude;

                SmsManager smsmanager = SmsManager.getDefault();
                //String phno = Preferences.readString(getApplicationContext(), Preferences.EMERGENCY_NUMBER);
                smsmanager.sendTextMessage(contact, null, msg , null, null);
                Log.d("service",msg);
                Toast.makeText(getApplicationContext(), "Emergency Message sent to " + contact, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
