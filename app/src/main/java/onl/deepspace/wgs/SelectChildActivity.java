package onl.deepspace.wgs;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectChildActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_child);

        Intent intent = getIntent();
        String[] children = intent.getStringArrayExtra(Helper.CHILDREN);

        setupChildrenList(children);
    }

    private void setupChildrenList(final String[] children) {
        LinearLayout container = (LinearLayout) findViewById(R.id.select_children_container);
        container.removeAllViews();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = (String) ((TextView) v).getText();
                int index = findIndex(children, name);
                setResult(index);
                finish();
            }
        };
        for (String aChildren : children) {
            TextView name = new TextView(this);
            name.setLayoutParams(layoutParams);
            name.setText(aChildren);
            name.setHeight(48);
            name.setTypeface(Typeface.DEFAULT_BOLD);
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            name.setGravity(Gravity.CENTER_VERTICAL);
            name.setOnClickListener(clickListener);
            container.addView(name);
        }
    }

    private int findIndex(String[] children, String name) {
        for (int i = 0; i < children.length; i++) {
            if(children[i].equals(name)) return i;
        }
        return -1;
    }


}
