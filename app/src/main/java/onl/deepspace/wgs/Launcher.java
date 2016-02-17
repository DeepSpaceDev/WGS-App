/*package onl.deepspace.wgs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Launcher extends AppCompatActivity implements OnTaskCompletedInterface{

    TextView vonlineusers, vofflineusers;
    SwipeRefreshLayout swipeContainer;
    Snackbar snackbar;

    boolean paused = false;
    final String LOGTAG = "DeepSpace";

    public void go(){
        new GetUserData(Launcher.this).execute(new Object());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        vonlineusers = (TextView) findViewById(R.id.onlineusers_view);
        vofflineusers = (TextView) findViewById(R.id.offlineusers_view);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                go();
            }
        });

        snackbar = Snackbar.make(findViewById(R.id.swipeContainer), "Refreshing", Snackbar.LENGTH_INDEFINITE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(paused) {
            snackbar.show();
            paused = false;
        }
        go();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    public void onTaskCompleted(String response) {
        try{
            JSONArray arr = new JSONArray(response);

            ArrayList<String> usernames = new ArrayList<>();
            ArrayList<Boolean> onlinestat = new ArrayList<>();

            ArrayList<String> onlineusers = new ArrayList<>();
            ArrayList<String> offlineusers = new ArrayList<>();

            for(int i = 0; i < arr.length(); i++){
                JSONObject obj = new JSONObject(arr.get(i).toString());
                usernames.add(obj.getString("name"));
                onlinestat.add(obj.getBoolean("online"));
            }

            for(int i = 0; i < usernames.size(); i++){
                if(onlinestat.get(i)){
                    onlineusers.add(usernames.get(i));
                }
                else{
                    offlineusers.add(usernames.get(i));
                }
            }

            sort(onlineusers);
            sort(offlineusers);

            String onlu = "";
            String offu = "";

            for(int i = 0; i < onlineusers.size(); i++){
                onlu += onlineusers.get(i);
                if(i != onlineusers.size() - 1){
                    onlu += ", ";
                }
            }
            for(int i = 0; i < offlineusers.size(); i++){
                offu += offlineusers.get(i);
                if(i != offlineusers.size() - 1){
                    offu += ", ";
                }
            }

            vonlineusers.setText(onlu);
            vofflineusers.setText(offu);

            swipeContainer.setRefreshing(false);
            snackbar.dismiss();
        }
        catch(JSONException e){
            Log.e(LOGTAG, e.getMessage());
        }
        //users.setText(response);
    }

    public void sort(ArrayList e){
        Collections.sort(e, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.toLowerCase().compareTo(rhs.toLowerCase());
            }
        });
    }

    public class GetUserData extends AsyncTask<Object, Void, String>{

        String result = "";

        private OnTaskCompletedInterface taskCompleted;

        @Override
        protected String doInBackground(Object... params) {
            return GetSomething();
        }

        public GetUserData(OnTaskCompletedInterface activityContext){
            this.taskCompleted = activityContext;
        }

        final String GetSomething()
        {
            String url = "https://deepspace.onl/scripts/sites/teamspeak.php";
            BufferedReader inStream = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpRequest = new HttpGet(url);
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
                Log.e(LOGTAG, e.toString());
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

        protected void onPostExecute(String page)
        {
            taskCompleted.onTaskCompleted(page);
        }
    }

}*/
