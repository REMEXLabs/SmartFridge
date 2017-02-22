package com.example.poiz.fridgetablet.voiceRecognision;


import java.util.ArrayList;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
/**
 *
 */
public class VoiceRecognitionListener implements RecognitionListener {

    private static VoiceRecognitionListener instance = null;

    IVoiceControl listener;

    /**
     * VoiceRecognitionListener singelton
     * @return
     */
    public static VoiceRecognitionListener getInstance() {
        if (instance == null) {
            instance = new VoiceRecognitionListener();
        }
        return instance;
    }

    /**
     * constructor
     */
    private VoiceRecognitionListener() { }

    /**
     * sets the listener
     * @param listener
     */
    public void setListener(IVoiceControl listener) {
        this.listener = listener;
    }

    /**
     * processVoiceCommands
     * @param voiceCommands
     */
    public void processVoiceCommands(String... voiceCommands) {
        listener.processVoiceCommands(voiceCommands);
    }

    /**
     * gets executet if voicevommands were found
     * @param data
     */
    public void onResults(Bundle data) {
        ArrayList matches = data.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String[] commands = new String[matches.size()];

        for(int i = 0;i<matches.size();i++){
            commands[i] = matches.get(i).toString();
        }


        processVoiceCommands(commands);
    }

    /**
     * gets executed if user starts speaking
     */
    public void onBeginningOfSpeech() {
        System.out.println("Starting to listen");
    }

    /**
     * onBufferReceived
     * @param buffer
     */
    public void onBufferReceived(byte[] buffer) { }

    /**
     *gets executed if user stops speaking
     */
    public void onEndOfSpeech() {
        System.out.println("Waiting for result...");
    }

    /**
     * Error
     * @param error
     */
    public void onError(int error) {
        if (listener != null) {
            listener.restartListeningService();
        }
    }

    /**
     * onEvent
     * @param eventType
     * @param params
     */
    public void onEvent(int eventType, Bundle params) { }

    /**
     * onPartialResults
     * @param partialResults
     */
    public void onPartialResults(Bundle partialResults) { }

    /**
     * onReadyForSpeech
     * @param params
     */
    public void onReadyForSpeech(Bundle params) { }

    /**
     * onRmsChanged
     * @param rmsdB
     */
    public void onRmsChanged(float rmsdB) { }
}
