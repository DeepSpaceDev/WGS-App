package onl.deepspace.wgs.activities.colorpicker;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import onl.deepspace.wgs.R;

/**
 * Created by Dennis on 12.09.2016.
 */
public class ColorSpinnerAdapter extends ArrayAdapter<ColorSpinnerItem> {

    private Activity activity;
    private ColorSpinnerItem[] items;

    public ColorSpinnerAdapter(Activity activity, int resource, ColorSpinnerItem[] objects) {
        super((Context) activity, resource, objects);
        this.activity = activity;
        this.items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        //TODO check if warning is valid for our purpose
        View view = inflater.inflate(R.layout.list_view_color_spinner, parent, false);

        Resources res = activity.getResources();

        int colorId = items[position].colorId;
        String colorName = items[position].color;
        String colorStrength = items[position].strength;

        if (colorStrength == null) {
            View divider = view.findViewById(R.id.lv_copi_divider);
            divider.setVisibility(View.VISIBLE);
        }

        CircleImageView color = (CircleImageView) view.findViewById(R.id.lv_copi_color);
        color.setImageResource(colorId);

        TextView name = (TextView) view.findViewById(R.id.lv_copi_color_name);
        name.setText(res.getIdentifier(colorName, "string", activity.getPackageName()));

        TextView strength = (TextView) view.findViewById(R.id.lv_copi_color_strength);
        strength.setText(colorStrength);

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
