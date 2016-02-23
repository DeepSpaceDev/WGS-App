package onl.deepspace.wgs.PortalUpdate;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import onl.deepspace.wgs.Helper;

/**
 * Created by Dennis on 20.02.2016.
 */
public class PortalPullService extends IntentService {

    private static final String LOG_TAG = "PortalPullService";
    private static final String REPRESENTATIONS = "representation";
    private static final String TODAY = "today";
    private static final String TOMORROW = "tomorrow";
    private static final String DATE = "date";
    private static final String DATA = "data";

    public PortalPullService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String email = intent.getStringExtra("email");
        String pw = intent.getStringExtra("pw");

        //TODO make request to eltern-portal.org, check if new Infos are available, then send notification
        String fetchedResult = GetSomething(email, pw);
        String cachedResult = Helper.getApiResult(this);

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

                String fTodayDate = fToday.getString(DATE).substring(15);
                String fTomorrowDate = fTomorrow.getString(DATE).substring(15);
                String cTodayDate = cToday.getString(DATE).substring(15);
                String cTomorrowDate = cTomorrow.getString(DATE).substring(15);


                if(fTodayDate == cTodayDate && fTomorrowDate == cTomorrowDate) {
                    JSONArray fTodayRep = fToday.getJSONArray(DATA);
                    JSONArray fTomorrowRep = fTomorrow.getJSONArray(DATA);
                    JSONArray cTodayRep = cToday.getJSONArray(DATA);
                    JSONArray cTomorrowRep = cTomorrow.getJSONArray(DATA);

                    ArrayList<JSONObject> newToday = getNewRepresentations(fTodayRep, cTodayRep);
                    ArrayList<JSONObject> newTomorrow = getNewRepresentations(fTomorrowRep, cTomorrowRep);

                    if(newToday.size() > 0 || newTomorrow.size() > 0)
                        showNewRepresentation();
                } else if (fTodayDate == cTomorrowDate) {
                    JSONArray fetchedRep = fToday.getJSONArray(DATA);
                    JSONArray cachedRep = cTomorrow.getJSONArray(DATA);

                    ArrayList<JSONObject> newRep = getNewRepresentations(fetchedRep, cachedRep);

                    if (newRep.size() > 0) {
                        showNewRepresentation();
                    }
                }
            } catch (JSONException e) {
                Log.e(Helper.LOGTAG, e.getMessage());
            }

            //TODO set fetched to new cached
            Helper.setApiResult(this, fetchedResult);
        }


        // Helper.sendNotification(this, "Test", fetchedResult);
        AlarmReceiver.completeWakefulIntent(intent);
    }

    private void showNewRepresentation() {
        Helper.sendNotification(this, 202, "Neue Vertretung", "Hier klicken um sie anzuzeigen");
    }

    private ArrayList<JSONObject> getNewRepresentations(JSONArray fetched, JSONArray cached) throws JSONException{
        ArrayList<JSONObject> result = new ArrayList<>();

        for (int f=0; f<fetched.length(); f++) {
            JSONObject tempFetched = fetched.getJSONObject(f);
            boolean equals = false;
            for(int c=0; c<cached.length(); c++) {
                JSONObject tempCached = cached.getJSONObject(c);
                if (tempFetched.equals(tempCached)) equals = true;
            }
            if(!equals)
                result.add(tempFetched);
        }

        return result;
    }


    private static String GetSomething(String username, String password) {
        String url = "https://deepspace.onl/scripts/sites/wgs/eltern-portal.php";
        String result = "";
        Log.d(Helper.LOGTAG, url);
        BufferedReader inStream = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpRequest = new HttpPost(url);
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>(3);
            nameValuePairList.add(new BasicNameValuePair("username", username));
            nameValuePairList.add(new BasicNameValuePair("password", password));
            nameValuePairList.add(new BasicNameValuePair("token", "WaoJrllHRkckNAhm4635MiVKgFhOpigmfV6EmvTt41xtTFbjkimUraFBQsOwS5Cj\n"));

            httpRequest.setEntity(new UrlEncodedFormEntity(nameValuePairList));
            HttpResponse response = httpClient.execute(httpRequest);
            inStream = new BufferedReader(
                    new InputStreamReader(
                            response.getEntity().getContent()));

            StringBuffer buffer = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = inStream.readLine()) != null) {
                buffer.append(line + NL);
            }
            inStream.close();

            result = buffer.toString();
        } catch (Exception e) {
            Log.e(Helper.LOGTAG, e.toString());
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
