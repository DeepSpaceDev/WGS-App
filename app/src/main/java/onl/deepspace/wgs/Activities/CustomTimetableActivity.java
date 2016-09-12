package onl.deepspace.wgs.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;

import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;
import onl.deepspace.wgs.fragments.CustomTimetableDayFragment;

public class CustomTimetableActivity extends AppCompatActivity
        implements CustomTimetableDayFragment.OnFragmentInteractionListener{

    private JSONArray mTimetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_timetable);

        mTimetable = Helper.getCustomTimetable(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_timetable_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        DayPagerAdapter adapter = new DayPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.custom_timetable_viewpager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.custom_timetable_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onTimetableChanged(int dayIndex, int lesson, String subject) {
        try {
            JSONArray array = mTimetable.getJSONArray(dayIndex);
            array.put(lesson, subject);
        } catch (JSONException e) {
            Log.e(Helper.LOGTAG, "onTimetableChanged: ", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_custom_timetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save_timetable) {
            Helper.setCustomTimetable(this, mTimetable);
            setResult(RESULT_OK);
            finish();
        }
        if (id == R.id.action_discard_timetable) {
            setResult(RESULT_CANCELED);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    class DayPagerAdapter extends FragmentPagerAdapter {

        DayPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            try {
                return CustomTimetableDayFragment
                        .newInstance(position, mTimetable.getJSONArray(position));
            } catch (JSONException e) {
                try {
                    JSONArray array = new JSONArray(
                            "[\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"]");
                    mTimetable.put(position, new JSONArray());
                    return CustomTimetableDayFragment.newInstance(position, array);
                } catch (JSONException e1) {
                    Log.e(Helper.LOGTAG, "getItem: ", e);
                    return null;
                }
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.long_monday);
                case 1: return getString(R.string.long_tuesday);
                case 2: return getString(R.string.long_wednesday);
                case 3: return getString(R.string.long_thursday);
                case 4: return getString(R.string.long_friday);
                default:return "";
            }
        }
    }

}
