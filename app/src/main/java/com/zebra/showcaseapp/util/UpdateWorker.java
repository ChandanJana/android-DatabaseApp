package com.zebra.showcaseapp.util;

import static com.zebra.showcaseapp.util.Constants.COMMAND;
import static com.zebra.showcaseapp.util.Constants.COMMAND_INSTALL;
import static com.zebra.showcaseapp.util.Constants.COMPLETED;
import static com.zebra.showcaseapp.util.Constants.MSG;
import static com.zebra.showcaseapp.util.Constants.NATIVE_APP_INSTALL_PATH;
import static com.zebra.showcaseapp.util.Constants.RESULT;
import static com.zebra.showcaseapp.util.Constants.STATUS;
import static com.zebra.showcaseapp.util.Constants.SUCCESS;
import static com.zebra.showcaseapp.util.Constants.UPDATE_FILE_NAME;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.zebra.showcaseapp.data.BetaAppDAO;
import com.zebra.showcaseapp.data.BetaAppModel;
import com.zebra.showcaseapp.data.MasterAppModel;
import com.zebra.showcaseapp.data.ShowcaseDatabase;
import com.zebra.showcaseapp.data.UpdateDemoAppDAO;
import com.zebra.showcaseapp.data.UpdateDemoAppModel;
import com.zebra.showcaseapp.ui.HomeActivity;
import com.zebra.showcaseapp.ui.ZebraApplication;
import com.zebra.showcaseapp.zdm.ConnectToZDMService;
import com.zebra.showcaseapp.zdm.IZDMResponseCallback;
import com.zebra.showcaseapp.zdm.IncomingHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Chandan Jana on 11-05-2023.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
// If you want to execute an asynchronous operation you need to use a CoroutineWorker or a ListenableWorker.
public class UpdateWorker extends ListenableWorker {

    public static final String WORK_RESULT = "work_result";
    private static final String TAG = UpdateWorker.class.getSimpleName();
    private Context context;
    private String appName = null,appType = null;
    private String callingType = null;

    private static FileDownloadTask fileDownloadTask = null;


    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        Data taskData = getInputData();
        callingType = taskData.getString(Constants.CALL_PURPOSE);

        if (callingType.equals("betaAppDownload")){

            appName = taskData.getString(Constants.APP_NAME);
            appType = taskData.getString(Constants.APP_TYPE);
            Log.d(TAG, "appName "+ appName);
            Log.d(TAG, "appType "+ appType);

        }else if (callingType.equals("demoAppUpdateDownload")){

            appName = taskData.getString(Constants.APP_NAME);

        }else if (callingType.equals("cancel_download")){
            appName = taskData.getString(Constants.APP_NAME);
        }
        //String appName = taskData.getString(Constants.APP_NAME);
        //String appType = taskData.getString(Constants.APP_TYPE);
        Log.d(TAG, "appName " + appName);
        Log.d(TAG, "appType " + appType);
        return CallbackToFutureAdapter.getFuture(completer -> {
            MyCallback callback = new MyCallback() {
                @Override
                public void onSuccess(Data outputData) {
                    if (callingType.equals("betaAppDownload")) {
                        BetaAppDAO betaAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).betaAppDAO();
                        int updateId = betaAppDAO.updateBetaAppModelByName(appName.trim(), true, 1);
                        Log.d(TAG, "downloadBetaApp update id " + updateId);
                        completer.set(Result.success(outputData));
                    }else if (callingType.equals("demoAppUpdateDownload")){
                        UpdateDemoAppDAO updateDemoAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).updateDemoAppDAO();
                        int upid = updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 1);
                        Log.d(TAG, "downloadUpdatedDemoFile update id " + upid);
                        Log.d("TAG", "downloadUpdatedDemoFile update id " + upid);
                    }else if (callingType.equals("cancel_download")){

                    }
                }

