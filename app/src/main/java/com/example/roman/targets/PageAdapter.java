package com.example.roman.targets;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageAdapterViewHolder> {
    private ArrayList<Page> mDataset;
    private PageAdapter pageAdapter = this;
    private String newPage; //when page title set to empty (do like that cuz need to access string resources)

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final PageAdapterViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //shows pages relative to current section
        if (mDataset.get(position).section != MainActivity.currentSection || mDataset.get(position).id == 0)
        {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }
        else {
            holder.mTextView.setText(mDataset.get(position).title);

            //show category or not
            if (MainActivity.allPagesList.get(position).category) {
                holder.mDivider.setVisibility(View.VISIBLE);
                holder.mCategory.setVisibility(View.VISIBLE);
                holder.mCategory.setText(MainActivity.allPagesList.get(position).category_name);
            } else {
                holder.mDivider.setVisibility(View.GONE);
                holder.mCategory.setVisibility(View.GONE);
            }

            //navigate to page's cards
            holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CardsFragment cardsFragment = CardsFragment.newInstance(MainActivity.allPagesList.get(position).id);
                    MainActivity.activity.navigate(cardsFragment);
                }
            });
            //menu for current page
            holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.change_title:
                            AlertDialog.Builder titleBuilder = new AlertDialog.Builder(MainActivity.activityContext());
                            titleBuilder.setMessage(R.string.change_title);
                            LayoutInflater inflater = LayoutInflater.from(MainActivity.activityContext());
                            View v = inflater.inflate(R.layout.change_page_title, null);
                            final EditText editText = v.findViewById(R.id.change_page_title);
                            editText.setText(MainActivity.allPagesList.get(position).title);
                            titleBuilder.setView(v);
                            titleBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (editText.getText().toString().equals(""))
                                        MainActivity.allPagesList.get(position).title = newPage;
                                    else
                                        MainActivity.allPagesList.get(position).title = editText.getText().toString();
                                    try {
                                        MainActivity.db.editPage(MainActivity.allPagesList.get(position));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    pageAdapter.notifyDataSetChanged();
                                }
                            });
                            titleBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            });
                            AlertDialog changeTitleDialog = titleBuilder.create();
                            changeTitleDialog.show();
                            break;
                        case R.id.change_category:
                            AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(MainActivity.activityContext());
                            LayoutInflater inflater2 = LayoutInflater.from(MainActivity.activityContext());
                            View v2 = inflater2.inflate(R.layout.change_page_category, null);
                            final Switch sw = v2.findViewById(R.id.category_switch);
                            sw.setChecked(MainActivity.allPagesList.get(position).category);
                            final EditText editText2 = v2.findViewById(R.id.change_category_title);
                            editText2.setText(MainActivity.allPagesList.get(position).category_name);
                            categoryBuilder.setView(v2);
                            categoryBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.allPagesList.get(position).category = sw.isChecked();
                                    MainActivity.allPagesList.get(position).category_name = editText2.getText().toString();
                                    try {
                                        MainActivity.db.editPage(MainActivity.allPagesList.get(position));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    pageAdapter.notifyDataSetChanged();
                                }
                            });
                            categoryBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            });
                            AlertDialog categoryDialog = categoryBuilder.create();
                            categoryDialog.show();
                            break;
                        case R.id.delete:
                            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(MainActivity.activityContext());
                            deleteBuilder.setMessage(R.string.delete_message);
                            deleteBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ArrayList<Card> pageCards = MainActivity.db.findPageCards(MainActivity.allPagesList.get(position).id);
                                    for (int i = 0; i < pageCards.size(); i++) {
                                        Card c = new Card();
                                        c.id = pageCards.get(i).id;
                                        MainActivity.db.removeCard(c);
                                        try {
                                            MainActivity.db.editPage(MainActivity.allPagesList.get(MainActivity.allPagesList.get(position).id));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    MainActivity.db.removePage(MainActivity.allPagesList.get(holder.getAdapterPosition()));
                                    MainActivity.allPagesList.remove(holder.getAdapterPosition());

                                    pageAdapter.notifyDataSetChanged();
                                }
                            });
                            deleteBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            });
                            AlertDialog deleteDialog = deleteBuilder.create();
                            deleteDialog.show();
                            break;
                    }
                    return true;
                }
            });
        }
    }

    public PageAdapter(ArrayList<Page> myDataset, String newPageTitle) {
        mDataset = myDataset;
        newPage = newPageTitle;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PageAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_page, parent, false);
        PageAdapterViewHolder vh = new PageAdapterViewHolder(v);
        return vh;
    }

    public static class PageAdapterViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageButton mButton;
        public PopupMenu popupMenu;
        public TextView mCategory;
        public View mDivider;
        public LinearLayout mLinearLayout;

        public PageAdapterViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.pagename);
            mButton = v.findViewById(R.id.pageActionButton);
            popupMenu = new PopupMenu(MainActivity.applicationContext(), mButton);
            popupMenu.getMenuInflater().inflate(R.menu.page, popupMenu.getMenu());

            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
            mCategory = v.findViewById(R.id.category_title);
            mDivider = v.findViewById(R.id.category_divider);

            mLinearLayout = v.findViewById(R.id.show_cards);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
