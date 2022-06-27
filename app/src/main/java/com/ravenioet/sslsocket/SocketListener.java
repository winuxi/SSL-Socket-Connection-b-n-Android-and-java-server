package com.ravenioet.sslsocket;

import android.os.AsyncTask;

public class SocketListener extends AsyncTask<String, String, TCPClient> {

    private final Home home;
    public TCPClient mTcpClient;

    public SocketListener(Home home) {
        this.home = home;
    }

    @Override
    protected TCPClient doInBackground(String... message) {

        mTcpClient = new TCPClient(home, Home.SERVER_IP, Home.SERVER_PORT,
                this::publishProgress
        );
        mTcpClient.run();

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        home.appendServerMessageToLog(values[0]);
    }
}
