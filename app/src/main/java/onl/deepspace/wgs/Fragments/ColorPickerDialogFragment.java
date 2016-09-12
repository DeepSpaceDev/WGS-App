package onl.deepspace.wgs.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import onl.deepspace.wgs.R;
import onl.deepspace.wgs.activities.ChangeColorActivity;
import onl.deepspace.wgs.activities.colorpicker.ColorPickerItem;
import onl.deepspace.wgs.activities.colorpicker.ListViewColorPicker;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class ColorPickerDialogFragment extends DialogFragment {

    public ColorPickerDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.color_picker_dialog, null);

        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler_color_picker);
        ListViewColorPicker adapter = new ListViewColorPicker((ChangeColorActivity) getActivity());

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
        adapter.add(new ColorPickerItem("red", true));
        adapter.add(new ColorPickerItem("pink", true));
        adapter.add(new ColorPickerItem("purple", true));
        adapter.add(new ColorPickerItem("deep_purple", true));
        adapter.add(new ColorPickerItem("indigo", true));
        adapter.add(new ColorPickerItem("blue", true));
        adapter.add(new ColorPickerItem("light_blue", true));
        adapter.add(new ColorPickerItem("cyan", true));
        adapter.add(new ColorPickerItem("teal", true));
        adapter.add(new ColorPickerItem("green", true));
        adapter.add(new ColorPickerItem("light_green", true));
        adapter.add(new ColorPickerItem("lime", true));
        adapter.add(new ColorPickerItem("yellow", true));
        adapter.add(new ColorPickerItem("amber", true));
        adapter.add(new ColorPickerItem("orange", true));
        adapter.add(new ColorPickerItem("deep_orange", true));
        adapter.add(new ColorPickerItem("brown", false));
        adapter.add(new ColorPickerItem("grey", false));
        adapter.add(new ColorPickerItem("blue_grey", false));

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
