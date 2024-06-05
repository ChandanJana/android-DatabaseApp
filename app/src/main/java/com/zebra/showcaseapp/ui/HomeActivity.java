package com.zebra.showcaseapp.ui;

import static com.zebra.showcaseapp.util.Constants.APPUPDATEDATA;
import static com.zebra.showcaseapp.util.Constants.COMMAND;
import static com.zebra.showcaseapp.util.Constants.COMMAND_DOWNLOAD;
import static com.zebra.showcaseapp.util.Constants.COMMAND_INSTALL;
import static com.zebra.showcaseapp.util.Constants.COMMAND_UPGRADE;
import static com.zebra.showcaseapp.util.Constants.COMPLETED;
import static com.zebra.showcaseapp.util.Constants.EB_DOWNLOAD_INSTALL_PATH;
import static com.zebra.showcaseapp.util.Constants.EB_DOWNLOAD_UPGRADE_PATH;
import static com.zebra.showcaseapp.util.Constants.ERROR;
import static com.zebra.showcaseapp.util.Constants.EXCEPTION;
import static com.zebra.showcaseapp.util.Constants.FILEUPDATEDATA;
import static com.zebra.showcaseapp.util.Constants.FIND_EB_LOWEST_VERSION_TO_SUPPORT;
import static com.zebra.showcaseapp.util.Constants.IN_PROGRESS;
import static com.zebra.showcaseapp.util.Constants.LICENCE_PATH;
import static com.zebra.showcaseapp.util.Constants.MSG;
import static com.zebra.showcaseapp.util.Constants.RESPONSE;
import static com.zebra.showcaseapp.util.Constants.RESULT;
import static com.zebra.showcaseapp.util.Constants.SHOWCASE_DOWNLOAD_UPGRADE_PATH;
import static com.zebra.showcaseapp.util.Constants.STARTED;
import static com.zebra.showcaseapp.util.Constants.STATUS;
import static com.zebra.showcaseapp.util.Constants.SUCCESS;
import static com.zebra.showcaseapp.util.Constants.UPDATE_FILE_NAME;
import static com.zebra.showcaseapp.util.Constants.ZDM_PACKAGE;
import static com.zebra.showcaseapp.util.Constants.ZDM_SERVICE_CLASS;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.zebra.showcaseapp.BuildConfig;
import com.zebra.showcaseapp.R;
import com.zebra.showcaseapp.data.AppExecutors;
import com.zebra.showcaseapp.data.AppSettingsModel;
import com.zebra.showcaseapp.data.AppUpdateDAO;
import com.zebra.showcaseapp.data.AppUpdateModel;
import com.zebra.showcaseapp.data.DemoAvailabilityModel;
import com.zebra.showcaseapp.data.MasterAppModel;
import com.zebra.showcaseapp.data.RemoteMappingModel;
import com.zebra.showcaseapp.data.ShowcaseAppAnalytics;
import com.zebra.showcaseapp.data.ShowcaseDatabase;
import com.zebra.showcaseapp.data.UpdateDemoAppDAO;
import com.zebra.showcaseapp.data.UpdateDemoAppModel;
import com.zebra.showcaseapp.util.ConnectionReceiver;
import com.zebra.showcaseapp.util.Constants;
import com.zebra.showcaseapp.util.SSM;
import com.zebra.showcaseapp.util.SymbolSecurityHelper;
import com.zebra.showcaseapp.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;


public class HomeActivity extends AppCompatActivity implements ConnectionReceiver.NetworkConnectionListener {

    private static String TAGG = HomeActivity.class.getSimpleName();
    private static boolean isEBOpened = true;

    //private SimpleLeftMenuView mLeftMenuView;

    //private DrawerLayout mDrawerLayout;

    //private ImageView drawerOpenImg;
    private CardView btnDownload, btnInstalling;
    private Messenger mService;
    private StorageReference mStorageRef;
    private IncomingHandler incomingHandler;
    private CollectionReference dbDemoApp, dbAppUpdate;
    private FirebaseFirestore db;
    private ShowcaseDatabase mDb;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Dialog dialog = null;

    private ServiceConnection ZDMServiceConnection = null;

    private ConnectionReceiver receiver = null;

    private ArrayList<String> permissionsList;

    private String[] permissionsStr = {Manifest.permission.POST_NOTIFICATIONS/*, Manifest.permission.MANAGE_EXTERNAL_STORAGE,

            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE*/};

