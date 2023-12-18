package com.elihu.mlapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class Translator extends AppCompatActivity {

    private Spinner from_language_spinner, to_language_spinner;
    private Button translate_btn;
    private EditText text_to_translate_et;
    private TextView result_lbl;

    String[] fromLanguageArr = {"Arabic","Hebrew","English","Russian","Ukraine"};
    String[] toLanguageArr = {"Arabic","Hebrew","English","Russian","Ukraine"};
    String fromLanguageCode, toLanguageCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);

        from_language_spinner = findViewById(R.id.from_language_spinner);
        to_language_spinner = findViewById(R.id.to_language_spinner);
        translate_btn = findViewById(R.id.translate_btn);
        text_to_translate_et = findViewById(R.id.text_to_translate_et);
        result_lbl = findViewById(R.id.result_lbl);


        //SETUP SPINNERS
        //ADAPTER
        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.language_item, fromLanguageArr);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        from_language_spinner.setAdapter(fromAdapter);

        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.language_item, toLanguageArr);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        to_language_spinner.setAdapter(toAdapter);

        from_language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromLanguageCode = getLanguageCode(fromLanguageArr[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        to_language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLanguageCode = getLanguageCode(toLanguageArr[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        translate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result_lbl.setText("");
                if(text_to_translate_et.getText().toString().isEmpty()){
                    Toast.makeText(Translator.this, "Please type some text", Toast.LENGTH_LONG).show();
                } else {
                    TranslateText(fromLanguageCode,toLanguageCode,text_to_translate_et.getText().toString());
                }
            }
        });
    }

    private void TranslateText(String fromLanguageCode, String toLanguageCode, String query) {
        result_lbl.setText("Downloading language model...");
        try {
            TranslatorOptions options = new TranslatorOptions.Builder()
                    .setSourceLanguage(fromLanguageCode)
                    .setTargetLanguage(toLanguageCode)
                    .build();
            com.google.mlkit.nl.translate.Translator translator = Translation.getClient(options);

            DownloadConditions conditions = new DownloadConditions.Builder().build();
            translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            result_lbl.setText("Translating...");
                            translator.translate(query)
                                    .addOnSuccessListener(new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            result_lbl.setText(s);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Translator.this, "Error: " + e, Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Translator.this, "Error: " + e, Toast.LENGTH_LONG).show();
                        }
                    });


        } catch(Exception e){
            Toast.makeText(Translator.this, "Error: " + e, Toast.LENGTH_LONG).show();
        }

    }

    private String getLanguageCode(String language) {
        String code;

        switch (language){
            case "Arabic":
                code = TranslateLanguage.ARABIC;
                break;
            case "Hebrew":
                code = TranslateLanguage.HEBREW;
                break;
            case "English":
                code = TranslateLanguage.ENGLISH;
                break;
            case "Russian":
                code = TranslateLanguage.RUSSIAN;
                break;
            case "Ukraine":
                code = TranslateLanguage.UKRAINIAN;
                break;
            default:
                code = TranslateLanguage.ENGLISH;
        }

        return code;
    }
}