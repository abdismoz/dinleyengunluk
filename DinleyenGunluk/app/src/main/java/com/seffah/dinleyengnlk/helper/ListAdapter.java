package com.seffah.dinleyengnlk.helper;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seffah.dinleyengnlk.R;
import com.seffah.dinleyengnlk.entity.Note;
import com.seffah.dinleyengnlk.view.NewNote;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by iabdu on 1.10.2017.
 */

public class ListAdapter extends ArrayAdapter<Note> {
    private final Context context;
    private final List<Note> notes;


    public ListAdapter(Context context, int resource, List<Note> objects) {
        super(context, resource, objects);
        this.context = context;
        notes = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Note note = notes.get(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        TextView time = (TextView) rowView.findViewById(R.id.time);
        TextView month = (TextView) rowView.findViewById(R.id.month);
        TextView day = (TextView) rowView.findViewById(R.id.day);
        TextView txt = (TextView) rowView.findViewById(R.id.note);
        if (note.isEncrypted())
            txt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        time.setText(new SimpleDateFormat("HH:mm").format(note.getTimestamp()));
        month.setText(new SimpleDateFormat("MMMM").format(note.getTimestamp()) + " " + new SimpleDateFormat("yyyy").format(note.getTimestamp()));
        day.setText(new SimpleDateFormat("dd").format(note.getTimestamp()));
        txt.setText(note.getNote());

        final Intent intent = new Intent(this.context, NewNote.class);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("NOTE", note.getId());
                context.startActivity(intent);
            }
        });
        return rowView;
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private Date getDate(String dateStr) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = fmt.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
