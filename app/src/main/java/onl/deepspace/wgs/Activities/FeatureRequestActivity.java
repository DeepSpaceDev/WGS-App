package onl.deepspace.wgs.Activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import onl.deepspace.wgs.AsyncTasks.FeedbackSender;
import onl.deepspace.wgs.R;

public class FeatureRequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_request);

        final Activity activity = this;

        Button button = (Button) findViewById(R.id.feature_request_send);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup group = (RadioGroup) findViewById(R.id.feature_request_radio);
                assert group != null;
                int checkedId = group.getCheckedRadioButtonId();
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                assert checkedButton != null;
                String feature = checkedButton.getHint().toString();
                TextView textView = (TextView) findViewById(R.id.feature_request_additional);
                assert textView != null;
                String additionalFeature = textView.getText().toString();
                if(additionalFeature.length() > 0)
                    feature += ", " + additionalFeature;
                new FeedbackSender(activity, "https://deepspace.onl/scripts/sites/wgs/feedback.php",
                        "features", feature).execute();
            }
        });
    }

}
