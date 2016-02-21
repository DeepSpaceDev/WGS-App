package onl.deepspace.wgs;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * A simple {@link Fragment} subclass.
 * Created by Dennis on 17.02.2016.
 */
public class TimetableFragment extends Fragment {

    static JSONObject timetable;
    static Activity activity;
    static View inflator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflator = inflater.inflate(R.layout.fragment_timetable, container, false);
        setTimetable(timetable);
        return inflator;
    }

    public static void setActivity(Activity activity) {
        TimetableFragment.activity = activity;
    }

    /**
     * Set the lessons of the whole timetable
     * Provide the date like this: {"monday": ["D", "E", ...], "tuesday": [...], ...}
     * @param timetable The data of the timetable
     */
    public static void setTimetable(JSONObject timetable) {
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
     * @param day The day to set the values for
     * @param data The lessons on the specified day
     */
    public static void setDay(String day, JSONArray data) {
        try {
            Resources res = inflator.getResources();
            for (int i = 1; i <= 11; i++) {
                String viewId = day.substring(0, 2) + i;

                int identifier = res.getIdentifier(viewId, "id", activity.getPackageName());
                int subjectId = Helper.getSubjectId(data.getString(i - 1));

                View view = inflator.findViewById(identifier);
                TextView lesson = (TextView) view;

                lesson.setText(subjectId == 0 ? data.getString(i - 1): activity.getString(subjectId));
            }
        } catch(Exception e) {
            e.printStackTrace();
            Log.e(Helper.LOGTAG, e.getLocalizedMessage());
        }
    }


}
