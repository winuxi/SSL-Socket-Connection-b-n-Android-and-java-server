package com.ravenioet.sslsocket;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ravenioet.sslsocket.databinding.HomeBinding;

public class Home extends Fragment {

    private HomeBinding binding;
    public static final int SERVER_PORT = 9999;
    public static final String SERVER_IP = "192.168.8.102"; //"10.42.0.1"; // In case I have to use a hotspot


    private SocketListener connectionManager;

    public static final String TAG = "TCP Client";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = HomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Connect to the server
        connectionManager = new SocketListener(this);
        connectionManager.execute("thanks");
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = String.valueOf(binding.ipValue.getText());
                String port = String.valueOf(binding.portValue.getText());
                String message = String.valueOf(binding.message.getText());
                if(Patterns.IP_ADDRESS.matcher(ip).matches()){
                    binding.ipLay.setError(null);
                    String server;
                    if(!port.equals("")) {
                        binding.portLay.setError(null);
                        //server = ip+":"+port;
                        if(!message.equals("")){
                            binding.messageLay.setError(null);
                            try{
                                if (connectionManager.mTcpClient != null) {
                                    if(connectionManager.mTcpClient.socket.isConnected())
                                        connectionManager.mTcpClient.sendMessage(message);
                                }

                                // Append the message we sent to our output log
                                String previousOutput = binding.response.getText().toString();
                                previousOutput =previousOutput + "\r\n"+  "client: "+message;
                                binding.response.setText(previousOutput);
                                // Clear the input field
                                binding.message.setText("");
                            }
                            catch (Exception e) {
                                Log.e(TAG, "Error sending: "+ e.getMessage());
                            }
                        }else {
                            binding.messageLay.setError("Please write something");
                            Toast.makeText(getContext(),"Please use valid Port number",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        server = ip;
                        binding.portLay.setError("Please use valid Port number");
                        //Toast.makeText(getContext(),"Please use valid Port number",Toast.LENGTH_LONG).show();
                    }
                }else {
                    binding.ipLay.setError("Please use valid IP Address");
                    //Toast.makeText(getContext(),"Please use valid IP Address",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void appendServerMessageToLog(String message){
        // Append this new message to our log TextView
        String outputLogText = binding.response.getText().toString();
        outputLogText = outputLogText + "\r\n"+ "server: " + message;
        binding.response.setText(outputLogText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}