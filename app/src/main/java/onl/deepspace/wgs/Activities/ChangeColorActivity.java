package onl.deepspace.wgs.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import onl.deepspace.wgs.fragments.ColorPickerDialogFragment;
import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;
import onl.deepspace.wgs.activities.colorpicker.ListViewColorChange;
import onl.deepspace.wgs.activities.colorpicker.SubjectColorItem;

public class ChangeColorActivity extends AppCompatActivity {

    static ListViewColorChange adapter;
    private String subject;
    private ColorPickerDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_color);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_subject_colors);
        adapter = new ListViewColorChange(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        setAdapter();
    }

    public void openColorPickerDialog(String subject) {
        if (dialogFragment == null) dialogFragment = new ColorPickerDialogFragment();
        dialogFragment.show(getFragmentManager(), "colorpicker");
        this.subject = subject;
    }

    public void setColor(int color) {
        if (dialogFragment != null)
            dialogFragment.dismiss();
        Helper.setColorForSubject(this, subject, color);
        adapter.reset();
        setAdapter();
        setResult(RESULT_OK);
    }

    public void setAdapter() {
        for (int i = 0; i < Helper.ALL_SUBJECTS.length; i++) {
            adapter.add(new SubjectColorItem(Helper.ALL_SUBJECTS[i]));
        }
        adapter.notifyDataSetChanged();
    }
}
