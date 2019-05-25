package com.example.roman.targets;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MoveCopyDialogFragment extends DialogFragment {
    static ArrayList<Integer> cards;
    static int currentPageId;

    private static RadioGroup radioGroup;

    MoveCopyDialogFragment(ArrayList<Integer> cards, int currentPageId)
    {
        this.currentPageId = currentPageId;
        this.cards = (ArrayList<Integer>)cards.clone();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Title!");
        View v = inflater.inflate(R.layout.move_copy_dialog, null);
        final RecyclerView rv = v.findViewById(R.id.destination_pages);
        TabLayout tabLayout = v.findViewById(R.id.page_tabs);
        radioGroup = v.findViewById(R.id.movecopy_radiogroup);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                DestinationPageAdapter adapter = (DestinationPageAdapter)rv.getAdapter();
                adapter.setPage(tab.getPosition());
                Log.d("myDebug", String.valueOf(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new DestinationPageAdapter());
        return v;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }



    public static class DestinationPageAdapter extends RecyclerView.Adapter<DestinationPageAdapter.DestinationAdapterViewHolder> {
        private ArrayList<Page> mDataSet;

        static class DestinationAdapterViewHolder extends RecyclerView.ViewHolder {
            Button text;

            DestinationAdapterViewHolder(View v) {
                super(v);
                text = v.findViewById(R.id.destination_page_item);
            }
        }
        @Override
        public DestinationAdapterViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
            // create a new view
            Button b = (Button) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_destination_page, parent, false);
            DestinationAdapterViewHolder vh = new DestinationAdapterViewHolder(b);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull DestinationAdapterViewHolder holder, int position) {
            final Page selectedPage = mDataSet.get(holder.getAdapterPosition());
            Button text = (Button) holder.itemView;
            if (selectedPage.id == 0)
                text.setText(R.string.title_main);
            else
                text.setText(selectedPage.title);

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Card> inp = MainActivity.db.findPageCards(currentPageId);
                    switch (radioGroup.getCheckedRadioButtonId())
                    {
                        case R.id.radio_copy:
                            for(int i=0; i<cards.size(); i++)
                            {
                                Card c = findCardById(cards.get(i), inp);
                                c.id = MainActivity.db.cardTableSize();
                                MainActivity.db.addCard(c);
                                selectedPage.cards.add(c.id);
                                MainActivity.db.editPage(selectedPage);
                                Log.d("<", "Copy to page with id "+selectedPage.id);
                                Log.d("<", "It's cards: "+selectedPage.cards);
                            }
                            break;
                        case R.id.radio_move:
                            for(int i=0; i<cards.size(); i++) {
                                Card c = findCardById(cards.get(i), inp);
                                MainActivity.db.addCard(c);
                                selectedPage.cards.add(c.id);
                                MainActivity.db.editPage(selectedPage);
                                MainActivity.allPagesList.get(currentPageId).cards.remove(new Integer(c.id));
                                MainActivity.db.editPage(MainActivity.allPagesList.get(currentPageId));
                            }
                            break;
                        case R.id.radio_instance:
                            for(int i=0; i<cards.size(); i++)
                            {
                                Card c = findCardById(cards.get(i), inp);
                                MainActivity.db.addCard(c);
                                selectedPage.cards.add(c.id);
                                MainActivity.db.editPage(selectedPage);
                                Log.d("<", "Copy to page with id "+selectedPage.id);
                                Log.d("<", "It's cards: "+selectedPage.cards);
                            }
                            break;
                    }
                    Fragment fragment = MainActivity.activity.getSupportFragmentManager().findFragmentById(R.id.navigation_content);
                    if (fragment instanceof CardsFragment) {
                        CardAdapter adapter = ((CardsFragment)fragment).mAdapter;
                        adapter.updateState();
                        CardAdapter.dismissDialog();
                    }
                    if (fragment instanceof CardEditFragment) {
                        CardAdapter adapter = ((CardEditFragment)fragment).mAdapter;
                        adapter.updateState();
                        CardAdapter.dismissDialog();
                    }
                }
            });
        }

        public void setPage(int page)
        {
            mDataSet = new ArrayList<>();
            switch (page)
            {
                case 0:
                    mDataSet.add(MainActivity.allPagesList.get(0));
                    break;
                case 1:
                    for (Page p : MainActivity.allPagesList)
                        if (p.section && p.id != 0)
                            mDataSet.add(p);
                    break;
                case 2:
                    for (Page p : MainActivity.allPagesList)
                        if (!p.section && p.id != 0)
                            mDataSet.add(p);
                    break;
            }
            notifyDataSetChanged();
        }
        private Card findCardById(int id, ArrayList<Card> list)
        {
            for (int i = 0; i<list.size(); i++)
            {
                if (list.get(i).id == id)
                    return list.get(i);
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        DestinationPageAdapter()
        {
            setPage(0);
        }
    }

}
