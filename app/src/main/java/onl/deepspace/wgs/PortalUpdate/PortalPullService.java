package onl.deepspace.wgs.portalupdate;

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

        String fetchedResult = Helper.loginToPortal(email, pw, true);
        String cachedResult = Helper.getApiResult(this);

        if(!fetchedResult.equals(cachedResult)) {
            try {
                JSONObject fetched = new JSONObject(fetchedResult);
                JSONObject cached = new JSONObject(cachedResult);

                JSONArray fChildren = fetched.getJSONArray(Helper.API_RESULT_CHILDREN);
                JSONArray cChildren = cached.getJSONArray(Helper.API_RESULT_CHILDREN);

                ArrayList<String> updatedNames = new ArrayList<>();

                for(int i=0; i<cChildren.length(); i++) {
                    // Get name with class
                    String name = fChildren.getJSONObject(i).getString(Helper.API_RESULT_NAME);
                    JSONObject fRepresentations = fChildren.getJSONObject(i).getJSONObject(REPRESENTATIONS);
                    JSONObject cRepresentations = cChildren.getJSONObject(i).getJSONObject(REPRESENTATIONS);

                    if(checkForChanges(fRepresentations, cRepresentations)) updatedNames.add(name);
                }

                if (updatedNames.size() > 0) showNewRepresentation(updatedNames);

            } catch (JSONException e) {
                Log.e(Helper.LOGTAG, e.getMessage());
            }

            Helper.setApiResult(this, fetchedResult);
        }

        AlarmReceiver.completeWakefulIntent(intent);
    }

    private boolean checkForChanges(JSONObject fRepresentations, JSONObject cRepresentations) throws JSONException{
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

            boolean bnewToday = newToday.size() > 0;
            boolean bnewTomorrow = newTomorrow.size() > 0;

            if(bnewToday || bnewTomorrow) return true;

        } else if (fTodayDate.equals(cTomorrowDate)) {
            JSONArray fetchedRep = fToday.getJSONArray(DATA);
            JSONArray cachedRep = cTomorrow.getJSONArray(DATA);

            ArrayList<String> newRep = getNewRepresentations(fetchedRep, cachedRep);

            if (newRep.size() > 0 || fTomorrow.getJSONArray(DATA).length() > 0) return true;
        }
        return false;
    }

    private void showNewRepresentation(ArrayList<String> children) {
        String representation = "Neue Vertretung für:";
        representation += " " + children.get(0);
        for (int i = 1; i < children.size(); i++) {
            representation += ", " + children.get(i);
        }

        Helper.sendNotification(this, 202, representation, "Hier klicken um sie anzuzeigen");
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
