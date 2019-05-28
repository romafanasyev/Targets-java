package com.example.roman.targets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


public class PreferencesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    SeekBar displaySeekBar, editSeekBar;
    TextView displayText, editText, tv4;

    public PreferencesFragment() {
        // Required empty public constructor
    }

    public static PreferencesFragment newInstance(String param1, String param2) {
        PreferencesFragment fragment = new PreferencesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);
        displaySeekBar = view.findViewById(R.id.displaySeekBar);
        editSeekBar = view.findViewById(R.id.editSeekBar);
        displayText = view.findViewById(R.id.colnum_display);
        editText = view.findViewById(R.id.colnum_edit);
        tv4 = view.findViewById(R.id.textView4);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        int defaultValue = 2;
        int count = sharedPref.getInt("displayColumns", defaultValue);
        displaySeekBar.setProgress(count-1);
        displayText.setText(String.valueOf(count));

        defaultValue = 1;
        count = sharedPref.getInt("editColumns", defaultValue);
        editSeekBar.setProgress(count-1);
        editText.setText(String.valueOf(count));

        displaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                displayText.setText(String.valueOf(progress+1));
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("displayColumns", progress+1);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        editSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editText.setText(String.valueOf(progress+1));
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("editColumns", progress+1);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }

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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}