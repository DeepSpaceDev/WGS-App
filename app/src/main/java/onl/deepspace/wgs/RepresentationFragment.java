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
     * Set the representations of today and tomorrow
     * Provide the date like this: {"today":
     *                                  {"date": "1.12.15", "data":
     *                                      [{"lesson": 1, "subject": "...", "room": "..."}, ...]},
     *                              "tomorrow": ... }
     * @param representations The data of the representations
     */
    public static void setRepesentations(JSONObject representations) {
        try {
            JSONObject today = representations.getJSONObject("today");
            JSONObject tomorrow = representations.getJSONObject("tomorrow");

            setDates(today.getString("date"), today.getString("date"));

            clearRepesentations();

            String student = representations.getString("name");
            ((TextView) inflator.findViewById(R.id.studentName)).setText(student);

            String refresh = representations.getString("lastrefresh");
            refresh = refresh.trim();
            String hour = refresh.substring(0, 34);
            hour = hour.substring(5, hour.length() - 1);
            ((TextView) inflator.findViewById(R.id.updated)).setText("aktualisiert um " + hour);

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
            String teacher = representation.getString("teacher");
            String room = representation.getString("room");

            TableRow row = new TableRow(activity);
            if(i % 2 == 1) row.setBackgroundColor(ContextCompat.getColor(activity, R.color.grey));
            row.setGravity(Gravity.CENTER);
            row.setWeightSum(3);

            TextView lessonView = new TextView(activity);
            TextView subjectView = new TextView(activity);
            TextView teacherView = new TextView(activity);
            TextView roomView = new TextView(activity);

            int subjectId = Helper.getSubjectId(subject);
            String subjectString = subjectId == 0 ? subject : activity.getString(subjectId);

            lessonView.setText(String.valueOf(lesson));
            subjectView.setText(subjectString);
            teacherView.setText(teacher);
            roomView.setText(room);

            lessonView.setPadding(8, 8, 8, 8);
            subjectView.setPadding(8, 8, 8, 8);
            teacherView.setPadding(8, 8, 8, 8);
            roomView.setPadding(8, 8, 8, 8);

            lessonView.setGravity(Gravity.CENTER);
            subjectView.setGravity(Gravity.CENTER);
            teacherView.setGravity(Gravity.CENTER);
            roomView.setGravity(Gravity.CENTER);

            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1.0f);

            lessonView.setLayoutParams(params);
            subjectView.setLayoutParams(params);
            teacherView.setLayoutParams(params);
            roomView.setLayoutParams(params);

            row.addView(lessonView);
            row.addView(subjectView);
            row.addView(teacherView);
            row.addView(roomView);

            table.addView(row);
        }
        if(data.length() > 0) {
            if(day.equals("today")) {
                TextView noToday = (TextView) inflator.findViewById(R.id.noToday);
                noToday.setVisibility(View.GONE);
            } else {
                TextView noTomorrow = (TextView) inflator.findViewById(R.id.noTomorrow);
                noTomorrow.setVisibility(View.GONE);
            }
        }
    }

    public static void clearRepesentations() {
        TableLayout today = (TableLayout) inflator.findViewById(R.id.representationsToday);
        TableLayout tomorrow = (TableLayout) inflator.findViewById(R.id.representationsTomorrow);
        TextView noToday = (TextView) inflator.findViewById(R.id.noToday);
        TextView noTomorrow = (TextView) inflator.findViewById(R.id.noTomorrow);

        noToday.setVisibility(View.VISIBLE);
        noTomorrow.setVisibility(View.VISIBLE);

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
