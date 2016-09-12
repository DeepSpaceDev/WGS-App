package onl.deepspace.wgs.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;

import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomTimetableDayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomTimetableDayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomTimetableDayFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DAY_INDEX = "dayIndex";
    private static final String ARG_DAY_TIMETABLE = "dayTimetable";

    private static final String SEPARATOR = ",";

    private int mDayIndex;
    private JSONArray mDayTimetable;

    private OnFragmentInteractionListener mListener;

    public CustomTimetableDayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dayIndex The index of the day in the work week.
     * @return A new instance of fragment CustomTimetableDayFragment.
     */
    public static CustomTimetableDayFragment newInstance(int dayIndex, JSONArray dayTimetable) {
        CustomTimetableDayFragment fragment = new CustomTimetableDayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DAY_INDEX, dayIndex);
        String timeTable;
        timeTable = dayTimetable.toString();
        args.putString(ARG_DAY_TIMETABLE, timeTable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mDayIndex = args.getInt(ARG_DAY_INDEX);
            String timeTable = args.getString(ARG_DAY_TIMETABLE);
            try {
                mDayTimetable = new JSONArray(timeTable);
            } catch (JSONException e) {
                Log.e(Helper.LOGTAG, "onCreate: ", e);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_timetable_day, container, false);
        try {
            setUpSpinners(view);
        } catch (JSONException e) {
            Log.e(Helper.LOGTAG, "onCreateView: ", e);
        }
        return view;
    }

    private void setUpSpinners(View view) throws JSONException {
        Resources res = getResources();

        for (int i = 0; i < 11; i++) {
            int id = res.getIdentifier(
                    "custom_timetable_spinner_" + (i + 1), "id", getActivity().getPackageName());

            final int lessonId = i;

            final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    getContext(), R.array.all_subjects, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinner = (Spinner) view.findViewById(id);
            spinner.setAdapter(adapter);

            String subject = mDayTimetable.getString(i);
            if (subject.length() > 0) {
                int longSubjectId = Helper.getLongSubjectId(subject);
                subject = getContext().getString(longSubjectId);
            }

            int position = adapter.getPosition(subject);
            spinner.setSelection(position);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                boolean firstSelect = true;
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (firstSelect) {
                        firstSelect = false;
                        return;
                    }
                    if (mListener != null) {
                        String subject = (String) adapter.getItem(position);
                        mListener.onTimetableChanged(mDayIndex, lessonId,
                                Helper.convertLongSubjectToShortForm(getContext(), subject));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onTimetableChanged(int dayIndex, int lesson, String subject);
    }
}
