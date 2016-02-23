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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import onl.deepspace.wgs.Helper;

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
        Helper.sendNotification(this, "Test", GetSomething(email, pw));

        AlarmReceiver.completeWakefulIntent(intent);
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
