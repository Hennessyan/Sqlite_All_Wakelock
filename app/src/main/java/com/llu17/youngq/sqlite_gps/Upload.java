package com.llu17.youngq.sqlite_gps;

import android.content.ContentValues;
import android.database.SQLException;
import android.util.Log;

import com.llu17.youngq.sqlite_gps.data.GpsContract;

import java.util.TimerTask;

import static com.llu17.youngq.sqlite_gps.CollectorService.id;
import static com.llu17.youngq.sqlite_gps.MainActivity.mDb;

/**
 * Created by youngq on 17/2/9.
 */

public class Upload extends TimerTask {
    int count = 0;
    private String timestamp;
    double[] nums1,nums2,nums3;
    int step = 0;
    private final static long begin_time =  System.currentTimeMillis();

    public Upload(double[] array1, double[] array2, double[] array3){
        nums1 = array1;
        nums2 = array2;
        nums3 = array3;
//        step = a;
    }


    @Override
    public void run() {
        count++;
        long temp_time = System.currentTimeMillis();
        long sub_time = temp_time - begin_time;
//        timestamp = String.valueOf(sub_time);
        timestamp = String.valueOf(sub_time);
        ContentValues cv_acce = new ContentValues();
        cv_acce.put(GpsContract.AccelerometerEntry.COLUMN_ID,id);
        cv_acce.put(GpsContract.AccelerometerEntry.COLUMN_TIMESTAMP,temp_time);
        cv_acce.put(GpsContract.AccelerometerEntry.COLUMN_X, nums1[0]);
        cv_acce.put(GpsContract.AccelerometerEntry.COLUMN_Y, nums1[1]);
        cv_acce.put(GpsContract.AccelerometerEntry.COLUMN_Z, nums1[2]);

        ContentValues cv_gyro = new ContentValues();
        cv_gyro.put(GpsContract.GyroscopeEntry.COLUMN_ID,id);
        cv_gyro.put(GpsContract.GyroscopeEntry.COLUMN_TIMESTAMP,temp_time);
        cv_gyro.put(GpsContract.GyroscopeEntry.COLUMN_X, nums2[0]);
        cv_gyro.put(GpsContract.GyroscopeEntry.COLUMN_Y, nums2[1]);
        cv_gyro.put(GpsContract.GyroscopeEntry.COLUMN_Z, nums2[2]);

        step = (int)nums3[1];
        ContentValues cv_step = new ContentValues();
        cv_step.put(GpsContract.StepEntry.COLUMN_ID,id);
        cv_step.put(GpsContract.StepEntry.COLUMN_TIMESTAMP,temp_time);
        cv_step.put(GpsContract.StepEntry.COLUMN_COUNT, step);
        try
        {
            mDb.beginTransaction();
            mDb.insert(GpsContract.AccelerometerEntry.TABLE_NAME, null, cv_acce);
            mDb.insert(GpsContract.GyroscopeEntry.TABLE_NAME, null, cv_gyro);
            mDb.insert(GpsContract.StepEntry.TABLE_NAME, null, cv_step);
            mDb.setTransactionSuccessful();
            Log.e("===insert sensor===","success!" + count);
        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            mDb.endTransaction();
        }
    }
    
}

