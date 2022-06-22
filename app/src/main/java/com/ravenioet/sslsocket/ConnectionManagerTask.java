package com.ravenioet.sslsocket;

import android.os.AsyncTask;

public class ConnectionManagerTask extends AsyncTask<String, String, TCPClient> {

    private Home mainActivity;
    public TCPClient mTcpClient;

    public ConnectionManagerTask(Home mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected TCPClient doInBackground(String... message) {

        mTcpClient = new TCPClient(mainActivity, Home.SERVER_IP, Home.SERVER_PORT,
                this::publishProgress
        );
        mTcpClient.run();

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        mainActivity.appendServerMessageToLog(values[0]);
    }
}
