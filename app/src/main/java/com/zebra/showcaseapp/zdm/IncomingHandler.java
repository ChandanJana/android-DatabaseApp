/*
 * Copyright (C) 2021 Zebra Technologies Corporation and/or its affiliates.
 * All rights reserved.
 */
package com.zebra.showcaseapp.zdm;

import static com.zebra.showcaseapp.util.Constants.COMPLETED;
import static com.zebra.showcaseapp.util.Constants.ERROR;
import static com.zebra.showcaseapp.util.Constants.EXCEPTION;
import static com.zebra.showcaseapp.util.Constants.IN_PROGRESS;
import static com.zebra.showcaseapp.util.Constants.RESPONSE;
import static com.zebra.showcaseapp.util.Constants.STATUS;
import static com.zebra.showcaseapp.util.Constants.SUBMITTED_JSON;
import static com.zebra.showcaseapp.util.Constants.SUCCESS;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;


public class IncomingHandler extends Handler {
    private static final String TAG = IncomingHandler.class.getSimpleName();
    //private static final String ERROR = "ERROR";
    //private static final String EXCEPTION = "EXCEPTION";
    //private static final String SUCCESS = "SUCCESS";
    //private static final String SUBMITTED_JSON = "submitted json";
    //private static final String COMPLETED = "COMPLETED";
    //private static final String IN_PROGRESS = "IN_PROGRESS";
    //case sensitive
    //private static final String RESPONSE = "response";
    //private static final String STATUS = "status";
    IZDMResponseCallback izdmResponseCallback;
    StringBuilder zdmResponse = new StringBuilder();

    public IncomingHandler(Looper looper, IZDMResponseCallback callback) {
        super(looper);
        izdmResponseCallback = callback;
    }

    @Override
    public void handleMessage(Message msg) {
        String status = null;
        try {
            String response = msg.getData().getString(RESPONSE);
            Log.e(TAG, "UpdateWorker response " + response);
            JSONObject resObj = new JSONObject(response);
            status = resObj.getString(STATUS);
            if (SUCCESS.equalsIgnoreCase(status)
                    || ERROR.equalsIgnoreCase(status)
                    || EXCEPTION.equalsIgnoreCase(status)
                    || SUBMITTED_JSON.equalsIgnoreCase(status)
                    || COMPLETED.equalsIgnoreCase(status)) {
                String result;
                result = response;
                zdmResponse.append(result);
            }
        } catch (Exception e) {
            Log.d(TAG, "UpdateWorker Exception - " + e.getMessage());
        } finally {
            if (!IN_PROGRESS.equalsIgnoreCase(status)) {
                if (izdmResponseCallback != null) {
                    izdmResponseCallback.notifyResponse(zdmResponse);
                    zdmResponse = new StringBuilder();
                }
            }
        }
    }
}
