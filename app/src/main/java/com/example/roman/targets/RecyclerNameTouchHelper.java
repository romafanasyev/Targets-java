package com.example.roman.targets;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.roman.targets.MainActivity.activity;
import static com.example.roman.targets.MainActivity.allPagesList;
import static com.example.roman.targets.MainActivity.db;

public class RecyclerNameTouchHelper extends ItemTouchHelper.Callback {

    private AnimationListener animationListener;

    public RecyclerNameTouchHelper(AnimationListener animationListener) {
        this.animationListener = animationListener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = ItemTouchHelper.ACTION_STATE_IDLE;
        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        if(animationListener != null) {
            animationListener.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            Fragment f = activity.getSupportFragmentManager().findFragmentById(R.id.navigation_content);
            if (f instanceof CardsFragment)
            {
                CardsFragment cardsFragment = (CardsFragment)(f);
                ArrayList<Card> cards = (ArrayList<Card>) cardsFragment.mAdapter.mDataset.clone();
                for (int i=0; i<cards.size(); i++)
                {
                    if (cards.get(i).divider)
                        cards.remove(i--);
                }
                allPagesList.get(cardsFragment.pageID).cards.clear();
                for(int i=0;i<cards.size();i++){
                    allPagesList.get(cardsFragment.pageID).cards.add(cards.get(i).id);

                }
                db.editPage(allPagesList.get(cardsFragment.pageID));

            }
        }
            return true;

    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }


    interface  AnimationListener{
        void onMove(int fromPos, int toPos);
    }
}
