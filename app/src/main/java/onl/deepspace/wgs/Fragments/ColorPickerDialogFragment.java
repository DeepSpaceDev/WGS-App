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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ColorPickerDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ColorPickerDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorPickerDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ColorPickerDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ColorPickerDialogFragment newInstance(String param1, String param2) {
        ColorPickerDialogFragment fragment = new ColorPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
