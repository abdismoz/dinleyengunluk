package com.seffah.dinleyengnlk.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.seffah.dinleyengnlk.R;
import com.seffah.dinleyengnlk.entity.Note;
import com.seffah.dinleyengnlk.helper.CryptoHelper;
import com.seffah.dinleyengnlk.helper.DBHelper;

import java.util.ArrayList;
import java.util.Locale;

public class NewNote extends AppCompatActivity implements RecognitionListener {
    private final int REQUEST_RECORD_PERMISSION = 100;
    private int REQ_CODE_SPEECH_INPUT = 100;
    private EditText txtSpeechInput;
    private View btnSpeak;
    private boolean tryAgain;
    private ImageButton btnSave;
    private ImageButton btnDel;
    private ImageButton btnEnc;
    private ImageButton bt;
    private DBHelper dbHelper;
    private Note note;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);
        dbHelper = new DBHelper(this);
        long noteId = getIntent().getIntExtra("NOTE", 0);
        note = noteId != 0 ? dbHelper.getNote(noteId) : new Note();
        tryAgain = true;
        initViews();
        initListener();
        if (noteId != 0) {
            txtSpeechInput.setText(note.getNote());
        } else {
            note.setEncrypted(false);
        }
        btnSave.setEnabled(false);
        btnEnc.setEnabled(txtSpeechInput.getText().length() > 0);
    }

    private void initListener() {
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        String language = getResources().getConfiguration().locale.toString();
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
//        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000);
//        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10000);
//        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, language);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, language);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_RESULTS, language);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        txtSpeechInput = (EditText) findViewById(R.id.speechInput);
        txtSpeechInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(txtSpeechInput.getText().length() > 0){
                    btnSave.setEnabled(true);
                    btnEnc.setEnabled(true);
                }else{
                    btnSave.setEnabled(false);
                    btnEnc.setEnabled(false);
                }
            }
        });
        btnSpeak = findViewById(R.id.fab);
        btnSave = (ImageButton) findViewById(R.id.save);
        btnEnc = (ImageButton) findViewById(R.id.encrypt);
        btnDel = (ImageButton) findViewById(R.id.del);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);
        if (note.isEncrypted()) {
            btnEnc.setImageResource(R.drawable.lock_button);
            txtSpeechInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnSpeak.setVisibility(View.GONE);
            txtSpeechInput.setEnabled(false);
        }
        btnEnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sh = getSharedPreferences("pass", MODE_PRIVATE);
                if (!sh.contains("passwd")) {
                    Intent intent = new Intent(NewNote.this, Sign.class);
                    startActivity(intent);
                } else {
                    final SharedPreferences prefs = getSharedPreferences("PrefsPreferences", MODE_PRIVATE);

                    AlertDialog.Builder builder = new AlertDialog.Builder(NewNote.this);
                    builder.setMessage(getString(R.string.pass_req));
                    builder.setCancelable(false);

                    final EditText input = new EditText(NewNote.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //My code
                            if (sh.getString("passwd", null).equals(CryptoHelper.MD5(input.getText().toString()))) {
                                try {
                                    txtSpeechInput.setText(CryptoHelper.encDEC(note.isEncrypted(), txtSpeechInput.getText().toString()));
                                    if (note.isEncrypted()) {
                                        note.setEncrypted(false);
                                        btnEnc.setImageResource(R.drawable.unlock_button);
                                        txtSpeechInput.setInputType(InputType.TYPE_CLASS_TEXT);
                                        txtSpeechInput.setEnabled(true);
                                        btnSpeak.setVisibility(View.VISIBLE);
                                    } else {
                                        note.setEncrypted(true);
                                        btnEnc.setImageResource(R.drawable.lock_button);
                                        txtSpeechInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        txtSpeechInput.setEnabled(false);
                                        btnSpeak.setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else{
                                Toast.makeText(NewNote.this, getString(R.string.pass_cur), Toast.LENGTH_LONG).show();
                            }
                        }
                    })

                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();

                    alert.show();
                }
            }
        });

        btnSpeak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (tryAgain) {
                            startListening();
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (!tryAgain) {
                            stopListening();
                        }
                        return true;
                }
                return false;
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (note.getId() == 0)
                    dbHelper.insertNote(txtSpeechInput.getText().toString(), note.isEncrypted());
                else {
                    note.setNote(txtSpeechInput.getText().toString());
                    dbHelper.updateNote(note);
                }
                goBack();
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(NewNote.this)
                        .setMessage(R.string.del_confirm)
                        .setIcon(R.drawable.del)
                        .setPositiveButton(R.string.del_note, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (note.getId() == 0)
                                    goBack();
                                else {
                                    dbHelper.deleteNote(note);
                                    goBack();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, null).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (btnSave.isEnabled()) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.exit_confirm)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            goBack();
                        }

                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        } else
            goBack();
    }

    private void startListening() {
        tryAgain = false;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        ActivityCompat.requestPermissions
                (NewNote.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);
    }

    private void stopListening() {
        tryAgain = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
        speech.stopListening();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                }
        }
    }

    public void goBack() {
        this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
        }
    }


    @Override
    public void onBeginningOfSpeech() {
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
        progressBar.setIndeterminate(true);
        tryAgain = true;
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
    }

    @Override
    public void onPartialResults(Bundle arg0) {
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = matches.get(0);
        int start = Math.max(txtSpeechInput.getSelectionStart(), 0);
        int end = Math.max(txtSpeechInput.getSelectionEnd(), 0);
        txtSpeechInput.getText().replace(Math.min(start, end), Math.max(start, end),
                text, 0, text.length());
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        progressBar.setProgress((int) rmsdB);
    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = this.getResources().getString(R.string.ERROR_AUDIO);
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = this.getResources().getString(R.string.ERROR_CLIENT);
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = this.getResources().getString(R.string.ERROR_INSUFFICIENT_PERMISSIONS);
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = this.getResources().getString(R.string.ERROR_NETWORK);
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = this.getResources().getString(R.string.ERROR_NETWORK_TIMEOUT);
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = this.getResources().getString(R.string.ERROR_NO_MATCH);
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = this.getResources().getString(R.string.ERROR_RECOGNIZER_BUSY);
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = this.getResources().getString(R.string.ERROR_SERVER);
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = this.getResources().getString(R.string.ERROR_SPEECH_TIMEOUT);
                break;
            default:
                message = this.getResources().getString(R.string.ERROR_NO_MATCH);
                break;
        }
        return message;
    }
}
