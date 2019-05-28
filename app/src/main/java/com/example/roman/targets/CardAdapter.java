package com.example.roman.targets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.roman.targets.helper.ItemTouchHelperAdapter;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.example.roman.targets.MainActivity.activity;
import static com.example.roman.targets.MainActivity.allPagesList;
import static com.example.roman.targets.MainActivity.db;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter, Filterable {

    private int pageId;
    boolean selectCard = true;
    private int selectedCardPosition;
    public boolean editMode;

    private static final int TYPE_DIVIDER = 1;
    private static final int TYPE_NOTE = 0;
    private static final int TYPE_DEADLINE = 2;
    private static final int TYPE_QUESTION = 3;
    private static final int TYPE_LIST = 4;

    public ArrayList<Card> mDataset = new ArrayList<>();
    public ArrayList<Card> mDatasetFull;
    public boolean selectionMode = false;
    ArrayList<Integer> selectedCards = new ArrayList<>();

    public CardAdapter(int pageId, boolean editMode, int selectedCardPosition) {
        this.pageId = pageId;
        this.editMode = editMode;
        this.selectedCardPosition = selectedCardPosition;
        mDataset.clear();
        mDataset.addAll(db.findPageCards(pageId));
        mDatasetFull = new ArrayList<>(mDataset);
        Card div = new Card(-1, pageId, "", "");
        div.divider = true;
        for (int i = 0; i < mDataset.size(); i++)
        {
            if (mDataset.get(i).hasDivider)
                mDataset.add(i++, div);
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mDataset, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Card> filteredList = new ArrayList<>();

            if(constraint== null){
                filteredList.addAll(mDatasetFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Card i : mDatasetFull){
                    if(i.getText().toLowerCase().contains(filterPattern)){
                        filteredList.add(i);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
         mDataset.clear();
         mDataset.addAll((List)results.values);
         notifyDataSetChanged();
        }

    };
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        View divider;
        LinearLayout content;

        TextView mTitle;
        TextView mText;

        EditText title;
        EditText text;

        LinearLayout cardMenu;
        ImageButton b1, b2, b3, b4, b5;
        PopupMenu popupMenu;

        public NoteViewHolder(CardView v) {
            super(v);
            divider = v.findViewById(R.id.divider);
            content = v.findViewById(R.id.card_content);

            mTitle = v.findViewById(R.id.card_m_title);
            mText = v.findViewById(R.id.card_m_text);

            title = v.findViewById(R.id.card_title);
            text = v.findViewById(R.id.card_text);

            cardMenu = v.findViewById(R.id.edit_mode_menu);
            b1 = v.findViewById(R.id.add_picture);
            b2 = v.findViewById(R.id.add_question);
            b3 = v.findViewById(R.id.add_divider);
            b4 = v.findViewById(R.id.add_link);
            b5 = v.findViewById(R.id.main_funcs);

            popupMenu = new PopupMenu(MainActivity.applicationContext(), b5);
            popupMenu.getMenuInflater().inflate(R.menu.card_actions, popupMenu.getMenu());
            b5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
        }
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv_Title;
        EditText et_Title;
        Button addPointButton;
        RecyclerView points;
        LinearLayout cardMenu;
        PopupMenu popupMenu;
        ImageButton b1, b2, b3, b4;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_Title = itemView.findViewById(R.id.card_m_title);
            et_Title = itemView.findViewById(R.id.card_title);
            addPointButton = itemView.findViewById(R.id.add_point);
            points = itemView.findViewById(R.id.point_rv);
            cardMenu = itemView.findViewById(R.id.edit_mode_menu);

            b1 = itemView.findViewById(R.id.add_picture);
            b2 = itemView.findViewById(R.id.add_question);
            b3 = itemView.findViewById(R.id.add_divider);
            b4 = itemView.findViewById(R.id.main_funcs);

            popupMenu = new PopupMenu(MainActivity.applicationContext(), b4);
            popupMenu.getMenuInflater().inflate(R.menu.card_actions, popupMenu.getMenu());
            b4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
        }
    }

    public static class DeadlineViewHolder extends RecyclerView.ViewHolder
    {
        EditText text;
        TextView setTime;
        ImageButton mainFuncs;
        PopupMenu popupMenu;

        public DeadlineViewHolder(@NonNull View v) {
            super(v);
            text = v.findViewById(R.id.card_text);
            setTime = v.findViewById(R.id.setTime);
            mainFuncs = v.findViewById(R.id.main_funcs);

            popupMenu = new PopupMenu(MainActivity.applicationContext(), mainFuncs);
            popupMenu.getMenuInflater().inflate(R.menu.card_actions, popupMenu.getMenu());
            mainFuncs.setOnClickListener(new View.OnClickListener() {
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
            mode.setTitle("0 " + MainActivity.activityContext().getResources().getString(R.string.selected));
            mode.getMenuInflater().inflate(R.menu.card_actions, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch(item.getItemId()) {
                case  R.id.delete_card:
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
                case R.id.share:

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/html");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Note");
                    intent.putExtra(Intent.EXTRA_TEXT, "Here is the note:\n\n" + mDataset.get(selectedCardPosition).text);

                    c.startActivity(Intent.createChooser(intent, "Send Email"));

                    return true;
                case R.id.move_copy:
                    c = new MoveCopyDialogFragment(selectedCards, pageId);
                    c.show(activity.getSupportFragmentManager(), "copy");

                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    View view = activity.getCurrentFocus();
                    //If no view currently has focus, create a new one, just so we can grab a window token from it
                    if (view == null) {
                        view = new View(activity);
                    }
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                                final int viewType) {
        final CardView view = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_note, parent, false);
        if (viewType == TYPE_DIVIDER) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    final ViewGroup.LayoutParams lp = view.getLayoutParams();
                    if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                        StaggeredGridLayoutManager.LayoutParams sglp =
                                (StaggeredGridLayoutManager.LayoutParams) lp;
                            sglp.setFullSpan(true);
                            view.setLayoutParams(sglp);
                            final StaggeredGridLayoutManager lm =
                                    (StaggeredGridLayoutManager) ((RecyclerView) parent).getLayoutManager();
                            lm.invalidateSpanAssignments();
                    }
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
            for (int i = 0; i < view.getChildCount(); i++)
            {
                view.getChildAt(i).setVisibility(View.GONE);
            }
            view.findViewById(R.id.divider).setVisibility(View.VISIBLE);
            NoteViewHolder vh = new NoteViewHolder(view);
            return vh;
        }
        else if (viewType == TYPE_NOTE) {
            final CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_note, parent, false);
            return new NoteViewHolder(v);
        }
        else if (viewType == TYPE_LIST) {
            final CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_list, parent, false);
            return new ListViewHolder(v);
        }
        else if (viewType == TYPE_DEADLINE)
        {
            final CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_deadline, parent, false);
            v.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    final ViewGroup.LayoutParams lp = v.getLayoutParams();
                    if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                        StaggeredGridLayoutManager.LayoutParams sglp =
                                (StaggeredGridLayoutManager.LayoutParams) lp;
                        sglp.setFullSpan(true);
                        sglp.width = v.getWidth() / 2;
                        v.setLayoutParams(sglp);
                        final StaggeredGridLayoutManager lm =
                                (StaggeredGridLayoutManager) ((RecyclerView) parent).getLayoutManager();
                        lm.invalidateSpanAssignments();
                    }
                    v.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
            return new DeadlineViewHolder(v);
        }
        return new NoteViewHolder(view);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder h, final int position) {
        Fragment f = activity.getSupportFragmentManager().findFragmentById(R.id.navigation_content);
        if (f instanceof CardsFragment) {
            CardsFragment cf = ((CardsFragment)f);
            cf.checkCards();
            if (cf.mAdapter.getItemCount() == 0)
                cf.selectionButton.setVisibility(View.GONE);
            else cf.selectionButton.setVisibility(View.VISIBLE);
        }

        if (getItemViewType(h.getAdapterPosition()) == TYPE_NOTE) {
            final NoteViewHolder holder = (NoteViewHolder)h;

            // select card
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectionMode) {
                        if (!selectedCards.contains(mDataset.get(holder.getAdapterPosition()).id))
                            selectedCards.add(mDataset.get(holder.getAdapterPosition()).id);
                        else
                            selectedCards.remove(new Integer(mDataset.get(holder.getAdapterPosition()).id));
                        activity.actionMode.setTitle(selectedCards.size() + " " + MainActivity.activityContext().getResources().getString(R.string.selected));

                        if (!selectedCards.isEmpty()) notifyDataSetChanged();
                        else hideActions();
                    } else if (!editMode) {
                        CardEditFragment editFragment = CardEditFragment.newInstance(pageId, holder.getAdapterPosition());
                        activity.navigate(editFragment);
                    }
                }
            };

            holder.cardMenu.setVisibility(View.GONE);
            if (!mDataset.get(holder.getAdapterPosition()).divider)
                holder.divider.setVisibility(View.GONE);
            if (editMode) {
                holder.mTitle.setVisibility(View.GONE);
                holder.mText.setVisibility(View.GONE);

                holder.title.setText(mDataset.get(holder.getAdapterPosition()).title);
                holder.text.setText(mDataset.get(holder.getAdapterPosition()).text);

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
                        mDataset.get(holder.getAdapterPosition()).title = holder.title.getText().toString();
                        mDataset.get(holder.getAdapterPosition()).text = holder.text.getText().toString();
                        db.editCard(mDataset.get(holder.getAdapterPosition()));

                        if (mDataset.get(holder.getAdapterPosition()).title.equals("") && mDataset.get(holder.getAdapterPosition()).text.equals("")) {

                            db.removeCard(mDataset.get(holder.getAdapterPosition()));
                            updateState();
                        }
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
                            CardEditFragment fragment = (CardEditFragment) activity.getSupportFragmentManager().findFragmentById(R.id.navigation_content);
                            fragment.mRecyclerView.scrollToPosition(position);
                            holder.cardMenu.setVisibility(View.VISIBLE);
                        } else if (!holder.title.hasFocus()) {
                            holder.text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            // hide keyboard
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            holder.cardMenu.setVisibility(View.GONE);
                        }
                    }
                };
                View.OnFocusChangeListener titleFocusListener = new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            int height = Math.round(MainActivity.activityContext().getResources().getDisplayMetrics().heightPixels * 0.7f);
                            if (holder.text.getHeight() < height)
                                holder.text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
                            CardEditFragment fragment = (CardEditFragment) activity.getSupportFragmentManager().findFragmentById(R.id.navigation_content);
                            fragment.mRecyclerView.scrollToPosition(position);
                            holder.cardMenu.setVisibility(View.VISIBLE);
                        } else if (!holder.text.hasFocus()) {
                            holder.text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            // hide keyboard
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            holder.cardMenu.setVisibility(View.GONE);
                        }
                    }
                };

                holder.title.addTextChangedListener(textWatcher);
                holder.title.setOnFocusChangeListener(titleFocusListener);
                holder.text.addTextChangedListener(textWatcher);
                holder.text.setOnFocusChangeListener(textFocusListener);

                if (holder.getAdapterPosition() == selectedCardPosition && selectCard) {
                    holder.text.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            } else {
                holder.title.setVisibility(View.GONE);
                holder.text.setVisibility(View.GONE);

                holder.mTitle.setText(mDataset.get(holder.getAdapterPosition()).title);
                holder.mText.setText(mDataset.get(holder.getAdapterPosition()).text);

                holder.mTitle.setOnClickListener(clickListener);
                holder.mText.setOnClickListener(clickListener);
            }

            if (selectedCards.contains(mDataset.get(holder.getAdapterPosition()).id))
                holder.itemView.setBackgroundColor(Color.LTGRAY);
            else
                holder.itemView.setBackgroundColor(MainActivity.activityContext().getResources().getColor(R.color.design_default_color_background));

            if (selectedCards.isEmpty())
                hideActions();

            holder.b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    if (pos != 0) {
                        if (mDataset.get(holder.getAdapterPosition()).hasDivider) {
                            mDataset.remove(pos - 1);
                            notifyItemRemoved(pos - 1);
                            mDataset.get(holder.getAdapterPosition()).hasDivider = false;
                            db.editCard(mDataset.get(holder.getAdapterPosition()));
                        } else {
                            Card div = new Card(db.cardTableSize(), pageId, "", "");
                            div.divider = true;
                            mDataset.add(pos, div);
                            notifyItemInserted(pos);
                            selectCard = false;
                            mDataset.get(holder.getAdapterPosition()).hasDivider = true;
                            db.editCard(mDataset.get(holder.getAdapterPosition()));
                        }
                    } else {
                        Card div = new Card(db.cardTableSize(), pageId, "", "");
                        div.divider = true;
                        mDataset.add(pos, div);
                        notifyItemInserted(pos);
                        selectCard = false;
                        mDataset.get(holder.getAdapterPosition()).hasDivider = true;
                        db.editCard(mDataset.get(holder.getAdapterPosition()));
                    }
                }
            });

            holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.delete_card:
                            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(MainActivity.activityContext());
                            deleteDialog.setMessage(R.string.card_delete_message);
                            deleteDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Card c = new Card();
                                    c.id = mDataset.get(holder.getAdapterPosition()).id;
                                    db.removeCard(c);
                                    db.editPage(allPagesList.get(pageId));
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
                        case R.id.move_copy:
                            ArrayList<Integer> card = new ArrayList<>();
                            card.add(mDataset.get(holder.getAdapterPosition()).id);
                            c = new MoveCopyDialogFragment(card, pageId);
                            c.show(activity.getSupportFragmentManager(), "copy");

                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            View view = activity.getCurrentFocus();
                            //If no view currently has focus, create a new one, just so we can grab a window token from it
                            if (view == null) {
                                view = new View(activity);
                            }
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    return true;
                }
            });
        }
        else if (getItemViewType(h.getAdapterPosition()) == TYPE_LIST)
        {
            final ListViewHolder holder = (ListViewHolder)h;

            holder.points.setLayoutManager(new LinearLayoutManager(MainActivity.activityContext()));

            // select card
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectionMode) {
                        if (!selectedCards.contains(mDataset.get(holder.getAdapterPosition()).id))
                            selectedCards.add(mDataset.get(holder.getAdapterPosition()).id);
                        else
                            selectedCards.remove(new Integer(mDataset.get(holder.getAdapterPosition()).id));
                        activity.actionMode.setTitle(selectedCards.size() + " " + MainActivity.activityContext().getResources().getString(R.string.selected));

                        if (!selectedCards.isEmpty()) notifyDataSetChanged();
                        else hideActions();
                    } else if (!editMode) {
                        CardEditFragment editFragment = CardEditFragment.newInstance(pageId, holder.getAdapterPosition());
                        activity.navigate(editFragment);
                    }
                }
            };

            holder.cardMenu.setVisibility(View.GONE);

            if (editMode) {
                holder.points.setAdapter(new PointAdapter(true, mDataset.get(holder.getAdapterPosition()), mDataset.get(holder.getAdapterPosition()).points));

                holder.tv_Title.setVisibility(View.GONE);

                holder.et_Title.setText(mDataset.get(holder.getAdapterPosition()).title);

                holder.addPointButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PointAdapter pa = (PointAdapter)(holder.points.getAdapter());
                        pa.add();
                        holder.points.scrollToPosition(0);
                        pa.focus = true;
                    }
                });

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
                        mDataset.get(holder.getAdapterPosition()).title = holder.et_Title.getText().toString();
                        db.editCard(mDataset.get(holder.getAdapterPosition()));
                    }
                };
                // changing view size on focus
                View.OnFocusChangeListener titleFocusListener = new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            holder.cardMenu.setVisibility(View.VISIBLE);
                        } else {
                            // hide keyboard
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            holder.cardMenu.setVisibility(View.GONE);
                        }
                    }
                };

                holder.et_Title.addTextChangedListener(textWatcher);
                holder.et_Title.setOnFocusChangeListener(titleFocusListener);

                if (holder.getAdapterPosition() == selectedCardPosition && selectCard) {
                    holder.et_Title.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            } else {
                holder.points.setAdapter(new PointAdapter(false, mDataset.get(holder.getAdapterPosition()), mDataset.get(holder.getAdapterPosition()).points));

                holder.et_Title.setVisibility(View.GONE);

                holder.addPointButton.setVisibility(View.GONE);
                holder.tv_Title.setText(mDataset.get(holder.getAdapterPosition()).title);
                holder.tv_Title.setOnClickListener(clickListener);
            }

            if (selectedCards.contains(mDataset.get(holder.getAdapterPosition()).id))
                holder.itemView.setBackgroundColor(Color.LTGRAY);
            else
                holder.itemView.setBackgroundColor(MainActivity.activityContext().getResources().getColor(R.color.design_default_color_background));

            if (selectedCards.isEmpty())
                hideActions();

            holder.b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    if (pos != 0) {
                        if (mDataset.get(holder.getAdapterPosition()).hasDivider) {
                            mDataset.remove(pos - 1);
                            notifyItemRemoved(pos - 1);
                            mDataset.get(holder.getAdapterPosition()).hasDivider = false;
                            db.editCard(mDataset.get(holder.getAdapterPosition()));
                        } else {
                            Card div = new Card(db.cardTableSize(), pageId, "", "");
                            div.divider = true;
                            mDataset.add(pos, div);
                            notifyItemInserted(pos);
                            selectCard = false;
                            mDataset.get(holder.getAdapterPosition()).hasDivider = true;
                            db.editCard(mDataset.get(holder.getAdapterPosition()));
                        }
                    } else {
                        Card div = new Card(db.cardTableSize(), pageId, "", "");
                        div.divider = true;
                        mDataset.add(pos, div);
                        notifyItemInserted(pos);
                        selectCard = false;
                        mDataset.get(holder.getAdapterPosition()).hasDivider = true;
                        db.editCard(mDataset.get(holder.getAdapterPosition()));
                    }
                }
            });

            holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.delete_card:
                            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(MainActivity.activityContext());
                            deleteDialog.setMessage(R.string.card_delete_message);
                            deleteDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Card c = new Card();
                                    c.id = mDataset.get(holder.getAdapterPosition()).id;
                                    db.removeCard(c);
                                    db.editPage(allPagesList.get(pageId));
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
                        case R.id.move_copy:
                            ArrayList<Integer> card = new ArrayList<>();
                            card.add(mDataset.get(holder.getAdapterPosition()).id);
                            c = new MoveCopyDialogFragment(card, pageId);
                            c.show(activity.getSupportFragmentManager(), "copy");

                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            View view = activity.getCurrentFocus();
                            //If no view currently has focus, create a new one, just so we can grab a window token from it
                            if (view == null) {
                                view = new View(activity);
                            }
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    return true;
                }
            });
        }
        else if (getItemViewType(h.getAdapterPosition()) == TYPE_DEADLINE)
        {
            final DeadlineViewHolder holder = (DeadlineViewHolder) h;

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
                        mDataset.get(holder.getAdapterPosition()).text = holder.text.getText().toString();
                        db.editCard(mDataset.get(holder.getAdapterPosition()));
                    }
                };

                holder.text.addTextChangedListener(textWatcher);

                holder.text.setText(mDataset.get(holder.getAdapterPosition()).text);
                holder.setTime.setText(mDataset.get(holder.getAdapterPosition()).End());

            holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.delete_card:
                            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(MainActivity.activityContext());
                            deleteDialog.setMessage(R.string.card_delete_message);
                            deleteDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Card c = new Card();
                                    c.id = mDataset.get(holder.getAdapterPosition()).id;
                                    db.removeCard(c);
                                    db.editPage(allPagesList.get(pageId));
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
                        case R.id.move_copy:
                            ArrayList<Integer> card = new ArrayList<>();
                            card.add(mDataset.get(holder.getAdapterPosition()).id);
                            c = new MoveCopyDialogFragment(card, pageId);
                            c.show(activity.getSupportFragmentManager(), "copy");

                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            View view = activity.getCurrentFocus();
                            //If no view currently has focus, create a new one, just so we can grab a window token from it
                            if (view == null) {
                                view = new View(activity);
                            }
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    return true;
                }
            });
        }
    }

    public void showActions()
    {
        selectionMode = true;
        activity.showActionMode(callback);
    }

    public void hideActions()
    {
        if (activity.actionMode != null) {
            activity.actionMode.finish();
        }
    }

    private void removeCards() {
        for (int i = 0; i < selectedCards.size(); i++) {
            Card c = new Card();
            c.id = selectedCards.get(i);
            db.removeCard(c);
            db.editPage(MainActivity.allPagesList.get(pageId));
        }
        updateState();
    }

    public void updateState() {
        mDataset.clear();
        mDataset.addAll(db.findPageCards(pageId));
        Card div = new Card(-1, pageId, "", "");
        div.divider = true;
        for (int i = 0; i < mDataset.size(); i++)
        {
            if (mDataset.get(i).hasDivider)
                mDataset.add(i++, div);
        }

        notifyDataSetChanged();
        hideActions();
    }

    public static MoveCopyDialogFragment c;

    public static void dismissDialog()
    {
        c.dismiss();
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataset.get(position).divider)
            return TYPE_DIVIDER;
        else if (mDataset.get(position).type == Card.TYPE_NOTE) return TYPE_NOTE;
        else if (mDataset.get(position).type == Card.TYPE_LIST) return TYPE_LIST;
        else if (mDataset.get(position).type == Card.TYPE_DEADLINE) return TYPE_DEADLINE;
        else if (mDataset.get(position).type == Card.TYPE_QUESTION) return TYPE_QUESTION;
        Toast.makeText(MainActivity.activityContext(), "epic fail", Toast.LENGTH_LONG).show();
        return -1;
    }
}
