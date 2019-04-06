package com.example.roman.targets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CardsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardsFragment extends Fragment implements RecyclerNameTouchHelper.AnimationListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PAGE_ID = "param1";
    public int pageID;
    private OnFragmentInteractionListener mListener;

    Card.CardType cardType;

    public CardsFragment() {
        // Required empty public constructor
    }

    public static CardsFragment newInstance(int pageId) {
        CardsFragment fragment = new CardsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_ID, pageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageID = getArguments().getInt(ARG_PAGE_ID);
        }
    }

    RecyclerView mRecyclerView;
    public CardAdapter mAdapter;
    StaggeredGridLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cards, container, false);
        mRecyclerView = view.findViewById(R.id.card_list);
        TextView title = view.findViewById(R.id.currentPageName);
        String section = "";
        if (pageID !=0)
            section = MainActivity.allPagesList.get(pageID).section ? getResources().getString(R.string.title_personal) : getResources().getString(R.string.title_work);
        title.setText(section + " \\ " + MainActivity.allPagesList.get(pageID).title);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

            // use a layout manager
            mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new CardAdapter(pageID,false, 0);
            mRecyclerView.setAdapter(mAdapter);

            new ItemTouchHelper(new RecyclerNameTouchHelper(this)).attachToRecyclerView(mRecyclerView);

            // add card button & dialog
            FloatingActionButton add_button = view.findViewById(R.id.add_card);
            add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater1 = getLayoutInflater();
                    final View d = inflater1.inflate(R.layout.add_card, null);

                    final Spinner spinner = d.findViewById(R.id.spinner);
                    spinner.setSelection(0);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 0:
                                    d.findViewById(R.id.note_layout).setVisibility(View.VISIBLE);
                                    d.findViewById(R.id.list_layout).setVisibility(View.GONE);
                                    d.findViewById(R.id.question_layout).setVisibility(View.GONE);
                                    d.findViewById(R.id.deadline_layout).setVisibility(View.GONE);

                                    d.findViewById(R.id.note_text).requestFocus();
                                    break;
                                case 1:
                                    d.findViewById(R.id.note_layout).setVisibility(View.GONE);
                                    d.findViewById(R.id.list_layout).setVisibility(View.VISIBLE);
                                    d.findViewById(R.id.question_layout).setVisibility(View.GONE);
                                    d.findViewById(R.id.deadline_layout).setVisibility(View.GONE);
                                    break;
                                case 2:
                                    d.findViewById(R.id.note_layout).setVisibility(View.GONE);
                                    d.findViewById(R.id.list_layout).setVisibility(View.GONE);
                                    d.findViewById(R.id.question_layout).setVisibility(View.GONE);
                                    d.findViewById(R.id.deadline_layout).setVisibility(View.VISIBLE);
                                    break;
                                case 3:
                                    d.findViewById(R.id.note_layout).setVisibility(View.GONE);
                                    d.findViewById(R.id.list_layout).setVisibility(View.GONE);
                                    d.findViewById(R.id.question_layout).setVisibility(View.VISIBLE);
                                    d.findViewById(R.id.deadline_layout).setVisibility(View.GONE);
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                            builder.setView(d);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            switch (spinner.getSelectedItemPosition()) {
                                case 0:
                                    EditText mTitle = d.findViewById(R.id.note_title);
                                    EditText mText = d.findViewById(R.id.note_text);
                                    if (!mTitle.getText().toString().trim().isEmpty() || !mText.getText().toString().trim().isEmpty()) {
                                        Card res = new Card(MainActivity.db.cardTableSize(), pageID, mTitle.getText().toString(), mText.getText().toString());
                                        MainActivity.db.addCard(res);
                                        MainActivity.allPagesList.get(pageID).cards.add(MainActivity.db.cardTableSize() - 1);
                                        MainActivity.db.editPage(MainActivity.allPagesList.get(pageID));
                                        mAdapter.updateState();
                                    }
                                    break;
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            });
        return view;
    }

    private AlertDialog switchCardType(Card.CardType cardType1, AlertDialog dialog){
        cardType = cardType1;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater1 = getLayoutInflater();
        View d = inflater1.inflate(R.layout.item_card_note, null);
        d.findViewById(R.id.card_m_title).setVisibility(View.GONE);
        d.findViewById(R.id.card_m_text).setVisibility(View.GONE);
        d.findViewById(R.id.delete_card).setVisibility(View.GONE);
        final EditText mTitle = d.findViewById(R.id.card_title);
        final EditText mText = d.findViewById(R.id.card_text);
        builder.setView(d);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!mTitle.getText().toString().trim().isEmpty() || !mText.getText().toString().trim().isEmpty()) {
                    Card res = new Card(MainActivity.db.cardTableSize(), pageID, mTitle.getText().toString(), mText.getText().toString());
                    MainActivity.db.addCard(res);
                    MainActivity.allPagesList.get(pageID).cards.add(MainActivity.db.cardTableSize() - 1);
                    MainActivity.db.editPage(MainActivity.allPagesList.get(pageID));
                    mAdapter.updateState();
                }
            }
        });
        return builder.create();
    }
    //another default class members:
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
        mAdapter.hideActions();
        mAdapter.selectedCards.clear();
    }

    @Override
    public void onMove(int fromPos, int toPos) {

        if (!mAdapter.editMode) {
            mAdapter.mDataset.add(toPos, mAdapter.mDataset.remove(fromPos));
            mAdapter.notifyItemMoved(fromPos, toPos);
        }
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
