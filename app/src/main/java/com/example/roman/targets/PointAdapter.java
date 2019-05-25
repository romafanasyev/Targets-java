
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
        card = new Card();
        card.id = MainActivity.db.cardTableSize();
        card.type = Card.TYPE_LIST;
    }
    PointAdapter(boolean editMode, Card card, ArrayList<Integer> pointList)
    {
        this.editMode = editMode;
        this.card = card;
        for (int i = 0; i < pointList.size(); i++)
        {
            points.add(findPointById(pointList.get(i)));
        }
    }
    private Point findPointById(int id)
    {
        for (int i=0; i<MainActivity.allPointsList.size(); i++)
        {
            if (MainActivity.allPointsList.get(i).id == id)
                return MainActivity.allPointsList.get(i);
        }
        return null;
    }

    public static class PointAdapterViewHolder extends RecyclerView.ViewHolder {
        public MaterialCheckBox check;
        public TextView text;
        public EditText e_text;
        ImageButton backspace, drag;

        public PointAdapterViewHolder(View v) {
            super(v);
            check = v.findViewById(R.id.point_check);
            text = v.findViewById(R.id.point_text_view);
            e_text = v.findViewById(R.id.point_edit_text);
            backspace = v.findViewById(R.id.backspace);
            drag = v.findViewById(R.id.drag_button);
        }
    }
    boolean editMode;
    private ArrayList<Point> points = new ArrayList<>();
    boolean focus = true;
    Card card;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final PointAdapterViewHolder h, final int position) {
        h.e_text.setText(points.get(h.getAdapterPosition()).text);
        h.text.setText(points.get(h.getAdapterPosition()).text);
        h.check.setChecked(points.get(h.getAdapterPosition()).checked);

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
                    points.get(position).save();
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

            h.backspace.setVisibility(View.GONE);
            h.drag.setVisibility(View.GONE);

        }
        h.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                points.get(position).checked = isChecked;
                points.get(position).save();
            }
        });

        if (focus) {
            focus = false;
            h.e_text.requestFocus();
        }
    }

    public void add()
    {
        Point point = new Point();
        points.add(0, point);
        card.points.add(point.id);
        MainActivity.db.editCard(card);
        notifyItemInserted(0);
    }
    public void delete(int index)
    {
        notifyItemRemoved(index);
        points.get(index).delete();
        card.points.remove(index);
        MainActivity.db.editCard(card);
        points.remove(index);
    }
    public ArrayList<Integer> getPointsIds()
    {
        ArrayList<Integer> res = new ArrayList<>();
        for (Point p : points)
        {
            res.add(p.id);
        }
        return res;
    }

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