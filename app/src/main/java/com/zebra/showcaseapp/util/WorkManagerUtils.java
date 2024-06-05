package com.zebra.showcaseapp.util;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

/**
 * Created by Chandan Jana on 11-05-2023.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
public class WorkManagerUtils {

    public static final String MESSAGE_STATUS = "message_status";
    private static String TAG = WorkManagerUtils.class.getSimpleName();
    private static WorkManager mWorkManager;
    private static OneTimeWorkRequest mRequest;

    private WorkManagerUtils() {
    }

    public static void startBackgroundWork(Context context, String betaAppName, String betaAppType, String purpose){

        Data data = null;

        if(purpose.equals("betaAppDownload")){

            data = new Data.Builder()
                    .putString(Constants.APP_NAME, betaAppName)
                    .putString(Constants.APP_TYPE, betaAppType)
                    .putString(Constants.CALL_PURPOSE, purpose)
                    .build();

        }else if (purpose.equals("demoAppUpdateDownload")){

            data = new Data.Builder()
                    .putString(Constants.APP_NAME, betaAppName)
                    .putString(Constants.CALL_PURPOSE, purpose)
                    .build();

        }else if (purpose.equals("cancel_download")){
            data = new Data.Builder()
                    .putString(Constants.APP_NAME, betaAppName)
                    .putString(Constants.CALL_PURPOSE, purpose)
                    .build();
        }

        //creating constraints
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // you can add as many constraints as you want
                .build();
        mWorkManager = WorkManager.getInstance();
        mRequest = new OneTimeWorkRequest.Builder(UpdateWorker.class)
                .setInputData(data)
                .setConstraints(constraints)
                .build();
        mWorkManager.enqueue(mRequest);

        /*mWorkManager.getWorkInfoByIdLiveData(mRequest.getId()).observe((LifecycleOwner) context, new Observer<WorkInfo>() {
            @Override
            public void onChanged(@Nullable WorkInfo workInfo) {
                if (workInfo != null) {
                    Log.e(TAG, "WorkInfo received: state: " + workInfo.getState());
                    if (workInfo.getState().isFinished()) {
                        Log.e(TAG, "WorkInfo received: isFinished: " + workInfo.getState().isFinished());
                        switch (workInfo.getState()) {
                            case FAILED:
                                Log.e(TAG, "OBSERVING :: fail");
                                break;
                            case BLOCKED:
                                Log.e(TAG, "OBSERVING :: blocked");
                                break;
                            case RUNNING:
                                Log.e(TAG, "OBSERVING :: running");
                                break;
                            case ENQUEUED:
                                Log.e(TAG, "OBSERVING :: enqueued");
                                break;
                            case CANCELLED:
                                Log.e(TAG, "OBSERVING :: cancelled");
                                break;
                            case SUCCEEDED:
                                Log.e(TAG, "OBSERVING :: succeeded");
                                String workManagerOutput = workInfo.getOutputData().getString(UpdateWorker.WORK_RESULT);
                                Log.e(TAG, " workManagerOutput: " + workManagerOutput);
                                break;
                        }
                    }

                }
            }
        });*/
    }

    public static void cancelDownload(){
        UpdateWorker.cancelDownload();
    }

    public static void cancelBackgroundWork(){
        mWorkManager.cancelAllWork();
    }

}
