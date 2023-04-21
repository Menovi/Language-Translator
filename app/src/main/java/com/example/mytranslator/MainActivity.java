package com.example.mytranslator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText input;
    private Button button;
    private TextView output;

    private Spinner to_spinner;
    private Spinner from_spinner;
    private ImageView speak_btn, sound_btn;
    private TextToSpeech textToSpeech;
    String[] fromLanguages = {"English","Afrikaans", "Arabic", "Belarusian","Bulgarian",
            "Bengali", "Chinese", "Czech", "French","German","Gujarati","Hindi",
            "Japanese","Kannada", "Marathi","Russian","Tamil", "Telugu", "Urdu"};
    String[] toLanguages = {"Select Language","Afrikaans", "Arabic", "Belarusian","Bulgarian",
            "Bengali", "Chinese", "Czech", "English","French","German","Gujarati","Hindi",
            "Japanese","Kannada", "Marathi", "Russian","Tamil", "Telugu", "Urdu"};
    String languageCode, toLanguageCode, fromLanguageCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //
        from_spinner = findViewById(R.id.from_spinner);
        to_spinner = findViewById(R.id.to_spinner);
        input = findViewById(R.id.input);
        button = findViewById(R.id.button);
        output = findViewById(R.id.output);
        speak_btn =findViewById(R.id.speak_btn);
        sound_btn = findViewById(R.id.sound_btn);

        //from spinner
        from_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromLanguageCode = getLanguageCode(fromLanguages[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter fromAdapter = new ArrayAdapter(this,R.layout.spinner_item,fromLanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        from_spinner.setAdapter(fromAdapter);


        //from spinner
        to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLanguageCode = getLanguageCode(toLanguages[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(this,R.layout.spinner_item,toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        to_spinner.setAdapter(toAdapter);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(input.getText().toString())){
                    Toast.makeText(MainActivity.this, "Enter Text to Translate", Toast.LENGTH_SHORT).show();
                }
                else{
                    TranslatorOptions options = new TranslatorOptions.Builder()
                            .setTargetLanguage(toLanguageCode)
                            .setSourceLanguage(fromLanguageCode).build();

                    Translator translator = Translation.getClient(options);


                    String sourceText = input.getText().toString();

                    //Download progress dialog
                    ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Downloading the Translation Model...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });


                    Task<String> result = translator.translate(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            output.setText(s);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,"Model Not Found" , Toast.LENGTH_SHORT).show(); //e.getMessage().toString()
                        }
                    });
                }

                // Hide the keyboard when translate button is clicked
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });


        //Speech to text
        speak_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Speak(view);
            }
        });

        //Text to Speech
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // TextToSpeech engine initialization successful
                    //textToSpeech.setLanguage(new Locale(""));
                    int result = textToSpeech.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // TextToSpeech engine initialization failed
                    Toast.makeText(MainActivity.this, "TextToSpeech engine initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sound_btn.setOnClickListener(v -> {
            String text = output.getText().toString();
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        });

    }
    public String getLanguageCode(String language) {
        switch(language){

            case"Afrikaans":
                languageCode = TranslateLanguage.AFRIKAANS;
                break;
            case"Arabic":
                languageCode = TranslateLanguage.ARABIC;
                break;
            case"Belarusian":
                languageCode = TranslateLanguage.BELARUSIAN;
                break;
            case"Bulgarian":
                languageCode = TranslateLanguage.BULGARIAN;
                break;
            case"Bengali":
                languageCode = TranslateLanguage.BENGALI;
                break;
            case"Chinese":
                languageCode = TranslateLanguage.CHINESE;
                break;
            case"Czech":
                languageCode = TranslateLanguage.CZECH;
                break;
            case"English":
                languageCode = TranslateLanguage.ENGLISH;
                break;
            case"French":
                languageCode = TranslateLanguage.FRENCH;
                break;
            case"German":
                languageCode = TranslateLanguage.GERMAN;
                break;
            case"Gujarati":
                languageCode = TranslateLanguage.GUJARATI;
                break;
            case"Hindi":
                languageCode = TranslateLanguage.HINDI;
                break;
            case"Japanese":
                languageCode = TranslateLanguage.JAPANESE;
                break;
            case"Kannada":
                languageCode = TranslateLanguage.KANNADA;
                break;
            case"Marathi":
                languageCode = TranslateLanguage.MARATHI;
                break;
            case"Russian":
                languageCode = TranslateLanguage.RUSSIAN;
                break;
            case"Tamil":
                languageCode = TranslateLanguage.TAMIL;
                break;
            case"Telugu":
                languageCode = TranslateLanguage.TELUGU;
                break;
            case"Urdu":
                languageCode = TranslateLanguage.URDU;
                break;

            default:
                languageCode="";
        }
        return languageCode;
    }


    //toolbar menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //Speech to text
    private void Speak(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, fromLanguageCode);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...");
        startActivityForResult(intent, 111);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==111 && resultCode==RESULT_OK){
            input.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }
    }


    //Text to Speech
    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

}