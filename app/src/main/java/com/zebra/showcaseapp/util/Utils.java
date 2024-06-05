package com.zebra.showcaseapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.zebra.security.Signaturecheck.VerifyAPKSigningCertificates;
import com.zebra.showcaseapp.BuildConfig;
import com.zebra.showcaseapp.R;
import com.zebra.showcaseapp.data.ContentProviderDao;
import com.zebra.showcaseapp.data.ContentProviderModel;
import com.zebra.showcaseapp.data.ShowcaseDatabase;
import com.zebra.showcaseapp.ui.ZebraApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;

/**
 * Created by Chandan Jana on 14-12-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
public class Utils {

    public static final String BETA_FILE_PATH = "/data/user/0/com.zebra.showcaseapp/files/";
    private static final String TAG = Utils.class.getSimpleName();

    private Utils() {
    }

    public static boolean hasConnection(Context context) {

        Log.d("hasConnection : ", "called");

        // initialize connectivity manager
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Initialize network info
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isConnectedToInternet(Context mContext) {
        if (mContext == null)
            return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    final NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(network);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            } else {
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                for (NetworkInfo tempNetworkInfo : networkInfos) {
                    if (tempNetworkInfo.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getCurrentDateTime() {
        Date date = new Date();
        // set format in 12 hours
        SimpleDateFormat formatTime = new SimpleDateFormat("MM-dd-yyyy hh.mm aa");
        String dateTime = formatTime.format(date);
        Log.d(TAG, "Current dateTime " + dateTime);
        return dateTime;
    }

    public static void unzipAndInsertTemplate(File zipFile, File targetDirectory, Context context) {

        Log.v("TAGG", "unzipDemoFile");
        try (FileInputStream fileInputStream = new FileInputStream(zipFile);

             ZipInputStream zis = new ZipInputStream(
                     new BufferedInputStream(fileInputStream))

        ) {

            boolean lastModified = false;

            int fileSize = fileInputStream.available();

            ZipEntry ze;
            int count;
            byte[] buffer = new byte[fileSize];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
                long time = ze.getTime();
                if (time > 0)
                    lastModified = file.setLastModified(time);
                Log.d("lastModified", "" + lastModified);

            }

            SSM.insertTemplateURI(zipFile, new File(targetDirectory.getAbsolutePath() + "/masterDemoApp"), context);

        } catch (Exception e) {

            Log.d("TAGG", "Into Catch Section " + e.getMessage());

        }
    }

    public static void changeAppRunningState(Context context, boolean runningState) {

        Log.d("cursorLauncher_showcasedemoRunningOrNot", "called");

        ContentValues values = new ContentValues();
        values.put(ContentProviderModel.PACKAGE_NAME, BuildConfig.find_packagename);
        values.put(ContentProviderModel.SHOWCASEDEMO_RUNNING_OR_NOT, runningState);

        ContentProviderDao contentProviderDao = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).launcherDao();
        Cursor cursorLauncher = contentProviderDao.selectById(BuildConfig.find_packagename);

        if (cursorLauncher != null) {
            Log.d("cursorLauncher_showcasedemoRunningOrNot", DatabaseUtils.dumpCursorToString(cursorLauncher));
            if (cursorLauncher.getCount() == 0) {

                Log.d("cursorLauncher_showcasedemoRunningOrNot", "value 0");
                contentProviderDao.insert(ContentProviderModel.fromContentValues(values));

            } else {

                Log.d("cursorLauncher_showcasedemoRunningOrNot", "value greater than 0");

                contentProviderDao.updateAppRunningState(BuildConfig.find_packagename, runningState);

            }
        } else {
            Log.d("cursorLauncher_showcasedemoRunningOrNot", "null");
            contentProviderDao.insert(ContentProviderModel.fromContentValues(values));
        }

    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    boolean fileDelete = files[i].delete();
                    Log.d("fileDelete", "" + fileDelete);
                }
            }
        }
        return (path.delete());
    }

    public static boolean isZebraPrivilegedKeySignedSignatureForPackage(Context context
            , String packageName) {
        PackageManager packageManager = context.getPackageManager();

        VerifyAPKSigningCertificates verifyAPKSigningCertificates = new VerifyAPKSigningCertificates();
        return verifyAPKSigningCertificates.isZebraPrivilegedKeySigned(packageManager, packageName);
    }

    public static JSONArray getJsonStringFromFile(Context mContext, String file_name) {

        File file = new File(mContext.getFilesDir(), file_name);
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            Log.e("error", e.getMessage());
        } catch (IOException e) {
            Log.e("error", e.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e("error", e.getMessage());
                }
            }
        }

        JSONArray createdArray = null;
        try {
            createdArray = new JSONArray(stringBuilder.toString());
        } catch (JSONException e) {
            Log.e("error", e.getMessage());
        }

        return createdArray;

    }

    public static boolean isPackageWhitelisted(Context mContext, String packageName) {

        JSONArray mArrray = getJsonStringFromFile(mContext, "masterDemoApp/WhiteListedPackageNames" +
                "/whitelisted_packagenames.json");


        if (mArrray != null) {

            for (int i = 0; i < mArrray.length(); i++) {

                try {
                    if (mArrray.getString(i).equals(packageName)) {

                        return isZebraPrivilegedKeySignedSignatureForPackage(mContext, packageName);
                        //return true;

                    }
                } catch (JSONException e) {
                    Log.e("error", e.getMessage());
                }

            }

        }

        return false;

    }

    public static String getCallerSignatureBase64Encoded(String packageName, Context context) {
        String callerSignature = null;
        try {
            Signature sig = context.getPackageManager().getPackageInfo(packageName, 64).signatures[0];
            if (sig != null) {
                byte[] data = Base64.encode(sig.toByteArray(), 0);
                String signature = new String(data, StandardCharsets.UTF_8);
                callerSignature = signature.replaceAll("\\s+", "");
                Log.d("SignatureVerifier", "caller signature:" + callerSignature);
            }
        } catch (Exception var6) {
            Log.e("SignatureVerifier", "exception in getting application signature:" + var6.toString());
        }
        return callerSignature;
    }

    public static boolean getBetaApk(File zipFile, String betaAppName) {

        try (FileInputStream fileInputStream = new FileInputStream(zipFile);

             ZipInputStream zis = new ZipInputStream(
                     new BufferedInputStream(fileInputStream))

        ) {

            boolean lastModified = false;

            int fileSize = fileInputStream.available();

            ZipEntry ze;
            int count;
            byte[] buffer = new byte[fileSize];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(new File(ZebraApplication.getInstance().getFilesDir(), ""), ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
                long time = ze.getTime();
                if (time > 0)
                    lastModified = file.setLastModified(time);
                Log.d("lastModified", "" + lastModified);

            }
            //boolean de = zipFile.delete();
            //Log.d("TAGG", "beta zip deleted " + de);
            return checkAuthenticatedBetaApp(betaAppName);

        } catch (Exception e) {
            Log.e("TAGG", "getBetaApk " + e.getMessage());
        }

        return false;

    }

    public static boolean checkAuthenticatedBetaApp(String betaAppName) {

        Log.d("TAGG", "checkAuthenticatedBetaApp");

        try {
            File masterFile = null;
            String betaAppFinalName = BETA_FILE_PATH + betaAppName + "Beta";
            Log.v("TAGG", "betaAppFinalName " + betaAppFinalName);
            masterFile = new File(betaAppFinalName);

            Log.d("TAGG", "MasterFile path " + masterFile.getPath());

            if (new File(masterFile.getPath() + "/checksum.txt").exists()) {
                Log.d("TAGG", "ChecksumText Exist");
                String betaZip = "/" + betaAppName + ".zip";
                if (new File(masterFile.getPath() + betaZip).exists()) {

                    Log.d("TAGG", "Beta Exist");

                    if (getChecksum(new File(masterFile.getPath() + betaZip)).equals(decrypt(new File(masterFile.getPath() + "/checksum.txt")))) {

                        Log.d("TAGG", "Beta Checksum Matched");
                        File destinationFilePath = new File(masterFile.getPath() + betaZip);
                        File destinationFilePathAfterUnzip = new File(ZebraApplication.getInstance().getFilesDir(), "");
                        return unzipBetaFile(masterFile, destinationFilePath, destinationFilePathAfterUnzip);

                    } else {
                        deleteDirectory(masterFile);
                    }

                } else {
                    deleteDirectory(masterFile);
                }
            } else {
                deleteDirectory(masterFile);
            }

        } catch (Exception e) {
            Log.e("TAGG", "checkAuthenticatedBetaApp " + e.getMessage());
        }

        return false;

    }

    public static boolean unzipBetaFile(File masterFile, File zipFile, File targetDirectory) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(zipFile);
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(fileInputStream));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[fileInputStream.available()];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                try (FileOutputStream fout = new FileOutputStream(file)) {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                }
                long time = ze.getTime();
                if (time > 0) {
                    boolean fb = file.setLastModified(time);
                    Log.d("TAGG", "fb " + fb);
                }

            }
            boolean deleteMasterFile = deleteDirectory(masterFile);
            Log.d("TAGG", "beta file deleted " + deleteMasterFile);
            return true;

        } catch (Exception e) {
            Log.e("TAGG", "unzipBetaFile " + e.getMessage());
        } finally {
            zis.close();
            fileInputStream.close();
        }
        return false;
    }

    private static String getChecksum(File file) throws IOException {

        MessageDigest digest = null;
        FileInputStream fis = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            // Get file input stream for reading the file
            // content
            fis = new FileInputStream(file);

            // Create byte array to read data in chunks
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;

            // read the data from file and update that data in
            // the message digest
            while ((bytesCount = fis.read(byteArray)) != -1) {
                if (digest != null)
                    digest.update(byteArray, 0, bytesCount);
            }

        } catch (Exception e) {
            Log.e("TAGG", "getChecksum " + e.getMessage());
        } finally {
            // close the input stream
            if (fis != null)
                fis.close();
        }


        // store the bytes returned by the digest() method
        byte[] bytes = new byte[0];
        if (digest != null)
            bytes = digest.digest();

        // this array of bytes has bytes in decimal format
        // so we need to convert it into hexadecimal format

        // for this we create an object of StringBuilder
        // since it allows us to update the string i.e. its
        // mutable
        StringBuilder sb = new StringBuilder();

        // loop through the bytes array
        for (byte aByte : bytes) {

            // the following line converts the decimal into
            // hexadecimal format and appends that to the
            // StringBuilder object
            sb.append(Integer
                    .toString((aByte & 0xff) + 0x100, 16)
                    .substring(1));
        }

        // finally we return the complete hash
        return sb.toString();
    }

    public static String decrypt(File encryptedChecksumFile) {

        //File encryptedChecksumFile = new File(ContextFactory.getAppContext().getFilesDir(), ENCRYPTED_CHECKSUM_FILE_NAME);

        String keyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcIgfGbzT7wSq6hT7RU9wLIakb/DwkNqnAr+b1XP+05aqYXaE3L0ZyzUvmfLYKQir33c87snYON/MH3BxHGsivD+jf1gfi2kyeA1jdwYcTlhXTOwzSnGaXllFJJIqxb0J3Y8f7d8MqhdZMm5yqvEMrSTpTWO6i8Amt/jJiiJcdXwIDAQAB";

        String encodedString = null;
        try {
            encodedString = getEncryptedChecksumFromFile(encryptedChecksumFile.getPath());
        } catch (Exception e) {
            //e.printStackTrace();
            Log.d("decrypt : ", e.getMessage());
        }

        // converts the String to a PublicKey instance
        //byte[] keyBytes = Base64.decodeBase64(keyString.getBytes("utf-8"));
        try {
            byte[] keyBytes = Base64.decode(keyString.trim().getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ZebraApplication.getInstance().getString(R.string.rsa));
            PublicKey key = null;
            key = keyFactory.generatePublic(spec);
            // decrypts the message
            byte[] dectyptedText = null;
            Cipher cipher = Cipher.getInstance(ZebraApplication.getInstance().getString(R.string.rsa));
            cipher.init(Cipher.DECRYPT_MODE, key);
            if (encodedString != null)
                dectyptedText = cipher.doFinal(Base64.decode(encodedString.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
            Log.d("decryptedText", new String(dectyptedText, StandardCharsets.UTF_8).replaceAll("[^\\x0F-\\x7F]", ""));
            return new String(dectyptedText, StandardCharsets.UTF_8).replaceAll("[^\\x0F-\\x7F]", "");
        } catch (Exception e) {
            Log.d("decrypt : ", e.getMessage());
        }
        return null;

    }

    public static String getEncryptedChecksumFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        Log.d("ENCYPTED_CHECKSUM : ", ret);
        return ret;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static boolean getDownloadUrl(String betaAppName) throws IOException {
        Log.d(TAG, "UpdateWorker getDownloadUrl start ");
        File localFile = new File(ZebraApplication.getInstance().getFilesDir(), betaAppName + "/download/file_url.json");
        if (localFile.exists()) {
            //Log.d(TAG, "UpdateWorker localFile " + localFile.getPath());
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;
            try {
                fileReader = new FileReader(localFile);
                bufferedReader = new BufferedReader(fileReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
                // This responce will have Json Format String
                String responce = stringBuilder.toString();
                JSONObject jsonObject = new JSONObject(responce);
                String downLoadUrl = jsonObject.getString(Constants.URL);
                String apkName = jsonObject.getString(Constants.APK_NAME);
                //Log.d(TAG, "UpdateWorker downLoadUrl " + downLoadUrl);
                Log.d(TAG, "UpdateWorker apkName " + apkName);
                Constants.UPDATE_DOWNLOAD_PATH = 4;
                //Constants.NATIVE_APP_INSTALL_PATH = "installPackage('" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + files[0].getName() + "',1)";
                Constants.NATIVE_APP_INSTALL_PATH = "download('" + downLoadUrl + "','/sdcard/" + apkName + "'); installPackage('/sdcard/" + apkName + "',1)";
                //Log.d(TAG, "UpdateWorker NATIVE_APP_INSTALL_PATH " + Constants.NATIVE_APP_INSTALL_PATH);
                Log.d(TAG, "UpdateWorker getDownloadUrl end true ");
                return true;
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Error FileNotFoundException " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "Error IOException " + e.getMessage());
            } catch (JSONException e) {
                Log.e(TAG, "Error JSONException " + e.getMessage());
            }finally {
                if (bufferedReader != null)
                    bufferedReader.close();
            }

        } else {
            Log.d(TAG, "UpdateWorker Json file not exist. ");
        }
        Log.d(TAG, "UpdateWorker getDownloadUrl end false ");
        return false;


    }

    public static String getSerial() {
        try {
            String uri = "content://oem_info/oem.zebra.secure/build_serial";
            try {
                Cursor cursor = ZebraApplication.getInstance().getContentResolver().query(Uri.parse(uri), null, null, null, null);
                cursor.moveToFirst();
                String deviceId = cursor.getString(0);
                return deviceId;
            } catch (Exception ex) {
                Log.e(TAG, "Can't run application exception:" + ex.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "Can't get property : " + e);
        }

        return null;
    }

    public static String getDeviceName() {
        try {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            //if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        /*} else {
            return capitalize(manufacturer) + " " + model;
        }*/
        }catch (Exception e){
            Log.e(TAG, "Error "+ e.getMessage());
        }
        return "0";

    }


    private static String capitalize(String s) {
        try {
            if (s == null || s.length() == 0) {
                return "0";
            }
            char first = s.charAt(0);
            if (Character.isUpperCase(first)) {
                return s;
            } else {
                return Character.toUpperCase(first) + s.substring(1);
            }
        }catch (Exception e){
            Log.e(TAG, "Error "+ e.getMessage());
        }
        return "0";
    }
}
