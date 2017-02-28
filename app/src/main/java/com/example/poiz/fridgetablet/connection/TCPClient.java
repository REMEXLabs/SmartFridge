package com.example.poiz.fridgetablet.connection;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;


/**
 * TCPClient opens the SocketConnection. It sends the Informations wich are given by the Asynctask and Receives the answer from the client
 */
public class TCPClient{

    private static final String TAG = "TCPClient"     ;
    private String ipNumber, incomingMessage, command;
    BufferedReader in                                ;
    PrintWriter out                               ;
    private MessageCallback   listener        = null            ;
    private boolean           mRun            = false           ;
    long startTime;
    boolean localConnection = true;
    private final String USER_AGENT = "Mozilla/5.0";

    public boolean isRunning(){
        return mRun;

    }

    /**
     * constructor
     * @param command string with the taskcode and the information the server needs to solve the task
     * @param ipNumber ip of the server
     * @param listener callbacklistener
     */
    public TCPClient(String command, String ipNumber, MessageCallback listener) {
        this.listener         = listener;
        this.ipNumber         = ipNumber;
        this.command          = command ;
        startTime = System.currentTimeMillis();
    }

    /**
     * Prints the message to the stream wich is receivd by the Socket of the Server
     * @param message tring with the taskcode and the information the server needs to solve the task
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
            Log.d(TAG, "Sent Message: " + message);

        }
    }

    /**
     * Stops the Connection by stopping the while Loop
     */
    public void stopClient() {
        Log.d(TAG, "Client stopped!");
        mRun = false;
    }

    /**
     * run method, is running until the server solves the given task and answers
     */
    boolean reachable;
    public void run() throws IOException {



        if(command.substring(0,3).equals("010")|| command.substring(0,3).equals("011") || command.substring(0,3).equals("012") || command.substring(0,3).equals("014")|| command.substring(0,3).equals("014")) {

            try {

                reachable = InetAddress.getByName(ipNumber).isReachable(2000);
                Log.d(TAG, "mist");
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            mRun = true;

            try {
                // Creating InetAddress object from ipNumber passed via constructor from IpGetter class.
                InetAddress serverAddress = InetAddress.getByName(ipNumber);
                Log.d(TAG, "Connecting...");
                Socket socket = new Socket(serverAddress, 9000);


                try {

                    // Create PrintWriter object for sending messages to server.
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    //Create BufferedReader object for receiving messages from server.
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    Log.d(TAG, "In/Out created");
                    this.sendMessage(command);
                    while (mRun) {
                        incomingMessage = in.readLine();
                        if (incomingMessage != null && listener != null) {
                            if (incomingMessage.substring(0, 1).equals("0")) {
                                listener.callbackMessageReceiver(incomingMessage);
                                mRun = false;
                            }
                        }
                        incomingMessage = null;
                    }
                    Log.d(TAG, "Received Message: " + incomingMessage);
                } catch (Exception e) {

                    Log.d(TAG, "Error", e);

                } finally {

                    out.flush();
                    out.close();
                    in.close();
                    socket.close();
                    Log.d(TAG, "Socket Closed");
                }

            } catch (Exception e) {

                Log.d(TAG, "Error", e);
                localConnection = false;

            }
            Log.d("http", "time for http!1");
        }if(!command.substring(0,3).equals("010")&& !command.substring(0,3).equals("011") && !command.substring(0,3).equals("012")){
            //do httpRequest
            Log.d("http", "time for http!");
            String taskcode = "";
            String msg = "";
/*
            if(command.length()>=3){
                taskcode  = command.substring(0,3);
            }
            if(command.length()>3){

                msg = command.substring(3,command.length());
            }

          */

            Log.d("httpget", command);

            byte[] data = command.getBytes("UTF-8");
            String base64Params = Base64.encodeToString(data, Base64.NO_WRAP);
            //base64Params.replaceAll("\n", "");


            String url = "http://176.28.55.242:8080/Webserver/rest/hello/" + base64Params;
            //String url = "http://192.168.0.109:8080/Webserver/rest/hello/" + base64Params;

            Log.d("httpget", url);


            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("POST");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            Log.d("httpget","Response Code : : "+responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result

            Log.d("httpget","response: "+response.toString());

            //print result
            Log.d("httpresponse: ",response.toString());
            incomingMessage = response.toString();

            if (incomingMessage != null && listener != null) {
                if(incomingMessage.substring(0,1).equals("0")){
                    listener.callbackMessageReceiver(incomingMessage);
                    mRun = false;
                }
            }
            incomingMessage = null;
        }


    }


    /**
     * Callback interface
     */
    public interface MessageCallback {
        /**
         * Method overriden in AsyncTask 'doInBackground' method while creating the TCPClient object.
         * @param message Received message from server app.
         */
        public void callbackMessageReceiver(String message);
    }
}
