package com.example.mytranslator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

    private TextInputEditText input;
    private Button button;
    private TextView output;

    private Spinner to_spinner;
    private Spinner from_spinner;
    String[] fromLanguages = {"Select from Language","Afrikaans", "Arabic", "Belarusian","Bulgarian",
            "Bengali", "Czech","English", "French","German","Gujarati","Hindi",
            "Japanese","Kannada","Russian","Tamil"};
    String[] toLanguages = {"Select to Language","Afrikaans", "Arabic", "Belarusian","Bulgarian",
            "Bengali", "Czech", "English","French","German","Gujarati","Hindi",
            "Japanese","Kannada","Russian","Tamil"};
    String languageCode, toLanguageCode, fromLanguageCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        from_spinner = findViewById(R.id.from_spinner);
        to_spinner = findViewById(R.id.to_spinner);
        input = findViewById(R.id.input);
        button = findViewById(R.id.button);
        output = findViewById(R.id.output);

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
            }
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
            case"Russian":
                languageCode = TranslateLanguage.RUSSIAN;
                break;
            case"Tamil":
                languageCode = TranslateLanguage.TAMIL;
                break;


            default:
                languageCode="";
        }
        return languageCode;
    }
}