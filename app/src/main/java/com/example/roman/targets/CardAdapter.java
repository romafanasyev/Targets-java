package com.example.roman.targets;

import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardAdapterViewHolder> {
    private int pageId;
    private ArrayList<Card> mDataset;
    private boolean selectionMode = false;
    ArrayList<Integer> selectedCards = new ArrayList<>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CardAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mText;

        EditText title;
        EditText text;
        public CardAdapterViewHolder(CardView v) {
            super(v);
            mTitle = v.findViewById(R.id.card_m_title);
            mText = v.findViewById(R.id.card_m_text);

            title = v.findViewById(R.id.card_title);
            text = v.findViewById(R.id.card_text);
        }
    }

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("1 " + MainActivity.getActivityContext().getResources().getString(R.string.selected));
            mode.getMenuInflater().inflate(R.menu.card_actions, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.delete_card) {
                removeCards();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            selectionMode = false;
            selectedCards.clear();
            notifyDataSetChanged();
        }
    };

    // Create new views (invoked by the layout manager)
    @Override
    public CardAdapter.CardAdapterViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        CardAdapterViewHolder vh = new CardAdapterViewHolder(v);
        return vh;
    }

    public CardAdapter(int pageId) {
        this.pageId = pageId;
        mDataset = MainActivity.db.findPageCards(pageId);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final CardAdapterViewHolder holder, final int position) {
        final Card currentCard = mDataset.get(position);

        //all listeners:
        // select card for editing
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectionMode) {
                    if (!selectedCards.contains(currentCard.id))
                        selectedCards.add(currentCard.id);
                    else selectedCards.remove(new Integer(currentCard.id));
                    MainActivity.activity.actionMode.setTitle(selectedCards.size() + " " + MainActivity.getActivityContext().getResources().getString(R.string.selected));

                    if (!selectedCards.isEmpty()) notifyDataSetChanged();
                    else hideActions();
                }
            }
        };

        // card long press action
        View.OnLongClickListener longListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!selectionMode) {
                    showActions();
                    selectedCards.add(currentCard.id);
                    notifyDataSetChanged();
                }
                return true;
            }
        };

        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        /*
        if (Card.editId == currentCard.id) {
            holder.mTitle.setVisibility(View.GONE);
            holder.mText.setVisibility(View.GONE);

            holder.title.setText(currentCard.title);
            holder.text.setText(currentCard.text);

            holder.title.setOnLongClickListener(longListener);
            holder.text.setOnLongClickListener(longListener);

            // saving changes to cards
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    currentCard.title = holder.title.getText().toString();
                    currentCard.text = holder.text.getText().toString();
                    MainActivity.db.editCard(currentCard);
                }
            };
            holder.title.addTextChangedListener(textWatcher);
            holder.text.addTextChangedListener(textWatcher);
        }

        - need to rearrange in the fragment for card
        */

        if (selectedCards.contains(currentCard.id))
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        else
            holder.itemView.setBackgroundColor(MainActivity.getActivityContext().getResources().getColor(R.color.design_default_color_background));

        if (selectedCards.isEmpty())
            hideActions();

        //TODO: add conditions here
        holder.title.setVisibility(View.GONE);
        holder.text.setVisibility(View.GONE);

        holder.mTitle.setText(currentCard.title);
        holder.mText.setText(currentCard.text);

        holder.mTitle.setOnLongClickListener(longListener);
        holder.mText.setOnLongClickListener(longListener);

        holder.mTitle.setOnClickListener(clickListener);
        holder.mText.setOnClickListener(clickListener);
    }

    public void showActions()
    {
        selectionMode = true;
        MainActivity.activity.showActionMode(callback);
    }

    public void hideActions()
    {
        if (MainActivity.activity.actionMode != null) {
            MainActivity.activity.actionMode.finish();
        }
    }

    private void removeCards() {
        for (int i = 0; i < selectedCards.size(); i++) {
            Card c = new Card();
            c.id = selectedCards.get(i);
            MainActivity.db.removeCard(c);
            try {
                MainActivity.db.editPage(MainActivity.allPagesList.get(pageId));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }
}
