/*
 * Copyright (C) 2021 Zebra Technologies Corporation and/or its affiliates.
 * All rights reserved.
 */
package com.zebra.showcaseapp.zdm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

public class ConnectToZDMService {
    private static final String TAG = ConnectToZDMService.class.getSimpleName();
    private static ConnectToZDMService instance = new ConnectToZDMService();
    ConditionVariable conditionVariable = new ConditionVariable(true);
    ServiceConnection mConnection = null;
    private Context mContext;
    private Messenger mService = null;

    private ConnectToZDMService() {
    }

    public static ConnectToZDMService getInstance(Context context) {
        instance.mContext = context.getApplicationContext();
        return instance;
    }

    public boolean isPackageAvailable(Context context, String packageName) {
        boolean found = true;
        PackageManager packageManager = context.getPackageManager();

        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException ex) {
            found = false;
        }

        return found;
    }

    public void bindZDM(Context context, ServiceConnectionCallBack connectionCallBack) {
        final String ZDM_PACKAGE = "com.zebra.devicemanager";
        final String ZDM_SERVICE_CLASS = "com.zebra.devicemanager.ZebraDeviceMgr";
        Intent zdmIntent = new Intent();
        zdmIntent.setComponent(new ComponentName(ZDM_PACKAGE, ZDM_SERVICE_CLASS));
        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                mService = new Messenger(service);
                Log.d(TAG, "UpdateWorker ZDM bind status: Connected");
                //conditionVariable.open();
                connectionCallBack.serviceConnected(true, mService);
            }

            public void onServiceDisconnected(ComponentName className) {
                //mService = null;
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                Log.d(TAG, "UpdateWorker ZDM bind status: Disconnected");
            }
        };
        context.bindService(zdmIntent, mConnection,
                Context.BIND_AUTO_CREATE);
    }

    public void unBindZDM() {
        try {
            mContext.unbindService(mConnection);
        } catch (Exception exception) {
            Log.e(TAG, "UpdateWorker " + exception.getMessage());
        }
        mService = null;
    }

    public void getZDMService(ServiceConnectionCallBack connectionCallBack) throws NotSupportedException, BindException {
        if (!isPackageAvailable(mContext, "com.zebra.devicemanager")) {
            throw new NotSupportedException("Zebra device manager not present");
        }

        if (mService == null || (!mService.getBinder().pingBinder() && !mService.getBinder().isBinderAlive())) {

            //conditionVariable.close();
            this.bindZDM(mContext, connectionCallBack);
            //conditionVariable.block(5000);
        }
        if (mService != null) {
            connectionCallBack.serviceConnected(true, mService);
        }
        /*if (mService == null) {
            throw new BindException("Unable to bind ZDM service");
        }
        return this.mService;*/
    }

    public interface ServiceConnectionCallBack {
        void serviceConnected(boolean connected, Messenger messenger);
    }

    public static class NotSupportedException extends Exception {
        public NotSupportedException(String msg) {
            super(msg);
        }
    }

    public static class BindException extends Exception {
        public BindException(String msg) {
            super(msg);
        }
    }
}
