package com.example.myworld.backendservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectionReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null &&activeNetwork.isConnectedOrConnecting();

        if(connectivityReceiverListener!=null ){
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }

    //create on method to check manually eg click on button
    public static boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) ManualChecker
                                    .getInstance()
                                    .getApplicationContext()
                                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork!=null && activeNetwork.isConnected();
    }

    //create an interface
    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
