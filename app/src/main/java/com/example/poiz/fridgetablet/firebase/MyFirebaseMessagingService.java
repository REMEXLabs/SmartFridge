package com.example.poiz.fridgetablet.firebase;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Service wich listens to incoming GCM
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyGcmListenerService";
    public final static String SEND_STORED_ITEM = "storedItem";

    /**
     * Receives the GoogleMsg from the sever and Sends the Breadcast to the Activitys
     * @param message message wich has been received from client via GCM
     */
    @Override
    public void onMessageReceived (RemoteMessage message){

        String text = message.getData().toString();
        String msg = message.getData().get("message");
        String taskcode = message.getData().get("taskCode");

        int id = 0;
        Object obj = message.getData().get("id");
        if (obj != null) {
            id = Integer.valueOf(obj.toString());
        }

        //if(taskcode.equals("010"))
        {
            Intent intent = new Intent();
            intent.setAction(SEND_STORED_ITEM);
            intent.putExtra("ITEM", msg);
            sendBroadcast(intent);
        }
        Log.d(TAG, "erhaltene nachricht:  " + msg);
        //this.sendNotification(new NotificationData(image, id, title, text, sound));
    }


}
