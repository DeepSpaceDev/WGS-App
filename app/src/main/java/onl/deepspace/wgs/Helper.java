package onl.deepspace.wgs;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

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

/**
 * Created by Dennis on 18.02.2016.
 */
public class Helper {
    public static final int MILLIS_TO_DAYS = 1/(1000 * 60 * 60 * 24);

    public static final String CHILD_INDEX = "childIndex";
    public static final String CHILDREN = "children";
    public static String LOGTAG = "Deepspace";
    public static String PW = "password_v2";
    public static String EMAIL = "userEmail_v2";
    public static String HASADS = "hasDisabledAds";
    public static String LAST_BOTTOM_ACTION = "lastBottomAction";
    public static String NEXT_BOTTOM_ACTION = "nextBottomAction";
    public static String BOTTOM_ACTION_ACTION = "action";
    public static String BOTTOM_ACTION_TYPE = "type";
    public static String BOTTOM_ACTION_HINT = "hint";
    public static String BOTTOM_ACTION_ADDITIONAL = "additional";
    public static String BOTTOM_ACTION_DAYS_AFTER = "daysAfter";

    public static String API_RESULT = "onl.deepspace.wgs.api_result";

    public static final String WGSPortalAPI = "http://62.75.208.57/scripts/sites/wgs/eltern-portal_v2.php";
    public static final String WGSPortalAPI_USERNAME = "username";
    public static final String WGSPortalAPI_PASSWORD = "password";
    public static final String WGSPortalAPI_TOKEN = "token";
    public static final String WGSPortalAPI_AUTOREFRESH = "autorefresh";
    public static final String WGSPortalAPI_VERSION = "version";
    public static final String API_TOKEN = "gt4D3YFHynOycAS2YWAjIrcd65idPJXwqhfi18uKZZRN7b6DLcBldpjhY4rSJ8Me";

    public static final String API_RESULT_LOGIN = "login";
    public static final String API_RESULT_CHILDREN = "children";
    public static final String API_RESULT_NAME = "name";
    public static final String API_RESULT_TIMETABLE = "timetable";
    public static final String API_RESULT_MONDAY = "monday";
    public static final String API_RESULT_TUESDAY = "tuesday";
    public static final String API_RESULT_THURSDAY = "thursday";
    public static final String API_RESULT_WEDNESDAY = "wednesday";
    public static final String API_RESULT_FRIDAY = "friday";
    public static final String API_RESULT_REPRESENTATION = "representation";
    public static final String API_RESULT_TODAY = "today";
    public static final String API_RESULT_TOMORROW = "tomorrow";
    public static final String API_RESULT_DATE = "date";
    public static final String API_RESULT_DATA = "data";
    public static final String API_RESULT_LAST_REFRESH = "lastrefresh";

