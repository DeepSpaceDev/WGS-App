package onl.deepspace.wgs.Fragments;

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

import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;

/**
 * A simple {@link Fragment} subclass.
 * Created by Dennis on 17.02.2016.
 */
public class RepresentationFragment extends Fragment {

    private static Activity mActivity;
    public static JSONObject representation;
    static View mInflater;

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
        mInflater = inflater.inflate(R.layout.fragment_representation, container, false);
        setRepresentations(representation);

        if(Helper.getHasNoAds(getContext())){
            TextView representations = (TextView) mInflater.findViewById(R.id.representations_disclaimer);
            representations.setPadding(representations.getPaddingLeft(), representations.getPaddingTop(), representations.getPaddingRight(), 8);
        }

        return mInflater;
    }

    /**
     * Set the representations of today and tomorrow
     * Provide the date like this: {"today":
     *                                  {"date": "1.12.15", "data":
     *                                      [{"lesson": 1, "subject": "...", "room": "..."}, ...]},
     *                              "tomorrow": ... }
     * @param representations The data of the representations
     */
    public static void setRepresentations(JSONObject representations) {
        try {
            JSONObject today = representations.getJSONObject("today");
            JSONObject tomorrow = representations.getJSONObject("tomorrow");

            setDates(today.getString("date"), tomorrow.getString("date"));

            clearRepesentations();

            String refresh = representations.getString("lastrefresh");
            refresh = refresh.trim();
            String hour = refresh.substring(34, 39);
            String date = refresh.substring(22, 29);

            ((TextView) mInflater.findViewById(R.id.updated)).setText(String.format("%s %s %s", mActivity.getString(R.string.updated_at), date, hour));

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
                        mInflater.findViewById(R.id.representationsToday) :
                        mInflater.findViewById(R.id.representationsTomorrow));
        for(int i=0; i<data.length(); i++) {
            JSONObject representation = data.getJSONObject(i);
            int lesson = representation.getInt("lesson");
            String subject = representation.getString("subject");
            String teacher = representation.getString("teacher");
            String room = representation.getString("room");

            TableRow row = new TableRow(mActivity);
            if(i % 2 == 1) row.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.grey));
            row.setGravity(Gravity.CENTER);
            row.setWeightSum(4);

            TextView lessonView = new TextView(mActivity);
            TextView subjectView = new TextView(mActivity);
            TextView teacherView = new TextView(mActivity);
            TextView roomView = new TextView(mActivity);

            int subjectId = Helper.getSubjectId(subject);
            String subjectString = subjectId == 0 ? subject : mActivity.getString(subjectId);

            lessonView.setText(mActivity.getString(Helper.getLessonId(lesson)));
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
                TextView noToday = (TextView) mInflater.findViewById(R.id.noToday);
                noToday.setVisibility(View.GONE);
            } else {
                TextView noTomorrow = (TextView) mInflater.findViewById(R.id.noTomorrow);
                noTomorrow.setVisibility(View.GONE);
            }
        }
    }

    public static void clearRepesentations() {
        TableLayout today = (TableLayout) mInflater.findViewById(R.id.representationsToday);
        TableLayout tomorrow = (TableLayout) mInflater.findViewById(R.id.representationsTomorrow);
        TextView noToday = (TextView) mInflater.findViewById(R.id.noToday);
        TextView noTomorrow = (TextView) mInflater.findViewById(R.id.noTomorrow);

        noToday.setVisibility(View.VISIBLE);
        noTomorrow.setVisibility(View.VISIBLE);

        today.removeViews(1, today.getChildCount() - 1);
        tomorrow.removeViews(1, tomorrow.getChildCount() - 1);
    }

    public static void setDates(String today, String tomorrow) {
        TextView todayView = (TextView) mInflater.findViewById(R.id.dateToday);
        TextView tomorrowView = (TextView) mInflater.findViewById(R.id.dateTomorrow);

        String todayDate = mActivity.getString(R.string.today, today.substring(0, today.length() - 8));
        String tomorrowDate = mActivity.getString(R.string.tomorrow, tomorrow.substring(0, tomorrow.length() - 8));

        todayView.setText(todayDate);
        tomorrowView.setText(tomorrowDate);
    }

    public static void setActivity(Activity mActivity) {
        RepresentationFragment.mActivity = mActivity;
    }
}
