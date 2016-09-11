package onl.deepspace.wgs.bottomaction;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import onl.deepspace.wgs.activities.FeatureRequestActivity;
import onl.deepspace.wgs.asynctasks.FeedbackSender;
import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BottomAction.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BottomAction#showBottomSheet(Class, int, String, String, String, String)} factory method to
 * create an instance of this fragment.
 */
public class BottomAction extends Fragment {
    private static final int INTERVAL = 1000;
    private static final int DEFAULT_DELAY = 10000;
    private static final Handler handler = new Handler();
    private static boolean ACTIVE_ACTION = false;
    private static ArrayList<BottomActionElement> ACTION_QUEUE = new ArrayList<>();
    private static HashMap<Class, Activity> objectForActivity = new HashMap<>();

    private static final String FEATURE_REQUEST_ACTIVITY = "FeatureRequestActivity";

    private static final String ARG_TYPE = "type";
    private static final String ARG_ACTION = "action";
    private static final String ARG_QUESTION = "question";
    private static final String ARG_ADDITIONAL = "additional";
    public static final String TYPE_RATING = "rating";
    public static final String TYPE_URL = "url";
    public static final String TYPE_ACTIVITY = "activity";

    private String mType;
    private String mAction;
    private String mQuestion;
    private String mAdditional;
    private View mView;
    private int mDelay;
    private int rating;

    private OnFragmentInteractionListener mListener;

    public BottomAction() {
        // Required empty public constructor
    }

    public static void setObjectForActivity(Class activityClass, Activity activity) {
        objectForActivity.put(activityClass, activity);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param type The type of bottom sheet.
     */
    public static void showBottomSheet(Class activityClass, int containerId, String type,
                                       String action, String question, String additional) {
        BottomActionElement waitingAction =
                new BottomActionElement(activityClass, containerId, type, action, question, additional);
        if(!ACTIVE_ACTION) {
            openBottomSheet(waitingAction);
        } else {
            // If there is a active action put this action is the queue
            ACTION_QUEUE.add(waitingAction);
        }
    }

    private static void openBottomSheet(BottomActionElement bottomAction) {
        final Class activityClass = bottomAction.getActivity();
        final Activity activity = objectForActivity.get(activityClass);
        final int containerId = bottomAction.getContainerId();
        final String type = bottomAction.getType();
        final String action = bottomAction.getAction();
        final String question = bottomAction.getQuestion();
        final String additional = bottomAction.getAdditional();

        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        BottomAction fragment = new BottomAction();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putString(ARG_ACTION, action);
        args.putString(ARG_QUESTION, question);
        args.putString(ARG_ADDITIONAL, additional);
        fragment.setArguments(args);

        fragmentTransaction.add(containerId, fragment);
        fragmentTransaction.commitAllowingStateLoss();

        ACTIVE_ACTION = true;
    }

    private static void showNextAction() {
        if (ACTION_QUEUE.size() > 0) {
            BottomActionElement upcomingAction = ACTION_QUEUE.get(0);
            if (upcomingAction != null) {
                ACTION_QUEUE.remove(0);
                openBottomSheet(upcomingAction);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getString(ARG_TYPE);
            mAction = bundle.getString(ARG_ACTION);
            mQuestion = bundle.getString(ARG_QUESTION);
            mAdditional = bundle.getString(ARG_ADDITIONAL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mType.equals(TYPE_RATING))
            mView = inflater.inflate(R.layout.bottom_action_rating, container, false);
        else
            mView = inflater.inflate(R.layout.bottom_action, container, false);

        ((TextView) mView.findViewById(R.id.bottom_action_hint)).setText(mQuestion);

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_up);
        mView.startAnimation(animation);
        mView.setVisibility(View.VISIBLE);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomActionClicked();
            }
        });

        if(mType.equals(TYPE_RATING)) {
            rating = -1;
            setUpRatingBarListeners();
        } else if (mType.equals(TYPE_URL)) {
            ImageView image = (ImageView) mView.findViewById(R.id.bottom_action_image);
            Resources res = getResources();
            int imageId = res.getIdentifier(mAdditional, "drawable", getActivity().getPackageName());
            image.setImageResource(imageId);
        }

        setUpClosingHandler();
        return mView;
    }

    private void rate(int n) {
        rating = n;
        mDelay = 3000;
        Resources res = getResources();
        for (int i = 0; i < 5; i++) {
            int imageNr = i + 1;
            int id = res.getIdentifier("bottom_action_" + imageNr + "s", "id", getActivity().getPackageName());
            ImageView image = (ImageView) mView.findViewById(id);
            int imageId = i < n ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp;
            image.setImageResource(imageId);
        }
    }

    public void bottomActionClicked() {
        boolean shouldClose = false;
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
        switch(mType) {
            case TYPE_URL:
                Uri webPage = Uri.parse(mAction);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);
                if(webIntent.resolveActivity(getActivity().getPackageManager()) != null)
                    startActivity(webIntent);
                shouldClose = true;
                break;
            case TYPE_RATING: break;
            case TYPE_ACTIVITY:
                Intent activityIntent = new Intent(getActivity(), getActivityClass(mAction));
                startActivity(activityIntent);
                break;
        }
        if(shouldClose) closeBottomSheet();
    }

    public static Class getActivityClass(String activity) {
        switch (activity) {
            case FEATURE_REQUEST_ACTIVITY: return FeatureRequestActivity.class;
            default: return null;
        }

    }

    public static String getActivityName(Class activity) {
        if(activity == FeatureRequestActivity.class) {
            return FEATURE_REQUEST_ACTIVITY;
        } else {
            return null;
        }
    }

    private void setUpClosingHandler() {
        handler.removeCallbacksAndMessages(null);
        mDelay = DEFAULT_DELAY;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDelay -= INTERVAL;

                if(mDelay < 0) closeBottomSheet();
                else handler.postDelayed(this, INTERVAL);
            }
        }, INTERVAL);
    }

    private void setUpRatingBarListeners() {
        //TODO support swiping to change rating with in pressed state
        mView.findViewById(R.id.bottom_action_1s).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rate(1);
                return true;
            }
        });
        mView.findViewById(R.id.bottom_action_2s).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rate(2);
                return true;
            }
        });
        mView.findViewById(R.id.bottom_action_3s).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rate(3);
                return true;
            }
        });
        mView.findViewById(R.id.bottom_action_4s).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rate(4);
                return true;
            }
        });
        mView.findViewById(R.id.bottom_action_5s).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rate(5);
                return true;
            }
        });
    }

    public void closeBottomSheet() {
        handler.removeCallbacksAndMessages(null);
        mView.setVisibility(View.GONE);
        final Fragment fragment = this;
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
                ACTIVE_ACTION = false;
                showNextAction();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        mView.startAnimation(animation);
        if(mType.equals(TYPE_RATING)) {
            Log.i(Helper.LOGTAG, rating + "");
            if(rating > 0) {
                sendFeedback(mAction, mAdditional, rating);
                Toast.makeText(getActivity(), "Danke für dein Feedback", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendFeedback(String action, String category, int rating) {
        new FeedbackSender(getActivity(), action, category, rating + "").execute();
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
        void onFragmentInteraction();
    }
}