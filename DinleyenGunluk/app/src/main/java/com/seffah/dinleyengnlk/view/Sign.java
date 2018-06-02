package com.seffah.dinleyengnlk.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.seffah.dinleyengnlk.R;
import com.seffah.dinleyengnlk.helper.CryptoHelper;

public class Sign extends AppCompatActivity {

    private EditText pass;
    private EditText pass2;
    private EditText pass_curr;
    private TextView message;

    private Button ok;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        pass = (EditText) findViewById(R.id.pass);
        pass2 = (EditText) findViewById(R.id.pass2);
        ok = (Button) findViewById(R.id.button_ok);
        cancel = (Button) findViewById(R.id.button_cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sh = getSharedPreferences("pass", MODE_PRIVATE);
                sh.edit().putString("passwd", CryptoHelper.MD5(pass.getText().toString())).apply();
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextWatcher tw = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                ok.setEnabled(passMatches() && oldPasswordTrue());
            }
        };
        pass.addTextChangedListener(tw);
        pass2.addTextChangedListener(tw);
        SharedPreferences sh = getSharedPreferences("pass", MODE_PRIVATE);
        if (sh.getString("passwd", null) == null) {
            findViewById(R.id.pass_curr).setVisibility(View.GONE);
        } else {
            findViewById(R.id.desc).setVisibility(View.GONE);
        }

        pass_curr = (EditText) findViewById(R.id.current);
        pass_curr.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                ok.setEnabled(oldPasswordTrue() && passMatches());
            }
        });
        message = (TextView) findViewById(R.id.msg);
    }

    private boolean passMatches() {
        boolean result = pass.getText().length() > 0 && pass2.getText().length() > 0 && pass.getText().toString().equals(pass2.getText().toString());
        message.setText(result ? R.string.empty : (pass.getText().length() == 0 && pass2.getText().length() == 0 ? R.string.pass2_req : R.string.pass_match));
        return result;
    }

    private boolean oldPasswordTrue() {
        SharedPreferences sh = getSharedPreferences("pass", MODE_PRIVATE);
        String s = sh.getString("passwd", null);
        boolean result = s == null ? true : s.equals(CryptoHelper.MD5(pass_curr.getText().toString()));
        message.setText(result ? R.string.empty : (pass_curr.getText().length() == 0 ? R.string.pass_req : R.string.pass_old_false));
        return result;
    }
}
