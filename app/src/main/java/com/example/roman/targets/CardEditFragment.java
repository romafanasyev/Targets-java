package com.example.roman.targets;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


public class CardEditFragment extends Fragment {

    private static final String ARG_PAGE_ID = "param1";
    private static final String ARG_CARD_POSITION = "param2";

    public int pageID;
    private int cardPosition;

    FloatingActionButton actionButton;

    private OnFragmentInteractionListener mListener;

    public CardEditFragment() {
        // Required empty public constructor
    }

    public static CardEditFragment newInstance(int pageId, int card_position) {
        CardEditFragment fragment = new CardEditFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_ID, pageId);
        args.putInt(ARG_CARD_POSITION, card_position);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageID = getArguments().getInt(ARG_PAGE_ID);
            cardPosition = getArguments().getInt(ARG_CARD_POSITION);
        }
    }

    public RecyclerView mRecyclerView;
    CardAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cards, container, false);
        view.findViewById(R.id.no_cards).setVisibility(View.GONE);

        mRecyclerView = view.findViewById(R.id.card_list);
        TextView title = view.findViewById(R.id.currentPageName);
        String section = "";
        if (pageID !=0)
            section = MainActivity.allPagesList.get(pageID).section ? getResources().getString(R.string.title_personal) : getResources().getString(R.string.title_work);
        title.setText(section + " \\ " + MainActivity.allPagesList.get(pageID).title);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView

        mRecyclerView.setHasFixedSize(true);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int defaultValue = 1;
        int count = sharedPref.getInt("editColumns", defaultValue);
        // use a layout manager
        mLayoutManager = new StaggeredGridLayoutManager(count,StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CardAdapter(pageID, true, cardPosition);
        mRecyclerView.setAdapter(mAdapter);

        actionButton = view.findViewById(R.id.add_card);
        actionButton.setVisibility(View.GONE);

        mRecyclerView.scrollToPosition(cardPosition);

        return view;
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
