package com.project.firetranslator.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.project.firetranslator.R;
import com.project.firetranslator.data.model.Translation;
import com.project.firetranslator.databinding.ActivityMainBinding;
import com.project.firetranslator.ui.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Boolean isSaved = false;
    private TextToSpeech mTTS;
    private MainViewModel mViewModel;
    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceText;
    private String outputText;
    private TextView translateTV;
    private Translation savedT;
    String[] fromLanguage = {"From", "English", "Afrikaans", "Arabic", "Belarusian", "Bulgarian", "Chinese" , "Czech", "French",
            "Welsh", "Hindi", "Russian", "Japanese", "Korean", "Malaysia", "Vietnamese"};
    String[] toLanguage = {"To", "Vietnamese", "Afrikaans", "Arabic", "Belarusian", "Bulgarian", "Chinese", "Czech", "French",
            "Welsh", "Hindi", "Russian", "Japanese", "Korean", "Malaysia", "English"};
    private static final int REQUEST_PERMISSION_CODE = 1;
    int  languageCode, fromLanguageCode, toLanguageCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mViewModel.init(getApplication());

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        binding.idIVSpeak.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });


        binding.idIVSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech(sourceText.getText().toString());
            }
        });

        fromSpinner = findViewById(R.id.idFromSpinner);
        toSpinner = findViewById(R.id.idToSpinner);
        sourceText = findViewById(R.id.idEditSource);
        translateTV = findViewById(R.id.idTranslatedTV);
        
        binding.idTranslatedTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech(translateTV.getText().toString());
            }
        });
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromLanguageCode = getLanguageCode(fromLanguage[i]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.spinner_item, fromLanguage);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLanguageCode = getLanguageCode(toLanguage[i]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.spinner_item, toLanguage);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        binding.idIVMic.setOnClickListener(new View.OnClickListener() { //
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something to translate");
                try {
                    startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        binding.idBtnTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateTV.setVisibility(View.VISIBLE);
                translateTV.setText("");
                binding.ivStar.setVisibility(View.GONE);
                if (sourceText.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter text to translate", Toast.LENGTH_SHORT).show();
                }else if (fromLanguageCode == 0){
                    Toast.makeText(MainActivity.this, "Please select Source Language", Toast.LENGTH_SHORT).show();
                }else if (toLanguageCode == 0){
                    Toast.makeText(MainActivity.this, "Please select the language to make translation", Toast.LENGTH_SHORT).show();
                }else {
                    translateText(fromLanguageCode, toLanguageCode,sourceText.getText().toString());
                }
            }
        });
        createEventForStarOnClick();
        showSavedList();
        binding.btnSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Show Saved Translations Activity
                Intent intent = new Intent(MainActivity.this, SavedActivity.class);
                startActivity(intent);
            }
        });
    }
    private void createEventForStarOnClick(){
        binding.ivStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText = sourceText.getText().toString();
                Translation t = new Translation(inputText, outputText);

                if(sourceText.getText().toString().isEmpty()) {
                    binding.ivStar.setVisibility(View.GONE);
                }
                if(isSaved){
                    if (savedT != null){
                        Log.d("Activity", "remove " + String.valueOf(savedT));
                        mViewModel.removeTranslation(savedT);
                    }
                    isSaved = false;
                    binding.ivStar.setImageResource(R.drawable.ic_baseline_star_outline_24);

                } else{

                    mViewModel.insertTranslation(t);
                    savedT = t;
                    isSaved = true;
                    binding.ivStar.setImageResource(R.drawable.ic_baseline_star_24);
                }

            }
        });
    }
    private void showSavedList(){
        mViewModel.getListTranslations().observe(this, new Observer<List<Translation>>() {
            @Override
            public void onChanged(List<Translation> translations) {
                Log.d("Activity", String.valueOf(translations));
            }
        });
    }

    private void textToSpeech(String text) {
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source) {
        translateTV.setText("Downloading model, please wait...");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();

        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translateTV.setText("Translation..");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        // IMPORTANT!!!
                        outputText  = s;
                        translateTV.setText(outputText);
                        binding.ivStar.setVisibility(View.VISIBLE);
                        Single<Translation> temp = mViewModel.findTranslation(sourceText.getText().toString());
                        temp.subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableSingleObserver<Translation>() {
                                    @Override
                                    public void onSuccess(@NonNull Translation translation) {
                                        binding.ivStar.setImageResource(R.drawable.ic_baseline_star_24);
                                        savedT = translation;
                                        isSaved = true;
                                    }
                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        binding.ivStar.setImageResource(R.drawable.ic_baseline_star_outline_24);
                                        savedT = null;
                                        isSaved = false;
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.ivStar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Failed to translate!! try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.ivStar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Failed to download model!! Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_CODE){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            sourceText.setText(result.get(0));
        }
    }

    private int getLanguageCode(String language) {
        switch (language){
            case "English":
                languageCode = FirebaseTranslateLanguage.EN;
                break;
            case "Afrikaans":
                languageCode = FirebaseTranslateLanguage.AF;
                break;
            case "Arabic":
                languageCode = FirebaseTranslateLanguage.AR;
                break;
            case "Belarusian":
                languageCode = FirebaseTranslateLanguage.BE;
                break;
            case "Bulgarian":
                languageCode = FirebaseTranslateLanguage.BG;
                break;
            case "Chinese":
                languageCode = FirebaseTranslateLanguage.ZH;
                break;
            case "Czech":
                languageCode = FirebaseTranslateLanguage.CS;
                break;
            case "Welsh":
                languageCode = FirebaseTranslateLanguage.CY;
                break;
            case "Russian":
                languageCode = FirebaseTranslateLanguage.RU;
                break;
            case "Japanese":
                languageCode = FirebaseTranslateLanguage.JA;
                break;
            case "Korean":
                languageCode = FirebaseTranslateLanguage.KO;
                break;
            case "Malaysia":
                languageCode = FirebaseTranslateLanguage.MS;
                break;
            case "Vietnamese":
                languageCode = FirebaseTranslateLanguage.VI;
                break;

            default:
                languageCode = 0;
        }
        return languageCode;
    }
}
