package onl.deepspace.wgs.fragments;

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

import com.google.firebase.analytics.FirebaseAnalytics;

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

    Runnable showCrateTimetable;
    private JSONObject timetable;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_timetable, container, false);

        if (Helper.getHasNoAds(getContext())) {
            TextView timetable = (TextView) view.findViewById(R.id.timetable_disclaimer);
            timetable.setPadding(timetable.getPaddingLeft(), timetable.getPaddingTop(), timetable.getPaddingRight(), 8);
        }

        //Needed because setUserVisibleHint is called before onCreateView
        if (showCrateTimetable != null) {
            showCrateTimetable.run();
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (!isTimetablePresent(timetable)) {
                //Needed because setUserVisibleHint is called before onCreateView
                showCrateTimetable = new Runnable() {
                    @Override
                    public void run() {
                        Snackbar snackbar = Snackbar.make(
                                getActivity().findViewById(R.id.main_content),
                                R.string.no_timetable,
                                Snackbar.LENGTH_LONG);
                        snackbar.setAction(R.string.create_own_timetable, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), CustomTimetableActivity.class);
                                startActivityForResult(intent, CUSTOM_TIMETABLE_REQUEST);
                            }
                        });
                        snackbar.show();
                    }
                };
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimetable();

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        firebaseAnalytics.logEvent(Helper.EVENT_SHOW_TIMETABLE, new Bundle());
    }

    /**
     * Set the lessons of the whole timetable
     * Provide the date like this: {"monday": ["D", "E", ...], "tuesday": [...], ...}
     *
     * @param timetable The data of the timetable
     */
    public void displayTimetable(JSONObject timetable) {
        timetable = Helper.getTimetableWithCustomVersion(getActivity(), timetable);
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

    /**
     * Set the values for the specified day
     *
     * @param day  The day to set the values for
     * @param data The lessons on the specified day
     */
    public void setDay(String day, JSONArray data) {
        try {
            Resources res = view.getResources();
            for (int i = 1; i <= 11; i++) {
                String viewId = day.substring(0, 2) + i;

                int identifier = res.getIdentifier(viewId, "id", getActivity().getPackageName());
                int subjectId = Helper.getSubjectId(data.getString(i - 1));

                TextView lesson = (TextView) view.findViewById(identifier);
                String lessonIdentifier = subjectId == 0 ? data.getString(i - 1) : getActivity().getString(subjectId);
                lesson.setText(lessonIdentifier);
            }
        } catch (JSONException e) {
            Log.e(Helper.LOGTAG, e.getLocalizedMessage());
        }
    }

    public void setTimetable(JSONObject timetable) {
        this.timetable = timetable;
        updateTimetable();
    }

    public void updateTimetable() {
        displayTimetable(timetable);
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

    public static TimetableFragment newInstance(JSONObject timetable) {
        TimetableFragment tf = new TimetableFragment();
        tf.timetable = timetable;
        return tf;
    }
}
