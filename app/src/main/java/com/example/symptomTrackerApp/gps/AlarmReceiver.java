package com.example.symptomTrackerApp.gps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.e("Receive","Receiving");

        Toast.makeText(context,"The service is working fine",Toast.LENGTH_SHORT).show();
        Intent in = new Intent(context, GpsService.class);
        context.startService(in);
       setAlarm(context);
    }

    public void setAlarm(Context context)
    {
        AlarmManager am =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        assert am != null;
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 900000, pi); //Next alarm in 15s
    }
}
