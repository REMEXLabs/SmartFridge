package com.example.poiz.fridgetablet.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.poiz.fridgetablet.connection.TCPClient;

import java.io.IOException;

/**
 * Created by poiz on 25.08.2016.
 */

/**
 * sends the GCMToken to the server
 */
public class SendToken {
    private static String MY_PREFS_NAME = "GCM";

    /**
     * sends the GCMToken to the server
     */
    public void sendGCMToke(String token){
        TCPClient tcpClient = new TCPClient("012"+token,"192.168.0.62", new TCPClient.MessageCallback() {

            @Override
            public void callbackMessageReceiver(String message) {

            }
        });

        try {
            tcpClient.run();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
