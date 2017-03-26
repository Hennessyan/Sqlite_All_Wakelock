package com.llu17.youngq.sqlite_gps;

import android.content.ContentValues;
import android.database.SQLException;
import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.llu17.youngq.sqlite_gps.data.GpsContract;

import static com.llu17.youngq.sqlite_gps.CollectorService.id;
import static com.llu17.youngq.sqlite_gps.MainActivity.mDb;

/**
 * Created by pradeepsaiuppula on 2/7/17.
 */

public class UploadData {

    long logTime = System.currentTimeMillis();

    public void upload_activity(int num){
        long temp_time = System.currentTimeMillis();
        ContentValues cv_motion = new ContentValues();
        cv_motion.put(GpsContract.MotionStateEntry.COLUMN_ID,id);
        cv_motion.put(GpsContract.MotionStateEntry.COLUMN_TIMESTAMP,temp_time);
        cv_motion.put(GpsContract.MotionStateEntry.COLUMN_STATE, num);
        try
        {
            mDb.beginTransaction();
            mDb.insert(GpsContract.MotionStateEntry.TABLE_NAME, null, cv_motion);
            mDb.setTransactionSuccessful();
            Log.e("===insert motion===","success!");
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
