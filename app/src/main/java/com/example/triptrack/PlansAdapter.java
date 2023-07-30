package com.example.triptrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PlansAdapter extends ArrayAdapter<Plans> {

    public PlansAdapter(Context context, ArrayList<Plans> plans) {
        super(context, 0, plans);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Plans plan = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_plan, parent, false);
        }

        TextView timeTextView = convertView.findViewById(R.id.timeTextView);
        TextView locationTextView = convertView.findViewById(R.id.locationTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        ImageView priorityImageView = convertView.findViewById(R.id.priorityImageView);

        timeTextView.setText(plan.getTime());
        locationTextView.setText(plan.getLocation());
        descriptionTextView.setText(plan.getDescription());

        switch (plan.getPriority()) {
            case 1:
                priorityImageView.setImageResource(R.drawable.red_dot);
                break;
            case 2:
                priorityImageView.setImageResource(R.drawable.yellow_dot);
                break;
            case 3:
                priorityImageView.setImageResource(R.drawable.green_dot);
                break;
        }

        return convertView;
    }
}
