package com.example.poiz.fridgetablet.connection;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.poiz.fridgetablet.activitys.MainActivity;
import com.example.poiz.fridgetablet.interfaces.AsyncResponse;

import java.io.IOException;

/**
 * AsyncTask wich gets executet when data is send via the SecketConnection
 * an AsyncTask is needed because Networkingoperations cant run in the Mainthread
 */
public class SocketClientAsyncTask extends AsyncTask<String, String, TCPClient>{
    //private  String  massage     = ""      ;
    public TCPClient  tcpClient;
    private String ip = "192.168.0.62";
    private static final String TAG  = "SocketClientAsyncTask";
    private Context context = null;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public AsyncResponse delegate = null;


    /**
     * Constructor
     * @param delegate what started the asynctask
     * @param c context of the view wich started the task
     */
    public SocketClientAsyncTask(AsyncResponse delegate,Context c){


        this.delegate = delegate;
        context = c;
        getIpFromSharedPrefernces();


    }

    /**
     * Gets the ip from The SHaredPreferemces wich is needed to connect to the Server
     */
    public void getIpFromSharedPrefernces(){

        sharedPref = context.getSharedPreferences("IP", Context.MODE_PRIVATE);
        sharedPref.getInt("ip1",0);
        ip = sharedPref.getInt("ip1",0)+"."+sharedPref.getInt("ip2",0)+"."+sharedPref.getInt("ip3",0)+"."+sharedPref.getInt("ip4",0);


    }


    /**
     * doInBackground method of the Asynctask
     * it creates an Object of tcpClient wich connects via Socketconnection to the server
     * @param params
     * @return a objcet of the tcpClient wich was created to connect to the server
     */
    @Override
    protected TCPClient doInBackground(String... params) {
        Log.d(TAG, "asyncmsg In do in background "+params[0]);

        try{
            tcpClient = new TCPClient(params[0],ip, new TCPClient.MessageCallback() {

                        @Override
                        public void callbackMessageReceiver(String message) {
                            publishProgress(message);
                        }
                    });

        }catch (NullPointerException e){
            Log.d(TAG, "Caught null pointer exception");
            e.printStackTrace();
        }
        try {
            tcpClient.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tcpClient;
    }

    /**
     * delegates the interfaces Methods of the calls wich started the Asynctask
     * @param values the string which contains the taskcode and the from the server needed informations to solve the task
     */
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "In progress update, values: " + values[0].toString());

        for(int i = 0; i<values.length;i++){
              delegate.processFinish(values[0].toString());

        }

        tcpClient.stopClient();
    }

    /**
     * gets called when the Asynctask is started
     * @param result
     */
    @Override
    protected void onPostExecute(TCPClient result){
        super.onPostExecute(result);
        Log.d(TAG, "asyncmsg In on post execute");
        if(result != null){
           // result.stopClient();
        }
    }
}
