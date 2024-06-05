package com.zebra.showcaseapp.util;

import static com.zebra.showcaseapp.util.Constants.AUTHORITY_FILE;
import static com.zebra.showcaseapp.util.Constants.AUTHORITY_FILE1;
import static com.zebra.showcaseapp.util.Constants.DATA_NAME;
import static com.zebra.showcaseapp.util.Constants.DATA_PERSIST_REQUIRED;
import static com.zebra.showcaseapp.util.Constants.DATA_VALUE;
import static com.zebra.showcaseapp.util.Constants.FILE_AUTHORITY;
import static com.zebra.showcaseapp.util.Constants.SIGNATURE;
import static com.zebra.showcaseapp.util.Constants.TARGET_APP_PACKAGE;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.zebra.showcaseapp.BuildConfig;
import com.zebra.showcaseapp.ui.ZebraApplication;

import java.io.File;
import java.util.Calendar;

/**
 * Created by Chandan Jana on 03-11-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
public class SSM {

    private static final String TAGG = SSM.class.getSimpleName();

    private SSM() {

    }

    public static synchronized void insertFileURI(File sourcePath) {
        Log.d("Chandan", "insertFileURI start " + Calendar.getInstance().getTime());
        Log.i("targetDirectory", "*************insertFileURI********************");
        //Log.i("targetDirectory", "sourcePath path " + sourcePath.getPath());
        //Log.i("targetDirectory", "sourcePath AbsolutePath " + sourcePath.getAbsolutePath());
        // Replace “sourcePath” with the file path of the file to deploy located on the device, e.g. "/sdcard/A.txt"

        Uri contentUri = FileProvider.getUriForFile(ZebraApplication.getInstance().getApplicationContext(), FILE_AUTHORITY, sourcePath);
        // The "file" path is passed to the FileProvier() API, which returns the source input uri to deploy the file.
        // Example content uri returned: content://com.zebra.sampleapp.provider/enterprise/usr/A.txt
        ZebraApplication.getInstance().getApplicationContext().grantUriPermission("com.zebra.securestoragemanager", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION); // Needed to grant permission for SSM to read the uri
        //Log.i("targetDirectory", "File Content Uri " + contentUri);

        Uri cpUriQuery = Uri.parse(AUTHORITY_FILE + ZebraApplication.getInstance().getApplicationContext().getPackageName());
        //Log.i("targetDirectory", "authority  " + cpUriQuery.toString());
        try {
            ContentValues values = new ContentValues();
            values.put(TARGET_APP_PACKAGE, String.format("{\"pkgs_sigs\": [{\"pkg\":\"%s\",\"sig\":\"%s\"}]}", BuildConfig.find_packagename, Utils.getCallerSignatureBase64Encoded(BuildConfig.find_packagename, ZebraApplication.getInstance().getApplicationContext())));
            values.put(DATA_NAME, String.valueOf(contentUri)); // Passes the content uri as a input source
            values.put(DATA_VALUE, BuildConfig.find_packagename + "/" + sourcePath.getAbsolutePath()); // Replace “targetPath” with the package name of the target app that is accessing the deployed file (or retrieve the app package using context.getPackageName()) followed by "/" and the full path of the file, e.g. "context.getPackageName()/A.txt"
            values.put(DATA_PERSIST_REQUIRED, false);
            Log.d("targetDirectory", "insertFileURI started " + Calendar.getInstance().getTime());
            Uri createdRow = ZebraApplication.getInstance().getApplicationContext().getContentResolver().insert(cpUriQuery, values);
            Log.d("Chandan", "insertFileURI end " + Calendar.getInstance().getTime());
            Log.d("targetDirectory", "insertFileURI ended " + Calendar.getInstance().getTime());
            Log.d(TAGG, "SSM.insertFileURI worker thread task Completed : " + Thread.currentThread().getName());
            Log.d("targetDirectory", "SSM Insert File: insertFileURI " + createdRow.toString());
        } catch (Exception e) {
            Log.e("targetDirectory", "SSM Insert File - insertFileURI error: " + e.getMessage() + "\n\n");
        }
        //queryFile(context);
        Log.i("targetDirectory", "**************insertFileURI*******************");


    }

    private static void insertFileURIToDW(File sourcePath, Context context) {

        Log.d("Chandan", "insertFileURIToDW start " + Calendar.getInstance().getTime());
        Log.i("targetDirectory", "**************insertFileURIToDW*******************");
        //Log.i("targetDirectory", "sourcePath path " + sourcePath.getPath());
        //Log.i("targetDirectory", "sourcePath AbsolutePath " + sourcePath.getAbsolutePath());
        // Replace “sourcePath” with the file path of the file to deploy located on the device, e.g. "/sdcard/A.txt"

        Uri contentUri = FileProvider.getUriForFile(context, FILE_AUTHORITY, sourcePath);
        // The "file" path is passed to the FileProvier() API, which returns the source input uri to deploy the file.
        // Example content uri returned: content://com.zebra.sampleapp.provider/enterprise/usr/A.txt
        context.getApplicationContext().grantUriPermission("com.zebra.securestoragemanager", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION); // Needed to grant permission for SSM to read the uri
        //Log.i(TAGG, "File Content Uri " + contentUri);

        Uri cpUriQuery = Uri.parse(AUTHORITY_FILE + context.getPackageName());
        //Log.i("targetDirectory", "authority  " + cpUriQuery.toString());
        try {
            ContentValues values = new ContentValues();
            values.put(TARGET_APP_PACKAGE, String.format("{\"pkgs_sigs\": [{\"pkg\":\"%s\",\"sig\":\"%s\"}]}", BuildConfig.find_packagename_dw, Utils.getCallerSignatureBase64Encoded(BuildConfig.find_packagename_dw, context)));
            values.put(DATA_NAME, String.valueOf(contentUri)); // Passes the content uri as a input source
            values.put(DATA_VALUE, BuildConfig.find_packagename_dw + "/ngsstemplate/" + sourcePath.getName()); // Replace “targetPath” with the package name of the target app that is accessing the deployed file (or retrieve the app package using context.getPackageName()) followed by "/" and the full path of the file, e.g. "context.getPackageName()/A.txt"
            values.put(DATA_PERSIST_REQUIRED, false);
            Uri createdRow = context.getContentResolver().insert(cpUriQuery, values);
            Log.i("targetDirectory", "SSM Insert File: insertFileURIToDW " + createdRow.toString());
            //Log.d(TAGG, "SSM.insertFileURIToDW called from insertTemplateURI() Worker task completed : "+Thread.currentThread().getName());
            Log.d("Chandan", "insertFileURIToDW end " + Calendar.getInstance().getTime());
        } catch (Exception e) {
            Log.e("targetDirectory", "SSM Insert File - insertFileURIToDW error: " + e.getMessage() + "\n\n");
        }
        //queryFile(context);
        Log.i("targetDirectory", "*************insertFileURIToDW********************");


    }

    public static void insertTemplateURI(File sourcePath, File dir, Context context) {

        /*try {
            Thread thread = new Thread(new Runnable() {
                 @Override
                 public void run() {*/
        try {
            Log.d("Chandan", "insertTemplateURI start " + Calendar.getInstance().getTime());
            File template = new File(dir.getAbsolutePath() + "/template");

            //Log.d("targetDirectory", "TemplateFile : " + template.getAbsolutePath());

            if (template.exists()) {

                Log.d("targetDirectory", "TemplateFile Exist");

                File[] files = template.listFiles();

                Log.d("targetDirectory", "Files Size : " + files.length);

                if (files != null) {

                    Log.d("targetDirectory", "Files not null");

                    for (int i = 0; i < files.length; ++i) {
                        File file = files[i];

                        //Log.d("targetDirectory", "File : " + String.valueOf(i) + " " + file.getAbsolutePath());

                        if (file.exists()) {
                            Log.d("targetDirectory", "File Exist");
                            //Log.d("targetDirectory", "File : " + String.valueOf(i) + " " + file.getAbsolutePath());

                            insertFileURIToDW(file, context);
                            Log.d(TAGG, "SSM.insertTemplateURI  Worker Thread completed task: " + Thread.currentThread().getName());

                        } else {
                            // do something here with the file
                            Log.d(TAGG, "SSM.insertTemplateURI  File not exist: " + file.getName());

                            Log.d(TAGG, "SSM.insertTemplateURI  Worker Thread completed task: " + Thread.currentThread().getName());

                        }
                    }

                    //                        boolean fileDeleted = Utils.deleteDirectory(dir);
                    //                        Log.d("targetDirectory","File Deleted Status : "+fileDeleted);
                    Log.d("targetDirectory", "Call insertFileURI");
                    Log.d("Chandan", "insertTemplateURI end " + Calendar.getInstance().getTime());
                    insertFileURI(sourcePath);
                }
            }

        } catch (Exception ex) {

            Log.d("targetDirectory", "Catch : " + ex.getMessage());

        }
               /*  }

             });
            thread.start();
            thread.join();
            Log.d(TAGG, "SSM.insertTemplateURI MainThread Joins with Worker : "+Thread.currentThread().getName());

        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    @SuppressLint("Range")
    public static void queryFile(Context context) {

        Uri cpUriQuery = Uri.parse(AUTHORITY_FILE1);
        String selection = TARGET_APP_PACKAGE + " = '" + BuildConfig.find_packagename_dw + "'" + " AND " + DATA_PERSIST_REQUIRED + " = '" + false + "'";

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(cpUriQuery, null, selection, null, null);
            Log.i(TAGG, "cursor " + DatabaseUtils.dumpCursorToString(cursor));

        } catch (Exception e) {
            Log.e(TAGG, "Error: " + e.getMessage());
        }

        try {
            if (cursor != null && cursor.moveToFirst()) {

                StringBuilder strBuild = new StringBuilder();
                String uriString = null;
                while (!cursor.isAfterLast()) {

                    uriString = cursor.getString(cursor.getColumnIndex("secure_file_uri"));
                    String fileName = cursor.getString(cursor.getColumnIndex("secure_file_name"));
                    String isDir = cursor.getString(cursor.getColumnIndex("secure_is_dir"));
                    String crc = cursor.getString(cursor.getColumnIndex("secure_file_crc"));

                    strBuild.append("fileURI - " + uriString).append("\n").
                            append("fileName - " + fileName).append("\n").
                            append("isDirectory - " + isDir).append("\n").
                            append("CRC - " + crc);

                    cursor.moveToNext();
                }
                Log.d(TAGG, "strBuild " + strBuild);
            }
        } catch (Exception e) {
            Log.e(TAGG, "Query data error: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static void deleteFile(Context context) {
        Uri cpUriQuery = Uri.parse(AUTHORITY_FILE + context.getPackageName());

        try {
            Log.d(TAGG, "Before Delete");
            //queryFile(context);
            String selection = TARGET_APP_PACKAGE + " = '" + BuildConfig.find_packagename + "'" + " AND " + DATA_PERSIST_REQUIRED + " = '" + false + "'";
            int deleteStatus = context.getContentResolver().delete(cpUriQuery, selection, null);
            Log.d(TAGG, "File deleted, status = " + deleteStatus);   // 0 means success
            Log.d(TAGG, "After Delete");
            //queryFile(context);

        } catch (Exception e) {
            Log.d(TAGG, "Delete file - error: " + e.getMessage());
        }
    }

    //private final String signature = "MIIC5DCCAcwCAQEwDQYJKoZIhvcNAQEFBQAwNzEWMBQGA1UEAwwNQW5kcm9pZ"; // Replace with target app signature

    public static void updateFile(Context context, File sourcePath) {
        Uri cpUriQuery = Uri.parse(AUTHORITY_FILE + context.getPackageName());

        try {
            ContentValues values = new ContentValues();
            values.put(TARGET_APP_PACKAGE, String.format("{\"pkgs_sigs\": [{\"pkg\":\"%s\",\"sig\":\"%s\"}]}", context.getPackageName(), SIGNATURE)); // If app signature is not used, pass in "null" for "signature"
            values.put(DATA_NAME, sourcePath.getAbsolutePath());   // Replace “sourcePath” with the updated file located on the device, e.g. "/sdcard/A.txt"
            values.put(DATA_VALUE, context.getPackageName() + "/" + sourcePath.getAbsolutePath());  // Replace “targetPath” with the package name of the target app that is updating the deployed file (or get the app package name using context.getPackageName()) followed by "/" and the full path of the existing deployed file, e.g. "context.getPackageName()/A.txt"
            values.put(DATA_PERSIST_REQUIRED, false);
            int rowNumbers = context.getContentResolver().update(cpUriQuery, values, null, null);
            Log.d(TAGG, "Files updated: " + rowNumbers);
        } catch (Exception e) {
            Log.d(TAGG, "SSM Update File - Error: " + e.getMessage());
        }
    }
}
