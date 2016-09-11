package onl.deepspace.wgs.activities.colorpicker;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;

/**
 * Created by Dennis on 11.09.2016.
 */

public class ListViewColorPicker extends RecyclerView.Adapter<ListViewColorPicker.ViewHolder> {

    List<ColorPickerItem> items;
    Activity activity;

    public ListViewColorPicker(Activity activity) {
        this.activity = activity;
        this.items = new ArrayList<>();
    }

    public void add(ColorPickerItem item) {
        items.add(item);
    }

    public void reset() {
        items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_color_picker, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ColorPickerItem item = items.get(position);
        String color = item.color;
        boolean accentColors = item.accentColors;

        ColorSpinnerItem[] colors = new ColorSpinnerItem[accentColors ? 14 : 10];
        Resources res = activity.getResources();
        for (int i = 0; i < 10; i++) {
            String strength = String.valueOf(i == 0 ? 50 : i * 100);
            Log.d(Helper.LOGTAG, color + '_' + strength);
            int id = res.getIdentifier(
                    color + '_' + strength, "color", activity.getPackageName());
            Log.d(Helper.LOGTAG, id + "");
            colors[i] = new ColorSpinnerItem(color, id, strength);
        }
        if (accentColors) {
            for (int i = 10; i < 14; i++) {
                String strength;
                switch (i) {
                    case 10: strength = "A100"; break;
                    case 11: strength = "A200"; break;
                    case 12: strength = "A400"; break;
                    case 13: strength = "A700"; break;
                    default: strength = "A100";
                }
                Log.d(Helper.LOGTAG, color + '_' + strength);
                int id = res.getIdentifier(
                        color + '_' + strength, "color", activity.getPackageName());
                Log.d(Helper.LOGTAG, id + "");
                colors[i] = new ColorSpinnerItem(color, id, strength);
            }
        }

        ColorSpinnerAdapter adapter = new ColorSpinnerAdapter(
                activity, R.layout.list_view_color_spinner, colors);
        holder.spinner.setAdapter(adapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final Spinner spinner;

        public ViewHolder(View itemView) {
            super(itemView);
            spinner = (Spinner) itemView.findViewById(R.id.lv_copi_spinner);
        }
    }
}
