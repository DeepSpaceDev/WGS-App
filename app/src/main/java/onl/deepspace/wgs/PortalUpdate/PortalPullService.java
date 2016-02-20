package onl.deepspace.wgs.PortalUpdate;

import android.app.IntentService;
import android.content.Intent;

import onl.deepspace.wgs.PortalUpdate.AlarmReceiver;

/**
 * Created by Dennis on 20.02.2016.
 */
public class PortalPullService extends IntentService {

    private static final String LOG_TAG = "PortalPullService";

    public PortalPullService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String email = intent.getStringExtra("email");
        String pw = intent.getStringExtra("pw");

        //TODO make request to eltern-portal.org, check if new Infos are available, then send notification


        AlarmReceiver.completeWakefulIntent(intent);
    }

}
