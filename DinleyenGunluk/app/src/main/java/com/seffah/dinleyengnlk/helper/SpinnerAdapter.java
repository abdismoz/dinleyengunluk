package com.seffah.dinleyengnlk.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seffah.dinleyengnlk.R;
import java.util.List;

/**
 * Created by iabdu on 1.10.2017.
 */

public class SpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> items;


    public SpinnerAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.spinner_layout, parent, false);
        TextView txt = (TextView) rowView.findViewById(R.id.header);
        txt.setText(items.get(position));
        return txt;
    }
}
