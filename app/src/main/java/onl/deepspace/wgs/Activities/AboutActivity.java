package onl.deepspace.wgs.Activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import onl.deepspace.wgs.R;

public class AboutActivity extends AppCompatActivity {

    private int clickcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView authors = (TextView) findViewById(R.id.about_authors);
        authors.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        //easteregg
        clickcount = 0;
        ImageView logo = (ImageView) findViewById(R.id.deepspace_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickcount >= 5){
                    Snackbar.make(findViewById(R.id.about_activity), R.string.wgs_assitance, Snackbar.LENGTH_LONG).show();
                    clickcount = 0;
                }
                else{
                    clickcount++;
                }
            }
        });
    }
}
