package onl.deepspace.wgs.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;
import onl.deepspace.wgs.activities.CustomTimetableActivity;

import static onl.deepspace.wgs.activities.PortalActivity.CUSTOM_TIMETABLE_REQUEST;

/**
 * A simple {@link Fragment} subclass.
 * Created by Dennis on 17.02.2016.
 */
public class TimetableFragment extends Fragment {

    public static JSONObject timetable;
    static Activity mActivity;
    static View mInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mInflater = inflater.inflate(R.layout.fragment_timetable, container, false);

        if (isTimetablePresent(timetable)) {
            setTimetable(getContext(), timetable);
        } else {
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.main_content), R.string.no_timetable, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.create_own_timetable, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CustomTimetableActivity.class);
                    startActivityForResult(intent, CUSTOM_TIMETABLE_REQUEST);
                }
            });
            snackbar.show();
        }

        if(Helper.getHasNoAds(getContext())){
            TextView timetable = (TextView) mInflater.findViewById(R.id.timetable_disclaimer);
            timetable.setPadding(timetable.getPaddingLeft(), timetable.getPaddingTop(), timetable.getPaddingRight(), 8);
        }

        return mInflater;
    }

    public static void setActivity(Activity activity) {
        TimetableFragment.mActivity = activity;
    }

    /**
     * Set the lessons of the whole timetable
     * Provide the date like this: {"monday": ["D", "E", ...], "tuesday": [...], ...}
     * @param timetable The data of the timetable
     */
    public static void setTimetable(Context context, JSONObject timetable) {
        timetable = Helper.getTimetableWithCustomVersion(context, timetable);
        try {
            JSONArray monday = timetable.getJSONArray("monday");
            JSONArray tuesday = timetable.getJSONArray("tuesday");
            JSONArray wednesday = timetable.getJSONArray("wednesday");
            JSONArray thursday = timetable.getJSONArray("thursday");
            JSONArray friday = timetable.getJSONArray("friday");

            setDay("monday", monday);
            setDay("tuesday", tuesday);
            setDay("wednesday", wednesday);
            setDay("thursday", thursday);
            setDay("friday", friday);

        } catch (JSONException e) {
            Log.e(Helper.LOGTAG, e.toString());
        }
    }

    public static boolean isTimetablePresent(JSONObject timetable) {
        try {
            JSONArray monday = timetable.getJSONArray("monday");
            JSONArray tuesday = timetable.getJSONArray("tuesday");
            JSONArray wednesday = timetable.getJSONArray("wednesday");
            JSONArray thursday = timetable.getJSONArray("thursday");
            JSONArray friday = timetable.getJSONArray("friday");

            for (int i = 0; i < monday.length(); i++) {
                if (!monday.getString(i).equals(""))
                    return true;
                if (!tuesday.getString(i).equals(""))
                    return true;
                if (!wednesday.getString(i).equals(""))
                    return true;
                if (!thursday.getString(i).equals(""))
                    return true;
                if (!friday.getString(i).equals(""))
                    return true;
            }

        } catch (JSONException e) {
            Log.e(Helper.LOGTAG, e.toString());
        }
        return false;
    }

    /**
     * Set the values for the specified day
     * @param day The day to set the values for
     * @param data The lessons on the specified day
     */
    public static void setDay(String day, JSONArray data) {
        try {
            Resources res = mInflater.getResources();
            for (int i = 1; i <= 11; i++) {
                String viewId = day.substring(0, 2) + i;

                int identifier = res.getIdentifier(viewId, "id", mActivity.getPackageName());
                int subjectId = Helper.getSubjectId(data.getString(i - 1));

                View view = mInflater.findViewById(identifier);
                TextView lesson = (TextView) view;

                lesson.setText(subjectId == 0 ? data.getString(i - 1): mActivity.getString(subjectId));
            }
        } catch(Exception e) {
            e.printStackTrace();
            Log.e(Helper.LOGTAG, e.getLocalizedMessage());
        }
    }


}
