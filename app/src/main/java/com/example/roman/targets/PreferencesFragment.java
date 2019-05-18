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
    Switch quickEditSwitch, maxHeightSwitch;
    Button cardMaxHeightButton;

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
        quickEditSwitch = view.findViewById(R.id.quick_edit_switch);
        maxHeightSwitch = view.findViewById(R.id.max_height_switch);
        tv4 = view.findViewById(R.id.textView4);
        cardMaxHeightButton = view.findViewById(R.id.card_max_height);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        boolean quickEditEnabled = sharedPref.getBoolean("quickEdit", false);
        quickEditSwitch.setChecked(quickEditEnabled);
        if (quickEditEnabled) {
            editSeekBar.setVisibility(View.GONE);
            editText.setVisibility(View.GONE);
            tv4.setVisibility(View.GONE);
        }

        boolean maxHeightEnabled = sharedPref.getBoolean("maxHeightEnabled", true);
        maxHeightSwitch.setChecked(maxHeightEnabled);
        if (!maxHeightEnabled) {
            cardMaxHeightButton.setVisibility(View.GONE);
        }

        int cardMaxHeight = sharedPref.getInt("maxHeight", 40);
        cardMaxHeightButton.setText(String.valueOf(cardMaxHeight));

        int defaultValue = 2;
        int count = sharedPref.getInt("displayColumns", defaultValue);
        displaySeekBar.setProgress(count-1);
        displayText.setText(String.valueOf(count));

        defaultValue = 1;
        count = sharedPref.getInt("editColumns", defaultValue);
        editSeekBar.setProgress(count-1);
        editText.setText(String.valueOf(count));

        quickEditSwitch.setChecked(sharedPref.getBoolean("quickEdit", false));

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
        quickEditSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if (isChecked) {
                    editSeekBar.setVisibility(View.GONE);
                    editText.setVisibility(View.GONE);
                    tv4.setVisibility(View.GONE);
                    editor.putBoolean("quickEdit", true);
                    editor.apply();
                }
                else {
                    editSeekBar.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.VISIBLE);
                    tv4.setVisibility(View.VISIBLE);
                    editor.putBoolean("quickEdit", false);
                    editor.apply();
                }
                MainActivity.switchQuickEditMode(isChecked);
            }
        });
        maxHeightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if (isChecked) {
                    cardMaxHeightButton.setVisibility(View.VISIBLE);
                    editor.putBoolean("maxHeightEnabled", true);
                    editor.apply();
                }
                else {
                    cardMaxHeightButton.setVisibility(View.GONE);
                    editor.putBoolean("maxHeightEnabled", false);
                    editor.apply();
                }
                MainActivity.switchQuickEditMode(isChecked);
            }
        });

        cardMaxHeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view1 = getLayoutInflater().inflate(R.layout.number_picker_dialog,null);
                final NumberPicker np = view1.findViewById(R.id.numberPicker);
                np.setMinValue(10);
                np.setMaxValue(100);
                builder.setView(view1);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cardMaxHeightButton.setText(String.valueOf(np.getValue()));
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("maxHeight", np.getValue());
                        editor.apply();
                    }
                });
                builder.create().show();
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