package com.zebra.showcaseapp.contentprovider;

import static com.zebra.showcaseapp.util.Constants.ANALYTICS_DIR;
import static com.zebra.showcaseapp.util.Constants.ANALYTICS_ITEM;
import static com.zebra.showcaseapp.util.Constants.AUTHORITY;
import static com.zebra.showcaseapp.util.Constants.BETA_APP_DIR;
import static com.zebra.showcaseapp.util.Constants.BETA_APP_ITEM;
import static com.zebra.showcaseapp.util.Constants.CODE_DIR;
import static com.zebra.showcaseapp.util.Constants.CODE_ITEM;
import static com.zebra.showcaseapp.util.Constants.EB_DOWNLOAD_UPGRADE_PATH;
import static com.zebra.showcaseapp.util.Constants.MASTER_APP_DIR;
import static com.zebra.showcaseapp.util.Constants.MASTER_APP_ITEM;
import static com.zebra.showcaseapp.util.Constants.SETTING_APP_DIR;
import static com.zebra.showcaseapp.util.Constants.SETTING_APP_ITEM;
import static com.zebra.showcaseapp.util.Constants.SHOWCASE_DOWNLOAD_UPGRADE_PATH;
import static com.zebra.showcaseapp.util.Constants.UPDATE_APP_DIR;
import static com.zebra.showcaseapp.util.Constants.UPDATE_APP_ITEM;
import static com.zebra.showcaseapp.util.Constants.UPDATE_DEMO_ZIP_DIR;
import static com.zebra.showcaseapp.util.Constants.UPDATE_DEMO_ZIP_ITEM;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.zebra.showcaseapp.BuildConfig;
import com.zebra.showcaseapp.data.AppSettingsModel;
import com.zebra.showcaseapp.data.AppUpdateDAO;
import com.zebra.showcaseapp.data.AppUpdateModel;
import com.zebra.showcaseapp.data.BetaAppDAO;
import com.zebra.showcaseapp.data.BetaAppModel;
import com.zebra.showcaseapp.data.ContentProviderDao;
import com.zebra.showcaseapp.data.ContentProviderModel;
import com.zebra.showcaseapp.data.DemoAvailabilityModel;
import com.zebra.showcaseapp.data.MasterAppDAO;
import com.zebra.showcaseapp.data.MasterAppModel;
import com.zebra.showcaseapp.data.ShowcaseAppAnalytics;
import com.zebra.showcaseapp.data.ShowcaseDatabase;
import com.zebra.showcaseapp.data.UpdateDemoAppDAO;
import com.zebra.showcaseapp.data.UpdateDemoAppModel;
import com.zebra.showcaseapp.ui.HomeActivity;
import com.zebra.showcaseapp.ui.ZebraApplication;
import com.zebra.showcaseapp.util.Constants;
import com.zebra.showcaseapp.util.Utils;
import com.zebra.showcaseapp.util.WorkManagerUtils;

import java.io.File;
import java.util.Base64;
import java.util.UUID;

public class ShowcaseContentProvider extends ContentProvider {

    public static final Uri URI_LAUNCHER = Uri.parse("content://" + AUTHORITY + "/" + ContentProviderModel.TABLE_NAME);

    /**
     * The URI matcher.
     */
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        MATCHER.addURI(AUTHORITY, ContentProviderModel.TABLE_NAME, CODE_DIR);
        MATCHER.addURI(AUTHORITY, ContentProviderModel.TABLE_NAME + "/*", CODE_ITEM);

        MATCHER.addURI(AUTHORITY, DemoAvailabilityModel.TABLE_NAME, CODE_DIR);
        MATCHER.addURI(AUTHORITY, DemoAvailabilityModel.TABLE_NAME + "/*", CODE_ITEM);

        MATCHER.addURI(AUTHORITY, ShowcaseAppAnalytics.TABLE_NAME, ANALYTICS_DIR);
        MATCHER.addURI(AUTHORITY, ShowcaseAppAnalytics.TABLE_NAME + "/*", ANALYTICS_ITEM);

        MATCHER.addURI(AUTHORITY, MasterAppModel.TABLE_NAME, MASTER_APP_DIR);
        MATCHER.addURI(AUTHORITY, MasterAppModel.TABLE_NAME + "/*", MASTER_APP_ITEM);

