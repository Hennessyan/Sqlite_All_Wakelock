package com.llu17.youngq.sqlite_gps;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.llu17.youngq.sqlite_gps.data.GpsContract;
import com.llu17.youngq.sqlite_gps.data.GpsDbHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;

import static com.llu17.youngq.sqlite_gps.MainActivity.mDb;

/**
 * Created by youngq on 17/2/15.
 */

public class CollectorService extends Service implements SensorEventListener {

    PowerManager.WakeLock wakeLock = null;

    public static final String ACTION = "com.llu17.youngq.sqlite_gps.CollectorService";
    private final static String tag = "UploadService";
    /*===GPS===*/
    private LocationManager locationManager;
    private double latitude, longitude;
    public static String id = ""; //phone id

    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.e("===location===","changed!");
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            MainActivity.tv_longitude.setText("real longitude: " + longitude);
            MainActivity.tv_latitude.setText("real latitude: " + latitude);

            long temp_time = System.currentTimeMillis();
            ContentValues cv = new ContentValues();
            cv.put(GpsContract.GpsEntry.COLUMN_ID,id);
            cv.put(GpsContract.GpsEntry.COLUMN_TIMESTAMP,temp_time);
            cv.put(GpsContract.GpsEntry.COLUMN_LATITUDE, latitude);
            cv.put(GpsContract.GpsEntry.COLUMN_LONGITUDE, longitude);

            try
            {
                mDb.beginTransaction();
                mDb.insert(GpsContract.GpsEntry.TABLE_NAME, null, cv);
                mDb.setTransactionSuccessful();
                Log.e("===insert===","success!");
            }
            catch (SQLException e) {
                //too bad :(
            }
            finally
            {
                mDb.endTransaction();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);
        }
    };
    /*===Sensor:accelerometer & gyroscope===*/
    private static int RATE = 1000;  //100 -> 10 samples/s 50 -> 20 samples/s 20 -> 50 samples/s
    private SensorManager sensorManager;
    private Sensor sensor;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private long timestamp;
    private int label = 0;
    private double[] stepcount = new double[2]; //stepcount 1.last one 2.real step
    private double[] acce = new double[3];    //accelerator
    private double[] angle = new double[3];
    private double[] gyro = new double[3];  //gyroscope
    Timer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        acquireWakeLock();
        label = 0;
        id = getSerialNumber();
        /*===GPS===*/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }
        catch(SecurityException e){
            e.getStackTrace();
        }
        Log.e("===GPS===","===begin===");
//        Toast.makeText(this, "Starting the GPS!!!!!", Toast.LENGTH_SHORT).show();
        /*===Sensor===*/
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.e("===Sensor===","===begin===");
        /*---step count---*/
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
//        Toast.makeText(this, "Sensor Service Started", Toast.LENGTH_SHORT).show();
        timer = new Timer();
        timer.schedule(new Upload(acce, gyro, stepcount), 0, RATE);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
        try {
            locationManager.removeUpdates(locationListener);
//            Toast.makeText(this, "Stopping the GPS", Toast.LENGTH_SHORT).show();
            /*===show num and last timestamp of GPS Record===*/
//            int count = getCount();
//            MainActivity.tv_count.setText("count: " + count);
//            Log.e("===count==="," " +getCount());
//            MainActivity.tv_timestamp.setText("timestamp: "+getFinaltimestamp(count));
//            Log.e("===timestamp==="," " +getFinaltimestamp(count));
        }
        catch(SecurityException e){
            e.getStackTrace();
        }
        Log.e("===GPS===","===stop===");
        timer.cancel();
        sensorManager.unregisterListener(this);
        Log.e("===Sensor===","===stop===");
//        Toast.makeText(this, "Stopping the Sensor", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*---step count---*/
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            if(label == 0) {
                stepcount[0] = event.values[0];
                label++;
            }
            stepcount[1] = event.values[0] - stepcount[0];
            Log.e("stepcount[0]:",""+stepcount[0]);
            Log.e("stepcount[1]:",""+stepcount[1]);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acce[0] = event.values[0];
            acce[1] = event.values[1];
            acce[2] = event.values[2];
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (timestamp != 0) {
                // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
                final float dT = (event.timestamp - timestamp) * NS2S;
                // 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的旋转弧度
                angle[0] += event.values[0] * dT;
                angle[1] += event.values[1] * dT;
                angle[2] += event.values[2] * dT;
                // 将弧度转化为角度
                gyro[0] = (float) Math.toDegrees(angle[0]);
                gyro[1] = (float) Math.toDegrees(angle[1]);
                gyro[2] = (float) Math.toDegrees(angle[2]);
            }
            timestamp = event.timestamp;
        }
//        Log.d("hi","debug");
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.e("onAccuracy®Changed",""+accuracy);
    }

    private static String getSerialNumber(){

        String serial = null;

        try {

            Class<?> c =Class.forName("android.os.SystemProperties");

            Method get =c.getMethod("get", String.class);

            serial = (String)get.invoke(c, "ro.serialno");

        } catch (Exception e) {

            e.printStackTrace();

        }

        return serial;

    }

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock()
    {
        if (null == wakeLock)
        {
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock)
            {
                wakeLock.acquire();
            }
        }
    }
    //释放设备电源锁
    private void releaseWakeLock()
    {
        if (null != wakeLock)
        {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
