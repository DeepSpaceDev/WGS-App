package onl.deepspace.wgs.PortalUpdate;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onl.deepspace.wgs.Helper;

/**
 * Created by Dennis on 20.02.2016.
 *
 * PortalPullService is invoked for pulling data in the background,
 * check for changes and notify the user if the data changed
 */
public class PortalPullService extends IntentService {

    private static final String LOG_TAG = "PortalPullService";
    private static final String REPRESENTATIONS = "representation";
    private static final String TODAY = "today";
    private static final String TOMORROW = "tomorrow";
    private static final String DATE = "date";
    private static final String DATA = "data";
    public static final String EMAIL = "email";
    public static final String PW = "pw";

    public PortalPullService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String email = intent.getStringExtra(EMAIL);
        String pw = intent.getStringExtra(PW);

        String fetchedResult = Helper.GetSomething(email, pw, true);
        String cachedResult = Helper.getApiResult(this);

        //TODO rewrite for new API version
        if(!fetchedResult.equals(cachedResult)) {
            try {
                JSONObject fetched = new JSONObject(fetchedResult);
                JSONObject cached = new JSONObject(cachedResult);

                JSONObject fRepresentations = fetched.getJSONObject(REPRESENTATIONS);
                JSONObject cRepresentations = cached.getJSONObject(REPRESENTATIONS);

                JSONObject fToday = fRepresentations.getJSONObject(TODAY);
                JSONObject fTomorrow = fRepresentations.getJSONObject(TOMORROW);
                JSONObject cToday = cRepresentations.getJSONObject(TODAY);
                JSONObject cTomorrow = cRepresentations.getJSONObject(TOMORROW);

                String fTodayDate = fToday.getString(DATE);
                String fTomorrowDate = fTomorrow.getString(DATE);
                String cTodayDate = cToday.getString(DATE);
                String cTomorrowDate = cTomorrow.getString(DATE);


                if(fTodayDate.equals(cTodayDate) && fTomorrowDate.equals(cTomorrowDate)) {
                    JSONArray fTodayRep = fToday.getJSONArray(DATA);
                    JSONArray fTomorrowRep = fTomorrow.getJSONArray(DATA);
                    JSONArray cTodayRep = cToday.getJSONArray(DATA);
                    JSONArray cTomorrowRep = cTomorrow.getJSONArray(DATA);

                    ArrayList<String> newToday = getNewRepresentations(fTodayRep, cTodayRep);
                    ArrayList<String> newTomorrow = getNewRepresentations(fTomorrowRep, cTomorrowRep);

                    if(newToday.size() > 0 || newTomorrow.size() > 0)
                        showNewRepresentation();
                } else if (fTodayDate.equals(cTomorrowDate)) {
                    JSONArray fetchedRep = fToday.getJSONArray(DATA);
                    JSONArray cachedRep = cTomorrow.getJSONArray(DATA);

                    ArrayList<String> newRep = getNewRepresentations(fetchedRep, cachedRep);

                    if (newRep.size() > 0 || fTomorrow.getJSONArray(DATA).length() > 0) {
                        showNewRepresentation();
                    }
                }
            } catch (JSONException e) {
                Log.e(Helper.LOGTAG, e.getMessage());
            }

            Helper.setApiResult(this, fetchedResult);
        }

        AlarmReceiver.completeWakefulIntent(intent);
    }

    private void showNewRepresentation() {
        Helper.sendNotification(this, 202, "Neue Vertretung", "Hier klicken um sie anzuzeigen");
    }

    private ArrayList<String> getNewRepresentations(JSONArray fetched, JSONArray cached) throws JSONException{
        ArrayList<String> result = new ArrayList<>();

        for (int f=0; f<fetched.length(); f++) {
            String tempFetched = fetched.getJSONObject(f).toString();
            boolean equals = false;
            for(int c=0; c<cached.length(); c++) {
                String tempCached = cached.getJSONObject(c).toString();
                if (tempFetched.equals(tempCached)) equals = true;
            }
            if(!equals)
                result.add(tempFetched);
        }

        return result;
    }
}
