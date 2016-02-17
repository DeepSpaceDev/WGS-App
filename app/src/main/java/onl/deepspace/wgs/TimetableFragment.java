package onl.deepspace.wgs;

import android.app.Activity;
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

    public TimetableFragment() {
        // Required empty public constructor
    }

    static Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setActivity(getActivity());
        return inflater.inflate(R.layout.fragment_timetable, container, false);
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
            Log.d(LoginActivity.LOGTAG, timetable.toString());

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
            e.printStackTrace();
        }
    }

    /**
     * Set the values for the specified day
     * @param day The day to set the values for
     * @param data The lessons on the specified day
     */
    public static void setDay(String day, JSONArray data) {
        try {
            Class res = R.layout.class;
            for (int i = 1; i <= 11; i++) {
                String viewId = day.substring(0, 2) + i;
                Field field = res.getField(viewId);
                int identifier = field.getInt(null);
                TextView lesson = (TextView) activity.findViewById(identifier);
                int subjectId = getSubjectId(data.getString(i));
                lesson.setText(subjectId == 0 ? data.getString(i): activity.getString(subjectId));
            }
        } catch(JSONException e) {
            Log.d(LoginActivity.LOGTAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.d(LoginActivity.LOGTAG, e.getMessage());
        } catch (NoSuchFieldException e) {
            Log.d(LoginActivity.LOGTAG, e.getMessage());
        }
    }

    /**
     * Get the text id for the specified subject
     * @param subject The subject to get the id for
     * @return The id of the string resource
     */
    public static int getSubjectId(String subject) {
        int id = 0;

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
            case "SM/W": id = R.string.sports; break;
            case "C": id = R.string.chemistry; break;
            case "B": id = R.string.biology; break;
            case "G": id = R.string.history; break;
            case "SOZ": id = R.string.socialEdu; break;
            case "SOG": id = R.string.socialBaseEdu; break;
            case "RELIGION": id = R.string.religion; break;
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
}