    public static void fixLayout(Activity activity) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public static long getLastBottomAction(Context context) {
        PackageManager pm = context.getPackageManager();
        long firstInstallTime = 0;
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            firstInstallTime = packageInfo.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOGTAG, e.getMessage());
        }
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getLong(LAST_BOTTOM_ACTION, firstInstallTime);
    }

    public static void setLastBottomAction(Context context, long millis) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(LAST_BOTTOM_ACTION, millis);
        editor.apply();
    }

    public static JSONObject nextBottomAction(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int index = sharedPref.getInt(NEXT_BOTTOM_ACTION, 0);
        JSONObject bottomAction = new JSONObject();
        String action = null;
        String type = null;
        String hint = null;
        String additional = null;
        int daysAfter = 1;
        switch (index) {
            case 0:
                action = "https://twitter.com/deepspace_dev";
                type = BottomAction.TYPE_URL;
                hint = "Aktuelle Infos auf Twitter";
                additional = "twitter";
                daysAfter = 1;
                break;
            case 1:
                action = "https://deepspace.onl/scripts/sites/wgs/feedback.php";
                type = BottomAction.TYPE_RATING;
                hint = "Wie gefällt dir unser Design?";
                additional = "UI";
                daysAfter = 1;
                break;
            case 2:
                action = "https://deepspace.onl/scripts/sites/wgs/feedback.php";
                type = BottomAction.TYPE_RATING;
                hint = "Wie gut ist die App zu bedienen?";
                additional = "UX";
                daysAfter = 3;
                break;
            case 3:
                action = BottomAction.getActivityName(FeatureRequestActivity.class);
                type = BottomAction.TYPE_ACTIVITY;
                hint = "Vermisst du ein Feature?";
                additional = "ic_feedback_24dp";
                daysAfter = 2;
                break;
        }
        if (action == null) return null;
        try {
            bottomAction.put(BOTTOM_ACTION_ACTION, action);
            bottomAction.put(BOTTOM_ACTION_TYPE, type);
            bottomAction.put(BOTTOM_ACTION_HINT, hint);
            bottomAction.put(BOTTOM_ACTION_ADDITIONAL, additional);
            bottomAction.put(BOTTOM_ACTION_DAYS_AFTER, daysAfter);
        } catch (JSONException e) {
            Log.e(LOGTAG, e.getMessage());
            return null;
        }
        return bottomAction;
    }

    public static void incrementNextAction(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int index = sharedPref.getInt(NEXT_BOTTOM_ACTION, 0);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(NEXT_BOTTOM_ACTION, ++index);
        editor.apply();
    }

    public static void purchaseNoAd(Activity activity){
        ArrayList<String> skuList = new ArrayList<>();
        skuList.add("wgs_app_remove_ads");
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        Bundle skuDetails = new Bundle();

        try {
            /*skuDetails = PortalActivity.mService.getSkuDetails(3, activity.getPackageName(), "inapp", querySkus);
            int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> responseList
                        = skuDetails.getStringArrayList("DETAILS_LIST");

                for (String thisResponse : responseList) {
                    Log.v(LOGTAG, thisResponse);
                    JSONObject object = new JSONObject(thisResponse);
                    String sku = object.getString("productId");
                }
            }*/

            //Testing with android.test.purchased

            Bundle buyIntentBundle = PortalActivity.mService.getBuyIntent(3, activity.getPackageName(), "wgs_app_remove_ads", "inapp", "");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

            if(pendingIntent != null){ //Item is not bought
                activity.startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
            }
            else{ //Item is bought by user
                Bundle ownedItems = PortalActivity.mService.getPurchases(3, activity.getPackageName(), "inapp", "");
                ArrayList myPurchases = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");

                assert myPurchases != null;
                for(int i = 0; i < myPurchases.size(); i++){
                    if (myPurchases.get(i).equals("wgs_app_remove_ads")) {
                        activity.findViewById(R.id.adView).setVisibility(View.INVISIBLE);
                        setHasNoAds(activity, true);
                        Snackbar.make(activity.findViewById(R.id.main_content), "Werbung Entfernt! Danke für deinen Kauf.", Snackbar.LENGTH_LONG).show();
                    }
                }

            }

        } catch (RemoteException | NullPointerException | IntentSender.SendIntentException e) {
            Log.e(LOGTAG, e.getMessage());
        }
    }

    public static void sendNotification(Context activity, int notificationId, String title, String message){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(activity)
                        .setSmallIcon(R.mipmap.appicon)
                        .setContentTitle(title)
                        .setContentText(message);
        Intent resultIntent = new Intent(activity, LoginActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(activity, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(notificationId, mBuilder.build());
    }

    public static void setPw(Context context, String pw) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (pw == null) editor.remove(PW);
        else editor.putString(PW, pw);
        editor.apply();
    }

    public static String getPw(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(PW, "");
    }

    public static void setEmail(Context context, String email) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (email == null) editor.remove(EMAIL);
        else editor.putString(EMAIL, email);
        editor.apply();
    }

    public static String getEmail(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(EMAIL, "");
    }

    public static void setHasNoAds(Context context, Boolean ads) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(HASADS, ads);
        editor.apply();
    }

    public static Boolean getHasNoAds(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getBoolean(HASADS, false);
    }

    public static void setApiResult(Context context, String result) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(API_RESULT, result);
        editor.apply();
    }

    public static String getApiResult(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(API_RESULT, "");
    }

    public static void setChildIndex(Context context, int index) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(CHILD_INDEX, index);
        editor.apply();
    }

    public static int getChildIndex(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getInt(CHILD_INDEX, 0);
    }

    public static int getLessonId(int lesson) {
        switch (lesson) {
            case 1:  return R.string.time1;
            case 2:  return R.string.time2;
            case 3:  return R.string.time3;
            case 4:  return R.string.time4;
            case 5:  return R.string.time5;
            case 6:  return R.string.time6;
            case 7:  return R.string.time7;
            case 8:  return R.string.time8;
            case 9:  return R.string.time9;
            case 10: return R.string.time10;
            case 11: return R.string.time11;
            default: return 0;
        }
    }

    /**
     * Get the text id for the specified subject
     * @param subject The subject to get the id for
     * @return The id of the string resource
     */
    public static int getSubjectId(String subject) {
        int id = 0;

        subject = subject.replaceAll("[0-9]", "");

        subject = subject.toUpperCase();

        switch(subject) {
            case "D": id = R.string.german; break;
            case "M": id = R.string.maths; break;
            case "E": id = R.string.english; break;
            case "L": id = R.string.latin; break;
            case "PH": id = R.string.physics; break;
            case "INF": id = R.string.informatics; break;
            case "WR": id = R.string.economyNLaw; break;
            case "GEO": id = R.string.geographie; break;
            case "SM/SW": id = R.string.sports; break;
            case "C": id = R.string.chemistry; break;
            case "B": id = R.string.biology; break;
            case "G": id = R.string.history; break;
            case "SK": id = R.string.socialEdu; break;
            case "SOG": id = R.string.socialBaseEdu; break;
            case "ETH/EV/K": id = R.string.religion; break;
            case "F": id = R.string.french; break;
            case "S": id = R.string.spain; break;
            case "DRG": id = R.string.theatre; break;
            case "CHOR": id = R.string.choir; break;
            case "ORCH": id = R.string.orchestra; break;
            case "NT": id = R.string.NT; break;
            case "MU": id = R.string.music; break;
            case "KU": id = R.string.arts; break;
            case "PSY": id = R.string.psychology; break;
            case "BCP": id = R.string.bioChemPrak; break;
            case "ROB": id = R.string.robotic; break;
            case "IM": id = R.string.intMaths; break;
            case "ID": id = R.string.intGerman; break;
            case "IE": id = R.string.intEnglish; break;
            case "IF": id = R.string.intFrench; break;
            case "IL": id = R.string.intLatin; break;
            case "IPH": id = R.string.intPhysics; break;
            case "IC": id = R.string.intChemistry; break;
        }
        return id;
    }

    public static String GetSomething(String username, String password, boolean autorefresh) {
        String url = WGSPortalAPI;
        String result = "";
        BufferedReader inStream = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpRequest = new HttpPost(url);
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>(3);
            nameValuePairList.add(new BasicNameValuePair(WGSPortalAPI_USERNAME, username));
            nameValuePairList.add(new BasicNameValuePair(WGSPortalAPI_PASSWORD, password));
            nameValuePairList.add(new BasicNameValuePair(WGSPortalAPI_TOKEN, API_TOKEN));
            if(autorefresh) nameValuePairList.add(new BasicNameValuePair(WGSPortalAPI_AUTOREFRESH, "1"));
            nameValuePairList.add(new BasicNameValuePair(WGSPortalAPI_VERSION, BuildConfig.VERSION_CODE + ""));

            httpRequest.setEntity(new UrlEncodedFormEntity(nameValuePairList));
            HttpResponse response = httpClient.execute(httpRequest);
            inStream = new BufferedReader(
                    new InputStreamReader(
                            response.getEntity().getContent()));

            StringBuilder buffer = new StringBuilder("");
            String line;
            String NL = System.getProperty("line.separator");
            while ((line = inStream.readLine()) != null) {
                buffer.append(line).append(NL);
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
