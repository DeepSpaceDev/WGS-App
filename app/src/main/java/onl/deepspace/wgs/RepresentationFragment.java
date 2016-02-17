package onl.deepspace.wgs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.TextAttribute;
import java.lang.reflect.Field;

/**
 * A simple {@link Fragment} subclass.
 * Created by Dennis on 17.02.2016.
 */
public class RepresentationFragment extends Fragment {

    private static Activity activity;
    static JSONObject representation;
    static View inflator;

    public RepresentationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflator = inflater.inflate(R.layout.fragment_representation, container, false);
        setRepesentations(representation);
        return inflator;
    }

    /**
     * Set the repesentations of today and tomorrow
     * Provide the date like this: {"today":
     *                                  {"date": "1.12.15", "data":
     *                                      [{"lesson": 1, "subject": "...", "room": "..."}, ...]},
     *                              "tomorrow": ... }
     * @param repesentations The data of the repesentations
     */
    public static void setRepesentations(JSONObject repesentations) {
        try {
            JSONObject today = repesentations.getJSONObject("today");
            JSONObject tomorrow = repesentations.getJSONObject("tomorrow");

            setDates(today.getString("date"), today.getString("date"));

            clearRepesentations();

            addRepresentations("today", today.getJSONArray("data"));
            addRepresentations("tomorrow", tomorrow.getJSONArray("data"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the values for the specified day
     * @param day The day to set the values for
     * @param data The lessons on the specified day
     */
    public static void addRepresentations(String day, JSONArray data) throws JSONException{
        TableLayout table = (TableLayout)
                (day.equals("today") ?
                        inflator.findViewById(R.id.representationsToday) :
                        inflator.findViewById(R.id.representationsTomorrow));
        for(int i=0; i<data.length(); i++) {
            JSONObject representation = data.getJSONObject(i);
            int lesson = representation.getInt("lesson");
            String subject = representation.getString("subject");
            String room = representation.getString("room");

            TableRow row = new TableRow(activity);
            if(i % 2 == 1) row.setBackgroundColor(ContextCompat.getColor(activity, R.color.grey));
            row.setGravity(Gravity.CENTER);
            row.setWeightSum(3);

            TextView lessonView = new TextView(activity);
            TextView subjectView = new TextView(activity);
            TextView roomView = new TextView(activity);

            int subjectId = getSubjectId(subject);
            String subjectString = subjectId == 0 ? subject : activity.getString(subjectId);

            lessonView.setText(lesson);
            subjectView.setText(subjectString);
            roomView.setText(room);

            lessonView.setPadding(8, 8, 8, 8);
            subjectView.setPadding(8, 8, 8, 8);
            roomView.setPadding(8, 8, 8, 8);

            lessonView.setGravity(Gravity.CENTER);
            subjectView.setGravity(Gravity.CENTER);
            roomView.setGravity(Gravity.CENTER);

            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1.0f);

            lessonView.setLayoutParams(params);
            subjectView.setLayoutParams(params);
            roomView.setLayoutParams(params);

            row.addView(lessonView);
            row.addView(subjectView);
            row.addView(roomView);

            table.addView(row);
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
            case "SM/SW": id = R.string.sports; break;
            case "C": id = R.string.chemistry; break;
            case "B": id = R.string.biology; break;
            case "G": id = R.string.history; break;
            case "SOZ": id = R.string.socialEdu; break;
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

    public static void clearRepesentations() {
        TableLayout today = (TableLayout) inflator.findViewById(R.id.representationsToday);
        TableLayout tomorrow = (TableLayout) inflator.findViewById(R.id.representationsTomorrow);

        today.removeViews(1, today.getChildCount() - 1);
        tomorrow.removeViews(1, tomorrow.getChildCount() - 1);
    }

    public static void setDates(String today, String tomorrow) {
        TextView todayView = (TextView) inflator.findViewById(R.id.dateToday);
        TextView tomorrowView = (TextView) inflator.findViewById(R.id.dateTomorrow);

        String todayDate = activity.getString(R.string.today, today);
        String tomorrowDate = activity.getString(R.string.tomorrow, tomorrow);

        todayView.setText(todayDate);
        tomorrowView.setText(tomorrowDate);
    }

    public static void setActivity(Activity activity) {
        RepresentationFragment.activity = activity;
    }
}
