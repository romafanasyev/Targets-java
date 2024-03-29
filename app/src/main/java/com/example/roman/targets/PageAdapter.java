
package com.example.roman.targets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roman.targets.helper.ItemTouchHelperAdapter;
import com.example.roman.targets.helper.OnStartDragListener;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageAdapterViewHolder> implements ItemTouchHelperAdapter {

    private boolean section;
    private ArrayList<Page> mDataset;
    private PageAdapter pageAdapter = this;
    private String newPage; //when page title set to empty (do like that cuz need to access string resources)


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final PageAdapterViewHolder holder, final int position) {

        if (MainActivity.section == MainActivity.Section.Personal) {
            PersonalFragment pf = (PersonalFragment)MainActivity.activity.getSupportFragmentManager().findFragmentById(R.id.navigation_content);
            pf.checkPages();
        }
        if (MainActivity.section == MainActivity.Section.Work) {
            WorkFragment pf = (WorkFragment)MainActivity.activity.getSupportFragmentManager().findFragmentById(R.id.navigation_content);
            pf.checkPages();
        }

        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //shows pages relative to current section

        if (mDataset.get(position).section != MainActivity.currentSection || mDataset.get(position).id == 0)
        {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }

        else
            {

            holder.mTextView.setText(mDataset.get(position).title);
            holder.pageAvatar.setText(mDataset.get(position).title.substring(0,1));


            //show category or not
            if (mDataset.get(position).category) {
                holder.mDivider.setVisibility(View.VISIBLE);
                holder.mCategory.setVisibility(View.VISIBLE);
                holder.mCategory.setText(mDataset.get(position).category_name);
            } else {
                holder.mDivider.setVisibility(View.GONE);
                holder.mCategory.setVisibility(View.GONE);
            }

            //navigate to page's cards
            holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CardsFragment cardsFragment = CardsFragment.newInstance(mDataset.get(position).id);
                    MainActivity.activity.navigate(cardsFragment);
                    Log.d("<", "On page with id "+mDataset.get(position).id);
                    Log.d("<", "It's cards: "+mDataset.get(position).cards);
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
                                    changeTitle(editText, position);
                                    notifyDataSetChanged();
                                }
                            });
                            titleBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            });
                            final AlertDialog changeTitleDialog = titleBuilder.create();
                            changeTitleDialog.show();
                            editText.requestFocus();
                            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                                        changeTitle(editText, position);
                                        changeTitleDialog.dismiss();
                                        notifyDataSetChanged();
                                        return true;
                                    }
                                    return false;
                                }
                            });
                            changeTitleDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
                                    changeCategory(editText2, sw, position);
                                    notifyDataSetChanged();
                                }
                            });
                            categoryBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            });
                            final AlertDialog categoryDialog = categoryBuilder.create();
                            categoryDialog.show();
                            editText2.requestFocus();
                            editText2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                                        changeCategory(editText2, sw, position);
                                        categoryDialog.dismiss();
                                        notifyDataSetChanged();
                                        return true;
                                    }
                                    return false;
                                }
                            });
                            categoryDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
                                        MainActivity.db.editPage(MainActivity.allPagesList.get(MainActivity.allPagesList.get(position).id));
                                    }

                                    MainActivity.db.removePage(MainActivity.allPagesList.get(holder.getAdapterPosition()));
                                    mDataset.remove(holder.getAdapterPosition());

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
                        case R.id.menu_move_to:
                            changeSection(position);
                            notifyDataSetChanged();
                            break;
                    }
                    return true;
                }
            });
        }
    }
    private void changeTitle(EditText editText, int position)
    {
        if (editText.getText().toString().equals(""))
            MainActivity.allPagesList.get(position).title = newPage;
        else
            MainActivity.allPagesList.get(position).title = editText.getText().toString();
        MainActivity.db.editPage(MainActivity.allPagesList.get(position));
    }
    private void changeCategory(EditText editText2, Switch sw, int position) {
        MainActivity.allPagesList.get(position).category = sw.isChecked();
        MainActivity.allPagesList.get(position).category_name = editText2.getText().toString();
        MainActivity.db.editPage(MainActivity.allPagesList.get(position));
    }
    private void changeSection(int position) {
        MainActivity.allPagesList.get(position).section = !MainActivity.currentSection;
        MainActivity.db.editPage(MainActivity.allPagesList.get(position));
    }

    public PageAdapter(ArrayList<Page> myDataset, String newPageTitle, boolean isPersonal){

        mDataset = myDataset;
        newPage = newPageTitle;
        section = isPersonal;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public PageAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_page, parent, false);
        PageAdapterViewHolder vh = new PageAdapterViewHolder(v, section);
        return vh;
    }



    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mDataset, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public static class PageAdapterViewHolder extends RecyclerView.ViewHolder {

        public TextView pageAvatar;
        public TextView mTextView;
        public ImageButton mButton;
        public PopupMenu popupMenu;
        public TextView mCategory;
        public View mDivider;
        public LinearLayout mLinearLayout;


        public PageAdapterViewHolder(View v, boolean section) {

            super(v);
            pageAvatar = v.findViewById(R.id.page_avatar);
            mTextView = v.findViewById(R.id.pagename);
            mButton = v.findViewById(R.id.pageActionButton);
            popupMenu = new PopupMenu(MainActivity.applicationContext(), mButton);
            popupMenu.getMenuInflater().inflate(R.menu.page, popupMenu.getMenu());
            popupMenu.getMenu().getItem(2).setTitle(MainActivity.activity.getResources().getString(R.string.move_to) + " " + (section ? MainActivity.activity.getResources().getString(R.string.title_work) : MainActivity.activity.getResources().getString(R.string.title_personal)));

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