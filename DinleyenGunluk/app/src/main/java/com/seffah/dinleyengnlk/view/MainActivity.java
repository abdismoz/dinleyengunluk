package com.seffah.dinleyengnlk.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.seffah.dinleyengnlk.R;
import com.seffah.dinleyengnlk.entity.Note;
import com.seffah.dinleyengnlk.helper.DBHelper;
import com.seffah.dinleyengnlk.helper.ListAdapter;
import com.seffah.dinleyengnlk.helper.SpinnerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ListView lv;
    private Spinner month;
    private Spinner year;
    private EditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sh = getSharedPreferences("pass", MODE_PRIVATE);
        if(!sh.contains("passwd") ){
            Intent intent = new Intent(this, Sign.class);
            startActivity(intent);
        }
        lv = (ListView) findViewById(R.id.list);
        search = (EditText)findViewById(R.id.search);
        year = (Spinner) findViewById(R.id.year);
        month = (Spinner) findViewById(R.id.month);
        ArrayList<String> years = new ArrayList<>();
        for (int i = 1999; i < 2100; i++) {
            years.add(i+"");
        }
        ArrayList<String> months = new ArrayList<>();
        String s[] = getResources().getStringArray(R.array.months);
        for (int i = 1; i < 13; i++) {
            months.add(s[i - 1]);
        }
        year.setAdapter(new SpinnerAdapter(this, R.layout.spinner_layout, years));
        month.setAdapter(new SpinnerAdapter(this, R.layout.spinner_layout, months));
        Date date = new Date();
        month.setSelection(Integer.parseInt(new SimpleDateFormat("MM").format(date))-1);
        year.setSelection(Integer.parseInt(new SimpleDateFormat("yyyy").format(date))-1999);
        refreshList();
        final Intent intent = new Intent(this, NewNote.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        FloatingActionButton pass = (FloatingActionButton) findViewById(R.id.set);
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Sign.class));
            }
        });

        ImageButton done = (ImageButton)findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshList();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList(){
        String text = search.getText().toString();
        String y = year.getSelectedItem().toString();
        int m = month.getSelectedItemPosition() +1;
        String s = m < 10 ? "0" +m : ""+m;
        ArrayList<Note> notes = new DBHelper(this).getAllNotes(text,s,y);
        ListAdapter la = new ListAdapter(this, R.layout.row_layout,notes);
        lv.setAdapter(la);
    }
}
