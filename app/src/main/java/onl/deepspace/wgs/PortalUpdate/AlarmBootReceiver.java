package onl.deepspace.wgs.PortalUpdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import onl.deepspace.wgs.Helper;

/**
 * Created by Dennis on 20.02.2016.
 */
public class AlarmBootReceiver extends BroadcastReceiver {
    AlarmReceiver mAlarm = new AlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETE")) {
            if(Helper.getEmail(context) != null && Helper.getPw(context) != null)
                mAlarm.setAlarm(context);
        }
    }
}
