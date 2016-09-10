package onl.deepspace.wgs.BottomAction;

import android.app.Activity;

/**
 * Created by Dennis on 25.03.2016.
 */
public class BottomActionElement {

    private Class mActivity;
    private int mContainerId;
    private String mType;
    private String mAction;
    private String mQuestion;
    private String mAdditional;

    public BottomActionElement(Class activityClass, int containerId, String type, String action,
                               String question, String additional) {
        this.mActivity = activityClass;
        this.mContainerId = containerId;
        this.mType = type;
        this.mAction = action;
        this.mQuestion = question;
        this.mAdditional = additional;
    }

    public Class getActivity() {
        return mActivity;
    }

    public int getContainerId() {
        return mContainerId;
    }

    public String getType() {
        return mType;
    }

    public String getAction() {
        return mAction;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getAdditional() {
        return mAdditional;
    }
}
