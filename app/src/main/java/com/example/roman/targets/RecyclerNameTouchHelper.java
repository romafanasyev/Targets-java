package com.example.roman.targets;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

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
        if(animationListener != null){
            animationListener.onMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());}
            return true;


    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }
    interface  AnimationListener{
        void onMove(int fromPos, int toPos);
    }
}
