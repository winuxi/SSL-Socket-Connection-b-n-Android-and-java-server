package com.ravenioet.sslsocket;

/**
 * Created by miles on 12/16/15.
 * References:
 * http://www.myandroidsolutions.com/2012/07/20/android-tcp-connection-tutorial/
 * http://adblogcat.com/ssl-sockets-android-and-server-using-a-certificate/
 */
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import javax.net.ssl.SSLSocket;

public class TCPClient {

    public SSLSocket socket = null;

    private final Home home;
    private String serverMessage;
    private static String SERVER_IP = "";
    private static int SERVER_PORT = 0;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
    private final char[] keyStorePass = "sec-android".toCharArray(); // If your keystore has a password, put it here

    public static final String TAG = "TCP Client";

    // These handle the I/O
    private PrintWriter out;

    public TCPClient(Home home, String ip, int port, OnMessageReceived listener)
    {
        this.home = home;
        SERVER_IP = ip;
        SERVER_PORT = port;
        mMessageListener = listener;
    }

    public void sendMessage(String message){
        // As of Android 4.0 we have to send to network in another thread...
        TCPMessageSendTask sender = new TCPMessageSendTask(out, message);
        sender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void stopClient(){
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            Log.d(TAG, "Connecting...");

            //create a socket to make the connection with the server

            KeyStore ks = KeyStore.getInstance("BKS");
            // Load the keystore file
            InputStream keyin = home.getResources().openRawResource(R.raw.sec_android);
            ks.load(keyin, keyStorePass);

            // Create a SSLSocketFactory that allows for self signed certs
            SSLSocketFactory socketFactory = new SSLSocketFactory(ks);
            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            // Build our socket with the factory and the server info
            socket = (SSLSocket) socketFactory.createSocket(new Socket(SERVER_IP,SERVER_PORT), SERVER_IP, SERVER_PORT, false);
            socket.startHandshake();

            try {

                // Create the message sender
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.d(TAG, "Message Sent.");

                // Create the message receiver
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Listen for the messages sent by the server, stopClient breaks this loop
                while (mRun) {
                    serverMessage = in.readLine();

                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;

                }

                Log.d(TAG, "Received Message: '" + serverMessage + "'");

            } catch (Exception e) {

                Log.e(TAG, "Server Error", e);

            } finally {
                // Close the socket after stopClient is called
                socket.close();
            }

        } catch (Exception e) {

            Log.e(TAG, "Error", e);

        }

    }

    public interface OnMessageReceived {
        void messageReceived(String message);
    }

    /**
     * A simple task for sending messages across the network.
     */
    public static class TCPMessageSendTask extends AsyncTask<Void, Void, Void> {

        private final PrintWriter out;
        private final String message;

        public TCPMessageSendTask(PrintWriter out, String message){
            this.out = out;
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... arg0){
            if (out != null && !out.checkError()) {
                try{
                    out.println(message);
                    out.flush();
                }
                catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }
            }
            return null;
        }
    }
}