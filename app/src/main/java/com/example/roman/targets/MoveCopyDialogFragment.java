package com.example.roman.targets;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

public class MoveCopyDialogFragment extends DialogFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Title!");
        View v = inflater.inflate(R.layout.move_copy_dialog, null);
        final RecyclerView rv = v.findViewById(R.id.destination_pages);
        TabLayout tabLayout = v.findViewById(R.id.page_tabs);
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
}