        MATCHER.addURI(AUTHORITY, AppUpdateModel.TABLE_NAME, UPDATE_APP_DIR);
        MATCHER.addURI(AUTHORITY, AppUpdateModel.TABLE_NAME + "/*", UPDATE_APP_ITEM);

        MATCHER.addURI(AUTHORITY, AppSettingsModel.TABLE_NAME, SETTING_APP_DIR);
        MATCHER.addURI(AUTHORITY, AppSettingsModel.TABLE_NAME + "/*", SETTING_APP_ITEM);

        MATCHER.addURI(AUTHORITY, UpdateDemoAppModel.TABLE_NAME, UPDATE_DEMO_ZIP_DIR);
        MATCHER.addURI(AUTHORITY, UpdateDemoAppModel.TABLE_NAME + "/*", UPDATE_DEMO_ZIP_ITEM);

        MATCHER.addURI(AUTHORITY, BetaAppModel.TABLE_NAME, BETA_APP_DIR);
        MATCHER.addURI(AUTHORITY, BetaAppModel.TABLE_NAME + "/*", BETA_APP_ITEM);

    }

    private final String TAG = ShowcaseContentProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int code = MATCHER.match(uri);
        Log.v("TAGG", "Query_code " + String.valueOf(code));
        Log.v("TAGG", "uri " + String.valueOf(uri));
        Log.v("TAGG", "selection " + selection);
        String callingPackageName = getContext().getPackageManager().getNameForUid(
                Binder.getCallingUid());
        if (code == CODE_DIR || code == CODE_ITEM) {
            Context context = getContext();
            if (context == null) {
                return null;
            }

            Log.d("SignatureCheck : ", "isZebraPrivilegedKeySignedSignatureForPackage : "
                    + Utils.isZebraPrivilegedKeySignedSignatureForPackage(context.getApplicationContext(),
                    callingPackageName));

            Log.d("SignatureCheck : ", "isPackageWhitelisted : "
                    + Utils.isPackageWhitelisted(context.getApplicationContext(),
                    BuildConfig.find_packagename));

            ContentProviderDao contentProviderDao = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).launcherDao();
            Cursor cursorLauncher = null;
            Log.v("TAGG", "isZebraPrivilegedKeySignedSignatureForPackage Datawedge " + Utils.isZebraPrivilegedKeySignedSignatureForPackage(
                    context.getApplicationContext(), BuildConfig.find_packagename_dw));
            Log.v("TAGG", "isZebraPrivilegedKeySignedSignatureForPackage EB " + Utils.isZebraPrivilegedKeySignedSignatureForPackage(
                    context.getApplicationContext(), BuildConfig.find_packagename));
            Log.v("TAGG", "callingPackageName " + callingPackageName);
            if ((callingPackageName.equals(BuildConfig.find_packagename) ||
                    callingPackageName.equals(BuildConfig.APPLICATION_ID))
                    && Utils.isPackageWhitelisted(context.getApplicationContext(),
                    callingPackageName)) {

                if (code == CODE_DIR) {
                    cursorLauncher = contentProviderDao.selectAll();
                } else {

                    if (selection != null && selection.equals("uniqueid")) {

                        String uuid = UUID.randomUUID().toString();
                        String uniqueId = uuid + "#" + callingPackageName;
                        String encodedString = Base64.getEncoder().encodeToString(uniqueId.getBytes());

                        Log.d("Ritam_Mindteck : ", "Token : " + encodedString);

                        ContentValues values = new ContentValues();
                        values.put(ContentProviderModel.PACKAGE_NAME, callingPackageName);
                        values.put(ContentProviderModel.UUID, uuid);
                        values.put(ContentProviderModel.UNIQUEID, encodedString);

                        if (selectionArgs != null)
                            cursorLauncher = contentProviderDao.selectById(selectionArgs[0]);
                        if (cursorLauncher != null) {
                            if (cursorLauncher.getCount() == 0) {

                                contentProviderDao.insert(ContentProviderModel.fromContentValues(values));

                            } else {

                                contentProviderDao.updateIsDemo(callingPackageName, uuid, encodedString);

                            }
                        }
                        if (selectionArgs != null)
                            cursorLauncher = contentProviderDao.selectById(selectionArgs[0]);
                        if (cursorLauncher != null)
                            cursorLauncher.setNotificationUri(context.getContentResolver(), uri);

                        return cursorLauncher;
                    } else if (selection != null && selection.equals("showcasedemoRunningOrNot")) {

                        if (selectionArgs != null)
                            cursorLauncher = contentProviderDao.selectById(selectionArgs[0]);

                    } else if (selection != null && selection.equals("demoAvailableOrNot")) {

                        Log.d("Delete File", "Entering Here 1");
                        File localFile = new File(context.getFilesDir(), Constants.MASTER_DEMO_FILE_NAME);
                        boolean deletedOrNot = localFile.delete();

                        Log.d("TAGG", "deletedOrNot " + String.valueOf(deletedOrNot));
                        //File localFile1 = new File(ZebraApplication.getInstance().getFilesDir(), "masterDemoApp");
                        //boolean deleteMasterFile = Utils.deleteDirectory(localFile1);
                        //Log.v("TAGG", "master folder deletedOrNot " + deleteMasterFile);

                        cursorLauncher = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).demoAvailabalityDAO().getDemoAvailableOrNot();

                        return cursorLauncher;

                    } else if (selection != null && selection.equals("demoAvailableOrNot_RESET")) {

                        Log.d("NIR", "Called demoAvailableOrNot_RESET");
                        DemoAvailabilityModel model = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).demoAvailabalityDAO().isDemoAvailableOrNot();
                        if (model != null) {
                            Log.d("NIR", "Deleting ROW from DemoAvailable");
                            int deleteCount = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).demoAvailabalityDAO().deleteData(model);
                            Log.d("NIR", "Deleted ROW from DemoAvailable " + deleteCount);
                        } else {
                            Log.d("NIR", "DemoAvailabilityModel is Null");
                        }

                    }
                }

                return cursorLauncher;

            } else if (callingPackageName.equals(BuildConfig.find_packagename_dw) &&
                    Utils.isPackageWhitelisted(context.getApplicationContext(),
                            callingPackageName)) {

                Log.d("cursor", "Deleted Status : else if");
                Log.d("cursor", "selectionArgs[0] " + selectionArgs[0]);
                Log.d("cursor", "selectionArgs[1] " + selectionArgs[1]);
                cursorLauncher = contentProviderDao.selectById(selectionArgs[0], selectionArgs[1]);

                Log.d("cursor", "Deleted Status : above if");

                /*if (contentProviderDao.selectId(selectionArgs[0], selectionArgs[1]) != null) {

                    Log.d("cursor","Deleted Status : not null");

                    int deletedStatus = contentProviderDao.deleteById(contentProviderDao.selectId(selectionArgs[0], selectionArgs[1]).id);
                    Log.d("cursor","Deleted Status : "+deletedStatus);
                }*/
                Log.d("cursor", "cursorLauncher " + cursorLauncher);
                return cursorLauncher;

            } else {

                return null;

            }
        } else if (code == MASTER_APP_DIR || code == MASTER_APP_ITEM) {
            Log.v("TAGG", "MASTER_APP_DIR " + code);
            Context context = getContext();
            if (context == null) {
                return null;
            }
            if ((callingPackageName.equals(BuildConfig.find_packagename) ||
                    callingPackageName.equals(BuildConfig.APPLICATION_ID))
                    && Utils.isPackageWhitelisted(context.getApplicationContext(), callingPackageName)) {
                if (selectionArgs != null) {
                    Log.v("TAGG", "selectionArgs " + selectionArgs[0]);
                    MasterAppDAO masterAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).masterAppDAO();
                    Cursor masterAppModel = masterAppDAO.loadAllMasterAppModel1(selectionArgs[0], true);
                    return masterAppModel;
                } else {
                    Cursor masterCursor;
                    MasterAppDAO masterAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).masterAppDAO();
                    masterCursor = masterAppDAO.loadAllMasterAppModel(true);
                    Log.d("TAGG", "masterCursor " + DatabaseUtils.dumpCursorToString(masterCursor));
                    masterCursor.setNotificationUri(context.getContentResolver(), uri);
                    return masterCursor;
                }
            }
            return null;
        } else if (code == UPDATE_APP_DIR || code == UPDATE_APP_ITEM) {
            Log.v("TAGG", "UPDATE_APP_DIR " + code);
            Context context = getContext();
            if (context == null) {
                return null;
            }
            if ((callingPackageName.equals(BuildConfig.find_packagename) ||
                    callingPackageName.equals(BuildConfig.APPLICATION_ID))
                    && Utils.isPackageWhitelisted(context.getApplicationContext(), callingPackageName)) {
                if (selectionArgs != null) {
                    Log.v("TAGG", "selectionArgs " + selectionArgs[0]);
                    AppUpdateModel model = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appUpdateDAO().selectLastAppUpdateModel(true, selectionArgs[0].trim());
                    Log.v("TAGG", "selectionArgs appUpdateModel " + model);
                    if (model != null){
                        ContextCompat.getMainExecutor(context).execute(new Runnable() {
                            @Override
                            public void run() {
                                if (model.getAppName().equalsIgnoreCase("ShowcaseLauncher")) {
                                    Constants.UPDATE_DOWNLOAD_PATH = 1;
                                    SHOWCASE_DOWNLOAD_UPGRADE_PATH =
                                            "download('" + model.getAppLink().trim() + "', '/sdcard/ShowcaseDemo.apk'); upgradePackage('/sdcard/ShowcaseDemo.apk',1)";
                                    Log.v("TAGG", "selectionArgs SHOWCASE_DOWNLOAD_UPGRADE_PATH "+ SHOWCASE_DOWNLOAD_UPGRADE_PATH);
                                } else if (model.getAppName().equalsIgnoreCase("EnterpriseBrowser")) {
                                    Constants.UPDATE_DOWNLOAD_PATH = 2;
                                    Log.v("TAGG", "selectionArgs EB_DOWNLOAD_UPGRADE_PATH "+ EB_DOWNLOAD_UPGRADE_PATH);
                                }
                                Log.v("TAGG", "selectionArgs doBind Called start ");
                                HomeActivity.getInstance().doBind(context);
                                Log.v("TAGG", "selectionArgs doBind Called end ");
                            }
                        });
                    }
                } else {
                    AppUpdateDAO appUpdateDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appUpdateDAO();
                    Cursor appUpdateModel = appUpdateDAO.selectLastAppUpdateModel(true);
                    Log.d("TAGG", "appUpdateModel " + DatabaseUtils.dumpCursorToString(appUpdateModel));
                    appUpdateModel.setNotificationUri(context.getContentResolver(), uri);
                    return appUpdateModel;
                }

            }
            return null;
        } else if (code == UPDATE_DEMO_ZIP_DIR || code == UPDATE_DEMO_ZIP_ITEM) {
            Log.v("TAGG", "UPDATE_ZIP_DIR " + code);
            Context context = getContext();
            if (context == null) {
                return null;
            }
            if ((callingPackageName.equals(BuildConfig.find_packagename) ||
                    callingPackageName.equals(BuildConfig.APPLICATION_ID))
                    && Utils.isPackageWhitelisted(context.getApplicationContext(), callingPackageName)) {
                if (selectionArgs != null) {
                    Log.v("TAGG", "selectionArgs " + selectionArgs[0]);
                    Log.v("TAGG", "selection " + selection);

                    if (selection == null)
                        WorkManagerUtils.startBackgroundWork(context, selectionArgs[0], selection, "demoAppUpdateDownload");
                    else if (selection.equalsIgnoreCase("cancel_download")){
                        WorkManagerUtils.cancelDownload();
                    }


                } else {
                    UpdateDemoAppDAO updateDemoAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).updateDemoAppDAO();
                    Cursor updateDemoAppModel = updateDemoAppDAO.getDemoAppFilesModel();
                    Log.d("TAGG", "updateDemoAppModel " + DatabaseUtils.dumpCursorToString(updateDemoAppModel));
                    updateDemoAppModel.setNotificationUri(context.getContentResolver(), uri);
                    return updateDemoAppModel;
                }

            }
            return null;
        }else if (code == BETA_APP_DIR || code == BETA_APP_ITEM) {
            Log.v("TAGG", "BETA_APP_DIR " + code);
            Log.v("TAGG", "selection " + selection);
            Context context = getContext();
            if (context == null) {
                return null;
            }
            Log.v("TAGG", "after context selection " + selection);
            Log.v("TAGG", "after context callingPackageName " + callingPackageName.equals(BuildConfig.find_packagename));
            Log.v("TAGG", "after context selectionArgs " + selectionArgs[0]);
            if ((callingPackageName.equals(BuildConfig.find_packagename) ||
                    callingPackageName.equals(BuildConfig.APPLICATION_ID))
                    && Utils.isPackageWhitelisted(context.getApplicationContext(), callingPackageName)) {
                if (selectionArgs != null) {
                    if (code == BETA_APP_ITEM) {
                        Log.v("TAGG", "BETA_APP_ITEM selectionArgs " + selectionArgs[0]);
                        Log.v("TAGG", "BETA_APP_ITEM selection " + selection);
                        if (selection == null) {
                            BetaAppDAO betaAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).betaAppDAO();
                            Cursor betaAppModel = betaAppDAO.getDemoBetaAppModel(selectionArgs[0].trim());
                            BetaAppModel betaAppModel1 = betaAppDAO.loadBetaAppModelByName(selectionArgs[0].trim());
                            //Log.d("TAGG", "betaAppModel " + DatabaseUtils.dumpCursorToString(betaAppModel));
                            Log.d("TAGG", "betaAppModel1 " + betaAppModel1);
                            betaAppModel.setNotificationUri(context.getContentResolver(), uri);
                            return betaAppModel;
                        }else{
                            Log.v("TAGG", "else BETA_APP_ITEM selection " + selection);
                            Log.v("TAGG", "else BETA_APP_ITEM selectionArgs[0] " + selectionArgs[0]);
                            WorkManagerUtils.startBackgroundWork(context, selectionArgs[0], selection, "betaAppDownload");
                        }
                    }

                }

            }
            return null;
        }else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case CODE_DIR: {
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + ContentProviderModel.TABLE_NAME;
            }
            case CODE_ITEM: {
                return "vnd.android.cursor.item/" + AUTHORITY + "." + ContentProviderModel.TABLE_NAME;
            }
            case ANALYTICS_DIR: {
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + ShowcaseAppAnalytics.TABLE_NAME;
            }
            case ANALYTICS_ITEM: {
                return "vnd.android.cursor.item/" + AUTHORITY + "." + ShowcaseAppAnalytics.TABLE_NAME;
            }
            case MASTER_APP_DIR: {
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + MasterAppModel.TABLE_NAME;
            }
            case MASTER_APP_ITEM: {
                return "vnd.android.cursor.item/" + AUTHORITY + "." + MasterAppModel.TABLE_NAME;
            }

            case UPDATE_APP_DIR: {
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + AppUpdateModel.TABLE_NAME;
            }
            case UPDATE_APP_ITEM: {
                return "vnd.android.cursor.item/" + AUTHORITY + "." + AppUpdateModel.TABLE_NAME;
            }
            case SETTING_APP_DIR: {
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + AppSettingsModel.TABLE_NAME;
            }
            case SETTING_APP_ITEM: {
                return "vnd.android.cursor.item/" + AUTHORITY + "." + AppSettingsModel.TABLE_NAME;
            }
            case UPDATE_DEMO_ZIP_DIR: {
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + UpdateDemoAppModel.TABLE_NAME;
            }
            case UPDATE_DEMO_ZIP_ITEM: {
                return "vnd.android.cursor.item/" + AUTHORITY + "." + UpdateDemoAppModel.TABLE_NAME;
            }
            case BETA_APP_DIR: {
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + BetaAppModel.TABLE_NAME;
            }
            case BETA_APP_ITEM: {
                return "vnd.android.cursor.item/" + AUTHORITY + "." + BetaAppModel.TABLE_NAME;
            }

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int code = MATCHER.match(uri);
        Log.d("TAGG", "insert Query_uri " + uri);
        Log.d("TAGG", "insert Query_code " + code);
        Context context = getContext();
        if (code == CODE_DIR) {
            if (context == null) {
                return null;
            }
            long id = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).launcherDao().insert(ContentProviderModel.fromContentValues(values));
            context.getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, id);
        } else if (code == ANALYTICS_DIR || code == ANALYTICS_ITEM) {
            if (context == null) {
                return null;
            }
            Log.d("TAGG", "insert Query_appName " + values.getAsString("appName"));
            Log.d("TAGG", "insert Query_eventName " + values.getAsString("eventName"));
            long id1 = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appAnalyticsDAO().insertShowcaseAppAnalytics(ShowcaseAppAnalytics.fromContentValues(values, getContext()));
            Log.d("TAGG", "insert Query_id1 " + id1);
            context.getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, id1);
        }else if (code == SETTING_APP_DIR || code == SETTING_APP_ITEM) {
            if (context == null) {
                return null;
            }
            Log.d("TAGG", "insert settingsName " + values.getAsString("settingsName"));
            Log.d("TAGG", "insert isEnable " + values.getAsString("isEnable"));
            long id1 = 0;
            if (values.getAsString("settingsName") != null && !values.getAsString("settingsName").isEmpty()){
                AppSettingsModel appSettingsModel = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appSettingsDAO().lastAppSettingsModel(values.getAsString("settingsName").trim());
                if (appSettingsModel != null){
                    id1 = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appSettingsDAO()
                            .updateAppSettingsModelByName(values.getAsString("settingsName").trim(),
                                    Boolean.parseBoolean(values.getAsString("isEnable").trim())
                            );
                    Log.d("TAGG", "update Settings " + id1);
                }else {
                    id1 = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).appSettingsDAO().insertAppSettingsModel(AppSettingsModel.fromContentValues(values));
                    Log.d("TAGG", "insert Settings " + id1);
                }
            }

            context.getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, id1);
        }else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch (MATCHER.match(uri)) {
            case CODE_DIR:
                throw new IllegalArgumentException("Invalid URI, cannot update without ID: " + uri);
            case CODE_ITEM:
                final Context context = getContext();
                if (context == null) {
                    return 0;
                }
                final int count = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).launcherDao().deleteById(ContentUris.parseId(uri));
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int code = MATCHER.match(uri);
        Log.d("TAGG", "Update Query_code " + code);
        if (code == CODE_DIR) {
            throw new IllegalArgumentException("Invalid URI, cannot update without ID: " + uri);
        } else if (code == CODE_ITEM) {
            Context context = getContext();
            if (context == null) {
                return 0;
            }
            String callingPackageName = getContext().getPackageManager().getNameForUid(
                    Binder.getCallingUid());
            int count = 0;
            if (callingPackageName.equals(BuildConfig.find_packagename)) {

                final ContentProviderModel contentProviderModel = ContentProviderModel.fromContentValues(values);
                contentProviderModel.id = ContentUris.parseId(uri);
                count = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).launcherDao().update(contentProviderModel);
                context.getContentResolver().notifyChange(uri, null);

            }
            return count;
        } else if (code == MASTER_APP_DIR || code == MASTER_APP_ITEM) {
            Log.v("TAGG", "Update MASTER_APP_CODE " + code);
            Context context1 = getContext();
            if (context1 == null) {
                return 0;
            }
            String callingPackage = getContext().getPackageManager().getNameForUid(
                    Binder.getCallingUid());
            if (callingPackage.equals(BuildConfig.find_packagename)) {

                if (selectionArgs != null) {
                    Log.v("TAGG", "Update selectionArgs " + selectionArgs[0]);
                    MasterAppDAO masterAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).masterAppDAO();
                    MasterAppModel masterAppModel = masterAppDAO.loadAllMasterAppModel(selectionArgs[0], true);
                    Log.v("TAGG", "Update masterAppModel " + masterAppModel);
                    if (masterAppModel != null) {
                        int updateCount = 0;
                        updateCount += masterAppDAO.updateMasterAppModel(masterAppModel.getId(), false);
                        context1.getContentResolver().notifyChange(uri, null);
                        Log.v("TAGG", "Master folder updated " + updateCount);
                        return updateCount;
                    }
                    return 0;
                } else {
                    Log.v("TAGG", "Update folder updated ");
                    UpdateDemoAppDAO updateDemoAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).updateDemoAppDAO();
                    UpdateDemoAppModel updateDemoAppModel = updateDemoAppDAO.selectLastDemoAppFilesModel(true);
                    Log.v("TAGG", "Update updateDemoAppModel " + updateDemoAppModel);
                    int ii = 0;
                    if (updateDemoAppModel != null) {
                        ii = updateDemoAppDAO.updateDemoAppFilesModelByName(updateDemoAppModel.getFileName().trim(), false, false, -1);
                    }
                    context1.getContentResolver().notifyChange(uri, null);
                    Log.v("TAGG", "Update folder ii " + ii);
                    return ii;
                }

            }
        } else if (code == UPDATE_DEMO_ZIP_DIR || code == UPDATE_DEMO_ZIP_ITEM) {
            Log.v("TAGG", "Update UPDATE_DEMO_ZIP_DIR " + code);
            Context context1 = getContext();
            if (context1 == null) {
                return 0;
            }
            String callingPackage = getContext().getPackageManager().getNameForUid(
                    Binder.getCallingUid());
            if (callingPackage.equals(BuildConfig.find_packagename)) {
                UpdateDemoAppDAO updateDemoAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).updateDemoAppDAO();
                UpdateDemoAppModel updateDemoAppModel = updateDemoAppDAO.selectLastDemoAppFilesModel(true);
                Log.v("TAGG", "Update updateDemoAppModel " + updateDemoAppModel);
                int ii = 0;
                File localFile = new File(ZebraApplication.getInstance().getFilesDir(), Constants.UPDATE_FILE_NAME);
                try{
                    boolean deletedOrNot = localFile.delete();
                    Log.v("TAGG", "Update zip deletedOrNot " + deletedOrNot);
                }catch (Exception e){
                    Log.d("TAGG", "Update zip deletedOrNot error " + e.getMessage());
                }

                if (updateDemoAppModel != null) {
                    ii = updateDemoAppDAO.updateDemoAppFilesModelByName(updateDemoAppModel.getFileName().trim(), false, false, -1);
                }
                context1.getContentResolver().notifyChange(uri, null);
                Log.v("TAGG", "Update folder ii " + ii);
                return ii;
            }
        }else if (code == BETA_APP_DIR || code == BETA_APP_ITEM) {
            Context context1 = getContext();
            if (context1 == null) {
                return 0;
            }
            if (selectionArgs != null) {
                Log.v("TAGG", "Update selectionArgs " + selectionArgs[0]);
                BetaAppDAO betaAppDAO = ShowcaseDatabase.getInstance(ZebraApplication.getInstance()).betaAppDAO();
                BetaAppModel betaAppModel = betaAppDAO.loadBetaAppModelByName(selectionArgs[0].trim());
                Log.v("TAGG", "Update betaAppModel " + betaAppModel);
                File localFile = new File(ZebraApplication.getInstance().getFilesDir(), selectionArgs[0].trim().concat("Beta.zip"));
                try{
                    boolean deletedOrNot = localFile.delete();
                    Log.v("TAGG", "beta zip deletedOrNot " + deletedOrNot);
                    File localFile1 = new File(ZebraApplication.getInstance().getFilesDir(), selectionArgs[0].trim());
                    boolean deleteMasterFile = Utils.deleteDirectory(localFile1);
                    Log.v("TAGG", "beta folder deletedOrNot " + deleteMasterFile);
                }catch (Exception e){
                    Log.d("TAGG", "beta zip deletedOrNot error " + e.getMessage());
                }
                if (betaAppModel != null) {
                    int updateCount = 0;
                    updateCount += betaAppDAO.updateBetaAppModelByName(selectionArgs[0].trim(), false, -1);
                    context1.getContentResolver().notifyChange(uri, null);
                    Log.v("TAGG", "Beta updated " + updateCount);
                    return updateCount;
                }
                return 0;
            }
            return 0;
        }else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return 0;
    }

}