package onl.deepspace.wgs.portalupdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import onl.deepspace.wgs.Helper;

/**
 * Created by Dennis on 20.02.2016.
 *
 * Sets up the AlarmReceiver when device rebooted
 */
public class AlarmBootReceiver extends BroadcastReceiver {
    private AlarmReceiver mAlarm = new AlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals("android.intent.action.BOOT_COMPLETE") ||
           action.equals("android.intent.action.QUICKBOOT_POWERON")) {
            if(Helper.getEmail(context) != null && Helper.getPw(context) != null)
                mAlarm.setAlarm(context);
        }
    }
}
