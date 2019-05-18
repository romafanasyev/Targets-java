
package com.example.roman.targets;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;

public class PointAdapter extends RecyclerView.Adapter<PointAdapter.PointAdapterViewHolder> {
    PointAdapter (boolean editMode)
    {
        this.editMode = editMode;
        points.add(new Point());
    }
    public static class PointAdapterViewHolder extends RecyclerView.ViewHolder {
        public MaterialCheckBox check;
        public TextView text;
        public EditText e_text;
        ImageButton backspace;

        public PointAdapterViewHolder(View v) {
            super(v);
            check = v.findViewById(R.id.point_check);
            text = v.findViewById(R.id.point_text_view);
            e_text = v.findViewById(R.id.point_edit_text);
            backspace = v.findViewById(R.id.backspace);
        }
    }
    boolean editMode;
    private ArrayList<Point> points = new ArrayList<>();
    boolean focus = true;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final PointAdapterViewHolder h, final int position) {
        h.e_text.setText(points.get(position).text);
        h.text.setText(points.get(position).text);
        h.check.setChecked(points.get(position).checked);

        if (editMode)
        {
            h.e_text.setVisibility(View.VISIBLE);
            h.text.setVisibility(View.GONE);

            h.e_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    points.get(position).text = h.e_text.getText().toString();
                }
            });

            h.backspace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(h.getAdapterPosition());
                }
            });
        }
        else
        {
            h.e_text.setVisibility(View.GONE);
            h.text.setVisibility(View.VISIBLE);
        }
        h.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                points.get(position).checked = isChecked;
            }
        });

        if (focus) {
            focus = false;
            h.e_text.requestFocus();
        }
    }

    public void add()
    {
        points.add(0, new Point());
        notifyItemInserted(0);
    }
    public void delete(int index)
    {
        //if (index !=0) index--;
        points.remove(index);
        notifyItemRemoved(index);
        Log.d("myDebug", String.valueOf(index));
    }
    //int indof()

    @Override
    public PointAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_point, parent, false);
        PointAdapterViewHolder vh = new PointAdapterViewHolder(v);
        return vh;
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return points.size();
    }
}