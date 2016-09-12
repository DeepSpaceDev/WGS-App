package onl.deepspace.wgs.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;

public class SelectChildActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_child);

        Intent intent = getIntent();
        ArrayList<String> children = intent.getStringArrayListExtra(Helper.CHILDREN);

        try{
            setupChildrenList(children);
        }
        catch(JSONException e){
            Log.e(Helper.LOGTAG, e.toString());
        }
    }

    private void setupChildrenList(final ArrayList<String> children) throws JSONException {
        LinearLayout container = (LinearLayout) findViewById(R.id.select_children_container);
        container.removeAllViews();

        LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParamsCard = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsCard.setMargins(pixelToDP(8), pixelToDP(8), pixelToDP(8), pixelToDP(8));

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup parent = (ViewGroup) v.getParent();
                int index = parent.indexOfChild(v);

                setResult(RESULT_OK, new Intent().putExtra(Helper.CHILD_INDEX, index));
                finish();
            }
        };
        for (String aChildren : children) {
            String childName = new JSONObject(aChildren).getString("name");

            CardView card = new CardView(this);
            card.setLayoutParams(layoutParamsCard);
            card.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));

            TextView name = new TextView(this);
            name.setLayoutParams(layoutParamsTextView);
            name.setText(childName);
            name.setTextColor(getResources().getColor(R.color.white));
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            name.setPadding(pixelToDP(8), pixelToDP(8), pixelToDP(8), pixelToDP(8));
            name.setOnClickListener(clickListener);

            card.addView(name);
            container.addView(card);
        }
    }

    @Override
    public void onBackPressed() {
        Snackbar.make(findViewById(R.id.activity_select_child), "Bitte ein Kind auswählen", Snackbar.LENGTH_SHORT).show();
    }

    private int pixelToDP(int pixel) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) ((pixel * scale) + 0.5f);
    }
}
