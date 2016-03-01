package onl.deepspace.wgs;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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

            TextView name = new TextView(this);
            name.setLayoutParams(layoutParams);
            name.setText(childName);
            name.setHeight(150);
            name.setTypeface(Typeface.DEFAULT_BOLD);
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            name.setGravity(Gravity.CENTER_VERTICAL);
            name.setOnClickListener(clickListener);
            container.addView(name);
        }
    }
}
