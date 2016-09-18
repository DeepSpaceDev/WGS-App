package onl.deepspace.wgs.activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;

public class AboutActivity extends AppCompatActivity {

    private int clickCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // Firebase Analytics
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(Helper.EVENT_SHOW_ABOUT, new Bundle());

        TextView authors = (TextView) findViewById(R.id.about_authors);
        authors.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        //easter egg
        clickCount = 0;
        ImageView logo = (ImageView) findViewById(R.id.deepspace_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickCount >= 5){
                    Snackbar.make(findViewById(R.id.about_activity), R.string.wgs_assitance, Snackbar.LENGTH_LONG).show();
                    clickCount = 0;
                }
                else{
                    clickCount++;
                }
            }
        });
    }
}
