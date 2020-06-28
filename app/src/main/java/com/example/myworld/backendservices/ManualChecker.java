package com.example.myworld.backendservices;

import android.app.Application;

public class ManualChecker extends Application {
    private static ManualChecker manualChecker;

    @Override
    public void onCreate() {
        super.onCreate();
        manualChecker = this;
    }

    public static synchronized ManualChecker getInstance() {
        return manualChecker;
    }

    public void setConnectivityListener(ConnectionReceiver.ConnectivityReceiverListener listener) {
        ConnectionReceiver.connectivityReceiverListener = listener;
    }
}
