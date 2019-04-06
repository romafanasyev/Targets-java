package com.example.roman.targets;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    public PersonalFragment() {
        // Required empty public constructor
    }

    public static PersonalFragment newInstance() {
        PersonalFragment fragment = new PersonalFragment();

        return fragment;
    }


        //my code
    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private Button add_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        mRecyclerView = view.findViewById(R.id.pages_list_personal);

        // use this setting(false) to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new PageAdapter(MainActivity.allPagesList, getString(R.string.new_page), true);
        mRecyclerView.setAdapter(mAdapter);

        add_button = view.findViewById(R.id.add_page_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder addBuilder = new AlertDialog.Builder(getContext());
                addBuilder.setMessage(R.string.add_page_personal);
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View dialog = inflater.inflate(R.layout.add_page_dialog, null);
                final EditText editText = dialog.findViewById(R.id.editPageName);
                addBuilder.setView(dialog);
                addBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addPage(editText);
                    }
                });
                addBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                final AlertDialog addDialog = addBuilder.create();
                addDialog.show();
                editText.requestFocus();
                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            addPage(editText);
                            addDialog.dismiss();
                            return true;
                        }
                        return false;
                    }
                });
                addDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        return view;
    }

    private void addPage(EditText editText) {
        String input = editText.getText().toString();
        if (input.equals("")) {
            MainActivity.allPagesList.add(new Page(MainActivity.allPagesList.size(), getString(R.string.new_page), true));
        }
        else {
            MainActivity.allPagesList.add(new Page(MainActivity.allPagesList.size(), input, true));
        }
        MainActivity.db.addPage(MainActivity.allPagesList.get(MainActivity.allPagesList.size()-1));
        mAdapter.notifyDataSetChanged();
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