                @Override
                public void onError() {
                    if (callingType.equals("betaAppDownload")) {
                        BetaAppDAO betaAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).betaAppDAO();
                        int updateId = betaAppDAO.updateBetaAppModelByName(appName.trim(), false, 0);
                        Log.d(TAG, "betaAppModel updateId " + updateId);
                        completer.set(Result.failure());
                    }else if (callingType.equals("demoAppUpdateDownload")){
                        UpdateDemoAppDAO updateDemoAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).updateDemoAppDAO();
                        int upid = updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);
                        Log.d(TAG, "downloadUpdatedDemoFile update id " + upid);
                        Log.d("TAG", "downloadUpdatedDemoFile update id " + upid);
                    }else if (callingType.equals("cancel_download")){

                    }
                }

                @Override
                public void onProgress() {
                    completer.set(Result.retry());
                }
            };
            Data outputData = new Data.Builder().putString(WORK_RESULT, "Jobs Finished").build();
            //putData(callback, outputData);
            if (callingType.equals("betaAppDownload")){

                BetaAppModel betaAppModel = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).betaAppDAO().loadBetaAppModelByName(appName.trim());
                Log.d(TAG, "betaAppModel before " + betaAppModel);

                if (betaAppModel != null) {
                    int updateId = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).betaAppDAO().updateBetaAppModelByName(appName.trim(), false, 2);
                    Log.d(TAG, "betaAppModel updateId " + updateId);
                } else {
                    BetaAppModel betaAppModel1 = new BetaAppModel();
                    betaAppModel1.setBetaAppName(appName.trim());
                    betaAppModel1.setBetaType(appType.trim());
                    betaAppModel1.setStatus(2);
                    betaAppModel1.setDownloaded(false);
                    long insertId = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).betaAppDAO().insertBetaAppModel(betaAppModel1);
                    Log.d(TAG, "betaAppModel insertId " + insertId);
                }
                BetaAppModel betaAppModel2 = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).betaAppDAO().loadBetaAppModelByName(appName.trim());
                Log.d(TAG, "BetaAppModel betaAppModel2 after " + betaAppModel2);
                //downloadBetaApp(callback, outputData, "nativeApp", "android");
                downloadBetaApp(callback, outputData, appName, appType);

            }else if (callingType.equals("demoAppUpdateDownload")){

                if (appName.equalsIgnoreCase("all")) {
                    List<MasterAppModel> masterAppModelList = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).masterAppDAO().selectMasterAppModel(true);
                    if (!masterAppModelList.isEmpty()) {
                        UpdateDemoAppDAO updateDemoAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).updateDemoAppDAO();
                        UpdateDemoAppModel updateDemoAppModel = updateDemoAppDAO.loadAllDemoAppFilesModel();
                        Log.d("TAGG", " AppInstallReceiver updateDemoAppModel before " + updateDemoAppModel);
                        if (updateDemoAppModel != null) {
                            updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, false, false, 2);
                        } else {
                            UpdateDemoAppModel demoAppModel = new UpdateDemoAppModel();
                            demoAppModel.setFileName(UPDATE_FILE_NAME.trim());
                            demoAppModel.setUpdateAvailable(false);
                            demoAppModel.setDownloaded(false);
                            demoAppModel.setStatus(2);
                            //demoAppModel.setLastModifiedDate(getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()));
                            int dele = updateDemoAppDAO.deleteUpdateDemo();
                            Log.d("TAGG", "File delete id " + dele);
                            long insertId = updateDemoAppDAO.insertDemoAppFilesModelModel(demoAppModel);
                            Log.d("TAGG", "File inserted id " + insertId);
                        }
                        UpdateDemoAppModel model1 = updateDemoAppDAO.loadAllDemoAppFilesModel();
                        Log.d("TAGG", "UpdateDemoAppModel model after " + model1);
                        downloadUpdatedDemoFile(callback, outputData);
                    }
                } else {
                    MasterAppModel masterAppModel = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).masterAppDAO().loadAllMasterAppModel(appName, true);
                    Log.d("TAGG", " AppInstallReceiver masterAppModel " + masterAppModel);
                    if (masterAppModel != null) {
                        UpdateDemoAppDAO updateDemoAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).updateDemoAppDAO();
                        UpdateDemoAppModel updateDemoAppModel = updateDemoAppDAO.loadAllDemoAppFilesModel();
                        Log.d("TAGG", " AppInstallReceiver updateDemoAppModel before " + updateDemoAppModel);
                        if (updateDemoAppModel != null) {
                            updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, false, false, 2);
                        } else {
                            UpdateDemoAppModel demoAppModel = new UpdateDemoAppModel();
                            demoAppModel.setFileName(UPDATE_FILE_NAME.trim());
                            demoAppModel.setUpdateAvailable(false);
                            demoAppModel.setDownloaded(false);
                            demoAppModel.setStatus(2);
                            //demoAppModel.setLastModifiedDate(getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()));
                            int dele = updateDemoAppDAO.deleteUpdateDemo();
                            Log.d("TAGG", "File delete id " + dele);
                            long insertId = updateDemoAppDAO.insertDemoAppFilesModelModel(demoAppModel);
                            Log.d("TAGG", "File inserted id " + insertId);
                        }
                        UpdateDemoAppModel model1 = updateDemoAppDAO.loadAllDemoAppFilesModel();
                        Log.d("TAGG", "UpdateDemoAppModel model after " + model1);
                        downloadUpdatedDemoFile(callback, outputData);
                    }
                }

            }else if (callingType.equals("cancel_download")){
                Log.d("TAGG", "callingType cancel_download ");
            }

            return callback;
        });
    }

    public static void cancelDownload(){
        Log.d("TAGG", "cancelDownload called ");
        Log.d(TAG, "cancelDownload called ");
        Log.d(TAG, "cancelDownload fileDownloadTask "+ fileDownloadTask);
        if (fileDownloadTask != null) {
            Log.d(TAG, "cancelDownload if");
            Log.d("TAGG", "cancelDownload if");
            fileDownloadTask.cancel();
            /*UpdateDemoAppDAO updateDemoAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).updateDemoAppDAO();
            //int upid = updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);
            UpdateDemoAppModel updateDemoAppModel = updateDemoAppDAO.loadAllDemoAppFilesModel();
            Log.d("TAGG", " Cancel updateDemoAppModel before " + updateDemoAppModel);
            if (updateDemoAppModel != null && updateDemoAppModel.getStatus() == 2 ) {
                updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, false, false, -1);
                UpdateDemoAppModel updateDemoAppModel1 = updateDemoAppDAO.loadAllDemoAppFilesModel();
                Log.d("TAGG", " Cancel updateDemoAppModel After " + updateDemoAppModel1);
            }*/
        }
    }

    public void downloadBetaApp(MyCallback callback, Data outputData, String betaAppName, String betaAppType) {
        Log.d(TAG, "downloadBetaApp betaAppName " + betaAppName);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        String betaAppZipNameFinal = betaAppName.concat("Beta.zip");
        String betaAppPath = Constants.BETA_FILE_PATH.concat(betaAppZipNameFinal);
        Log.d(TAG, "downloadBetaApp betaAppPath " + betaAppPath);
        StorageReference reference = mStorageRef.child(betaAppPath);
        reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                File localFile = new File(ZebraApplication.getInstance().getFilesDir(), betaAppZipNameFinal);
                reference
                        .getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                //Log.d(TAG, "downloadBetaApp beta zip available");
                                Log.d(TAG, "downloadBetaApp update zip available");
                                if (betaAppType.equalsIgnoreCase("a")) {
                                    boolean isSuccess = Utils.getBetaApk(localFile, betaAppName);
                                    Log.d(TAG, "getBetaApk " + isSuccess);
                                    if (isSuccess) {
                                        boolean isUrlFound = false;
                                        try {
                                            isUrlFound = Utils.getDownloadUrl(betaAppName);
                                        } catch (IOException e) {
                                            Log.e(TAG, "IOException "+e.getMessage());
                                        }
                                        if (isUrlFound) {
                                            Log.d(TAG, "isUrlFound " + isUrlFound);
                                            IZDMResponseCallback izdmResponseCallback = new IZDMResponseCallback() {
                                                @Override
                                                public void notifyResponse(StringBuilder result) {
                                                    Log.e(TAG, "notifyResponse " + result);
                                                    try {
                                                        JSONObject resObj = new JSONObject(String.valueOf(result));

                                                        String status = resObj.getString(STATUS);

                                                        String message = resObj.getString(MSG);

                                                        String zdmResult = resObj.getString(RESULT);
                                                        Log.e(TAG, "ZDM status " + status);
                                                        Log.e(TAG, "ZDM message " + message);
                                                        if (status.equals(COMPLETED)) {

                                                            if (!zdmResult.isEmpty()) {
                                                                Log.e(TAG, "ZDM message " + message);
                                                                JSONArray jsonArray = new JSONArray(zdmResult);
                                                                boolean isEB_ApkDownloadedByZDM = false;
                                                                boolean isInstalledByZDM = false;

                                                                for (int i = 0; i < jsonArray.length(); i++) {

                                                                    JSONObject obj = jsonArray.getJSONObject(i);
                                                                    /*if (obj.getString(COMMAND).equals(COMMAND_DOWNLOAD) && obj.getString(STATUS).equals(SUCCESS)) {
                                                                        isEB_ApkDownloadedByZDM = true;
                                                                        Log.i(TAG, "parseJSON result COMMAND_DOWNLOAD: " + obj);
                                                                    }*/
                                                                    if (obj.getString(COMMAND).equals(COMMAND_INSTALL) && obj.getString(STATUS).equals(SUCCESS)) {
                                                                        isInstalledByZDM = true;
                                                                        Log.i(TAG, "parseJSON result COMMAND_INSTALL: " + obj);
                                                                    }
                                                                }
                                                                if (/*isEB_ApkDownloadedByZDM &&*/ isInstalledByZDM) {
                                                                    File localFile1 = new File(ZebraApplication.getInstance().getFilesDir(), betaAppZipNameFinal);

                                                                    Log.d(TAG, "downloadBetaApp SSM insertion start " + System.currentTimeMillis());
                                                                    SSM.insertFileURI(localFile1);
                                                                    Log.d(TAG, "downloadBetaApp SSM insertion done " + System.currentTimeMillis());
                                                                    callback.onSuccess(outputData);
                                                                } else {
                                                                    callback.onError();
                                                                }

                                                            } else {
                                                                callback.onError();
                                                            }
                                                        } else {
                                                            callback.onError();
                                                        }
                                                    } catch (JSONException e) {
                                                        Log.e(TAG, "JSONException " + e.getMessage());
                                                        callback.onError();
                                                    }

                                                }
                                            };
                                            IncomingHandler incomingHandler = new IncomingHandler(Looper.getMainLooper(), izdmResponseCallback);
                                            //Messenger messenger = null;
                                            try {
                                                ConnectToZDMService.getInstance(context).getZDMService(new ConnectToZDMService.ServiceConnectionCallBack() {
                                                    @Override
                                                    public void serviceConnected(boolean connected, Messenger messenger) {
                                                        Log.i(TAG, "Service connected " + connected);
                                                        if (connected) {
                                                            Log.i(TAG, "if Service connected " + connected);
                                                            Message msg = Message.obtain();
                                                            msg.replyTo = new Messenger(incomingHandler);
                                                            Bundle bundle = new Bundle();
                                                            bundle.putString("data", NATIVE_APP_INSTALL_PATH);
                                                            msg.setData(bundle);
                                                            Log.i(TAG, "Before  calling ZDM send API for EB mService.send(msg)");
                                                            if (messenger != null) {
                                                                try {
                                                                    messenger.send(msg);
                                                                    Log.i(TAG, "After  calling ZDM send API for EB mService.send(msg)");
                                                                } catch (RemoteException e) {
                                                                    Log.i(TAG, "messenger sent " + e.getMessage());
                                                                    callback.onError();
                                                                }
                                                            } else {
                                                                Log.i(TAG, "messenger is null");
                                                                callback.onError();
                                                            }

                                                        }
                                                    }
                                                });

                                            } catch (
                                                    ConnectToZDMService.NotSupportedException |
                                                    ConnectToZDMService.BindException e) {
                                                Log.e(TAG, "connectToZDMService error " + e.getMessage());
                                                callback.onError();
                                            }
                                        } else {
                                            callback.onError();
                                        }

                                    } else {
                                        callback.onError();
                                    }
                                } else {
                                    File localFile1 = new File(ZebraApplication.getInstance().getFilesDir(), betaAppZipNameFinal);
                                    Log.d(TAG, "downloadBetaApp SSM insertion start " + System.currentTimeMillis());
                                    SSM.insertFileURI(localFile1);
                                    Log.d(TAG, "downloadBetaApp SSM insertion done " + System.currentTimeMillis());
                                    callback.onSuccess(outputData);
                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "File download error " + e.getMessage());
                                callback.onError();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, " downloadBetaApp betaAppModel error " + e.getMessage());
                callback.onError();
            }
        });
    }

    public void downloadUpdatedDemoFile(MyCallback callback, Data outputData) {

        ShowcaseDatabase mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

        UpdateDemoAppDAO updateDemoAppDAO = mDb.updateDemoAppDAO();


        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference reference = mStorageRef.child(Constants.UPDATE_FILE_PATH);

        reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {

                UpdateDemoAppModel updateDemoAppModel = updateDemoAppDAO.loadAllDemoAppFilesModel();
                Log.i(TAG, "downloadUpdatedDemoFile updateDemoAppModel " + updateDemoAppModel);
                Log.i("TAG", "downloadUpdatedDemoFile updateDemoAppModel " + updateDemoAppModel);
                if (updateDemoAppModel != null) {
                    if (updateDemoAppModel.getLastModifiedDate() != null) {
                        Log.d(TAG, "downloadUpdatedDemoFile " + compareTwoDates(getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()), updateDemoAppModel.getLastModifiedDate()));
                        Log.d("TAG", "downloadUpdatedDemoFile " + compareTwoDates(getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()), updateDemoAppModel.getLastModifiedDate()));

                        if (equalsTwoDates(getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()), updateDemoAppModel.getLastModifiedDate())) {

                            File localFile = new File(ZebraApplication.getInstance().getFilesDir(), UPDATE_FILE_NAME);
                            fileDownloadTask = reference.getFile(localFile);
                            fileDownloadTask.addOnSuccessListener(taskSnapshot -> {

                                        updateDemoAppDAO.updateDemoAppFilesModelByName(storageMetadata.getName(), true, true, 3, getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()));
                                        fileDownloadTask = null;
                                        Log.d(TAG, "downloadUpdatedDemoFile update zip available");
                                        Log.d("TAG", "downloadUpdatedDemoFile update zip available");
                                        File localFile1 = new File(ZebraApplication.getInstance().getFilesDir(), UPDATE_FILE_NAME);
                                        File destinationFilePathAfterUnzip = new File(ZebraApplication.getInstance().getFilesDir(), "");

                                        Log.d(TAG, "downloadUpdatedDemoFile update destinationFilePathAfterUnzip : " + destinationFilePathAfterUnzip.getAbsolutePath());
                                        Log.d("TAG", "downloadUpdatedDemoFile update destinationFilePathAfterUnzip : " + destinationFilePathAfterUnzip.getAbsolutePath());
                                        Log.d(TAG, "downloadUpdatedDemoFile SSM insertion start " + System.currentTimeMillis());
                                        Log.d("TAG", "downloadUpdatedDemoFile SSM insertion start " + System.currentTimeMillis());
                                        SSM.insertFileURI(localFile1);
                                        Log.d(TAG, "downloadUpdatedDemoFile SSM insertion done " + +System.currentTimeMillis());
                                        Log.d("TAG", "downloadUpdatedDemoFile SSM insertion done " + +System.currentTimeMillis());
                                        callback.onSuccess(outputData);
                                        /*AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                int upid = updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 1);
                                                Log.d(TAG, "downloadUpdatedDemoFile update id " + upid);
                                                Log.d("TAG", "downloadUpdatedDemoFile update id " + upid);
                                            }
                                        });*/

                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "downloadUpdatedDemoFile error " + e.getMessage());
                                            Log.d("TAG", "downloadUpdatedDemoFile error " + e.getMessage());
                                            callback.onError();
                                            fileDownloadTask = null;
                                            //updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);

                                        }

                                    });
                            fileDownloadTask.addOnCanceledListener(new OnCanceledListener() {
                                @Override
                                public void onCanceled() {
                                    Log.d(TAG, "downloadUpdatedDemoFile onCanceled true");
                                    Log.d("TAGG", "downloadUpdatedDemoFile onCanceled true");
                                    //UpdateDemoAppDAO updateDemoAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).updateDemoAppDAO();
                                    //int upid = updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);
                                    UpdateDemoAppModel updateDemoAppModel = updateDemoAppDAO.loadAllDemoAppFilesModel();
                                    Log.d("TAGG", " Cancel updateDemoAppModel before " + updateDemoAppModel);
                                    if (updateDemoAppModel != null && updateDemoAppModel.getStatus() == 2 ) {
                                        Log.d("TAGG", "downloadUpdatedDemoFile onCanceled if");
                                        Log.d(TAG, "downloadUpdatedDemoFile onCanceled if");
                                        updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, false, false, -1);
                                        UpdateDemoAppModel updateDemoAppModel1 = updateDemoAppDAO.loadAllDemoAppFilesModel();
                                        Log.d("TAGG", " Cancel updateDemoAppModel After " + updateDemoAppModel1);
                                    }
                                    fileDownloadTask = null;
                                }
                            });
                        } else {
                            callback.onError();
                            fileDownloadTask = null;
                            //updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);

                        }
                    } else {
                        File localFile = new File(ZebraApplication.getInstance().getFilesDir(), UPDATE_FILE_NAME);
                        fileDownloadTask = reference.getFile(localFile);
                        fileDownloadTask.addOnSuccessListener(taskSnapshot -> {

                                    updateDemoAppDAO.updateDemoAppFilesModelByName(storageMetadata.getName(), true, true, 3, getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()));
                                    fileDownloadTask = null;
                                    Log.d(TAG, "downloadUpdatedDemoFile update zip available");
                                    Log.d("TAG", "downloadUpdatedDemoFile update zip available");
                                    File localFile1 = new File(ZebraApplication.getInstance().getFilesDir(), UPDATE_FILE_NAME);
                                    File destinationFilePathAfterUnzip = new File(ZebraApplication.getInstance().getFilesDir(), "");

                                    Log.d(TAG, "downloadUpdatedDemoFile update destinationFilePathAfterUnzip : " + destinationFilePathAfterUnzip.getAbsolutePath());
                                    Log.d("TAG", "downloadUpdatedDemoFile update destinationFilePathAfterUnzip : " + destinationFilePathAfterUnzip.getAbsolutePath());

                                    Log.d(TAG, "downloadUpdatedDemoFile SSM insertion start " + Calendar.getInstance().getTime());
                                    Log.d("TAG", "downloadUpdatedDemoFile SSM insertion start " + Calendar.getInstance().getTime());
                                    SSM.insertFileURI(localFile1);
                                    Log.d(TAG, "downloadUpdatedDemoFile SSM insertion done " + Calendar.getInstance().getTime());
                                    Log.d("TAG", "downloadUpdatedDemoFile SSM insertion done " + Calendar.getInstance().getTime());
                                    callback.onSuccess(outputData);
                                    /*AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            int upid = updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 1);
                                            Log.d(TAG, "downloadUpdatedDemoFile update id " + upid);
                                            Log.d("TAG", "downloadUpdatedDemoFile update id " + upid);
                                        }
                                    });*/

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "downloadUpdatedDemoFile " + e.getMessage());
                                        Log.d("TAG", "downloadUpdatedDemoFile error " + e.getMessage());
                                        callback.onError();
                                        fileDownloadTask = null;
                                        updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);

                                    }

                                });
                        fileDownloadTask.addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                Log.d(TAG, "downloadUpdatedDemoFile onCanceled true");
                                //UpdateDemoAppDAO updateDemoAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).updateDemoAppDAO();
                                //int upid = updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);
                                UpdateDemoAppModel updateDemoAppModel = updateDemoAppDAO.loadAllDemoAppFilesModel();
                                Log.d("TAGG", " Cancel updateDemoAppModel before " + updateDemoAppModel);
                                if (updateDemoAppModel != null && updateDemoAppModel.getStatus() == 2 ) {
                                    updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, false, false, -1);
                                    UpdateDemoAppModel updateDemoAppModel1 = updateDemoAppDAO.loadAllDemoAppFilesModel();
                                    Log.d("TAGG", " Cancel updateDemoAppModel After " + updateDemoAppModel1);
                                }
                                fileDownloadTask = null;
                            }
                        });
                    }

                } else {
                    callback.onError();
                    fileDownloadTask = null;
                    //updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("TAG", "downloadUpdatedDemoFile error " + e.getMessage());
                callback.onError();
                fileDownloadTask = null;
                //updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);
            }
        });

    }

    private boolean equalsTwoDates(Date remoteDate, Date localDate) {

        if (remoteDate != null && localDate != null) {

            if (remoteDate.compareTo(localDate) > 0) {

                Log.d(TAG, "remoteDate is after localDate");

                return true;

            } else if (remoteDate.compareTo(localDate) == 0) {

                Log.d(TAG, "remoteDate is equal to localDate");

                return true;

            }

        }

        return false;

    }

    private boolean compareTwoDates(Date remoteDate, Date localDate) {

        Log.d(TAG, "remoteDate " + remoteDate);
        Log.d(TAG, "localDate " + localDate);
        if (remoteDate != null && localDate != null) {

            if (remoteDate.compareTo(localDate) > 0) {

                Log.d(TAG, "remoteDate is after localDate");

                return true;

            } else if (remoteDate.compareTo(localDate) < 0) {

                Log.d(TAG, "remoteDate is before localDate");

                return false;

            } else if (remoteDate.compareTo(localDate) == 0) {

                Log.d(TAG, "remoteDate is equal to localDate");

                return false;

            }

        }

        return false;

    }

    private Date getDateFromTimestamp(long timestamp) {

        try {

            Calendar c = Calendar.getInstance();

            c.setTimeInMillis(timestamp);

            return c.getTime();

        } catch (Exception e) {

            Log.e(TAG, "getDateFromTimestamp error " + e.getMessage());

        }

        return new Date();

    }

    interface MyCallback {
        void onSuccess(Data outputData);

        void onError();

        void onProgress();
    }
}
