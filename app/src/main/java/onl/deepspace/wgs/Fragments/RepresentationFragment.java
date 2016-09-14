package onl.deepspace.wgs.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;
import onl.deepspace.wgs.fragments.representation.ListViewRepresentation;
import onl.deepspace.wgs.fragments.representation.RepresentationItem;

/**
 * A simple {@link Fragment} subclass.
 * Created by Dennis on 17.02.2016.
 */
public class RepresentationFragment extends Fragment {

    public static JSONObject representation;
    static Activity mActivity;
    static View mInflater;
    static ListViewRepresentation listViewTodayAdapter, listViewTomorrowAdapter;

    public RepresentationFragment() {
        // Required empty public constructor
    }

    /**
     * Set the representations of today and tomorrow
     * Provide the date like this: {"today":
     * {"date": "1.12.15", "data":
     * [{"lesson": 1, "subject": "...", "room": "..."}, ...]},
     * "tomorrow": ... }
     *
     * @param representations The data of the representations
     */
    public static void setRepresentations(JSONObject representations) {
        try {
            JSONObject today = representations.getJSONObject("today");
            JSONObject tomorrow = representations.getJSONObject("tomorrow");

            setDates(today.getString("date"), tomorrow.getString("date"));

            clearRepresentations();

            String refresh = representations.getString("lastrefresh");
            refresh = refresh.trim();
            String hour = refresh.substring(34, 39);
            String date = refresh.substring(22, 29);

            ((TextView) mInflater.findViewById(R.id.updated)).setText(String.format("%s %s %s", mActivity.getString(R.string.updated_at), date, hour));

            addRepresentations("today", today.getJSONArray("data"));
            addRepresentations("tomorrow", tomorrow.getJSONArray("data"));

        } catch (JSONException | StringIndexOutOfBoundsException e) {
            //Hotfix portal wrong json
            e.printStackTrace();
        }
    }

    /**
     * Set the values for the specified day
     *
     * @param day  The day to set the values for
     * @param data The lessons on the specified day
     */
    public static void addRepresentations(String day, JSONArray data) throws JSONException {

        ListViewRepresentation adapter = (day.equals("today") ? listViewTodayAdapter : listViewTomorrowAdapter);

        for (int i = 0; i < data.length(); i++) {
            JSONObject representation = data.getJSONObject(i);
            int lesson = representation.getInt("lesson");
            String subject = representation.getString("subject");
            String teacher = representation.getString("teacher");
            String room = representation.getString("room");

            adapter.add(new RepresentationItem(subject, teacher, lesson, room));

        }


        if(data.length() > 0) {
            if(day.equals("today")) {
                CardView noToday = (CardView) mInflater.findViewById(R.id.no_representation_today);
                noToday.setVisibility(View.GONE);
            } else {
                CardView noTomorrow = (CardView) mInflater.findViewById(R.id.no_representation_tomorrow);
                noTomorrow.setVisibility(View.GONE);
            }
        }

        adapter.notifyDataSetChanged();
    }

    public static void clearRepresentations() {

        listViewTodayAdapter.reset();
        listViewTodayAdapter.notifyDataSetChanged();

        listViewTomorrowAdapter.reset();
        listViewTomorrowAdapter.notifyDataSetChanged();


        CardView noToday = (CardView) mInflater.findViewById(R.id.no_representation_today);
        CardView noTomorrow = (CardView) mInflater.findViewById(R.id.no_representation_tomorrow);

        noToday.setVisibility(View.VISIBLE);
        noTomorrow.setVisibility(View.VISIBLE);
    }

    public static void setDates(String today, String tomorrow) {
        TextView todayView = (TextView) mInflater.findViewById(R.id.dateToday);
        TextView tomorrowView = (TextView) mInflater.findViewById(R.id.dateTomorrow);

        //Heute, dd. DD.MM.YYYY
        String todayDate = mActivity.getString(
                R.string.today,
                today.substring(0, today.length() - 20) +
                        today.substring(today.length() - 19, today.length() - 8));
        String tomorrowDate = mActivity.getString(
                R.string.tomorrow,
                tomorrow.substring(0, tomorrow.length() - 20) +
                        tomorrow.substring(tomorrow.length() - 19, tomorrow.length() - 8));

        todayView.setText(todayDate);
        tomorrowView.setText(tomorrowDate);
    }

    public static void setActivity(Activity mActivity) {
        RepresentationFragment.mActivity = mActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mInflater = inflater.inflate(R.layout.fragment_representation, container, false);

        if (Helper.getHasNoAds(getContext())) {
            TextView representations = (TextView) mInflater.findViewById(R.id.representations_disclaimer);
            representations.setPadding(representations.getPaddingLeft(), representations.getPaddingTop(), representations.getPaddingRight(), 8);
        }

        // Recyclers
        // Today
        RecyclerView representationToday = (RecyclerView) mInflater.findViewById(R.id.recycler_today);
        listViewTodayAdapter = new ListViewRepresentation(getContext());

        representationToday.setLayoutManager(new LinearLayoutManager(getContext()));
        representationToday.setItemAnimator(new DefaultItemAnimator());
        representationToday.setAdapter(listViewTodayAdapter);

        //Tomorrow
        RecyclerView representationTomorrow = (RecyclerView) mInflater.findViewById(R.id.recycler_tomorrow);
        listViewTomorrowAdapter = new ListViewRepresentation(getContext());

        representationTomorrow.setLayoutManager(new LinearLayoutManager(getContext()));
        representationTomorrow.setItemAnimator(new DefaultItemAnimator());
        representationTomorrow.setAdapter(listViewTomorrowAdapter);

        /*listViewTodayAdapter.add(new RepresentationItem("Englisch", "entfällt", "7:45 - 8:30", "D105"));
        listViewTodayAdapter.add(new RepresentationItem("Deutsch", "entfällt", "8:30 - 9:15", "A206"));
        listViewTodayAdapter.notifyDataSetChanged();*/


        //Execute
        setRepresentations(representation);

        return mInflater;
    }

    public void notifyColorChange() {
        listViewTodayAdapter.notifyDataSetChanged();
        listViewTomorrowAdapter.notifyDataSetChanged();
    }
}