    private int permissionsCount = 0;
    private Thread downloadThread;
    private Thread openEBThread;
    private Thread webAppThread;
    private AlertDialog alertDialog;

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private ActivityResultLauncher<String[]> permissionsLauncher =

            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),

                    new ActivityResultCallback<Map<String, Boolean>>() {

                        @RequiresApi(api = Build.VERSION_CODES.M)

                        @Override

                        public void onActivityResult(Map<String, Boolean> result) {

                            ArrayList<Boolean> list = new ArrayList<>(result.values());

                            permissionsList = new ArrayList<>();

                            permissionsCount = 0;

                            for (int i = 0; i < list.size(); i++) {

                                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {

                                    Log.d(TAGG, "permissionsLauncher if ");

                                    permissionsList.add(permissionsStr[i]);

                                } else if (!hasPermission(HomeActivity.this, permissionsStr[i])) {

                                    permissionsCount++;

                                    Log.d(TAGG, "permissionsLauncher else permissionsCount " + permissionsCount);

                                }

                            }

                            Log.d(TAGG, "permissionsLauncher if ");

                            if (permissionsList.size() > 0) {

                                //Some permissions are denied and can be asked again.

                                askForPermissions(permissionsList);

                            } else if (permissionsCount > 0) {

                                //Show alert dialog

                                //showPermissionDialog();

                            } else {

                                //All permissions granted. Do your stuff 🤞

                            }

                        }

                    });

    private static HomeActivity homeActivity = null;

    public static HomeActivity getInstance() {
        if (homeActivity != null)
            return homeActivity;
        else
            return new HomeActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (homeActivity == null)
            homeActivity = this;

        try {

            SymbolSecurityHelper symbolSecurityHelper = new SymbolSecurityHelper();

            if (symbolSecurityHelper == null || !symbolSecurityHelper.isTrustedDevice(getApplicationContext())) {

                Log.d("TAG", "**** Authentication failed for device. Exiting DataWedge ***");

                finish();

                return;

            }

        } catch (Exception e) {

            Log.d(TAGG, "Authentication " + e.getMessage());

        }

        setContentView(R.layout.activity_home_new);

        permissionsList = new ArrayList<>();

        permissionsList.addAll(Arrays.asList(permissionsStr));

        askForPermissions(permissionsList);

        if (receiver == null) {
            receiver = new ConnectionReceiver();

            // register receiver

            registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            // Initialize listener

            receiver.setNetworkConnectionListener(this);
        }


        findViews();

        //incomingHandler = new IncomingHandler();

        //hide action bar

        getSupportActionBar().hide();

        //change status bar icon theme

        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        btnDownload.setOnClickListener(view -> {

            Log.d("btnDownload : ", "pressed");
            if (Utils.hasConnection(HomeActivity.this)) {

                openLicenseDialog();

            } else {

                noInternetDialog();
                btnInstalling.setVisibility(View.GONE);

                Log.d("btnDownload", " : Visible 1");

                btnDownload.setVisibility(View.VISIBLE);

            }

        });


    }

    private boolean hasPermission(Context context, String permissionStr) {

        Log.d(TAGG, "hasPermission permissionStr " + permissionStr);

        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;

    }

    private void askForPermissions(ArrayList<String> permissionsList) {

        String[] newPermissionStr = new String[permissionsList.size()];

        for (int i = 0; i < newPermissionStr.length; i++) {

            newPermissionStr[i] = permissionsList.get(i);

        }

        if (newPermissionStr.length > 0) {

            Log.d(TAGG, "askForPermissions if");

            permissionsLauncher.launch(newPermissionStr);

        } else {

            Log.d(TAGG, "askForPermissions else");

        /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog

        which will lead them to app details page to enable permissions from there. */

            showPermissionDialog();

        }

    }

    private void showPermissionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Permission required")

                .setMessage("Some permissions are need to be allowed to use this app without any problems.")

                .setPositiveButton("Settings", (dialog, which) -> {

                    dialog.dismiss();

                });

        if (alertDialog == null) {

            alertDialog = builder.create();

            if (!alertDialog.isShowing()) {

                alertDialog.show();

            }

        }

    }

    private void noInternetDialog() {

        try {

            if (dialog != null) {

                dialog.dismiss();

            } else {

                dialog = new Dialog(this);

            }

            Button closeBtn;

            dialog.setContentView(R.layout.dialog_no_internet);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog.setCancelable(false);

            dialog.setCanceledOnTouchOutside(false);

            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;


            closeBtn = dialog.findViewById(R.id.close_btn);

            closeBtn.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {

                    dialog.dismiss();
                    dialog = null;

                }

            });


            dialog.show();

        } catch (Exception e) {

            Log.e(TAGG, "Show Dialog: " + e.getMessage());

        }

    }

    private void upgradationDialog() {

        try {

            if (dialog != null) {

                dialog.dismiss();

            } else {

                dialog = new Dialog(this);

            }

            Button cancelBtn, upgradeBtn;

            dialog.setContentView(R.layout.upgradation_dialog);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog.setCancelable(false);

            dialog.setCanceledOnTouchOutside(false);

            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;


            cancelBtn = dialog.findViewById(R.id.cancel_btn);

            cancelBtn.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {

                    dialog.dismiss();
                    dialog = null;
                    btnInstalling.setVisibility(View.GONE);

                    Log.d("btnDownload", " : Visible 2");

                    btnDownload.setVisibility(View.VISIBLE);

                }

            });


            upgradeBtn = dialog.findViewById(R.id.upgrade_btn);

            upgradeBtn.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {

                    dialog.dismiss();
                    dialog = null;

                    if (Utils.hasConnection(getApplicationContext())) {


                        btnInstalling.setVisibility(View.VISIBLE);

                        btnDownload.setVisibility(View.GONE);

                        Log.i("NIR>>", "Constants.UPDATE_DOWNLOAD_PATH set as 3");

                        Constants.UPDATE_DOWNLOAD_PATH = 3;
                        Log.i("NIR>>", "Before ZDM binds 494");

                        doBind(HomeActivity.this);
                        Log.i("NIR>>", "After ZDM binds 497");


                    } else {
                        btnInstalling.setVisibility(View.GONE);

                        Log.d("btnDownload", " : Visible 3");

                        btnDownload.setVisibility(View.VISIBLE);
                        noInternetDialog();

                    }

                }

            });


            dialog.show();

        } catch (Exception e) {

            Log.e(TAGG, "Show Dialog: " + e.getMessage());

        }

    }

    @Override

    protected void onDestroy() {

        super.onDestroy();
        try {
            if (receiver != null) {
                unregisterReceiver(receiver);
                receiver = null;
            }
        } catch (Exception e) {
            Log.e("Exception:", e.getMessage());
        }


        try {
            if (mDb == null)

                mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

            Utils.changeAppRunningState(this, false);
        } catch (Exception e) {
            Log.e("Exception:", e.getMessage());
        }

    }

    @Override

    protected void onStart() {
        Log.i("NIR>>", "onStart() called ");

        super.onStart();

        isEBOpened = true;
        Log.i("NIR>>", "onStart() called setting isEBOpened to TRUE ");
        if (isDemoAvailable() && appInstalledOrNot(HomeActivity.this, BuildConfig.find_packagename) && isEbUptodated(BuildConfig.find_packagename)) {
            Log.i("NIR>>", "onStart() called setting isEBOpened to TRUE  app is installed and demo is configured");

            if (mDb == null)
                mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

            AppUpdateDAO appUpdateDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appUpdateDAO();
            AppUpdateModel showcaseLauncherModel = appUpdateDAO.lastAppUpdateModel("ShowcaseLauncher");
            if (showcaseLauncherModel != null) {
                try {
                    Log.d(TAGG, "showcaseLauncherModel " + showcaseLauncherModel);
                    String showcaseInstalledVersion = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, 0).versionName;
                    Log.d(TAGG, "showcaseInstalledVersion current " + showcaseInstalledVersion);
                    Log.d(TAGG, "showcaseInstalledVersion db " + showcaseLauncherModel.getVersion());
                    if (showcaseInstalledVersion.equalsIgnoreCase(showcaseLauncherModel.getVersion())) {
                        AppExecutors.getInstance().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                int ii = appUpdateDAO.updateAppUpdateModelByName(showcaseLauncherModel.getAppName(), false);
                                Log.d(TAGG, "Firestore update count " + ii);

                            }

                        });
                    } else {
                        Log.d(TAGG, "showcaseInstalledVersion false");
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("Exception: ", e.getMessage());
                }
            } else {
                Log.d(TAGG, "showcaseLauncherModel is NULL");
            }

            AppUpdateModel enterpriseBrowserModel = appUpdateDAO.lastAppUpdateModel("EnterpriseBrowser");
            if (enterpriseBrowserModel != null) {
                try {
                    String ebInstalledVersion = getPackageManager().getPackageInfo(BuildConfig.find_packagename, 0).versionName;
                    Log.d(TAGG, "ebInstalledVersion current " + ebInstalledVersion);
                    Log.d(TAGG, "ebInstalledVersion db " + enterpriseBrowserModel.getVersion());
                    if (ebInstalledVersion.equalsIgnoreCase(enterpriseBrowserModel.getVersion())) {
                        AppExecutors.getInstance().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                int ii = appUpdateDAO.updateAppUpdateModelByName(enterpriseBrowserModel.getAppName(), false);
                                Log.d(TAGG, "Firestore update count " + ii);

                            }

                        });
                    } else {
                        Log.d(TAGG, "ebInstalledVersion false");
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            if (mFirebaseAnalytics == null)

                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            ShowcaseAppAnalytics showcaseAppAnalytics = new ShowcaseAppAnalytics();

            showcaseAppAnalytics.setActivityDate(Utils.getCurrentDateTime());

            showcaseAppAnalytics.setDeviceName(Utils.getDeviceName());

            showcaseAppAnalytics.setDeviceSerialNo(Utils.getSerial());

            showcaseAppAnalytics.setAppName("ShowcaseDemo");

            showcaseAppAnalytics.setEventName("app_launch");

            ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appAnalyticsDAO().insertShowcaseAppAnalytics(showcaseAppAnalytics);

            if (Utils.hasConnection(this)) {

                List<ShowcaseAppAnalytics> analyticsList = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appAnalyticsDAO().getAllShowcaseAppAnalytics();

                Log.d(TAGG, "Analytics analyticsList " + analyticsList);

                if (analyticsList != null && analyticsList.size() > 0) {

                    for (ShowcaseAppAnalytics appAnalytics : analyticsList) {

                        Bundle bundle = new Bundle();

                        bundle.putString("device_serial_no", appAnalytics.getDeviceSerialNo());

                        bundle.putString("device_name", appAnalytics.getDeviceName());

                        bundle.putString("app_name", appAnalytics.getAppName());

                        bundle.putString("activity_date", appAnalytics.getActivityDate());

                        Log.d(TAGG, "Analytics bundle " + bundle);

                        Log.d(TAGG, "Analytics even name " + appAnalytics.getEventName());

                        mFirebaseAnalytics.logEvent(appAnalytics.getEventName(), bundle);

                        int del = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appAnalyticsDAO().deleteShowcaseAppAnalyticsById(appAnalytics.getId());

                        Log.d(TAGG, "Delete no " + del);

                    }

                }

            }

        } else {

            DemoAvailabilityModel model = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).demoAvailabalityDAO().isDemoAvailableOrNot();
            if (model != null) {
                int deleteCount = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).demoAvailabalityDAO().deleteData(model);
                Log.d("deleted status", String.valueOf(deleteCount));
            } else {
                Log.d("NIR", "DemoAvailabilityModel is Null");
            }

        }


        if (appInstalledOrNot(HomeActivity.this, BuildConfig.find_packagename) && isDemoAvailable() && isEbUptodated(BuildConfig.find_packagename)) {

            Log.i("NIR>>", "openEB() called from onStart() ");

            openEB();


        } else {
            Log.i("NIR", "appInstalledOrNot(HomeActivity.this, EB) is false and install btn visibility is true in onStart()");
            if (btnInstalling.getVisibility() != View.VISIBLE) {
                btnInstalling.setVisibility(View.GONE);

                Log.d("btnDownload", " : Visible 4");

                btnDownload.setVisibility(View.VISIBLE);
            }


        }

    }

    private void addNotification(String appName, String description) {

        Intent notificationIntent = new Intent(this, HomeActivity.class);

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //notification message will get at NotificationView

        //notificationIntent.putExtra(DIRECT_OPEN_EB, true);

        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        PendingIntent pendingIntent;

        String notificationTitle = appName;
        if (appName.equals("ShowcaseLauncher")) {
            notificationTitle = "Zebra Showcase";
        } else if (appName.equals("EnterpriseBrowser")) {
            notificationTitle = "Enterprise Browser";
        }
        Log.d("notificationTitle", notificationTitle);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.zebraicon_without_background) //set icon for notification
                .setContentTitle("New Version Available: ") //set title of notification
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationTitle))
                .setContentText(notificationTitle)//this is notification message
                .setAutoCancel(true) // makes auto cancel of notification
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setShowWhen(true)
                //.setGroup("Zebra_Showcase")
                .setGroupSummary(true) //set this notification as the summary for the group
                //.addAction(new NotificationCompat.Action(R.drawable.zebraicon_with_background, "Open",pendingIntent))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification
        //builder.setContentIntent(pendingIntent);
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("New Version Available:");
        String[] strings = notificationTitle.split(",");

        // Moves events into the expanded layout
        for (int i = 0; i < strings.length; i++) {
            inboxStyle.addLine(strings[i]);
        }
        // Moves the expanded layout object into the notification object.
        builder.setStyle(inboxStyle);

        builder.setSound(RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        // Add as notification

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "showcase_channel_id";
            AppSettingsModel appSettingsModelAudio = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appSettingsDAO().lastAppSettingsModel("audio");
            Log.d(TAGG, "appSettingsModelAudio " + appSettingsModelAudio);
            NotificationChannel channel;
            if (appSettingsModelAudio == null) {
                Log.d(TAGG, "appSettingsModelAudio enable");
                channel = new NotificationChannel(
                        channelId,
                        "Showcase notification settings",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
            } else {
                if (appSettingsModelAudio.isEnable()) {
                    Log.d(TAGG, "appSettingsModelAudio enable");
                    channel = new NotificationChannel(
                            channelId,
                            "Showcase notification settings",
                            NotificationManager.IMPORTANCE_HIGH);
                    channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
                } else {
                    Log.d(TAGG, "appSettingsModelAudio disable");
                    channel = new NotificationChannel(
                            channelId,
                            "Showcase notification settings",
                            NotificationManager.IMPORTANCE_LOW);
                    channel.setSound(null, null);
                }
            }

            AppSettingsModel appSettingsModelLed = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appSettingsDAO().lastAppSettingsModel("led");
            Log.d(TAGG, "appSettingsModelLed " + appSettingsModelLed);
            if (appSettingsModelLed == null) {
                Log.d(TAGG, "appSettingsModelLed enable");
                channel.enableLights(true);
                channel.setLightColor(Color.BLUE);
            } else {
                if (appSettingsModelLed.isEnable()) {
                    Log.d(TAGG, "appSettingsModelLed enable");
                    channel.enableLights(true);
                    channel.setLightColor(Color.BLUE);
                } else {
                    Log.d(TAGG, "appSettingsModelLed disable");
                    channel.enableLights(false);
                    channel.setLightColor(0);
                }
            }

            manager.createNotificationChannel(channel);

            builder.setChannelId(channelId);

        }

        if (appName.trim().equalsIgnoreCase("ShowcaseLauncher")) {
            pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, PendingIntent.FLAG_MUTABLE);
            builder.setContentIntent(pendingIntent);
            manager.notify(1, builder.build());

        } else if (appName.trim().equalsIgnoreCase("EnterpriseBrowser")) {
            pendingIntent = PendingIntent.getActivity(this, 2, notificationIntent, PendingIntent.FLAG_MUTABLE);
            builder.setContentIntent(pendingIntent);
            manager.notify(2, builder.build());

        } else {
            pendingIntent = PendingIntent.getActivity(this, 3, notificationIntent, PendingIntent.FLAG_MUTABLE);
            builder.setContentIntent(pendingIntent);
            manager.notify(3, builder.build());

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;

    }

    private void openLicenseDialog() {

        Dialog dialog = new Dialog(HomeActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_license);


        LinearLayout btnCancel = dialog.findViewById(R.id.cvCancel);

        CardView btnAgree = dialog.findViewById(R.id.cvAgree);

        WebView webLicenceContent = dialog.findViewById(R.id.webLicenceContent);

        Uri path = Uri.parse(LICENCE_PATH);

        String newPath = path.toString();

        webLicenceContent.loadUrl(newPath);

        btnAgree.setOnClickListener(view -> {
            dialog.dismiss();

            try {
                Log.i("NIR>>", "In dowload cycle before invoking worker thread to download ZIP and EB");
                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                for (Thread threadx : threadSet) {
                    Log.i("NIR Available Thread name: ", threadx.getName());
                }
                if (webAppThread != null && threadSet.stream().anyMatch(x -> x.getName().equalsIgnoreCase(webAppThread.getName()))
                        || openEBThread != null && threadSet.stream().anyMatch(x -> x.getName().equalsIgnoreCase(openEBThread.getName()))
                        || downloadThread != null && threadSet.stream().anyMatch(x -> x.getName().equalsIgnoreCase(downloadThread.getName()))
                ) {
                    Log.i("NIR: ", "Any 3 threads are running");
                    return;
                } else {
                    Log.i("NIR", "Worker Thread is not alive WORKER_THREAD");
                    downloadThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("NIR>>", "In dowload cycle before invoking masterData insert Workerthread");
                            masterDataInsert();
                            Log.i("NIR>>", "In dowload cycle before invoking masterData after insert Workerthread");

                            Log.i("NIR>>", "In dowload cycle before invoking masterData before start Installation");
                            Log.d("Chandan", "download button clicked " + Calendar.getInstance().getTime());
                            startInstallation();
                            Log.i("NIR>>", "In dowload cycle before invoking masterData after start Installation");

                        }
                    });
                    downloadThread.setName("DOWNLOAD_CLICK_WORKER_THREAD");
                    downloadThread.start();
                }

            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
            ///////////////


        });


        btnCancel.setOnClickListener(view -> {
            btnInstalling.setVisibility(View.GONE);

            Log.d("btnDownload", " : Visible 5");

            btnDownload.setVisibility(View.VISIBLE);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void startInstallation() {
        Log.i("NIR>>", "startInstallation called");

        if (!appInstalledOrNot(HomeActivity.this, BuildConfig.find_packagename)) {
            Log.i("NIR>>", "startInstallation called appInstalledOrNot 808");
            Log.d("Chandan", "appInstalledOrNot end " + Calendar.getInstance().getTime());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnInstalling.setVisibility(View.VISIBLE);
                    btnDownload.setVisibility(View.GONE);
                }
            });

            Constants.UPDATE_DOWNLOAD_PATH = 0;
            Log.i("NIR>>", "startInstallation called Constants.UPDATE_DOWNLOAD_PATH is set as 0");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("NIR>>", "Perform installation VIA ZDM");

                    doBind(HomeActivity.this);
                    Log.i("NIR>>", "After installation request by ZDM");

                }
            });

        } else {
            Log.i("NIR>>", "EB already available check for update");
            if (!isEbUptodated(BuildConfig.find_packagename)) {
                Log.i("NIR>>", "EB needs an upgrade");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("NIR>>", "EB upgrade dialog shown");

                        upgradationDialog();
                        Log.i("NIR>>", "EB after upgrade dialog shown");
                    }
                });

            } else {
                Log.i("NIR>>", "EB version supports showcaseDemo");

                if (Utils.hasConnection(getApplicationContext())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnInstalling.setVisibility(View.VISIBLE);
                            btnDownload.setVisibility(View.GONE);
                        }
                    });

                    Constants.UPDATE_DOWNLOAD_PATH = 0;
                    Log.i("NIR>>", "EB version supports showcaseDemo download demo app  Constants.UPDATE_DOWNLOAD_PATH set to 0");
                    downloadFileFromFirebaseStorage();
                    Log.i("NIR>>", " after downloadFileFromFirebaseStorage() EB version supports showcaseDemo download demo app  Constants.UPDATE_DOWNLOAD_PATH set to 0");

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnInstalling.setVisibility(View.GONE);

                            Log.d("btnDownload", " : Visible 6");

                            btnDownload.setVisibility(View.VISIBLE);
                            noInternetDialog();
                        }
                    });


                }
            }
        }
    }

    private boolean isEBUpdateAvailable = false;
    private boolean isShowcaseUpdateAvailable = false;
    private String showcaseVersion;
    private String showcaseLink;
    private String ebVersion;
    private String ebLink;

    private void appUpdate(AppUpdateModel appUpdateModel, boolean isFromDelete) {

        Log.d(TAGG, "Firestore appUpdateModels appName " + appUpdateModel);

        if (db == null)
            db = FirebaseFirestore.getInstance();

        if (dbAppUpdate == null)
            dbAppUpdate = db.collection(APPUPDATEDATA);

        if (mDb == null)
            mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

        AppUpdateDAO appUpdateDAO = mDb.appUpdateDAO();

        try {
            showcaseVersion = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, 0).versionName;
            ebVersion = getPackageManager().getPackageInfo(BuildConfig.find_packagename, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAGG, "NameNotFoundException " + e.getMessage());
        }


        if (appUpdateModel != null) {

            AppUpdateModel appUpdateModel1 = appUpdateDAO.lastAppUpdateModel(appUpdateModel.getAppName().trim());

            if (appUpdateModel1 == null) {
                if (!isFromDelete) {
                    if (appUpdateModel.getAppName().equalsIgnoreCase("ShowcaseLauncher")) {

                        try {
                            //Log.d(TAGG, "current getVersion() "+ appUpdateModel.getVersion()); //1.0.2
                            Log.d(TAGG, "upcoming getVersion() " + appUpdateModel.getVersion()); //1.0.3
                            Log.d(TAGG, "Firestore isShowcaseUpToDate Showcase " + isShowcaseUpToDate(BuildConfig.APPLICATION_ID, appUpdateModel.getVersion()));
                            if (!isShowcaseUpToDate(BuildConfig.APPLICATION_ID, appUpdateModel.getVersion())) {
                                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (mDb == null)
                                            mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                                        AppSettingsModel appSettingsModel = mDb.appSettingsDAO().lastAppSettingsModel("newUpdate");
                                        Log.d(TAGG, "appSettingsModel " + appSettingsModel);
                                        if (appSettingsModel == null)
                                            addNotification(appUpdateModel.getAppName(), "");
                                        else {
                                            if (appSettingsModel.isEnable())
                                                addNotification(appUpdateModel.getAppName(), "");
                                        }
                                        int id = appUpdateDAO.deleteAppUpdateModel(appUpdateModel.getAppName().trim());
                                        Log.d(TAGG, "Firestore AppUpdateModel1 delete id " + id);
                                        appUpdateModel.setUpdateAvailable(true);
                                        long ii = appUpdateDAO.insertAppUpdateModel(appUpdateModel);
                                        Log.d(TAGG, "Firestore AppUpdateModel1 insert id " + ii);
                                        //Log.d(TAGG, "Firestore AppUpdateModel1 insert id " + ii);
                                        // int ii = appUpdateDAO.updateAppUpdateModelByName(appUpdateModel1.getAppName().trim(), appUpdateModel1.getVersion(), true, appUpdateModel1.getAppLink());
                                        //Log.d(TAGG, "Firestore update count " + ii);

                                    }

                                });
                            }

                        } catch (Exception e) {
                            Log.e("Exception", e.getMessage());

                        }
                    } else if (appUpdateModel.getAppName().equalsIgnoreCase("EnterpriseBrowser")) {

                        try {
                            //Log.d(TAGG, "current getVersion() "+ appUpdateModel.getVersion());
                            Log.d(TAGG, "upcoming getVersion() " + appUpdateModel.getVersion());
                            Log.d(TAGG, "Firestore isShowcaseUpToDate EnterpriseBrowser " + isEbUpToDate(BuildConfig.find_packagename, appUpdateModel.getVersion()));
                            if (!isEbUpToDate(BuildConfig.find_packagename, appUpdateModel.getVersion())) {
                                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mDb == null)
                                            mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                                        AppSettingsModel appSettingsModel = mDb.appSettingsDAO().lastAppSettingsModel("newUpdate");
                                        Log.d(TAGG, "appSettingsModel " + appSettingsModel);
                                        if (appSettingsModel == null)
                                            addNotification(appUpdateModel.getAppName(), "");
                                        else {
                                            if (appSettingsModel.isEnable())
                                                addNotification(appUpdateModel.getAppName(), "");
                                        }
                                        int id = appUpdateDAO.deleteAppUpdateModel(appUpdateModel.getAppName().trim());
                                        Log.d(TAGG, "Firestore AppUpdateModel1 delete id " + id);
                                        appUpdateModel.setUpdateAvailable(true);
                                        long ii = appUpdateDAO.insertAppUpdateModel(appUpdateModel);
                                        Log.d(TAGG, "Firestore AppUpdateModel1 insert id " + ii);
                                        //int ii = appUpdateDAO.updateAppUpdateModelByName(appUpdateModel1.getAppName().trim(), appUpdateModel1.getVersion(), true, appUpdateModel1.getAppLink());
                                        //Log.d(TAGG, "Firestore update count " + ii);
                                    }

                                });
                            }

                        } catch (Exception e) {
                            Log.e("Exception", e.getMessage());

                        }
                    }
                }

            } else {

                if (isFromDelete) {
                    try {
                        if (appUpdateModel.getAppName().equalsIgnoreCase("ShowcaseLauncher")) {
                            int ii = appUpdateDAO.updateAppUpdateModelByName("ShowcaseLauncher", showcaseVersion, false, showcaseLink);
                            Log.d(TAGG, "Firestore isFromDelete update count " + ii);
                        } else if (appUpdateModel.getAppName().equalsIgnoreCase("EnterpriseBrowser")) {
                            int ii = appUpdateDAO.updateAppUpdateModelByName("EnterpriseBrowser", ebVersion, false, ebLink);
                            Log.d(TAGG, "Firestore isFromDelete update count " + ii);
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }

                } else {
                    if (appUpdateModel1.getAppName().equalsIgnoreCase("ShowcaseLauncher")) {

                        try {
                            Log.d(TAGG, "current getVersion() " + appUpdateModel1.getVersion());
                            Log.d(TAGG, "upcoming getVersion() " + appUpdateModel.getVersion());
                            Log.d(TAGG, "Firestore isShowcaseUpToDate Showcase " + isShowcaseUpToDate(BuildConfig.APPLICATION_ID, appUpdateModel.getVersion()));
                            if (!isShowcaseUpToDate(BuildConfig.APPLICATION_ID, appUpdateModel.getVersion())) {
                                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mDb == null)
                                            mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                                        AppSettingsModel appSettingsModel = mDb.appSettingsDAO().lastAppSettingsModel("newUpdate");
                                        Log.d(TAGG, "appSettingsModel " + appSettingsModel);
                                        if (appSettingsModel == null)
                                            addNotification(appUpdateModel.getAppName(), "");
                                        else {
                                            if (appSettingsModel.isEnable())
                                                addNotification(appUpdateModel.getAppName(), "");
                                        }
                                        int ii = appUpdateDAO.updateAppUpdateModelByName(appUpdateModel.getAppName().trim(), appUpdateModel.getVersion(), true, appUpdateModel.getAppLink());
                                        Log.d(TAGG, "Firestore update count " + ii);
                                    }

                                });
                            }

                        } catch (Exception e) {
                            Log.e("Exception", e.getMessage());

                        }
                    } else if (appUpdateModel1.getAppName().equalsIgnoreCase("EnterpriseBrowser")) {

                        try {
                            Log.d(TAGG, "current getVersion() " + appUpdateModel1.getVersion());
                            Log.d(TAGG, "upcoming getVersion() " + appUpdateModel.getVersion());
                            Log.d(TAGG, "Firestore isShowcaseUpToDate EnterpriseBrowser " + isEbUpToDate(BuildConfig.find_packagename, appUpdateModel.getVersion()));
                            if (!isEbUpToDate(BuildConfig.find_packagename, appUpdateModel.getVersion())) {
                                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mDb == null)
                                            mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                                        AppSettingsModel appSettingsModel = mDb.appSettingsDAO().lastAppSettingsModel("newUpdate");
                                        Log.d(TAGG, "appSettingsModel " + appSettingsModel);
                                        if (appSettingsModel == null)
                                            addNotification(appUpdateModel.getAppName(), "");
                                        else {
                                            if (appSettingsModel.isEnable())
                                                addNotification(appUpdateModel.getAppName(), "");
                                        }
                                        int ii = appUpdateDAO.updateAppUpdateModelByName(appUpdateModel.getAppName().trim(), appUpdateModel.getVersion(), true, appUpdateModel.getAppLink());
                                        Log.d(TAGG, "Firestore update count " + ii);

                                    }

                                });
                            }

                        } catch (Exception e) {
                            Log.e("Exception", e.getMessage());

                        }
                    }
                }

            }

        } else {

            Log.d(TAGG, "Firestore AppUpdateModel null");

            dbAppUpdate.get().addOnSuccessListener(queryDocumentSnapshots -> {

                Log.d(TAGG, "insert queryDocumentSnapshots size " + queryDocumentSnapshots.size());

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                    AppUpdateModel appUpdateModel1 = document.toObject(AppUpdateModel.class);
                    Log.d(TAGG, "Firestore AppUpdateModel1 " + appUpdateModel1);
                    AppUpdateModel appUpdateModel2 = appUpdateDAO.lastAppUpdateModel(appUpdateModel1.getAppName().trim());
                    Log.d(TAGG, "Firestore AppUpdateModel2 " + appUpdateModel2);

                    if (appUpdateModel2 == null) {
                        if (appUpdateModel1.getAppName().equalsIgnoreCase("ShowcaseLauncher")) {

                            try {
                                //Log.d(TAGG, "current getVersion() "+ appUpdateModel2.getVersion()); //1.0.2
                                Log.d(TAGG, "upcoming getVersion() " + appUpdateModel1.getVersion()); //1.0.3
                                Log.d(TAGG, "Firestore isShowcaseUpToDate Showcase " + isShowcaseUpToDate(BuildConfig.APPLICATION_ID, appUpdateModel1.getVersion()));
                                if (!isShowcaseUpToDate(BuildConfig.APPLICATION_ID, appUpdateModel1.getVersion())) {
                                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mDb == null)
                                                mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                                            AppSettingsModel appSettingsModel = mDb.appSettingsDAO().lastAppSettingsModel("newUpdate");
                                            Log.d(TAGG, "appSettingsModel " + appSettingsModel);
                                            if (appSettingsModel == null)
                                                addNotification(appUpdateModel1.getAppName(), "");
                                            else {
                                                if (appSettingsModel.isEnable())
                                                    addNotification(appUpdateModel1.getAppName(), "");
                                            }
                                            int id = appUpdateDAO.deleteAppUpdateModel(appUpdateModel1.getAppName().trim());
                                            Log.d(TAGG, "Firestore AppUpdateModel1 delete id " + id);
                                            appUpdateModel1.setUpdateAvailable(true);
                                            long ii = appUpdateDAO.insertAppUpdateModel(appUpdateModel1);
                                            Log.d(TAGG, "Firestore AppUpdateModel1 insert id " + ii);
                                            // int ii = appUpdateDAO.updateAppUpdateModelByName(appUpdateModel1.getAppName().trim(), appUpdateModel1.getVersion(), true, appUpdateModel1.getAppLink());
                                            //Log.d(TAGG, "Firestore update count " + ii);

                                        }

                                    });
                                    showcaseVersion = appUpdateModel1.getVersion();
                                    showcaseLink = appUpdateModel1.getAppLink();
                                    isShowcaseUpdateAvailable = true;
                                }

                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());

                            }
                        } else if (appUpdateModel1.getAppName().equalsIgnoreCase("EnterpriseBrowser")) {

                            try {
                                //Log.d(TAGG, "current getVersion() "+ appUpdateModel2.getVersion());
                                Log.d(TAGG, "upcoming getVersion() " + appUpdateModel1.getVersion());
                                Log.d(TAGG, "Firestore isShowcaseUpToDate EnterpriseBrowser " + isEbUpToDate(BuildConfig.find_packagename, appUpdateModel1.getVersion()));
                                if (!isEbUpToDate(BuildConfig.find_packagename, appUpdateModel1.getVersion())) {
                                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mDb == null)
                                                mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                                            AppSettingsModel appSettingsModel = mDb.appSettingsDAO().lastAppSettingsModel("newUpdate");
                                            Log.d(TAGG, "appSettingsModel " + appSettingsModel);
                                            if (appSettingsModel == null)
                                                addNotification(appUpdateModel1.getAppName(), "");
                                            else {
                                                if (appSettingsModel.isEnable())
                                                    addNotification(appUpdateModel1.getAppName(), "");
                                            }
                                            int id = appUpdateDAO.deleteAppUpdateModel(appUpdateModel1.getAppName().trim());
                                            Log.d(TAGG, "Firestore AppUpdateModel1 delete id " + id);
                                            appUpdateModel1.setUpdateAvailable(true);
                                            long ii = appUpdateDAO.insertAppUpdateModel(appUpdateModel1);
                                            Log.d(TAGG, "Firestore AppUpdateModel1 insert id " + ii);
                                            //int ii = appUpdateDAO.updateAppUpdateModelByName(appUpdateModel1.getAppName().trim(), appUpdateModel1.getVersion(), true, appUpdateModel1.getAppLink());
                                            // Log.d(TAGG, "Firestore update count " + ii);
                                        }

                                    });
                                    ebVersion = appUpdateModel1.getVersion();
                                    ebLink = appUpdateModel1.getAppLink();
                                    isEBUpdateAvailable = true;
                                }

                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());

                            }
                        }

                    } else {
                        if (appUpdateModel1.getAppName().equalsIgnoreCase("ShowcaseLauncher")) {

                            try {
                                Log.d(TAGG, "current getVersion() " + appUpdateModel2.getVersion()); //1.0.2
                                Log.d(TAGG, "upcoming getVersion() " + appUpdateModel1.getVersion()); //1.0.3
                                Log.d(TAGG, "Firestore isShowcaseUpToDate Showcase " + isShowcaseUpToDate(BuildConfig.APPLICATION_ID, appUpdateModel1.getVersion()));
                                if (!isShowcaseUpToDate(BuildConfig.APPLICATION_ID, appUpdateModel1.getVersion())) {
                                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mDb == null)
                                                mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                                            AppSettingsModel appSettingsModel = mDb.appSettingsDAO().lastAppSettingsModel("newUpdate");
                                            Log.d(TAGG, "appSettingsModel " + appSettingsModel);
                                            if (appSettingsModel == null)
                                                addNotification(appUpdateModel1.getAppName(), "");
                                            else {
                                                if (appSettingsModel.isEnable())
                                                    addNotification(appUpdateModel1.getAppName(), "");
                                            }
                                            int ii = appUpdateDAO.updateAppUpdateModelByName(appUpdateModel1.getAppName().trim(), appUpdateModel1.getVersion(), true, appUpdateModel1.getAppLink());
                                            Log.d(TAGG, "Firestore update count " + ii);

                                        }

                                    });
                                    showcaseVersion = appUpdateModel1.getVersion();
                                    showcaseLink = appUpdateModel1.getAppLink();
                                    isShowcaseUpdateAvailable = true;
                                }

                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());

                            }
                        } else if (appUpdateModel1.getAppName().equalsIgnoreCase("EnterpriseBrowser")) {

                            try {
                                Log.d(TAGG, "current getVersion() " + appUpdateModel2.getVersion());
                                Log.d(TAGG, "upcoming getVersion() " + appUpdateModel1.getVersion());
                                Log.d(TAGG, "Firestore isShowcaseUpToDate EnterpriseBrowser " + isEbUpToDate(BuildConfig.find_packagename, appUpdateModel1.getVersion()));
                                if (!isEbUpToDate(BuildConfig.find_packagename, appUpdateModel1.getVersion())) {
                                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mDb == null)
                                                mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                                            AppSettingsModel appSettingsModel = mDb.appSettingsDAO().lastAppSettingsModel("newUpdate");
                                            Log.d(TAGG, "appSettingsModel " + appSettingsModel);
                                            if (appSettingsModel == null)
                                                addNotification(appUpdateModel1.getAppName(), "");
                                            else {
                                                if (appSettingsModel.isEnable())
                                                    addNotification(appUpdateModel1.getAppName(), "");
                                            }
                                            int ii = appUpdateDAO.updateAppUpdateModelByName(appUpdateModel1.getAppName().trim(), appUpdateModel1.getVersion(), true, appUpdateModel1.getAppLink());
                                            Log.d(TAGG, "Firestore update count " + ii);
                                        }

                                    });
                                    ebVersion = appUpdateModel1.getVersion();
                                    ebLink = appUpdateModel1.getAppLink();
                                    isEBUpdateAvailable = true;
                                }

                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());

                            }
                        }
                    }

                    if (isShowcaseUpdateAvailable && isEBUpdateAvailable) {
                        break;
                    }

                }

                if (!isShowcaseUpdateAvailable) {
                    int ii = appUpdateDAO.updateAppUpdateModelByName("ShowcaseLauncher", showcaseVersion, false, showcaseLink);
                    Log.d(TAGG, "Firestore isShowcaseUpdateAvailable update count " + ii);
                }
                if (!isEBUpdateAvailable) {
                    int ii = appUpdateDAO.updateAppUpdateModelByName("EnterpriseBrowser", ebVersion, false, ebLink);
                    Log.d(TAGG, "Firestore isEBUpdateAvailable update count " + ii);
                }

            }).addOnFailureListener(e -> Log.e(TAGG, "Error getting documents.", e));

            if (mDb == null)
                mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());
            Utils.changeAppRunningState(this, true);

            if (isEBOpened) {

                PackageManager pm = this.getPackageManager();
                Log.d(TAGG, "Launching EB via getLaunchIntentForPackage");
                Intent appStartIntent = pm.getLaunchIntentForPackage("com.zebra.mdna.enterprisebrowser");
                appStartIntent.putExtra("DEMO_MODE", true);

                if (null != appStartIntent) {
                    Log.d(TAGG, "calling StartActivity");
                    Log.d("Chandan", "openEB end " + Calendar.getInstance().getTime());
                    this.startActivity(appStartIntent);
                    Log.d("Chandan", "download button end " + Calendar.getInstance().getTime());
                    isEBOpened = false;
                    finish();

                }

            }

        }


    }

    private String generateActualAppName(String[] strings) {
        int i = 0;
        String showName = "";
        for (String s : strings) {
            Log.e(TAGG, "newUpdateFileDownload s " + s);
            switch (s) {
                case "anyBarcode":
                    showName = showName.concat("Any Barcode");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "picklistMode":
                    showName = showName.concat("Picklist Mode");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "UDI":
                    showName = showName.concat("UDI Barcodes");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "variableQuantity":
                    showName = showName.concat("Variable or Fixed Quantity");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "autoGroupIdentification":
                    showName = showName.concat("Auto Group ID");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "specificBarcodes":
                    showName = showName.concat("Specific Barcodes");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "freeFormOCR":
                    showName = showName.concat("Free-From OCR");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "tireIdentificationNumber":
                    showName = showName.concat("TIN OCR Wedge");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "vehicleIdentificationNumber":
                    showName = showName.concat("VIN OCR Wedge");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "licensePlates":
                    showName = showName.concat("License Plate OCR Wedge");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "identificationDocuments":
                    showName = showName.concat("ID OCR Wedge");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "ocrBTravelDocuments":
                    showName = showName.concat("OCR-B Travel Documents");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "meterReading":
                    showName = showName.concat("Meter Reading OCD Wedge");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "shippingContainerID":
                    showName = showName.concat("Container ID OCR Wedge");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "freeFromImageCapture":
                    showName = showName.concat("Free-From Image Capture");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;
                case "documentCapture":
                    showName = showName.concat("Document Capture");
                    i = i + 1;
                    if (i < strings.length) {
                        showName = showName.concat(",");
                    }
                    break;

            }
        }
        return showName;
    }

    private void performZipDownloadAndConfigureEB() {
        Log.i("NIR>>", "downloadFileFromFirebaseStorage-->openEB() called");

        if (mFirebaseAnalytics == null)
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (db == null)
            db = FirebaseFirestore.getInstance();

        if (dbDemoApp == null) {
            dbDemoApp = db.collection(FILEUPDATEDATA);
            AtomicBoolean isFirstListener = new AtomicBoolean(true);
            dbDemoApp.addSnapshotListener((snapshots, error) -> {
                Log.i("NIR>>", "downloadFileFromFirebaseStorage-->dbDemoApp.addSnapshotListener() listener added for file update data: " + FILEUPDATEDATA);

                if (error != null) {
                    Log.e(TAGG, "Listen:error " + error.getMessage(), error);
                    return;

                }

                if (isFirstListener.get()) {
                    isFirstListener.set(false);
                    Log.i("NIR>>", "downloadFileFromFirebaseStorage-->dbDemoApp.addSnapshotListener()-->isFirstListener is true so return : 1180");

                    return;

                }
                Log.i("NIR>>", "downloadFileFromFirebaseStorage-->dbDemoApp.addSnapshotListener()-->isFirstListener is false so continue : 1185");

                if (snapshots != null) {
                    Log.i("NIR>>", "downloadFileFromFirebaseStorage-->dbDemoApp.addSnapshotListener()-->snapshot not NULL : 1188");

                    //Do what you need to do

                    for (DocumentChange document : snapshots.getDocumentChanges()) {

                        switch (document.getType()) {

                            case ADDED: {
                                Log.i("NIR>>", "downloadFileFromFirebaseStorage-->dbDemoApp.addSnapshotListener()-->document Added for demoapps : 1197");

                                RemoteMappingModel demoAppModel = document.getDocument().toObject(RemoteMappingModel.class);
                                if (demoAppModel.getAppNames() != null) {
                                    Log.d(TAGG, "New App: " + demoAppModel);
                                    if (mDb == null)
                                        mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                                    String[] strings = demoAppModel.getAppNames().split(",");
                                    /*for (String s : strings) {

                                        Log.e(TAGG, "newUpdateFileDownload s " + s);

                                        MasterAppModel masterAppModel = mDb.masterAppDAO().selectMasterAppModel(s.trim());

                                        Log.e(TAGG, "newUpdateFileDownload masterAppModel " + masterAppModel);

                                        if (masterAppModel != null)

                                            AppExecutors.getInstance().diskIO().execute(() -> mDb.masterAppDAO().updateMasterAppModel(masterAppModel.getId(), true));

                                    }*/
                                    newUpdateFileDownload(strings);

                                } else {

                                    Log.d(TAGG, "New App not added");

                                }
                                break;

                            }

                            case MODIFIED: {

                                RemoteMappingModel demoAppModel = document.getDocument().toObject(RemoteMappingModel.class);
                                Log.d(TAGG, "Modified App: " + demoAppModel);

                                break;

                            }

                            case REMOVED:

                                Log.d(TAGG, "Removed App: " + document.getDocument().getData());

                                break;

                        }

                    }

                }

            });
        }

        if (dbAppUpdate == null) {
            dbAppUpdate = db.collection(APPUPDATEDATA);
            AtomicBoolean isFirstListener1 = new AtomicBoolean(true);
            dbAppUpdate.addSnapshotListener((snapshots, error) -> {
                Log.i("NIR>>", "downloadFileFromFirebaseStorage-->dbAppUpdate.addSnapshotListener()-->Added listener for : 1245 " + APPUPDATEDATA);

                if (error != null) {

                    Log.e(TAGG, "Listen:error " + error.getMessage(), error);

                    return;

                }

                if (isFirstListener1.get()) {

                    isFirstListener1.set(false);
                    Log.i("NIR>>", "downloadFileFromFirebaseStorage-->dbAppUpdate.addSnapshotListener()-->return if isFirstListener1 is true : 1258 " + APPUPDATEDATA);

                    return;

                }
                Log.i("NIR>>", "downloadFileFromFirebaseStorage-->dbAppUpdate.addSnapshotListener()-->continue if isFirstListener1 is false : 1263 " + APPUPDATEDATA);

                if (snapshots != null) {

                    //Do what you need to do

                    for (DocumentChange document : snapshots.getDocumentChanges()) {

                        switch (document.getType()) {

                            case ADDED: {
                                Log.i("NIR>>", "downloadFileFromFirebaseStorage-->dbAppUpdate.addSnapshotListener()-->Notification added for apk update : 1274 " + APPUPDATEDATA);

                                AppUpdateModel appUpdateModel = document.getDocument().toObject(AppUpdateModel.class);

                                if (appUpdateModel.getAppName() != null) {
                                    Log.d(TAGG, "Update App: " + appUpdateModel);
                                    //addNotification(appUpdateModel.getAppName(), "");
                                    appUpdate(appUpdateModel, false);
                                } else {
                                    Log.d(TAGG, "Update App not added");
                                }
                                break;

                            }

                            case MODIFIED: {
                                AppUpdateModel appUpdateModel = document.getDocument().toObject(AppUpdateModel.class);
                                Log.d(TAGG, "Update Modified App: " + appUpdateModel);

                                break;

                            }

                            case REMOVED:
                                AppUpdateModel appUpdateModel = document.getDocument().toObject(AppUpdateModel.class);
                                Log.d(TAGG, "Removed App: " + appUpdateModel);
                                if (appUpdateModel.getVersion() != null && !appUpdateModel.getVersion().isEmpty()) {
                                    if (appUpdateModel.getAppName().equalsIgnoreCase("ShowcaseLauncher")) {
                                        if (!isShowcaseUpToDate(BuildConfig.APPLICATION_ID, appUpdateModel.getVersion())) {
                                            Log.d(TAGG, "Removed condition true- for Showcase");
                                            appUpdate(appUpdateModel, true);
                                        }
                                    } else if (appUpdateModel.getAppName().equalsIgnoreCase("EnterpriseBrowser")) {
                                        if (!isEbUpToDate(BuildConfig.find_packagename, appUpdateModel.getVersion())) {
                                            Log.d(TAGG, "Removed condition true for EB");
                                            appUpdate(appUpdateModel, true);
                                        }
                                    }
                                }
                                break;

                        }

                    }

                }

            });
        }

        Log.i("NIR>>", "downloadFileFromFirebaseStorage-->Before checking isDemoAvailable() : 1308 ");

        if (!isDemoAvailable()) {
            Log.i("NIR>>", "downloadFileFromFirebaseStorage--> isDemoAvailable() is false : 1311 ");

            File localFile = new File(getFilesDir(), Constants.MASTER_DEMO_FILE_NAME);

            File destinationFilePathAfterUnzip = new File(getFilesDir(), "");

            Log.d("targetDirectory", "destinationFilePathAfterUnzip : " + destinationFilePathAfterUnzip.getAbsolutePath());

            try {
                Log.i("NIR>>", "downloadFileFromFirebaseStorage--> Before Utils.unzipAndInsertTemplate() : 1320 " + Constants.MASTER_DEMO_FILE_NAME);
                Log.d("Chandan", "unzipAndInsertTemplate start " + Calendar.getInstance().getTime());
                Utils.unzipAndInsertTemplate(localFile, destinationFilePathAfterUnzip, getApplicationContext());
                Log.d("Chandan", "unzipAndInsertTemplate end " + Calendar.getInstance().getTime());
                Log.i("NIR>>", "downloadFileFromFirebaseStorage--> After Utils.unzipAndInsertTemplate() : 1323 " + Constants.MASTER_DEMO_FILE_NAME);
                ContentValues values = new ContentValues();
                values.put(DemoAvailabilityModel.DEMO_AVAILABLE_OR_NOT, true);

                ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).demoAvailabalityDAO()
                        .insertDemoAvailabalityModel(DemoAvailabilityModel
                                .fromContentValues(values));
                Log.i("NIR>>", "Before calling appUpdate(null)");
                appUpdate(null, false);
                Log.i("NIR>>", "After calling appUpdate(null)");

            } catch (Exception e) {

                Log.e("unzipAndInsertTemplate ", e.getMessage());

            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnInstalling.setVisibility(View.GONE);
                }
            });


            try {

                if (mDb == null)

                    mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());
                Log.i("NIR>>", "downloadFileFromFirebaseStorage--> Utils.changeAppRunningState(this, true) and isEBOpened flag value is  : 1344 " + isEBOpened);

                Utils.changeAppRunningState(this, true);

                if (isEBOpened) {
                    Log.i("NIR>>", "downloadFileFromFirebaseStorage-->  isEBOpened flag value is TRUE : 1349 " + isEBOpened);

                    PackageManager pm = this.getPackageManager();

                    Log.d(TAGG, "Launching EB via getLaunchIntentForPackage");

                    Intent appStartIntent = pm.getLaunchIntentForPackage("com.zebra.mdna.enterprisebrowser");

                    appStartIntent.putExtra("DEMO_MODE", true);

                    if (null != appStartIntent) {

                        Log.d(TAGG, "calling StartActivity");

                        HomeActivity.this.startActivity(appStartIntent);

                        isEBOpened = false;
                        Log.i("NIR>>", "downloadFileFromFirebaseStorage-->  setting isEBOpened flag value to FALSE : 1366 " + isEBOpened);
                        finish();

                    }

                }

            } catch (Exception e) {

                Log.e(TAGG, "Exception in calling StartActivity " + e.getMessage());
            }


        } else {
            Log.i("NIR>>", "isDEMOAvailable is true : 1383 ");

            if (Utils.hasConnection(getApplicationContext())) {
                Log.i("NIR>>", "Before calling fetchDemoApp : 1385 ");

                fetchDemoApp();
                Log.i("NIR>>", "After calling fetchDemoApp : 1388 ");

                appUpdate(null, false);

            } else {
                Log.i("NIR>>", "No connection available and DemoApp not configured: 1393 ");

                if (mDb == null)
                    mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                List<MasterAppModel> masterAppModel = mDb.masterAppDAO().selectMasterAppModel(true);

                if (!masterAppModel.isEmpty()) {

                    Log.d(TAGG, "update zip available");

                    File localFile = new File(getFilesDir(), UPDATE_FILE_NAME);
                    File destinationFilePathAfterUnzip = new File(getFilesDir(), "");
                    Log.d(TAGG, "update destinationFilePathAfterUnzip : " + destinationFilePathAfterUnzip.getAbsolutePath());


                    try {
                        /*Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {*/
                        SSM.insertFileURI(localFile);
                        Log.d(TAGG, "SSM.insertFileURI completed HomeActivity: " + Thread.currentThread().getName());

                           /* }
                        });
                        thread.start();
                        thread.join();
                        Log.d(TAGG, "SSM.insertFileURI worker thread task Completed HomeActivity: " + Thread.currentThread().getName());
*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (mDb == null)
                    mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

            }

        }

        Utils.changeAppRunningState(this, true);
        Log.i("NIR>>", "No connection available and DemoApp configured launch EB: 1431 isEBOpened =" + isEBOpened);

        if (isEBOpened) {
            PackageManager pm = this.getPackageManager();
            Log.d(TAGG, "Launching EB via getLaunchIntentForPackage");
            Intent appStartIntent = pm.getLaunchIntentForPackage("com.zebra.mdna.enterprisebrowser");
            appStartIntent.putExtra("DEMO_MODE", true);
            if (null != appStartIntent) {
                Log.d(TAGG, "calling StartActivity");
                this.startActivity(appStartIntent);
                isEBOpened = false;
                finish();

            }

        }

    }

    private void openEB() {

        try {

            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            for (Thread threadx : threadSet) {
                Log.i("NIR Available Thread name: ", threadx.getName());
            }
            if (openEBThread != null && threadSet.stream().anyMatch(x -> x.getName().equalsIgnoreCase(openEBThread.getName()))) {
                Log.i("NIR: Worker Thread is still alive: ", openEBThread.getName());
                return;
            } else {
                Log.i("NIR", "Worker Thread is not alive WORKER_THREAD");
                openEBThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("NIR>>", "downloadFileFromFirebaseStorage Before calling performZipDownloadAndConfigureEB()");
                        Log.d("Chandan", "openEB start " + Calendar.getInstance().getTime());
                        performZipDownloadAndConfigureEB();
                        Log.i("NIR>>", "downloadFileFromFirebaseStorage After calling performZipDownloadAndConfigureEB()");

                    }
                });
                openEBThread.setName("WORKER_THREAD");
                openEBThread.start();
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        ////////////////////////////


    }

    private Date getDateFromTimestamp(long timestamp) {

        try {

            Calendar c = Calendar.getInstance();

            c.setTimeInMillis(timestamp);

            return c.getTime();

        } catch (Exception e) {

            Log.e(TAGG, "getDateFromTimestamp error " + e.getMessage());

        }

        return new Date();

    }

    private void downloadWebAppFromFirebase() {

        Log.i("NIR>>", "downloadFileFromFirebaseStorage called 1446");

        if (mStorageRef == null)
            mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef = mStorageRef.child(Constants.MASTER_DEMO_FIREBASE_PATH);

        File localFile = new File(getFilesDir(), Constants.MASTER_DEMO_FILE_NAME);
        Log.i("NIR>>", "downloadFileFromFirebaseStorage called  and adding listener for Constants.MASTER_DEMO_FILE_NAME 1454" + Constants.MASTER_DEMO_FILE_NAME);

        riversRef.getFile(localFile)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.i("NIR>>", "downloadFileFromFirebaseStorage return success callback before taskSnapshot.getTask().isComplete()");
                    if (taskSnapshot.getTask().isComplete()) {
                        Log.i("NIR>>", "downloadFileFromFirebaseStorage return success callback after taskSnapshot.getTask().isComplete()");
                        Log.i(TAGG, "Demo Downloaded successfully Configure EB " + Thread.currentThread().getName());
                        Log.d("Chandan", "downloadFileFromFirebaseStorage end " + Calendar.getInstance().getTime());
                        openEB();

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    if (downloadThread != null) {
                                        downloadThread.interrupt();
                                        Log.d("Chandan", "downloadThread Thread close true");
                                    }
                                    if (openEBThread != null) {
                                        openEBThread.interrupt();
                                        Log.d("Chandan", "openEBThread Thread close true");
                                    }
                                    if (webAppThread != null) {
                                        webAppThread.interrupt();
                                        Log.d("Chandan", "webAppThread Thread close true");
                                    }
                                } catch (Exception e) {
                                    Log.d(TAGG, "Thread close true");
                                    Log.d("Chandan", "Thread close true");
                                }

                                downloadUnsuccessfulDialog();
                            }
                        });
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (downloadThread != null) {
                                        downloadThread.interrupt();
                                        Log.d("Chandan", "downloadThread Thread close true");
                                    }
                                    if (openEBThread != null) {
                                        openEBThread.interrupt();
                                        Log.d("Chandan", "openEBThread Thread close true");
                                    }
                                    if (webAppThread != null) {
                                        webAppThread.interrupt();
                                        Log.d("Chandan", "webAppThread Thread close true");
                                    }
                                } catch (Exception e) {
                                    Log.d(TAGG, "Thread close true");
                                    Log.d("Chandan", "Thread close true");
                                }
                                downloadUnsuccessfulDialog();
                            }
                        });


                    }

                });

    }

    private void downloadFileFromFirebaseStorage() {
        try {

            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            for (Thread threadx : threadSet) {
                Log.i("NIR Available Thread name: ", threadx.getName());
            }
            if (webAppThread != null && threadSet.stream().anyMatch(x -> x.getName().equalsIgnoreCase(webAppThread.getName()))
                    || openEBThread != null && threadSet.stream().anyMatch(x -> x.getName().equalsIgnoreCase(openEBThread.getName()))
            ) {
                Log.i("NIR", "One of the worker thread is alive");
                return;
            } else {
                Log.i("NIR", "Worker Thread is not alive WEBAPP_WORKER_THREAD");
                webAppThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("NIR>>", "downloadFileFromFirebaseStorage Before calling performZipDownloadAndConfigureEB()");
                        Log.d("Chandan", "downloadFileFromFirebaseStorage started " + Calendar.getInstance().getTime());
                        downloadWebAppFromFirebase();
                        Log.i("NIR>>", "downloadFileFromFirebaseStorage After calling performZipDownloadAndConfigureEB()");

                    }
                });
                webAppThread.setName("WORKER_WEBAPP_THREAD");
                webAppThread.start();
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }


    }

    private boolean isDemoAvailable() {
        DemoAvailabilityModel model = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).demoAvailabalityDAO().isDemoAvailableOrNot();
        Log.d("NIR>>", "isDemoAvailable model : " + model);
        if (model != null)
            return model.isDemoAvailableOrNot();
        else
            return false;
    }

    private void findViews() {
        //mLeftMenuView = findViewById(R.id.navigation_view);

        //mDrawerLayout = findViewById(R.id.drawerLayout);

        //drawerOpenImg = findViewById(R.id.drawer_open_img);

        btnDownload = findViewById(R.id.cvDownload);

        btnInstalling = findViewById(R.id.cvInstalling);


    }

    private boolean appInstalledOrNot(Context context, String packageName) {
        Log.i("NIR>>", "appInstalledOrNot  called 1508");
        Log.d("Chandan", "appInstalledOrNot started " + Calendar.getInstance().getTime());
        try {
            if (context.getPackageManager().getApplicationInfo(packageName, 0) != null)
                return true;
            else
                return false;

        } catch (PackageManager.NameNotFoundException e) {

            Log.e(TAGG, "Entering Error: " + e.getMessage());

            return false;

        }

    }

    private boolean isShowcaseUpToDate(String packageName, String version) {
        Log.d(TAGG, "isShowcaseUpToDate packageName " + packageName);
        Log.d(TAGG, "isShowcaseUpToDate version " + version);

        String[] splitEbLowestVersionToSupport = version.split("\\.");
        int firstEbLowestVersionToSupport = Integer.parseInt(splitEbLowestVersionToSupport[0]);
        int secondEbLowestVersionToSupport = Integer.parseInt(splitEbLowestVersionToSupport[1]);
        int thirdEbLowestVersionToSupport = Integer.parseInt(splitEbLowestVersionToSupport[2]);
        //int forthEbLowestVersionToSupport = Integer.parseInt(splitEbLowestVersionToSupport[3]);

        String[] splitEbInstalledVersion = new String[0];
        try {
            splitEbInstalledVersion = getPackageManager().getPackageInfo(packageName, 0).versionName.split("\\.");
            Log.d(TAGG, "isShowcaseUpToDate version current " + getPackageManager().getPackageInfo(packageName, 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Exception", e.getMessage());
        }

        int firstEbInstalledVersion = Integer.parseInt(splitEbInstalledVersion[0]);
        int secondEbInstalledVersion = Integer.parseInt(splitEbInstalledVersion[1]);
        int thirdEbInstalledVersion = Integer.parseInt(splitEbInstalledVersion[2]);
        //int forthEbInstalledVersion = Integer.parseInt(splitEbInstalledVersion[3]);

        if (firstEbInstalledVersion > firstEbLowestVersionToSupport) {
            return true;
        } else {
            if (firstEbInstalledVersion == firstEbLowestVersionToSupport) {
                if (secondEbInstalledVersion > secondEbLowestVersionToSupport) {
                    return true;
                } else {
                    if (secondEbInstalledVersion == secondEbLowestVersionToSupport) {
                        if (thirdEbInstalledVersion > thirdEbLowestVersionToSupport) {
                            return true;
                        } else {
                            if (thirdEbInstalledVersion == thirdEbLowestVersionToSupport) {
                                return true;
                            } else {
                                return false;
                            }

                        }

                    } else {
                        return false;

                    }

                }

            } else {
                return false;
            }

        }

    }

    private boolean isEbUpToDate(String packageName, String version) {

        String[] splitEbLowestVersionToSupport = version.split("\\.");
        int firstEbLowestVersionToSupport = Integer.parseInt(splitEbLowestVersionToSupport[0]);
        int secondEbLowestVersionToSupport = Integer.parseInt(splitEbLowestVersionToSupport[1]);
        int thirdEbLowestVersionToSupport = Integer.parseInt(splitEbLowestVersionToSupport[2]);
        int forthEbLowestVersionToSupport = Integer.parseInt(splitEbLowestVersionToSupport[3]);
        String[] splitEbInstalledVersion = new String[0];
        try {
            splitEbInstalledVersion = getPackageManager().getPackageInfo(packageName, 0).versionName.split("\\.");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Exception", e.getMessage());
        }
        int firstEbInstalledVersion = Integer.parseInt(splitEbInstalledVersion[0]);
        int secondEbInstalledVersion = Integer.parseInt(splitEbInstalledVersion[1]);
        int thirdEbInstalledVersion = Integer.parseInt(splitEbInstalledVersion[2]);
        int forthEbInstalledVersion = Integer.parseInt(splitEbInstalledVersion[3]);

        if (firstEbInstalledVersion > firstEbLowestVersionToSupport) {
            return true;
        } else {
            if (firstEbInstalledVersion == firstEbLowestVersionToSupport) {
                if (secondEbInstalledVersion > secondEbLowestVersionToSupport) {
                    return true;
                } else {
                    if (secondEbInstalledVersion == secondEbLowestVersionToSupport) {
                        if (thirdEbInstalledVersion > thirdEbLowestVersionToSupport) {
                            return true;
                        } else {
                            if (thirdEbInstalledVersion == thirdEbLowestVersionToSupport) {
                                if (forthEbInstalledVersion > forthEbLowestVersionToSupport) {
                                    return true;
                                } else {
                                    if (forthEbInstalledVersion == forthEbLowestVersionToSupport) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            } else {
                                return false;
                            }
                        }
                    } else {
                        return false;

                    }
                }

            } else {
                return false;
            }
        }

    }

    private boolean isEbUptodated(String packageName) {

        String[] splitEbLowestVersionToSupport = FIND_EB_LOWEST_VERSION_TO_SUPPORT.split("\\.");

        int firstEbLowestVersionToSupport = Integer.parseInt(splitEbLowestVersionToSupport[0]);

        int secondEbLowestVersionToSupport = Integer.parseInt(splitEbLowestVersionToSupport[1]);

        int thirdEbLowestVersionToSupport = Integer.parseInt(splitEbLowestVersionToSupport[2]);

        int forthEbLowestVersionToSupport = Integer.parseInt(splitEbLowestVersionToSupport[3]);

        String[] splitEbInstalledVersion = new String[0];

        try {

            splitEbInstalledVersion = getPackageManager().getPackageInfo(packageName, 0).versionName.split("\\.");

        } catch (PackageManager.NameNotFoundException e) {

            Log.e("Exception", e.getMessage());

        }

        int firstEbInstalledVersion = Integer.parseInt(splitEbInstalledVersion[0]);
        int secondEbInstalledVersion = Integer.parseInt(splitEbInstalledVersion[1]);
        int thirdEbInstalledVersion = Integer.parseInt(splitEbInstalledVersion[2]);
        int forthEbInstalledVersion = Integer.parseInt(splitEbInstalledVersion[3]);


        if (firstEbInstalledVersion > firstEbLowestVersionToSupport) {
            return true;
        } else {
            if (firstEbInstalledVersion == firstEbLowestVersionToSupport) {
                if (secondEbInstalledVersion > secondEbLowestVersionToSupport) {
                    return true;
                } else {
                    if (secondEbInstalledVersion == secondEbLowestVersionToSupport) {
                        if (thirdEbInstalledVersion > thirdEbLowestVersionToSupport) {
                            return true;
                        } else {
                            if (thirdEbInstalledVersion == thirdEbLowestVersionToSupport) {
                                if (forthEbInstalledVersion > forthEbLowestVersionToSupport) {
                                    return true;
                                } else {
                                    if (forthEbInstalledVersion == forthEbLowestVersionToSupport) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            } else {
                                return false;

                            }

                        }

                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }

    }

    private void fetchDemoApp() {

        if (mDb == null)
            mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

        UpdateDemoAppDAO demoAppDAO = mDb.updateDemoAppDAO();

        if (demoAppDAO.loadAllDemoAppFilesModel() != null) {

            if (db == null)
                db = FirebaseFirestore.getInstance();

            if (dbDemoApp == null)
                dbDemoApp = db.collection(FILEUPDATEDATA);

            dbDemoApp.whereGreaterThanOrEqualTo("updateDate", demoAppDAO.loadAllDemoAppFilesModel().getLastModifiedDate()).get().addOnSuccessListener(queryDocumentSnapshots -> {

                Log.d(TAGG, "Firestore queryDocumentSnapshots size " + queryDocumentSnapshots.size());

                if (queryDocumentSnapshots.size() > 0) {

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        RemoteMappingModel demoAppModel = document.toObject(RemoteMappingModel.class);
                        Log.d(TAGG, "Firestore RemoteMappingModel " + demoAppModel);
                        String[] namesList = demoAppModel.getAppNames().split(",");
                        Log.d(TAGG, "Remote namesList " + namesList.length);

                        /*for (String s : namesList) {

                            Log.e(TAGG, "newUpdateFileDownload s " + s);

                            MasterAppModel masterAppModel = mDb.masterAppDAO().selectMasterAppModel(s.trim());

                            Log.e(TAGG, "newUpdateFileDownload masterAppModel " + masterAppModel);

                            if (masterAppModel != null)

                                AppExecutors.getInstance().diskIO().execute(() -> mDb.masterAppDAO().updateMasterAppModel(masterAppModel.getId(), true));

                        }*/

                        newUpdateFileDownload(namesList);

                    }

                }


                if (mDb == null)

                    mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                Utils.changeAppRunningState(this, true);

                if (isEBOpened) {

                    PackageManager pm = this.getPackageManager();

                    Log.d(TAGG, "Launching EB via getLaunchIntentForPackage");

                    Intent appStartIntent = pm.getLaunchIntentForPackage("com.zebra.mdna.enterprisebrowser");

                    appStartIntent.putExtra("DEMO_MODE", true);

                    if (null != appStartIntent) {

                        Log.d(TAGG, "calling StartActivity");

                        HomeActivity.this.startActivity(appStartIntent);

                        isEBOpened = false;
                        finish();

                    }

                }

            }).addOnFailureListener(e -> Log.e(TAGG, "Error getting documents.", e));

        } else {

            if (db == null)
                db = FirebaseFirestore.getInstance();

            if (dbDemoApp == null)
                dbDemoApp = db.collection(FILEUPDATEDATA);

            dbDemoApp.get().addOnSuccessListener(queryDocumentSnapshots -> {

                Log.d(TAGG, "Firestore queryDocumentSnapshots size " + queryDocumentSnapshots.size());

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                    RemoteMappingModel demoAppModel = document.toObject(RemoteMappingModel.class);
                    Log.d(TAGG, "Firestore RemoteMappingModel " + demoAppModel);
                    String[] namesList = demoAppModel.getAppNames().split(",");
                    Log.d(TAGG, "Remote namesList " + namesList.length);

                    /*for (String s : namesList) {

                        Log.e(TAGG, "newUpdateFileDownload s " + s);

                        MasterAppModel masterAppModel = mDb.masterAppDAO().selectMasterAppModel(s.trim());

                        Log.e(TAGG, "newUpdateFileDownload masterAppModel " + masterAppModel);

                        if (masterAppModel != null)

                            AppExecutors.getInstance().diskIO().execute(() -> mDb.masterAppDAO().updateMasterAppModel(masterAppModel.getId(), true));

                    }*/

                    newUpdateFileDownload(namesList);

                }

                if (mDb == null)
                    mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                Utils.changeAppRunningState(this, true);

                if (isEBOpened) {

                    PackageManager pm = this.getPackageManager();

                    Log.d(TAGG, "Launching EB via getLaunchIntentForPackage");

                    Intent appStartIntent = pm.getLaunchIntentForPackage("com.zebra.mdna.enterprisebrowser");

                    appStartIntent.putExtra("DEMO_MODE", true);

                    if (null != appStartIntent) {

                        Log.d(TAGG, "calling StartActivity");

                        HomeActivity.this.startActivity(appStartIntent);

                        isEBOpened = false;
                        finish();

                    }

                }

            }).addOnFailureListener(e -> Log.e(TAGG, "Error getting documents.", e));

        }


    }


    public void downloadUpdatedDemoFile() {
        if (mDb == null)
            mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

        UpdateDemoAppDAO updateDemoAppDAO = mDb.updateDemoAppDAO();

        if (mStorageRef == null)
            mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference reference = mStorageRef.child(Constants.UPDATE_FILE_PATH);

        reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {

                UpdateDemoAppModel updateDemoAppModel = updateDemoAppDAO.loadAllDemoAppFilesModel();
                Log.i(TAGG, "downloadUpdatedDemoFile updateDemoAppModel " + updateDemoAppModel);
                Log.i("TAGG", "downloadUpdatedDemoFile updateDemoAppModel " + updateDemoAppModel);
                if (updateDemoAppModel != null) {
                    if (updateDemoAppModel.getLastModifiedDate() != null) {
                        Log.d(TAGG, "downloadUpdatedDemoFile " + compareTwoDates(getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()), updateDemoAppModel.getLastModifiedDate()));
                        Log.d("TAGG", "downloadUpdatedDemoFile " + compareTwoDates(getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()), updateDemoAppModel.getLastModifiedDate()));

                        if (equalsTwoDates(getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()), updateDemoAppModel.getLastModifiedDate())) {

                            File localFile = new File(ZebraApplication.getInstance().getFilesDir(), UPDATE_FILE_NAME);
                            reference
                                    .getFile(localFile)
                                    .addOnSuccessListener(taskSnapshot -> {

                                        updateDemoAppDAO.updateDemoAppFilesModelByName(storageMetadata.getName(), true, true, getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()));

                                        Log.d(TAGG, "downloadUpdatedDemoFile update zip available");
                                        Log.d("TAGG", "downloadUpdatedDemoFile update zip available");
                                        File localFile1 = new File(ZebraApplication.getInstance().getFilesDir(), UPDATE_FILE_NAME);
                                        File destinationFilePathAfterUnzip = new File(ZebraApplication.getInstance().getFilesDir(), "");

                                        Log.d(TAGG, "downloadUpdatedDemoFile update destinationFilePathAfterUnzip : " + destinationFilePathAfterUnzip.getAbsolutePath());
                                        Log.d("TAGG", "downloadUpdatedDemoFile update destinationFilePathAfterUnzip : " + destinationFilePathAfterUnzip.getAbsolutePath());
                                        Log.d(TAGG, "downloadUpdatedDemoFile SSM insertion start " + System.currentTimeMillis());
                                        Log.d("TAGG", "downloadUpdatedDemoFile SSM insertion start " + System.currentTimeMillis());
                                        SSM.insertFileURI(localFile1);
                                        Log.d(TAGG, "downloadUpdatedDemoFile SSM insertion done " + +System.currentTimeMillis());
                                        Log.d("TAGG", "downloadUpdatedDemoFile SSM insertion done " + +System.currentTimeMillis());
                                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                int upid = updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 1);
                                                Log.d(TAGG, "downloadUpdatedDemoFile update id " + upid);
                                                Log.d("TAGG", "downloadUpdatedDemoFile update id " + upid);
                                            }
                                        });

                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAGG, "downloadUpdatedDemoFile error " + e.getMessage());
                                            Log.d("TAGG", "downloadUpdatedDemoFile error " + e.getMessage());
                                            updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);

                                        }

                                    });
                        } else {

                            updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);

                        }
                    } else {
                        File localFile = new File(ZebraApplication.getInstance().getFilesDir(), UPDATE_FILE_NAME);
                        reference
                                .getFile(localFile)
                                .addOnSuccessListener(taskSnapshot -> {

                                    updateDemoAppDAO.updateDemoAppFilesModelByName(storageMetadata.getName(), true, true, getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()));

                                    Log.d(TAGG, "downloadUpdatedDemoFile update zip available");
                                    Log.d("TAGG", "downloadUpdatedDemoFile update zip available");
                                    File localFile1 = new File(ZebraApplication.getInstance().getFilesDir(), UPDATE_FILE_NAME);
                                    File destinationFilePathAfterUnzip = new File(ZebraApplication.getInstance().getFilesDir(), "");

                                    Log.d(TAGG, "downloadUpdatedDemoFile update destinationFilePathAfterUnzip : " + destinationFilePathAfterUnzip.getAbsolutePath());
                                    Log.d("TAGG", "downloadUpdatedDemoFile update destinationFilePathAfterUnzip : " + destinationFilePathAfterUnzip.getAbsolutePath());

                                    Log.d(TAGG, "downloadUpdatedDemoFile SSM insertion start " + Calendar.getInstance().getTime());
                                    Log.d("TAGG", "downloadUpdatedDemoFile SSM insertion start " + Calendar.getInstance().getTime());
                                    SSM.insertFileURI(localFile1);
                                    Log.d(TAGG, "downloadUpdatedDemoFile SSM insertion done " + Calendar.getInstance().getTime());
                                    Log.d("TAGG", "downloadUpdatedDemoFile SSM insertion done " + Calendar.getInstance().getTime());
                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            int upid = updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 1);
                                            Log.d(TAGG, "downloadUpdatedDemoFile update id " + upid);
                                            Log.d("TAGG", "downloadUpdatedDemoFile update id " + upid);
                                        }
                                    });

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAGG, "downloadUpdatedDemoFile " + e.getMessage());
                                        Log.d("TAGG", "downloadUpdatedDemoFile error " + e.getMessage());
                                        updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);

                                    }

                                });
                    }

                } else {
                    updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("TAGG", "downloadUpdatedDemoFile error " + e.getMessage());
                updateDemoAppDAO.updateDemoAppFilesModelByName(UPDATE_FILE_NAME, 0);
            }
        });

    }

    private void newUpdateFileDownload(String[] strings) {
        Log.i("NIR>>", "Inside newUpdateFileDownload " + strings);

        //Log.d(TAGG, "newUpdateFileDownload strings " + Arrays.asList(strings));

        if (mStorageRef == null)
            mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference reference = mStorageRef.child(Constants.UPDATE_FILE_PATH);

        reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                Log.i("NIR>>", "Inside reference.getMetadata() onSuccess()  1846");

                Log.d(TAGG, "newUpdateFileDownload success");

                if (mDb == null)
                    mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                UpdateDemoAppDAO updateDemoAppDAO = mDb.updateDemoAppDAO();
                UpdateDemoAppModel updateDemoAppModel = updateDemoAppDAO.loadAllDemoAppFilesModel();

                if (updateDemoAppModel != null) {

                    Log.d(TAGG, "newUpdateFileDownload " + compareTwoDates(getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()), updateDemoAppModel.getLastModifiedDate()));

                    if (compareTwoDates(getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()), updateDemoAppModel.getLastModifiedDate())) {

                        //String newString = "";
                        //int i = 0;
                        for (String s : strings) {

                            Log.e(TAGG, "newUpdateFileDownload s " + s);
                            /*newString = newString.concat(s);
                            i = i + 1;
                            if (i < strings.length){
                                newString = newString.concat(",");
                            }*/

                            MasterAppModel masterAppModel = mDb.masterAppDAO().selectMasterAppModel(s.trim());

                            Log.e(TAGG, "newUpdateFileDownload masterAppModel " + masterAppModel);

                            if (masterAppModel != null)
                                AppExecutors.getInstance().diskIO().execute(() -> mDb.masterAppDAO().updateMasterAppModel(masterAppModel.getId(), true));
                            else {

                                List<String> masterAppNames = mDb.masterAppDAO().loadAllMasterAppName();
                                Log.d(TAGG, "masterAppNames " + masterAppNames);

                                MasterAppModel appModel = new MasterAppModel();
                                appModel.setMasterAppName(s.trim());
                                appModel.setUpdateAvailable(true);
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        long masterInsertID = mDb.masterAppDAO().insertMasterAppModel(appModel);
                                        Log.d(TAGG, "masterInsertID " + masterInsertID);

                                    }
                                });

                            }

                        }
                        String showName = generateActualAppName(strings);
                        Log.d(TAGG, "newString " + showName);

                        if (!showName.isEmpty()) {
                            AppSettingsModel appSettingsModel = mDb.appSettingsDAO().lastAppSettingsModel("newUpdate");
                            Log.d(TAGG, "appSettingsModel " + appSettingsModel);
                            if (appSettingsModel == null)
                                addNotification(showName, "");
                            else {
                                if (appSettingsModel.isEnable())
                                    addNotification(showName, "");
                            }
                            //addNotification(showName, "");
                        }

                        /*File localFile = new File(getFilesDir(), UPDATE_FILE_NAME);

                        reference
                                .getFile(localFile)
                                .addOnSuccessListener(taskSnapshot -> {
                                    Log.i("NIR>>", "Downloading demo app update file name addOnSuccessListener  1871 " + UPDATE_FILE_NAME);

                                    updateDemoAppDAO.updateDemoAppFilesModelByName(storageMetadata.getName(), true, true, getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()));

                                    for (String s : strings) {

                                        Log.e(TAGG, "newUpdateFileDownload s " + s);

                                        MasterAppModel masterAppModel = mDb.masterAppDAO().selectMasterAppModel(s.trim());

                                        Log.e(TAGG, "newUpdateFileDownload masterAppModel " + masterAppModel);

                                        if (masterAppModel != null)

                                            AppExecutors.getInstance().diskIO().execute(() -> mDb.masterAppDAO().updateMasterAppModel(masterAppModel.getId(), true));

                                    }
                                    Log.i("NIR>>", "End of for loop   1888 " + strings);

                                    List<MasterAppModel> masterAppModel = mDb.masterAppDAO().selectMasterAppModel(true);

                                    if (!masterAppModel.isEmpty()) {

                                        Log.d(TAGG, "update zip available");


                                        File localFile1 = new File(getFilesDir(), UPDATE_FILE_NAME);

                                        File destinationFilePathAfterUnzip = new File(getFilesDir(), "");

                                        Log.d(TAGG, "update destinationFilePathAfterUnzip : " + destinationFilePathAfterUnzip.getAbsolutePath());
                                        Log.i("NIR>>", "Before inserting to SSM.insertFileURI 1906" + localFile1.getName());

                                        SSM.insertFileURI(localFile1, HomeActivity.this);
                                        Log.i("NIR>>", "After inserting to SSM.insertFileURI 1909" + localFile1.getName());

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("NIR>>", "After inserting to SSM onFailure 1928" + e.getMessage());

                                    }

                                });*/

                    }

                } else {
                    Log.i("NIR>>", "updateDemoApp is null 1937");

                    try {
                        long installed = ZebraApplication.getInstance().getPackageManager()
                                .getPackageInfo(getPackageName(), 0)
                                .firstInstallTime;
                        Log.i("NIR>>", "App installed first time " + installed);
                        int dele = updateDemoAppDAO.deleteUpdateDemo();
                        Log.d(TAGG, "File delete id " + dele);

                        UpdateDemoAppModel demoAppModel = new UpdateDemoAppModel();
                        demoAppModel.setFileName(storageMetadata.getName().trim());
                        demoAppModel.setUpdateAvailable(false);
                        demoAppModel.setDownloaded(false);
                        demoAppModel.setStatus(-1);
                        //demoAppModel.setLastModifiedDate(getDateFromTimestamp(installed));
                        demoAppModel.setLastModifiedDate(getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()));
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            Log.i("NIR>>", "updateDemoApp is null start insertDemoAppFilesModelModel");

                            long insertId = updateDemoAppDAO.insertDemoAppFilesModelModel(demoAppModel);
                            Log.i("NIR>>", "updateDemoApp is null End insertDemoAppFilesModelModel");

                            Log.d(TAGG, "File inserted id " + insertId);

                        });
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.i("NIR>>", "onStart() error " + e.getMessage());
                    }

                    /*File localFile = new File(getFilesDir(), UPDATE_FILE_NAME);

                    reference

                            .getFile(localFile)

                            .addOnSuccessListener(taskSnapshot -> {
                                Log.i("NIR>>", "updateDemoApp is null addOnSuccessListener 1946");

                                int dele = updateDemoAppDAO.deleteUpdateDemo();

                                Log.d(TAGG, "File delete id " + dele);

                                UpdateDemoAppModel demoAppModel = new UpdateDemoAppModel();

                                demoAppModel.setFileName(storageMetadata.getName().trim());

                                demoAppModel.setUpdateAvailable(false);

                                demoAppModel.setDownloaded(false);

                                demoAppModel.setLastModifiedDate(getDateFromTimestamp(storageMetadata.getUpdatedTimeMillis()));


                                AppExecutors.getInstance().diskIO().execute(() -> {
                                    Log.i("NIR>>", "updateDemoApp is null start insertDemoAppFilesModelModel");

                                    long insertId = updateDemoAppDAO.insertDemoAppFilesModelModel(demoAppModel);
                                    Log.i("NIR>>", "updateDemoApp is null End insertDemoAppFilesModelModel");

                                    Log.d(TAGG, "File inserted id " + insertId);

                                });


                                List<MasterAppModel> masterAppModel = mDb.masterAppDAO().selectMasterAppModel(true);

                                if (!masterAppModel.isEmpty()) {

                                    Log.d(TAGG, "update zip available");


                                    File localFile1 = new File(getFilesDir(), UPDATE_FILE_NAME);

                                    File destinationFilePathAfterUnzip = new File(getFilesDir(), "");

                                    Log.d(TAGG, "update destinationFilePathAfterUnzip : " + destinationFilePathAfterUnzip.getAbsolutePath());

                                    Log.i("NIR>>", "updateDemoApp SSM.insertFileURI 1990");

                                    SSM.insertFileURI(localFile1, HomeActivity.this);
                                    //Log.d(TAGG, "SSM.insertFileURI worker thread task Completed HomeActivity: " + Thread.currentThread().getName());
                                    Log.i("NIR>>", "updateDemoApp SSM.insertFileURI 1994");

                                }


                            }).addOnFailureListener(new OnFailureListener() {

                                @Override

                                public void onFailure(@NonNull Exception e) {

                                    //downloadUnsuccessfulDialog(e.getMessage());
                                    Log.i("NIR>>", "onFailure  2018" + e.getMessage());

                                }

                            });*/

                }

            }

        }).addOnFailureListener(new OnFailureListener() {

            @Override

            public void onFailure(@NonNull Exception exception) {

                Log.e(TAGG, "newUpdateFileDownload onFailure " + exception.getMessage());
                Log.i("NIR>>", "newUpdateFileDownload onFailure" + exception.getMessage());
                //downloadUnsuccessfulDialog(exception.getMessage());

            }

        });


    }

    private boolean equalsTwoDates(Date remoteDate, Date localDate) {

        if (remoteDate != null && localDate != null) {

            if (remoteDate.compareTo(localDate) > 0) {

                Log.d(TAGG, "remoteDate is after localDate");

                return true;

            } else if (remoteDate.compareTo(localDate) == 0) {

                Log.d(TAGG, "remoteDate is equal to localDate");

                return true;

            }

        }

        return false;

    }

    private boolean compareTwoDates(Date remoteDate, Date localDate) {

        Log.d(TAGG, "remoteDate " + remoteDate);
        Log.d(TAGG, "localDate " + localDate);
        if (remoteDate != null && localDate != null) {

            if (remoteDate.compareTo(localDate) > 0) {

                Log.d(TAGG, "remoteDate is after localDate");

                return true;

            } else if (remoteDate.compareTo(localDate) < 0) {

                Log.d(TAGG, "remoteDate is before localDate");

                return false;

            } else if (remoteDate.compareTo(localDate) == 0) {

                Log.d(TAGG, "remoteDate is equal to localDate");

                return false;

            }

        }

        return false;

    }

    private void buttonVisibility(boolean isDownloadVisible) {
        if (isDownloadVisible) {
            Log.d("btnDownload", " : Visible 7");
            btnDownload.setVisibility(View.VISIBLE);
            btnInstalling.setVisibility(View.GONE);
        }
    }

    private void downloadDemoAppTempletNames() {
        Log.i("NIR>>", "Inside  masterDataInsert");
        try {
            if (mStorageRef == null)
                mStorageRef = FirebaseStorage.getInstance().getReference();

            if (mDb == null)
                mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

            StorageReference listRef = mStorageRef.child(Constants.UPDATE_SPECIFIC_DEFAULT_FILE_PATH);

            List<MasterAppModel> masterAppModelList = new ArrayList<>();

            AppExecutors.getInstance().mainThread().execute(() -> {
                Log.i("NIR>>", "Inside  masterDataInsert Line 2074 " + Thread.currentThread().getName());
                masterAppModelList.addAll(mDb.masterAppDAO().loadAllMasterAppModel());

                if (masterAppModelList.isEmpty()) {

                    Log.d(TAGG, "masterDataInsert called");
                    Log.i("NIR>>", "masterDataInsert called 2080");

                    listRef.listAll().addOnSuccessListener(listResult -> {
                        Log.i("NIR>>", "Inside  masterDataInsert addOnSuccessListener called Line 2083 " + Thread.currentThread().getName());

                        String[] strings = new String[listResult.getPrefixes().size()];

                        int i = 0;

                        for (StorageReference masterPrefix : listResult.getPrefixes()) {

                            strings[i] = masterPrefix.getName();

                            i++;

                            //master folder insertion

                            MasterAppModel masterAppModel = new MasterAppModel();
                            Log.i("NIR>>", "Inside  masterDataInsert masterPrefix 2098" + masterPrefix.getName());

                            masterAppModel.setMasterAppName(masterPrefix.getName());

                            masterAppModel.setUpdateAvailable(false);

                            AppExecutors.getInstance().diskIO().execute(() -> mDb.masterAppDAO().insertMasterAppModel(masterAppModel));


                        }
                        Log.i("NIR>>", "for loop completed 2108 count =" + i);

                        if (strings.length > 0) {
                            Log.i("NIR>>", "Before invoking newUpdateFileDownload");
                            newUpdateFileDownload(strings);
                            Log.i("NIR>>", "After invoking newUpdateFileDownload");

                        }

                        Log.d(TAGG, "Master table data insert success");


                    }).addOnFailureListener(e -> Log.e(TAGG, "Master table data insert Error: " + e.getMessage()));

                }

            });
        } catch (Exception e) {
            buttonVisibility(true);
            Log.e("NIR>>", "Inside  masterDataInsert " + e.getMessage());
        }


    }

    private void masterDataInsert() {
        downloadDemoAppTempletNames();
    }

    @Override

    protected void onResume() {

        super.onResume();
        //WorkManagerUtils.startBackgroundWork(this, "The task data passed from HomeActivity");

    }

    public void doBind(Context context) {
        Log.i("NIR>>", "doBind called  2200");

        Log.d("TAGG", " AppInstallReceiver workerThread Constants.UPDATE_DOWNLOAD_PATH " + Constants.UPDATE_DOWNLOAD_PATH);
        Log.d("Chandan", "doBind started " + Calendar.getInstance().getTime());
        incomingHandler = new IncomingHandler();

        Intent bindZDMServiceIntent = new Intent();

        bindZDMServiceIntent.setComponent(new ComponentName(ZDM_PACKAGE, ZDM_SERVICE_CLASS));

        try {
            ZDMServiceConnection = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Log.i("NIR>>", "onServiceConnected ZDM  2215");

                    Log.d(TAGG, "onServiceConnected");

                    Log.d("TAGG", " AppInstallReceiver onServiceConnected Constants.UPDATE_DOWNLOAD_PATH " + Constants.UPDATE_DOWNLOAD_PATH);

                    mService = new Messenger(service);
                    Log.i("NIR>>", "onServiceConnected ZDM  Before calling installNewEBApk 2222");

                    installNewEBApk();
                    Log.i("NIR>>", "onServiceConnected ZDM  After calling installNewEBApk 2222");

                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    Log.i("NIR>>", "onServiceDisconnected ZDM  2229");

                    Log.d(TAGG, "onServiceDisconnected");

                }

            };
            Log.i("NIR>>", "Before Binding to ZDM  2234");

            context.getApplicationContext().bindService(bindZDMServiceIntent, ZDMServiceConnection, Context.BIND_AUTO_CREATE);
            Log.i("NIR>>", "After Binding to ZDM  2237");

        } catch (SecurityException e) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnInstalling.setVisibility(View.GONE);

                    Log.d("btnDownload", " : Visible 8");

                    btnDownload.setVisibility(View.VISIBLE);
                }
            });


            Log.e(TAGG, "Bind Service Error " + e.getMessage());

        }

    }

    private void installNewEBApk() {

        Log.d("TAGG", " AppInstallReceiver installNewEBApk Constants.UPDATE_DOWNLOAD_PATH " + Constants.UPDATE_DOWNLOAD_PATH);

        Message msg = Message.obtain();

        msg.replyTo = new Messenger(incomingHandler);

        Bundle bundle = new Bundle();

        //Log.d(TAGG, " installFun " + EB_DOWNLOAD_INSTALL_PATH);

        if (Constants.UPDATE_DOWNLOAD_PATH == 0)

            bundle.putString("data", EB_DOWNLOAD_INSTALL_PATH);

        else if (Constants.UPDATE_DOWNLOAD_PATH == 1)

            bundle.putString("data", SHOWCASE_DOWNLOAD_UPGRADE_PATH);

        else if (Constants.UPDATE_DOWNLOAD_PATH == 2 || Constants.UPDATE_DOWNLOAD_PATH == 3)

            bundle.putString("data", EB_DOWNLOAD_UPGRADE_PATH);

        msg.setData(bundle);
        Log.i("NIR>>", "Before sending msg to ZDM  installNewEBApk 2287 " + bundle.getString("data"));


        try {
            Log.i("NIR>>", "Before  calling ZDM send API for EB mService.send(msg)");
            Log.d("Chandan", "Message send to ZDM " + Calendar.getInstance().getTime());
            mService.send(msg);
            Log.i("NIR>>", "After  calling ZDM send API for EB mService.send(msg)");


        } catch (RemoteException e) {

            Log.e(TAGG, "Send message error " + e.getMessage());

        }

    }

    private void parseJSON(String response) {

        try {

            JSONObject resObj = new JSONObject(response);

            String status = resObj.getString(STATUS);

            String message = resObj.getString(MSG);

            String result = resObj.getString(RESULT);

            Log.d(TAGG, "parseJSON result " + result);

            Log.d(TAGG, "parseJSON status " + status);

            Log.d(TAGG, "parseJSON message " + message);

            if (status.equals(COMPLETED)) {

                if (!result.isEmpty()) {

                    JSONArray jsonArray = new JSONArray(result);
                    boolean isEB_ApkDownloadedByZDM = false;
                    boolean isEB_InstalledByZDM = false;

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject obj = jsonArray.getJSONObject(i);
                        if (obj.getString(COMMAND).equals(COMMAND_DOWNLOAD) && obj.getString(STATUS).equals(SUCCESS)) {
                            isEB_ApkDownloadedByZDM = true;
                            Log.i(TAGG, "parseJSON result COMMAND_DOWNLOAD: " + obj.toString());
                        }
                        if (obj.getString(COMMAND).equals(COMMAND_INSTALL) && obj.getString(STATUS).equals(SUCCESS)) {
                            isEB_InstalledByZDM = true;
                            Log.i(TAGG, "parseJSON result COMMAND_INSTALL: " + obj.toString());
                        }
                        if (obj.getString(COMMAND).equals(COMMAND_UPGRADE) && obj.getString(STATUS).equals(SUCCESS)) {
                            isEB_InstalledByZDM = true;
                            Log.i(TAGG, "parseJSON result COMMAND_UPGRADE: " + obj.toString());
                        }
                    }
                    if (isEB_ApkDownloadedByZDM && isEB_InstalledByZDM) {
                        Log.i("NIR>>", "After operation success and Constants.UPDATE_DOWNLOAD_PATH is " + Constants.UPDATE_DOWNLOAD_PATH);

                        if (Constants.UPDATE_DOWNLOAD_PATH == 0 || Constants.UPDATE_DOWNLOAD_PATH == 3) {
                            Log.i("NIR>>", "After operation success and Constants.UPDATE_DOWNLOAD_PATH is 0 or 3 :" + Constants.UPDATE_DOWNLOAD_PATH);

                            //Constants.UPDATE_DOWNLOAD_PATH = 0;

                            Log.d(TAGG, "parseJSON result EnterpriseBrowser installed");

                            if (Utils.hasConnection(ZebraApplication.getInstance())) {
                                Log.i("NIR>>", "Before downloading Demo app downloadFileFromFirebaseStorage 2347");
                                Log.d("Chandan", "doBind end " + Calendar.getInstance().getTime());
                                downloadFileFromFirebaseStorage();

                                Log.i("NIR>>", "After downloading Demo app downloadFileFromFirebaseStorage 2351");

                            } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //noInternetDialog();
                                        try {
                                            if (downloadThread != null) {
                                                downloadThread.interrupt();
                                                Log.d("Chandan", "downloadThread Thread close true");
                                            }
                                            if (openEBThread != null) {
                                                openEBThread.interrupt();
                                                Log.d("Chandan", "openEBThread Thread close true");
                                            }
                                            if (webAppThread != null) {
                                                webAppThread.interrupt();
                                                Log.d("Chandan", "webAppThread Thread close true");
                                            }
                                        } catch (Exception e) {
                                            Log.d(TAGG, "Thread close true");
                                            Log.d("Chandan", "Thread close true");
                                        }
                                        downloadUnsuccessfulDialog();
                                        //btnInstalling.setVisibility(View.GONE);

                                        //Log.d("btnDownload", " : Visible 9");

                                        //btnDownload.setVisibility(View.VISIBLE);
                                    }
                                });


                            }

                        } else if (Constants.UPDATE_DOWNLOAD_PATH == 1) {

                            Toast.makeText(ZebraApplication.getInstance(), "Zebra Showcase updated successfully", Toast.LENGTH_LONG).show();

                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(1);

                            Log.i("NIR>>", " Constants.UPDATE_DOWNLOAD_PATH is 1 assuming showcase apk upgraded 2366");

                            Log.d(TAGG, "parseJSON result ShowcaseLauncher upgraded");

                            if (mDb == null)

                                mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                            AppUpdateModel updateModel = mDb.appUpdateDAO().selectLastAppUpdateModel(true, "ShowcaseLauncher");

                            if (updateModel != null)

                                mDb.appUpdateDAO().updateAppUpdateModelByName("ShowcaseLauncher", false);

                            //Constants.UPDATE_DOWNLOAD_PATH = 0;


                        } else if (Constants.UPDATE_DOWNLOAD_PATH == 2) {

                            Toast.makeText(ZebraApplication.getInstance(), "Enterprise Browser updated successfully", Toast.LENGTH_LONG).show();

                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(2);

                            //Constants.UPDATE_DOWNLOAD_PATH = 0;
                            Log.i("NIR>>", " Constants.UPDATE_DOWNLOAD_PATH is 2 assuming EB apk upgraded 2386");

                            Log.d(TAGG, "parseJSON result EnterpriseBrowser upgraded");

                            if (mDb == null)

                                mDb = ShowcaseDatabase.getInstance(ZebraApplication.getInstance());

                            AppUpdateModel updateModel = mDb.appUpdateDAO().selectLastAppUpdateModel(true, "EnterpriseBrowser");

                            if (updateModel != null)

                                mDb.appUpdateDAO().updateAppUpdateModelByName("EnterpriseBrowser", false);

                        }

                    } else {
                        Log.i(TAGG, " Parse JSON not processed successfully");
                        btnInstalling.setVisibility(View.GONE);

                        Log.d("btnDownload", " : Visible 10");

                        btnDownload.setVisibility(View.VISIBLE);

                        if (Constants.UPDATE_DOWNLOAD_PATH == 1) {

                            if (Utils.hasConnection(ZebraApplication.getInstance())) {

                                Toast.makeText(ZebraApplication.getInstance(), "Update Failed: An error occurred while attempting to update Zebra Showcase", Toast.LENGTH_LONG).show();

                            } else {

                                Toast.makeText(ZebraApplication.getInstance(), "Update Failed: Internet connection is required to update Zebra Showcase", Toast.LENGTH_LONG).show();

                            }

                        } else if (Constants.UPDATE_DOWNLOAD_PATH == 2) {

                            if (Utils.hasConnection(ZebraApplication.getInstance())) {

                                Toast.makeText(ZebraApplication.getInstance(), "Update Failed: An error occurred while attempting to update Enterprise Browser", Toast.LENGTH_LONG).show();

                            } else {

                                Toast.makeText(ZebraApplication.getInstance(), "Update Failed: Internet connection is required to update Enterprise Browser", Toast.LENGTH_LONG).show();

                            }

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    downloadUnsuccessfulDialog();
                                }
                            });
                        }

                    }

                    //}

                } else {
                    Log.i("NIR>>", " Parse JSON result is empty");

                    if (Constants.UPDATE_DOWNLOAD_PATH == 0 || Constants.UPDATE_DOWNLOAD_PATH == 3) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnInstalling.setVisibility(View.GONE);

                                btnDownload.setVisibility(View.VISIBLE);
                                Log.i("NIR>>", " View GONE set");

                            }
                        });


                        if (!result.isEmpty()) {
                            Log.i("NIR>>", " Reult is not empty 2423");

                            JSONArray jsonArray = new JSONArray(result);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                Log.d(TAGG, "parseJSON result " + obj.toString());

                                if (!obj.getString(STATUS).equals(SUCCESS)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            btnInstalling.setVisibility(View.GONE);

                                            Log.d("btnDownload", " : Visible 11");

                                            btnDownload.setVisibility(View.VISIBLE);
                                            Log.i("NIR>>", " Installing VIEW has GONE and Download view VISIBLE");

                                        }
                                    });


                                    String msg = obj.getString("message");

                                    if (!msg.isEmpty())
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.i("NIR>>", " Showing downloadUnsuccessfulDialog() 2452 msg: " + msg);

                                                downloadUnsuccessfulDialog();
                                            }
                                        });


                                    break;

                                }

                            }

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (downloadThread != null) {
                                            downloadThread.interrupt();
                                            Log.d("Chandan", "downloadThread Thread close true");
                                        }
                                        if (openEBThread != null) {
                                            openEBThread.interrupt();
                                            Log.d("Chandan", "openEBThread Thread close true");
                                        }
                                        if (webAppThread != null) {
                                            webAppThread.interrupt();
                                            Log.d("Chandan", "webAppThread Thread close true");
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAGG, "Thread close true");
                                        Log.d("Chandan", "Thread close true");
                                    }
                                    downloadUnsuccessfulDialog();
                                }
                            });


                        }

                    } else if (Constants.UPDATE_DOWNLOAD_PATH == 1) {

                        if (Utils.hasConnection(ZebraApplication.getInstance())) {

                            Toast.makeText(ZebraApplication.getInstance(), "Update Failed: An error occurred while attempting to update Zebra Showcase", Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(ZebraApplication.getInstance(), "Update Failed: Internet connection is required to update Zebra Showcase", Toast.LENGTH_LONG).show();

                        }

                    } else if (Constants.UPDATE_DOWNLOAD_PATH == 2) {

                        if (Utils.hasConnection(ZebraApplication.getInstance())) {

                            Toast.makeText(ZebraApplication.getInstance(), "Update Failed: An error occurred while attempting to update Enterprise Browser", Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(ZebraApplication.getInstance(), "Update Failed: Internet connection is required to update Enterprise Browser", Toast.LENGTH_LONG).show();

                        }

                    }


                }


            } else if (status.equalsIgnoreCase(ERROR) || status.equalsIgnoreCase(EXCEPTION)) {
                Log.i("NIR>>", "ZDM status ERROR or Exception: " + status);
                Log.i("NIR>>", "ZDM message ERROR or Exception: " + message);

                if (Constants.UPDATE_DOWNLOAD_PATH == 0 || Constants.UPDATE_DOWNLOAD_PATH == 3) {
                    Log.i("NIR>>", "ZDM status ERROR or Exception Constants.UPDATE_DOWNLOAD_PATH 0 or 3. Hide installing/download Btn");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnInstalling.setVisibility(View.GONE);

                            btnDownload.setVisibility(View.GONE);

                        }
                    });

                    if (!result.isEmpty()) {

                        JSONArray jsonArray = new JSONArray(result);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject obj = jsonArray.getJSONObject(i);

                            Log.d(TAGG, "parseJSON result " + obj.toString());

                            if (!obj.getString(STATUS).equals(SUCCESS)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnInstalling.setVisibility(View.GONE);

                                        Log.d("btnDownload", " : Visible 12");

                                        btnDownload.setVisibility(View.VISIBLE);

                                    }
                                });

                                String msg = obj.getString("message");

                                if (!msg.isEmpty())
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (downloadThread != null) {
                                                    downloadThread.interrupt();
                                                    Log.d("Chandan", "downloadThread Thread close true");
                                                }
                                                if (openEBThread != null) {
                                                    openEBThread.interrupt();
                                                    Log.d("Chandan", "openEBThread Thread close true");
                                                }
                                                if (webAppThread != null) {
                                                    webAppThread.interrupt();
                                                    Log.d("Chandan", "webAppThread Thread close true");
                                                }
                                            } catch (Exception e) {
                                                Log.d(TAGG, "Thread close true");
                                                Log.d("Chandan", "Thread close true");
                                            }
                                            downloadUnsuccessfulDialog();
                                        }
                                    });


                                break;

                            }

                        }

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (downloadThread != null) {
                                        downloadThread.interrupt();
                                        Log.d("Chandan", "downloadThread Thread close true");
                                    }
                                    if (openEBThread != null) {
                                        openEBThread.interrupt();
                                        Log.d("Chandan", "openEBThread Thread close true");
                                    }
                                    if (webAppThread != null) {
                                        webAppThread.interrupt();
                                        Log.d("Chandan", "webAppThread Thread close true");
                                    }
                                } catch (Exception e) {
                                    Log.d(TAGG, "Thread close true");
                                    Log.d("Chandan", "Thread close true");
                                }
                                downloadUnsuccessfulDialog();
                            }
                        });


                    }

                } else if (Constants.UPDATE_DOWNLOAD_PATH == 1) {

                    if (Utils.hasConnection(ZebraApplication.getInstance())) {

                        Toast.makeText(ZebraApplication.getInstance(), "Update Failed: An error occurred while attempting to update Zebra Showcase", Toast.LENGTH_LONG).show();

                    } else {

                        Toast.makeText(ZebraApplication.getInstance(), "Update Failed: Internet connection is required to update Zebra Showcase", Toast.LENGTH_LONG).show();

                    }

                } else if (Constants.UPDATE_DOWNLOAD_PATH == 2) {

                    if (Utils.hasConnection(ZebraApplication.getInstance())) {

                        Toast.makeText(ZebraApplication.getInstance(), "Update Failed: An error occurred while attempting to update Enterprise Browser", Toast.LENGTH_LONG).show();

                    } else {

                        Toast.makeText(ZebraApplication.getInstance(), "Update Failed: Internet connection is required to update Enterprise Browser", Toast.LENGTH_LONG).show();

                    }

                }

            } else if (status.equalsIgnoreCase(IN_PROGRESS)) {
                Log.d("NIR>>", "parseJSON result " + result);
                if (Constants.UPDATE_DOWNLOAD_PATH == 0 || Constants.UPDATE_DOWNLOAD_PATH == 3) {

                    if (!result.isEmpty()) {

                        JSONArray jsonArray = new JSONArray(result);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject obj = jsonArray.getJSONObject(i);

                            Log.d(TAGG, "parseJSON result " + obj.toString());

                            if (!obj.getString(STATUS).equals(IN_PROGRESS) && !obj.getString(STATUS).equals(SUCCESS) &&

                                    !obj.getString(STATUS).equals(STARTED)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnInstalling.setVisibility(View.GONE);

                                        Log.d("btnDownload", " : Visible 13");

                                        btnDownload.setVisibility(View.VISIBLE);
                                    }
                                });


                                String msg = obj.getString("message");

                                if (!msg.isEmpty())
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                if (downloadThread != null) {
                                                    downloadThread.interrupt();
                                                    Log.d("Chandan", "downloadThread Thread close true");
                                                }
                                                if (openEBThread != null) {
                                                    openEBThread.interrupt();
                                                    Log.d("Chandan", "openEBThread Thread close true");
                                                }
                                                if (webAppThread != null) {
                                                    webAppThread.interrupt();
                                                    Log.d("Chandan", "webAppThread Thread close true");
                                                }
                                            } catch (Exception e) {
                                                Log.d(TAGG, "Thread close true");
                                                Log.d("Chandan", "Thread close true");
                                            }
                                            downloadUnsuccessfulDialog();
                                        }
                                    });


                                break;

                            }

                        }

                    }

                } else {

                    if (!result.isEmpty()) {

                        JSONArray jsonArray = new JSONArray(result);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject obj = jsonArray.getJSONObject(i);

                            Log.d(TAGG, "parseJSON result " + obj.toString());

                            if (!obj.getString(STATUS).equals(IN_PROGRESS) && !obj.getString(STATUS).equals(SUCCESS) &&

                                    !obj.getString(STATUS).equals(STARTED)) {

                                if (Constants.UPDATE_DOWNLOAD_PATH == 1) {

                                    if (Utils.hasConnection(ZebraApplication.getInstance())) {

                                        Toast.makeText(ZebraApplication.getInstance(), "Update Failed: An error occurred while attempting to update Zebra Showcase", Toast.LENGTH_LONG).show();

                                    } else {

                                        Toast.makeText(ZebraApplication.getInstance(), "Update Failed: Internet connection is required to update Zebra Showcase", Toast.LENGTH_LONG).show();

                                    }

                                } else if (Constants.UPDATE_DOWNLOAD_PATH == 2) {

                                    if (Utils.hasConnection(ZebraApplication.getInstance())) {

                                        Toast.makeText(ZebraApplication.getInstance(), "Update Failed: An error occurred while attempting to update Enterprise Browser", Toast.LENGTH_LONG).show();

                                    } else {

                                        Toast.makeText(ZebraApplication.getInstance(), "Update Failed: Internet connection is required to update Enterprise Browser", Toast.LENGTH_LONG).show();

                                    }

                                }


                            }

                        }

                    }

                }

            }

        } catch (JSONException e) {

            Log.e(TAGG, "parseJSON Error " + e.getMessage());

            if (Constants.UPDATE_DOWNLOAD_PATH == 0 || Constants.UPDATE_DOWNLOAD_PATH == 3) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            if (downloadThread != null) {
                                downloadThread.interrupt();
                                Log.d("Chandan", "downloadThread Thread close true");
                            }
                            if (openEBThread != null) {
                                openEBThread.interrupt();
                                Log.d("Chandan", "openEBThread Thread close true");
                            }
                            if (webAppThread != null) {
                                webAppThread.interrupt();
                                Log.d("Chandan", "webAppThread Thread close true");
                            }
                        } catch (Exception e) {
                            Log.d(TAGG, "Thread close true");
                            Log.d("Chandan", "Thread close true");
                        }
                        downloadUnsuccessfulDialog();
                    }
                });


            } else if (Constants.UPDATE_DOWNLOAD_PATH == 1) {

                if (Utils.hasConnection(ZebraApplication.getInstance())) {

                    Toast.makeText(ZebraApplication.getInstance(), "Update Failed: An error occurred while attempting to update Zebra Showcase", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(ZebraApplication.getInstance(), "Update Failed: Internet connection is required to update Zebra Showcase", Toast.LENGTH_LONG).show();

                }

            } else if (Constants.UPDATE_DOWNLOAD_PATH == 2) {

                if (Utils.hasConnection(ZebraApplication.getInstance())) {

                    Toast.makeText(ZebraApplication.getInstance(), "Update Failed: An error occurred while attempting to update Enterprise Browser", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(ZebraApplication.getInstance(), "Update Failed: Internet connection is required to update Enterprise Browser", Toast.LENGTH_LONG).show();

                }

            }

        }


    }

    private void downloadUnsuccessfulDialog() {

        btnInstalling.setVisibility(View.GONE);

        Log.d(TAGG, "downloadUnsuccessfulDialog");

        btnDownload.setVisibility(View.VISIBLE);
        if (dialog != null) {

            dialog.dismiss();

        } else {

            dialog = new Dialog(this);
        }

        Button closeBtn;

        TextView msgTxt;

        dialog.setContentView(R.layout.dialog_download_unsuccessfull);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setCancelable(false);

        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;


        closeBtn = dialog.findViewById(R.id.close_btn);

        msgTxt = dialog.findViewById(R.id.download_message);

        String message = "";

        if (Utils.hasConnection(HomeActivity.this)) {

            message = "An error occurred while attempting to download Zebra Showcase.";

        } else {

            message = "Internet connection is required to download Zebra Showcase.";

        }

        //String[] messageArr = message.split(":");

        StringBuilder s = new StringBuilder();

        /*for (String ms : messageArr) {

            Log.d(TAGG, "messageArr " + ms);

            if (!ms.equalsIgnoreCase("FileAction"))

                s.append(ms);

        }*/
        msgTxt.setText(message);

        /*if (s.length() > 0) {

            String s1 = s.toString().replace("Error", "");

            msgTxt.setText(s1);

        } else {

            msgTxt.setText(message);

        }*/


        closeBtn.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                dialog.dismiss();
                dialog = null;

            }

        });


        dialog.show();


    }

    @Override

    public void onNetworkChange(boolean isConnected) {

        if (isConnected) {

            List<ShowcaseAppAnalytics> analyticsList = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appAnalyticsDAO().getAllShowcaseAppAnalytics();

            Log.d(TAGG, "Analytics analyticsList " + analyticsList);

            if (analyticsList != null && analyticsList.size() > 0) {

                for (ShowcaseAppAnalytics appAnalytics : analyticsList) {

                    Bundle bundle = new Bundle();

                    bundle.putString("device_serial_no", appAnalytics.getDeviceSerialNo());

                    bundle.putString("device_name", appAnalytics.getDeviceName());

                    bundle.putString("app_name", appAnalytics.getAppName());

                    bundle.putString("activity_date", appAnalytics.getActivityDate());

                    Log.d(TAGG, "Analytics bundle " + bundle);

                    Log.d(TAGG, "Analytics even name " + appAnalytics.getEventName());

                    if (mFirebaseAnalytics != null) {

                        mFirebaseAnalytics.logEvent(appAnalytics.getEventName(), bundle);

                        int del = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appAnalyticsDAO().deleteShowcaseAppAnalyticsById(appAnalytics.getId());

                        Log.d(TAGG, "Delete no " + del);

                    }

                }

            }

        }

    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finishAndRemoveTask();
//        //moveTaskToBack(true);
//    }

    // 2.0 and above
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // Before 2.0
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class IncomingHandler extends Handler {


        @Override

        public void handleMessage(Message msg) {


            String response = msg.getData().getString(RESPONSE);

            Log.d(TAGG, "parseJSON response " + response);
            Log.d("Chandan", "response from ZDM " + response);
            Log.d("Chandan", "Message received from ZDM " + Calendar.getInstance().getTime());
            parseJSON(response);


        }


    }


}