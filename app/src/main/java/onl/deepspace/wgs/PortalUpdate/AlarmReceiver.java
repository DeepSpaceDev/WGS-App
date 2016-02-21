package onl.deepspace.wgs.PortalUpdate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;

/**
 * Created by Dennis on 20.02.2016.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    private AlarmManager mAlarm;
    private PendingIntent mAlarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, PortalPullService.class);

        String email = Helper.getEmail(context);
        String pw = Helper.getPw(context);
        service.putExtra("email", email);
        service.putExtra("pw", pw);

        startWakefulService(context, service);
    }

    public void setAlarm(Context context) {
        mAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        int interval = context.getResources().getInteger(R.integer.update_interval_in_mins) * 60 * 1000;

        mAlarm.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + interval,
                interval,
                mAlarmIntent);

        ComponentName reciever = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(reciever,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context) {
        if(mAlarm != null) {
            mAlarm.cancel(mAlarmIntent);
        }

        ComponentName reciever = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(reciever,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}