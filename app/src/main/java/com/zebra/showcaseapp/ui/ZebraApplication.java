package com.zebra.showcaseapp.ui;

import android.app.Application;

/**
 * Created by Chandan Jana on 01-03-2023.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
public class ZebraApplication extends Application {
    private static ZebraApplication mInstance;

    public static ZebraApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
