package com.example.roman.targets;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DestinationPageAdapter extends RecyclerView.Adapter<DestinationPageAdapter.DestinationAdapterViewHolder> {
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
        Page currentPage = mDataSet.get(position);
        Button text = (Button) holder.itemView;
        if (currentPage.id == 0)
            text.setText(R.string.title_main);
        else
            text.setText(currentPage.title);

        /*switch (page)
        {
            case 0:
                text.setText(R.string.title_main);
                if (currentPage.id != 0) {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }
                break;
            case 1:
                if (!currentPage.section || currentPage.id == 0)
                {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }
                break;
            case 2:
                if (currentPage.section || currentPage.id == 0)
                {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }
                break;
        }*/
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardAdapter.dismissDialog();
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

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    DestinationPageAdapter()
    {
        setPage(0);
    }
}
