package com.example.challange2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

public class NetworkConnectivityListener {

    private Context context;
    private ConnectivityManager connectivityManager;

    public NetworkConnectivityListener(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void registerNetworkCallback(ConnectivityManager.NetworkCallback callback) {
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(request, callback);
    }

    public void unregisterNetworkCallback(ConnectivityManager.NetworkCallback callback) {
        connectivityManager.unregisterNetworkCallback(callback);
    }
}
