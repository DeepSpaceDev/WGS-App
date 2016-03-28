package onl.deepspace.wgs;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dennis on 26.03.2016.
 */
public class FeedbackSender extends AsyncTask<Void, Void, String> {

    private Activity mActivity;
    private String mUrlString;
    private String mCategory;
    private String mFeedback;

    public FeedbackSender(Activity activity,String urlString, String category, String feedback) {
        this.mActivity = activity;
        this.mUrlString = urlString;
        this.mCategory = category;
        this.mFeedback = feedback;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.d(Helper.LOGTAG, "Sender started");
        return sendFeedback(mUrlString, mCategory, mFeedback);
    }

    private String sendFeedback(String urlString, String category, String feedback) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000/*ms*/);
            connection.setConnectTimeout(15000/*ms*/);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            JSONObject body = new JSONObject();
            body.put("category", category);
            body.put("rating", feedback);
            body.put("email", Helper.getEmail(mActivity));
            body.put("version", BuildConfig.VERSION_CODE);
            byte[] outputInBytes = body.toString().getBytes("UTF-8");
            OutputStream os = connection.getOutputStream();
            os.write(outputInBytes);
            os.close();

            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.d(Helper.LOGTAG, "Response code: " + responseCode);
            InputStream in = connection.getInputStream();
            Reader reader = new InputStreamReader(in, "UTF-8");
            char[] buffer = new char[500];
            reader.read(buffer);
            return new String(buffer);

        } catch (IOException | JSONException e) {
            Log.e(Helper.LOGTAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null) {
            //TODO do something
            Log.d(Helper.LOGTAG, response);
        } else {
            //TODO do something, maybe retry
            Log.d(Helper.LOGTAG, "Failed");
        }
    }
}