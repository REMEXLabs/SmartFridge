package com.example.poiz.fridgetablet.firebase;

/**
 * Created by poiz on 25.08.2016.
 */
import android.content.SharedPreferences;
import android.util.Log;

import com.example.poiz.fridgetablet.util.SendToken;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Class to Create GCMTokens wich are needed by the Server to send a GoogleMSG to the Specific Client
        */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static String TAG = "Registration";
    private static String MY_PREFS_NAME = "GCM";
    SendToken sTk;

    /**
     * gets a new Token if needed
     */
    @Override
    public void onTokenRefresh() {
        sTk = new SendToken();
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        System.out.println("Registration.onTokenRefresh TOKEN: " + refreshedToken );
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("GCMTOKEN", refreshedToken);
        editor.commit();
        sTk.sendGCMToke(refreshedToken);
    }


}
