package onl.deepspace.wgs.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.github.florent37.tutoshowcase.TutoShowcase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;
import onl.deepspace.wgs.bottomaction.BottomAction;
import onl.deepspace.wgs.fragments.FoodMenuFragment;
import onl.deepspace.wgs.fragments.RepresentationFragment;
import onl.deepspace.wgs.fragments.TimetableFragment;
import onl.deepspace.wgs.portalupdate.AlarmReceiver;

public class PortalActivity extends AppCompatActivity
        implements BottomAction.OnFragmentInteractionListener {

    public static final int PICK_CHILD_REQUEST = 1;
    public static final int CUSTOM_TIMETABLE_REQUEST = 3;
    private static final int CHANGE_COLOR_REQUEST = 2;
    private static final String INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    public static final int REMOVE_ADS = 1001;

    public static IInAppBillingService mService;
    static ServiceConnection mServiceConn;

    private Runnable updateData;

    AlarmReceiver mAlarm = new AlarmReceiver();
    JSONArray mChildren;
    AdView mAdView;

    private RepresentationFragment representationFragment;
    private TimetableFragment timetableFragment;

    private static JSONObject timetable, representation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EXTRAS verarbeitung
        setContentView(R.layout.activity_portal);

        // AdMob
        if (!Helper.getHasNoAds(getBaseContext())) {
            mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        // Bottom Action
        /*BottomAction.setObjectForActivity(PortalActivity.class, this); // Setup of Bottom Action
        BottomAction.showBottomSheet(PortalActivity.class, R.id.main_content, BottomAction.TYPE_URL, "https://play.google.com/store/apps/details?id=onl.deepspace.wgs", "Rate on Play Store", "playstore");
        try {
            long lastTime = Helper.getLastBottomAction(this);
            long currentTime = System.currentTimeMillis();
            JSONObject nextAction = Helper.nextBottomAction(this);
            assert nextAction != null;
            String action = nextAction.getString(Helper.BOTTOM_ACTION_ACTION);
            String type = nextAction.getString(Helper.BOTTOM_ACTION_TYPE);
            String hint = nextAction.getString(Helper.BOTTOM_ACTION_HINT);
            String additional = nextAction.getString(Helper.BOTTOM_ACTION_ADDITIONAL);
            int daysAfter = nextAction.getInt(Helper.BOTTOM_ACTION_DAYS_AFTER);

            long diff = currentTime - lastTime;
            diff *= Helper.MILLIS_TO_DAYS;

            if (diff > daysAfter) {
                BottomAction.showBottomSheet(PortalActivity.class, R.id.main_content,
                        type, action, hint, additional);
                Helper.incrementNextAction(this);
                Helper.setLastBottomAction(this, currentTime);
            }

        } catch (JSONException e) {
            Log.e(Helper.LOGTAG, e.getMessage());
        }*/

        // In App billing
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null)
            setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the mActivity.
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null)
            mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        if (tabLayout != null && mViewPager != null)
            tabLayout.setupWithViewPager(mViewPager);

        //Alarm Manager
        if (Helper.getEmail(this) != null & Helper.getPw(this) != null)
            mAlarm.setAlarm(this);

        //Handle multiple childs
        Bundle extras = getIntent().getExtras();
        try {
            mChildren = new JSONArray(extras.getString(Helper.API_RESULT_CHILDREN));

            int childIndex = Helper.getChildIndex(this);
            selectChild(childIndex);

            showTutorial(mChildren.length() > 1);

        } catch (JSONException e) {
            Log.e(Helper.LOGTAG, e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REMOVE_ADS) {
            //int responseCode = data.getIntExtra(RESPONSE_CODE, 0);
            String purchaseData = data.getStringExtra(INAPP_PURCHASE_DATA);
            //String dataSignature = data.getStringExtra(INAPP_DATA_SIGNATURE);

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    if (sku.equalsIgnoreCase("wgs_app_remove_ads")) {
                        Helper.setHasNoAds(this, true);
                        mAdView.setVisibility(View.INVISIBLE);
                        Snackbar.make(findViewById(R.id.main_content),
                                "Werbung Entfernt! Danke für deinen Kauf.",
                                Snackbar.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PortalActivity.this,
                                "Your request was not set. Please contact the developer!",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(PortalActivity.this,
                            "Failed to parse purchase.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(PortalActivity.this,
                        "Failed to parse purchase.", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == PICK_CHILD_REQUEST) {
            int childIndex = data.getIntExtra(Helper.CHILD_INDEX, 0);

            Helper.setChildIndex(this, childIndex);
            selectChild(childIndex);
        }
        if (requestCode == CHANGE_COLOR_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (representationFragment != null) {
                    representationFragment.notifyColorChange();
                }
            }
        }
        if (requestCode == CUSTOM_TIMETABLE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Force reset
                timetableFragment.updateTimetable();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(
                        this, R.string.timetable_changes_discarded, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_portal, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Helper.getHasNoAds(getBaseContext())) {
            MenuItem mi = menu.findItem(R.id.action_remads);
            mi.setVisible(false);
        }

        MenuItem selectChild = menu.findItem(R.id.action_select_child);
        if (mChildren.length() < 2) {
            selectChild.setVisible(false);
        } else {
            selectChild.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_select_child: {
                Intent intent = new Intent(this, SelectChildActivity.class);
                ArrayList<String> childrenNames = new ArrayList<>();

                try {
                    for (int i = 0; i < mChildren.length(); i++) {
                        String name = mChildren.getString(i);
                        childrenNames.add(name);
                    }
                } catch (JSONException e) {
                    Log.e(Helper.LOGTAG, e.getMessage());
                }

                intent.putExtra(Helper.CHILDREN, childrenNames);
                startActivityForResult(intent, PICK_CHILD_REQUEST);
                break;
            }
            case R.id.action_change_subject_colors: {
                Intent intent = new Intent(this, ChangeColorActivity.class);
                startActivityForResult(intent, CHANGE_COLOR_REQUEST);
                break;
            }
            case R.id.action_about: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.action_logout: {
                Helper.setEmail(this, null);
                Helper.setPw(this, null);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.action_remads:
                Helper.purchaseNoAd(this);
                break;
            case R.id.action_feature_request: {
                Intent intent = new Intent(this, FeatureRequestActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.action_create_timetable: {
                Intent intent = new Intent(this, CustomTimetableActivity.class);
                startActivityForResult(intent, CUSTOM_TIMETABLE_REQUEST);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(updateData != null) updateData.run();
    }

    private void showTutorial(boolean multipleChildren) {
        Log.d(Helper.LOGTAG, "Muliple Children: " + multipleChildren);

        int pseudoId = multipleChildren ? R.id.pseudo_cover_icon_multiple : R.id.pseudo_cover_icon_single;

        TutoShowcase.from(this)
                .setContentView(R.layout.tutorial_portal)
                .on(pseudoId)
                .addRoundRect()
                .withBorder()

                .on(R.id.container)
                .displaySwipableRight()
                .animated(true)
                .showOnce(Helper.PREF_PORTAL_TUTORIAL);

        if (!multipleChildren)
            ((TextView) findViewById(R.id.tutorial_text_1)).setText(R.string.tutorial_click_menu_single);
    }

    private void selectChild(int index) {
        try {
            if (index + 1 > mChildren.length()) {
                index = 0;
            }
            JSONObject child = mChildren.getJSONObject(index);
            Log.d(Helper.LOGTAG, child.toString());

            String name = child.getString(Helper.API_RESULT_NAME);
            ActionBar bar = getSupportActionBar();
            if (bar != null) bar.setTitle(name);

            timetable = child.getJSONObject(Helper.API_RESULT_TIMETABLE);
            representation = child.getJSONObject(Helper.API_RESULT_REPRESENTATION);

            updateData = new Runnable() {
                @Override
                public void run() {
                    if (timetableFragment != null && representationFragment != null) {
                        timetableFragment.setTimetable(timetable);
                        representationFragment.setRepresentation(representation);
                    }
                }
            };

        } catch (JSONException e) {
            Log.e(Helper.LOGTAG, e.getMessage());
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return representationFragment = RepresentationFragment.newInstance(representation);
                case 1:
                    return new FoodMenuFragment();
                case 2:
                    return timetableFragment = TimetableFragment.newInstance(timetable);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Vertretungen";
                case 1:
                    return "Speiseplan";
                case 2:
                    return "Stundenplan";
            }
            return null;
        }
    }
}
