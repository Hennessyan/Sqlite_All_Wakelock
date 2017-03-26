package com.llu17.youngq.sqlite_gps.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by youngq on 17/3/2.
 */

public class GpsDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "gps_location.db";
    private static final int DATABASE_VERSION = 1;

    public GpsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_GPS_LOACTION_TABLE = "CREATE TABLE " +
                GpsContract.GpsEntry.TABLE_NAME + " (" +
                GpsContract.GpsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GpsContract.GpsEntry.COLUMN_ID + " TEXT NOT NULL, " +
                GpsContract.GpsEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
                GpsContract.GpsEntry.COLUMN_LATITUDE + " REAL NOT NULL, " +
                GpsContract.GpsEntry.COLUMN_LONGITUDE + " REAL NOT NULL" +
                ");";
        db.execSQL(SQL_CREATE_GPS_LOACTION_TABLE);
        Log.e("=====GPS table=====","success!");
        final String SQL_CREATE_ACCELEROMETER_TABLE = "CREATE TABLE " +
                GpsContract.AccelerometerEntry.TABLE_NAME + " (" +
                GpsContract.AccelerometerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GpsContract.AccelerometerEntry.COLUMN_ID + " TEXT NOT NULL, " +
                GpsContract.AccelerometerEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
                GpsContract.AccelerometerEntry.COLUMN_X + " REAL NOT NULL, " +
                GpsContract.AccelerometerEntry.COLUMN_Y + " REAL NOT NULL, " +
                GpsContract.AccelerometerEntry.COLUMN_Z + " REAL NOT NULL" +
                ");";
        db.execSQL(SQL_CREATE_ACCELEROMETER_TABLE);
        Log.e("=====ACCELE table=====","success!");
        final String SQL_CREATE_GYROSCOPE_TABLE = "CREATE TABLE " +
                GpsContract.GyroscopeEntry.TABLE_NAME + " (" +
                GpsContract.GyroscopeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GpsContract.GyroscopeEntry.COLUMN_ID + " TEXT NOT NULL, " +
                GpsContract.GyroscopeEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
                GpsContract.GyroscopeEntry.COLUMN_X + " REAL NOT NULL, " +
                GpsContract.GyroscopeEntry.COLUMN_Y + " REAL NOT NULL, " +
                GpsContract.GyroscopeEntry.COLUMN_Z + " REAL NOT NULL" +
                ");";
        db.execSQL(SQL_CREATE_GYROSCOPE_TABLE);
        Log.e("=====GYRO table=====","success!");
        final String SQL_CREATE_MOTIONSTATE_TABLE = "CREATE TABLE " +
                GpsContract.MotionStateEntry.TABLE_NAME + " (" +
                GpsContract.MotionStateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GpsContract.MotionStateEntry.COLUMN_ID + " TEXT NOT NULL, " +
                GpsContract.MotionStateEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
                GpsContract.MotionStateEntry.COLUMN_STATE + " INTEGER NOT NULL" +
                ");";
        db.execSQL(SQL_CREATE_MOTIONSTATE_TABLE);
        Log.e("=====MOTION table=====","success!");
        final String SQL_CREATE_STEPCOUNT_TABLE = "CREATE TABLE " +
                GpsContract.StepEntry.TABLE_NAME + " (" +
                GpsContract.StepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GpsContract.StepEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                GpsContract.StepEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
                GpsContract.StepEntry.COLUMN_COUNT + " INTEGER NOT NULL" +
                ");";
        db.execSQL(SQL_CREATE_STEPCOUNT_TABLE);
        Log.e("=====STEP table=====","success!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GpsContract.GpsEntry.TABLE_NAME);
        Log.e("===update GPS table===","success!");
        onCreate(db);
    }
}
