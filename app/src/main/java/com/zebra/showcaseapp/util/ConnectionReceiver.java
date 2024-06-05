package com.zebra.showcaseapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Chandan Jana on 14-12-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
public class ConnectionReceiver extends BroadcastReceiver {

    // initialize listener
    private NetworkConnectionListener networkConnectionListener;

    public void setNetworkConnectionListener(NetworkConnectionListener networkConnectionListener) {
        this.networkConnectionListener = networkConnectionListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("enableDisableJson : ", "ConnectionReceiver");
        if (networkConnectionListener != null) {
            networkConnectionListener.onNetworkChange(Utils.hasConnection(context));
        }
    }

    public interface NetworkConnectionListener {
        void onNetworkChange(boolean isConnected);
    }
}