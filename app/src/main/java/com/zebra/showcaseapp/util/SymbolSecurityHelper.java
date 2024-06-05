/*
 * Copyright (C) 2021 Zebra Technologies Corporation and/or its affiliates.
 * All rights reserved.
 */
package com.zebra.showcaseapp.util;

import android.content.Context;
import android.util.Log;

import com.zebra.symbolsecurity.TrustedDevice;


public class SymbolSecurityHelper {
    private final String TAG = SymbolSecurityHelper.class.getCanonicalName();

    public boolean isTrustedDevice(Context context) {
        boolean trustedDevice = false;

        try {
            TrustedDevice aTrusted = new TrustedDevice();
            trustedDevice = aTrusted.isCallerNotAGoat(context);

        } catch (Exception t) {
            Log.e(TAG, "Error occurred while authenticating the device");
            Log.e(TAG, t.getMessage());
        }

        return trustedDevice;
    }
}
