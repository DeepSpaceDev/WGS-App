package onl.deepspace.wgs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements OnTaskCompletedInterface {

    AlarmReceiver mAlarm = new AlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAlarm.setAlarm(this);

        String savedPw = Helper.getPw(this);
        String savedEmail = Helper.getEmail(this);

        if(!(savedPw.equals("") && savedEmail.equals(""))) {
            new GetUserData(LoginActivity.this).execute(savedPw, savedEmail);
            setContentView(R.layout.activity_loading);
        } else {
            setContentView(R.layout.activity_login);

            TextView loginhint = (TextView) findViewById(R.id.loginhint);
            loginhint.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

            Button button = (Button) findViewById(R.id.email_sign_in_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pw = ((TextView) findViewById(R.id.email)).getText().toString();
                    String email = ((TextView) findViewById(R.id.password)).getText().toString();
                    Boolean saveLogin = ((CheckBox) findViewById(R.id.saveLogin)).isChecked();
                    if (saveLogin) {
                        Helper.setPw(getBaseContext(), pw);
                        Helper.setEmail(getBaseContext(), email);
                    }
                    login(pw, email);
                }
            });
        }
    }

    public void login(String pw, String email) {
        findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
        new GetUserData(LoginActivity.this).execute(pw, email);
    }

    public void registerAlarmManger() {
        Intent intent = new Intent(this, PortalPullService.class);
        PendingIntent updateIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 60 * 1000,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES/30, updateIntent);
    }

    @Override
    public void onTaskCompleted(String response) {
        Log.d(Helper.LOGTAG, response);
        try{
            JSONObject arr = new JSONObject(response);

            if(arr.getBoolean("login")) {
                Log.d(Helper.LOGTAG, Boolean.toString(arr.getBoolean("login")));
                Intent intent = new Intent(this, PortalActivity.class);
                intent.putExtra("timetable", arr.getJSONObject("timetable").toString());
                intent.putExtra("representation", arr.getJSONObject("representation").toString());
                startActivity(intent);
            }
            else{
                Log.d(Helper.LOGTAG, Boolean.toString(arr.getBoolean("login")));
                setContentView(R.layout.activity_login);
                Snackbar.make(
                        findViewById(R.id.login_activity),
                        arr.getString("error"),
                        Snackbar.LENGTH_SHORT).show();
            }
        }
        catch(JSONException e){
            setContentView(R.layout.activity_login);
            Toast.makeText(LoginActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
            Log.e(Helper.LOGTAG, e.toString());
        }
    }

    public class GetUserData extends AsyncTask<String, Void, String> {

        String result = "";

        private OnTaskCompletedInterface taskCompleted;

        @Override
        protected String doInBackground(String... params) {
            return GetSomething(params[0], params[1]);
        }

        public GetUserData(OnTaskCompletedInterface activityContext) {
            this.taskCompleted = activityContext;
        }

        final String GetSomething(String username, String password) {
            String url = "https://deepspace.onl/scripts/sites/wgs/eltern-portal.php";
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

        protected void onPostExecute(String page) {
            taskCompleted.onTaskCompleted(page);
        }

    }
}
