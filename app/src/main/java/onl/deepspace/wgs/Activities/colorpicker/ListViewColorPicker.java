package onl.deepspace.wgs.activities.colorpicker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;
import onl.deepspace.wgs.activities.ChangeColorActivity;

/**
 * Created by Dennis on 11.09.2016.
 */

public class ListViewColorPicker extends RecyclerView.Adapter<ListViewColorPicker.ViewHolder> {

    List<ColorPickerItem> items;
    ChangeColorActivity activity;

    public ListViewColorPicker(ChangeColorActivity activity) {
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
    public void onBindViewHolder(final ViewHolder holder, int position) {

        ColorPickerItem item = items.get(position);
        String color = item.color;
        boolean accentColors = item.accentColors;

        final ColorSpinnerItem[] colors = new ColorSpinnerItem[accentColors ? 15 : 11];
        Resources res = activity.getResources();
        for (int i = 0; i < 11; i++) {
            String strength;
            if (i == 0) {
                colors[i] = new ColorSpinnerItem(color, res.getIdentifier(
                        color + "_500", "color", activity.getPackageName()), null);
                continue;
            }
            switch (i) {
                case 1: strength = "50"; break;
                case 2: strength = "100"; break;
                case 3: strength = "200"; break;
                case 4: strength = "300"; break;
                case 5: strength = "400"; break;
                case 6: strength = "500"; break;
                case 7: strength = "600"; break;
                case 8: strength = "700"; break;
                case 9: strength = "800"; break;
                case 10: strength = "900"; break;
                default: strength = "500";
            }
            int id = res.getIdentifier(
                    color + '_' + strength, "color", activity.getPackageName());
            colors[i] = new ColorSpinnerItem(color, id, strength);
        }
        if (accentColors) {
            for (int i = 11; i < 15; i++) {
                String strength;
                switch (i) {
                    case 10: strength = "A100"; break;
                    case 11: strength = "A200"; break;
                    case 12: strength = "A400"; break;
                    case 13: strength = "A700"; break;
                    default: strength = "A100";
                }
                int id = res.getIdentifier(
                        color + '_' + strength, "color", activity.getPackageName());
                colors[i] = new ColorSpinnerItem(color, id, strength);
            }
        }

        ColorSpinnerAdapter adapter = new ColorSpinnerAdapter(
                activity, R.layout.list_view_color_spinner, colors);
        holder.spinner.setAdapter(adapter);
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (firstSelection) {
                    firstSelection = false;
                    return;
                }
                int colorId = colors[i].colorId;
                activity.setColor(colorId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final Spinner spinner;

        public ViewHolder(View itemView) {
            super(itemView);
            spinner = (Spinner) itemView.findViewById(R.id.lv_copi_spinner);
        }
    }
}
