package onl.deepspace.wgs;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectChildActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_child);

        Intent intent = getIntent();
        ArrayList<String> children = intent.getStringArrayListExtra(Helper.CHILDREN);

        setupChildrenList(children);
    }

    private void setupChildrenList(final ArrayList<String> children) {
        LinearLayout container = (LinearLayout) findViewById(R.id.select_children_container);
        container.removeAllViews();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup parent = (ViewGroup) v.getParent();
                int index = parent.indexOfChild(v);
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

    private int findIndex(ArrayList<String> children, String name) {
        for (int i = 0; i < children.size(); i++) {
            if(children.get(i).equals(name)) return i;
        }
        return -1;
    }


}
