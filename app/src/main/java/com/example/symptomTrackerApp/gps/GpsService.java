package com.example.symptomTrackerApp.gps;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.example.symptomTrackerApp.dbAdapter.ApplicationDataBase;
import com.example.symptomTrackerApp.dbAdapter.User;
import com.example.symptomTrackerApp.dbAdapter.DbHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import net.sqlcipher.database.SQLiteDatabase;

import java.sql.Date;

public class GpsService extends Service {

    FusedLocationProviderClient locationProviderClient;
    private ApplicationDataBase db;
    private String applicationPath = Environment.getExternalStorageDirectory().getPath();

    public long latitude;
    public long longitude;

    // This method run only one time. At the first time of service created and running
    @Override
    public void onCreate() {
        db = ApplicationDataBase.getInstance(getApplicationContext());

        SQLiteDatabase.loadLibs(this);


        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Log.d("onCreate()", "After service created");
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Here is the source of the TOASTS :D
        Toast.makeText(this, "Freshly Made toast!", Toast.LENGTH_SHORT).show();

        final Context context=this;
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//           // ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            return Service.START_NOT_STICKY;
//        }
        locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            public void onSuccess(Location location) {


                //System.out.println(longitude);
                if (location != null) {
                    // Logic to handle location object
                    latitude = (long) location.getLatitude();
                    longitude = (long) location.getLongitude();

                    //Adding to database


                    Thread thread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            User user=new User();
                            user.latitude=latitude;
                            user.longitude=longitude;
                            Date d=new Date(System.currentTimeMillis());
                            user.locationTimeStamp=d;

                            DbHelper.getInstance(context).InsertData(user);

                            //db.userInfoDao().insert(user);
                        }
                    });
                    thread.start();
                    Toast.makeText(context,"The latitude is "+latitude+" The longitude is "+longitude,Toast.LENGTH_SHORT).show();

                    Log.e("Over here","The lat "+latitude+" The long "+longitude);

                }
            }
        });
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding
        return null;
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            Toast.makeText(
                    getBaseContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " + loc.getLongitude();
            String latitude = "Latitude: " + loc.getLatitude();


        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

}