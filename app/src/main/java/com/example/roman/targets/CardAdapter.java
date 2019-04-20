package com.example.roman.targets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.logging.Logger;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardAdapterViewHolder> {
    private int pageId;
    private int selectedCardPosition;
    public boolean editMode;
    String TAG = "myDebug";

    public ArrayList<Card> mDataset;
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

        LinearLayout cardMenu;
        ImageButton b1,b2,b3,b4,b5,b6;
        PopupMenu popupMenu;

        public CardAdapterViewHolder(CardView v) {
            super(v);
            mTitle = v.findViewById(R.id.card_m_title);
            mText = v.findViewById(R.id.card_m_text);

            title = v.findViewById(R.id.card_title);
            text = v.findViewById(R.id.card_text);

            cardMenu = v.findViewById(R.id.edit_mode_menu);
            b1 = v.findViewById(R.id.add_image);
            b2 = v.findViewById(R.id.add_picture);
            b3 = v.findViewById(R.id.add_question);
            b4 = v.findViewById(R.id.add_divider);
            b5 = v.findViewById(R.id.add_link);
            b6 = v.findViewById(R.id.main_funcs);

            popupMenu = new PopupMenu(MainActivity.applicationContext(), b6);
            popupMenu.getMenuInflater().inflate(R.menu.card_actions, popupMenu.getMenu());
            b6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
        }
    }

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("1 " + MainActivity.activityContext().getResources().getString(R.string.selected));
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
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(MainActivity.activityContext());
                deleteDialog.setMessage(R.string.card_delete_message);
                deleteDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeCards();
                    }
                });
                deleteDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                deleteDialog.create().show();
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
                .inflate(R.layout.item_card_note, parent, false);
        CardAdapterViewHolder vh = new CardAdapterViewHolder(v);
        return vh;
    }

    public CardAdapter(int pageId, boolean editMode, int selectedCardPosition) {
        this.pageId = pageId;
        this.editMode = editMode;
        this.selectedCardPosition = selectedCardPosition;
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
        Fragment f = MainActivity.activity.getSupportFragmentManager().findFragmentById(R.id.navigation_content);
        if (f instanceof CardsFragment)
            ((CardsFragment)f).checkCards();

        final Card currentCard = mDataset.get(position);
        holder.cardMenu.setVisibility(View.GONE);

        // select card
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectionMode) {
                    if (!selectedCards.contains(currentCard.id))
                        selectedCards.add(currentCard.id);
                    else selectedCards.remove(new Integer(currentCard.id));
                    MainActivity.activity.actionMode.setTitle(selectedCards.size() + " " + MainActivity.activityContext().getResources().getString(R.string.selected));

                    if (!selectedCards.isEmpty()) notifyDataSetChanged();
                    else hideActions();
                }
                else if (!editMode) {
                    CardEditFragment editFragment = CardEditFragment.newInstance(pageId, position);
                    MainActivity.activity.navigate(editFragment);
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

        if (editMode)
        {
            holder.mTitle.setVisibility(View.GONE);
            holder.mText.setVisibility(View.GONE);

            holder.title.setText(currentCard.title);
            holder.text.setText(currentCard.text);

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
            // changing view size on focus
            View.OnFocusChangeListener textFocusListener = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        int height = Math.round(MainActivity.activityContext().getResources().getDisplayMetrics().heightPixels * 0.7f);
                        if (holder.text.getHeight() < height)
                            holder.text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
                        CardEditFragment fragment = (CardEditFragment)MainActivity.activity.getSupportFragmentManager().findFragmentById(R.id.navigation_content);
                        fragment.mRecyclerView.scrollToPosition(position);
                        holder.cardMenu.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        // hide keyboard
                        InputMethodManager imm = (InputMethodManager)MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        holder.cardMenu.setVisibility(View.GONE);
                    }
                }
            };

            holder.title.addTextChangedListener(textWatcher);
            holder.text.addTextChangedListener(textWatcher);
            holder.text.setOnFocusChangeListener(textFocusListener);

            if (position == selectedCardPosition) {
                holder.text.requestFocus();
                InputMethodManager imgr = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
        else {
            SharedPreferences sharedPref = MainActivity.activity.getPreferences(Context.MODE_PRIVATE);
            if (sharedPref.getBoolean("maxHeightEnabled", true)) {
                int percent = sharedPref.getInt("maxHeight", 40);
                int height = Math.round(MainActivity.activityContext().getResources().getDisplayMetrics().heightPixels * (percent / 100f));
                holder.text.setMaxHeight(height);
            }

            holder.title.setVisibility(View.GONE);
            holder.text.setVisibility(View.GONE);
            holder.cardMenu.setVisibility(View.GONE);

            holder.mTitle.setText(currentCard.title);
            holder.mText.setText(currentCard.text);

            holder.mTitle.setOnLongClickListener(longListener);
            holder.mText.setOnLongClickListener(longListener);

            holder.mTitle.setOnClickListener(clickListener);
            holder.mText.setOnClickListener(clickListener);
        }

        if (selectedCards.contains(currentCard.id))
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        else
            holder.itemView.setBackgroundColor(MainActivity.activityContext().getResources().getColor(R.color.design_default_color_background));

        if (selectedCards.isEmpty())
            hideActions();

        holder.itemView.findViewById(R.id.card_check).setVisibility(View.GONE);

        holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.delete_card:
                        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(MainActivity.activityContext());
                        deleteDialog.setMessage(R.string.card_delete_message);
                        deleteDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Card c = new Card();
                                c.id = mDataset.get(position).id;
                                MainActivity.db.removeCard(c);
                                MainActivity.db.editPage(MainActivity.allPagesList.get(pageId));
                                updateState();
                            }
                        });
                        deleteDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        });
                        deleteDialog.create().show();
                        break;
                }
                return true;
            }
        });
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
            MainActivity.db.editPage(MainActivity.allPagesList.get(pageId));
        }
        updateState();
    }

    public void updateState() {
        mDataset = MainActivity.db.findPageCards(pageId);
        notifyDataSetChanged();
        hideActions();
    }
}
