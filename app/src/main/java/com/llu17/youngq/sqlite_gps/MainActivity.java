package com.llu17.youngq.sqlite_gps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.llu17.youngq.sqlite_gps.data.GpsContract;
import com.llu17.youngq.sqlite_gps.data.GpsDbHelper;

import okhttp3.OkHttpClient;


public class MainActivity extends AppCompatActivity {

//    private GuestListAdapter mAdapter;

    private final static String tag = "Gps_SQLite";
    static TextView tv_longitude, tv_latitude;
    public static SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //////////
        /*===check sqlite data using "chrome://inspect"===*/
        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        //////////
        tv_longitude = (TextView)findViewById(R.id.longitude);
        tv_latitude = (TextView)findViewById(R.id.latitude);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}, 10);
//            Toast.makeText(this, "Check_Permission", Toast.LENGTH_SHORT).show();
        }
        GpsDbHelper dbHelper = new GpsDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        int v = 0;
        try {
            v = getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("version:-------",""+ v);

    }

    public void startService(View view) {
        Toast.makeText(this, "Starting the service", Toast.LENGTH_SHORT).show();
        startService(new Intent(getBaseContext(), CollectorService.class));
        startService(new Intent(getBaseContext(), Activity_Tracker.class));
    }

    // Method to stop the service
    public void stopService(View view) {
        Toast.makeText(this, "Stopping the service", Toast.LENGTH_SHORT).show();
        stopService(new Intent(getBaseContext(), CollectorService.class));
        stopService(new Intent(getBaseContext(), Activity_Tracker.class));
        stopService(new Intent(getBaseContext(), HandleActivity.class));
    }
}
