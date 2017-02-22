package com.example.poiz.fridgetablet.voiceRecognision;

/**
 * Created by poiz on 14.09.2016.
 */
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 *ListeningActivity Implements the functionality wich is needed to for VoiceRecognition.
 * All Activitys where Voicerecognition is needed, need ListeningActivity as Superclass
 */
public abstract class ListeningActivity extends AppCompatActivity implements IVoiceControl {

    protected SpeechRecognizer sr;
    protected Context context;
    private static final int REQUEST_INTERNET = 200;
    SharedPreferences sharedPref;

    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = this.getSharedPreferences("IP", Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
    }

    /**
     * method to start the service
     */
    protected void startListening() {

        if(sharedPref.getBoolean("voiceRec",false)) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(ListeningActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ListeningActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_INTERNET);
                }
            } else {
                initAndStart();
            }
        }


    }

    /**
     * method to stop the service
     */
    protected void stopListening() {
        if (sr != null) {
            sr.stopListening();
            sr.cancel();
            sr.destroy();
        }
        sr = null;
    }

    /**
     * onRequestPermissionsResult. Past Marschmellow, the User has to give the Permissions manuelly during the Runtime
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
  @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_INTERNET) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //start audio recording or whatever you planned to do
            }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(ListeningActivity.this, Manifest.permission.RECORD_AUDIO)) {
                    //Show an explanation to the user *asynchronously*
                    ActivityCompat.requestPermissions(ListeningActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_INTERNET);

                    initAndStart();

                }else{
                    //Never ask again and handle app without permission.
                }
            }
        }
    }

    /**
     * initAndStart service
     */
    protected void initAndStart(){
        initSpeech();

        try {

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            if (!intent.hasExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE))
            {
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                        "com.dummy");
            }
            sr.startListening(intent);
        } catch(Exception ex) {
            Log.d("SpeechRecognitionervice", "Bei der Initialisierung des SpeechRecognizers ist ein Fehler aufgereten");
        }

    }

    /**
     * Creates the Speechrecognizer
     */
    protected void initSpeech() {

        if (sr == null) {
            sr = SpeechRecognizer.createSpeechRecognizer(this);
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                Toast.makeText(context, "Speech Recognition is not available",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            sr.setRecognitionListener(VoiceRecognitionListener.getInstance());
        }
    }

    /**
     * finish
     */
    @Override
    public void finish() {
        stopListening();
        super.finish();
    }

    /**
     * onStop
     */
    @Override
    protected void onStop() {
        stopListening();
        super.onStop();
    }

    /**
     * onDestroy
     */
    @Override
    protected void onDestroy() {
        if (sr != null) {
            sr.stopListening();
            sr.cancel();
            sr.destroy();
        }
        super.onDestroy();
    }

    /**
     * onPause
     */
    @Override
    protected void onPause() {
        if(sr!=null){
            sr.stopListening();
            sr.cancel();
            sr.destroy();

        }
        sr = null;

        super.onPause();
    }

    /**
     * processVoiceCommands
     * @param voiceCommands
     */
    @Override
    public abstract void processVoiceCommands(String ... voiceCommands);

    /**
     * restartListeningService restarts the service
     */
    @Override
    public void restartListeningService() {
        stopListening();
        startListening();
    }
}
