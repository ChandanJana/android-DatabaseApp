package com.zebra.showcaseapp.util;

public class Constants {

    public static final String DEMO_MODE = "DEMO_MODE";
    public static final String FIND_EB_LOWEST_VERSION_TO_SUPPORT = "3.4.0.0";
    public static final String DIRECT_OPEN_EB = "direct_open_eb";
    public static final String DATA_PERSIST_REQUIRED = "data_persist_required";
    public static final String TARGET_APP_PACKAGE = "target_app_package";
    public static final String DATA_NAME = "data_name";
    public static final String DATA_VALUE = "data_value";
    //SSM
    public static final String FILE_AUTHORITY = "com.zebra.showcaseapp.provider";
    public static final String AUTHORITY_FILE = "content://com.zebra.securestoragemanager.securecontentprovider/files/";
    public static final String SIGNATURE = "MIIC5DCCAcwCAQEwDQYJKoZIhvcNAQEFBQAwNzEWMBQGA1UEAwwNQW5kcm9pZ"; // Replace with target app signature
    //public static final String DW_SIGNATURE = "MIID6TCCAtGgAwIBAgIJAJqOWgV07V6QMA0GCSqGSIb3DQEBBQUAMIGKMQswCQYDVQQGEwJVUzERMA8GA1UECAwITmV3IFlvcmsxEzARBgNVBAcMCkhvbHRzdmlsbGUxGTAXBgNVBAsMEE1vYmlsZSBDb21wdXRpbmcxGzAZBgNVBAoMElplYnJhIFRlY2hub2xvZ2llczEbMBkGA1UEAwwSQ29tbW9uIEFuZHJvaWQgS2V5MB4XDTE4MTEyOTIwNTU0OVoXDTQ2MDQxNjIwNTU0OVowgYoxCzAJBgNVBAYTAlVTMREwDwYDVQQIDAhOZXcgWW9yazETMBEGA1UEBwwKSG9sdHN2aWxsZTEZMBcGA1UECwwQTW9iaWxlIENvbXB1dGluZzEbMBkGA1UECgwSWmVicmEgVGVjaG5vbG9naWVzMRswGQYDVQQDDBJDb21tb24gQW5kcm9pZCBLZXkwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCwA3jRXXIKoF3lU4gBU0XM7LAzAc9pyxHFbqRoy+uGXzIdPGKNQVqyeQKT1gLnQWz7Nz+AiFbB3axfjpGZy8Z5gakp1i8avx+ZwwST++ufEs+jhKF9XJThOSc9MG5Cgk9+ByAZjBLwg0XX+IPqTO+xcxG8BlNIDOd1Ik+99MQezGn8mfS/1MMC1ynZRC+18z9VkD+FZeFYSHU1OoFBMhCjMKYApa08ZuPk/+lQTfDTs+JPxjsBqy906vt+PnDnYIfobjaxeEbaDQvjRLlLKKq1OsvdcmnrKWkW72Dt5XkIcTYHfEs6wfFrMiYhO3/gxhoqBuiHbNyIL9kKTwkMg2wfAgMBAAGjUDBOMB0GA1UdDgQWBBRXkmK7fyfhVMBWOrUJvkSHm9IapDAfBgNVHSMEGDAWgBRXkmK7fyfhVMBWOrUJvkSHm9IapDAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBBQUAA4IBAQB0BZ1Duu0U59p7U9pkdoS79GxhE8ALnYPkxeVX5ZqOBbS/BWG5t+M2EyfUc2pNKqAqopfnBdyXcxuw0JyQBIYKmDHG+YtxqxdqBcDrfmsxk1gyVLH4mZvhCTpPeUM5LivzFGdaqm4Dm2M5XG1BSDvclrUzoebDhg2VFJfRzYHnhZsBNVlcxQ/m8UUKmGl5sXgIHQCi4o0zrdRcWAnMqtxd0vzdRL2h6MTh56ICiOVG8rASXBdZmN49FjwlYFfQyetwdrrmpYleTWkyQteGCy7r5zMCXcMTRawRp5R6kP/w6FNcw3fpUs8qHlT47lhVNABQA94hkNW50Fv40xuSYOww";
    //public static final String DW_SIGNATURE_FOR_TEST_BUILD = "MIIEqDCCA5CgAwIBAgIJALOZgIbQVs/6MA0GCSqGSIb3DQEBBAUAMIGUMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNTW91bnRhaW4gVmlldzEQMA4GA1UEChMHQW5kcm9pZDEQMA4GA1UECxMHQW5kcm9pZDEQMA4GA1UEAxMHQW5kcm9pZDEiMCAGCSqGSIb3DQEJARYTYW5kcm9pZEBhbmRyb2lkLmNvbTAeFw0wODA0MTUyMjQwNTBaFw0zNTA5MDEyMjQwNTBaMIGUMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNTW91bnRhaW4gVmlldzEQMA4GA1UEChMHQW5kcm9pZDEQMA4GA1UECxMHQW5kcm9pZDEQMA4GA1UEAxMHQW5kcm9pZDEiMCAGCSqGSIb3DQEJARYTYW5kcm9pZEBhbmRyb2lkLmNvbTCCASAwDQYJKoZIhvcNAQEBBQADggENADCCAQgCggEBAJx4BZKsDV04HN6qZezIpgBuNkgMbXIHsSARvlCGOqvitV0Amt9xRtbyICKAx81Ne9smJDuKgGwms0sTdSOkkmgiSQTcAUk+fArPGgXIdPabA3tgMJ2QdNJCgOFrrSqHNDYZUer3KkgtCbIEsYdeEqyYwap3PWgAuer95W1Yvtjo2hb5o2AJnDeoNKbf7be2tEoEngeiafzPLFSW8s821k35CjuNjzSjuqtM9TNxqydxmzulh1StDFP8FOHbRdUeI0+76TybpO35zlQmE1DsU1YHv2mi/0qgfbX36iANCabBtJ4hQC+J7RGQiTqrWpGA8VLoL4WkV1PPX8GQccXuyCcCAQOjgfwwgfkwHQYDVR0OBBYEFE/koLPdnLop9x1yh8Tnw48ghsKZMIHJBgNVHSMEgcEwgb6AFE/koLPdnLop9x1yh8Tnw48ghsKZoYGapIGXMIGUMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNTW91bnRhaW4gVmlldzEQMA4GA1UEChMHQW5kcm9pZDEQMA4GA1UECxMHQW5kcm9pZDEQMA4GA1UEAxMHQW5kcm9pZDEiMCAGCSqGSIb3DQEJARYTYW5kcm9pZEBhbmRyb2lkLmNvbYIJALOZgIbQVs/6MAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEEBQADggEBAFclUbjZOh9z3g9tRp+G2tZwFAApPIigzXzXeLc9r8wZf6t25iEuVsHHYc/EL9cz3lLFCuCIFM78CjtaGkNGBU2Cnx2CtCsgSL+ItdFJKe+F9g7dEtctVWV+IuPoXQTIMdYT0Zk4u4mCJH+jISVroS0dao+S6h2xw3Mxe6DAN/DRr/ZFrvIkl5+6bnoUvAJccbmBOM7z3fwFlhfPJIRc97QNY4L3J17XOElatuWTG5QhdlxJG3L7aOCA29tYwgKdNHyLMozkPvaosVUz7fvpib1qSN1LIC7alMarjdW4OZID2q4u1EYjLk/pvZYTlMYwDlE448/Shebk5INTjLixs1c=";
    //public static final String EB_SIGNATURE = "MIIFqzCCA5MCCSMxNhM5AtLvezANBgkqhkiG9w0BAQsFADCBpDELMAkGA1UEBhMCVVMxETAPBgNVBAgMCE5ldyBZb3JrMRMwEQYDVQQHDApIb2x0c3ZpbGxlMSEwHwYDVQQKDBhaZWJyYSBUZWNobm9sb2dpZXMsIEluYy4xJDAiBgNVBAsMG0VudGVycHJpc2UgTW9iaWxlIENvbXB1dGluZzEkMCIGA1UEAwwbQW5kcm9pZCBQcml2aWxlZ2VkIEtleSBSb290MB4XDTIxMDkyMzIxMzAyMFoXDTQ2MTAyNzIxMzAyMFowgYkxCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJOWTETMBEGA1UEBwwKSG9sdHN2aWxsZTEfMB0GA1UECgwWWmVicmEgVGVjaG5vbG9naWVzIEluYzEMMAoGA1UECwwDRU1DMSkwJwYDVQQDDCBjb20uemVicmEubWRuYS5lbnRlcnByaXNlYnJvd3NlcjCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBANcrOBEJplQe0yXze3uC34SPOC6O6pdYdH5D7YQGIQnIeMZ/+9OA8cKQ/xRK9xqcQ7KJZAHaUr7UgJ9IAlqIXL4hOwV+gtrODlq7upAOSEL/EJ2uRjAkXkFGVxSNXaMVnnpTPCN808Hwzo1+cEhvoKOo7k9FGjOjdCIW/RY8PtpNtgdq2rGv/2RWm72pCbCoqeDZWIBivNScDNLvnsU/nxLUC5TrI13xWTmL+ZGGYO8k1bhu5QzfClb9g/3NpvvJYwuuH4ZXVZ3p9U7UWED2+lTLnHyXXhfVQCKQVM/rExBPOhky+9WNoZt8ofxyuyeSRbnJrXWPQxAxaeRyBH+/YZ7yWVMFMEEQhq3puDA3yCITdK69ldtGmoZKK/b+lBdMbCJdzC3VoB96rPVSB4oTw2+zO9BpeME5CQFMWcAd+okTFbHmFYsQHv1mNAphJICDzabv7F1LDK0AZr38Rzq/6cuyWow+puM0wgXGI5eMdRMjgpN37r031tI4bjXC/zjig0mnWwtDY0eH2sjfShQmJiGITIy3zWIaFNsw4lo3C0Z1ZYrNqEiBNT8oYJiWCTlfkBEWnm4eYfsBOJ8LvBcVsshDZMC8iz0hzimmslqF+6aZDbAKG7HLRJZqkiinUZKPD3gMIt22Jhz5gyrDBoNpNmKnQ+7F5q/k1QBBV14IIW8DAgMBAAEwDQYJKoZIhvcNAQELBQADggIBAB1sIrWyk/vS8F3qI/XBvMJCiwknMhjoXpa3P8L7yW4XNmm2cdAe0kB+gs3y5bXaHASNxD2tnUlpiGQtj2TBzqelOY2k+3lE+ZZk4YrM5hMlaCA7Fi3La3uie3Mzn267mjILqtXvMZUVaFky69obII2+edlh/LrlEMCd8xv4YtqgMBL98iwe3vbe0gI+23EQh5kBiyX9D4vkxtOwB0BVkI7m7q2ojvWdnJc72KVbtU0nqARfOTXVl8JCj4yhl2H7B++pqM2fOPv42XeJ69K1Oed/ODeU2qj4AlEWN/mgURuuDDN67uW9QWoSYbNuqW7PAtZeTlKVcuw+7yRWMcNzWK+uUQy56RglpXx+PrzWENgcWWRLF4XInutg6am68wk3GGRxq2cgV8aJEDzuVIfzUMBwavjlQ3PJmeLFyo3yoZxf10L3fm6OrFFO2mCKio4k8Mzw96UO9IZYR5PgHrfzr22EgWaVUZik1UWEp/xY251RbMdwOggM+5tLhEHP0P8p3SzZRtLDQ4dEJftHId/F0jJ3em4vvMR3dmUAeGBBoqI8JkNkjDd4tjisw5nyfIDmQYHlHRh9udTBRCn1i0IgyO6vmZVq55voKGwBpuOH9NQMlzvWpBJcyjtmt3/wPlWlPC8w9924KjZq2hVLG1L7JntYB0Isaq1ySOZ0i7Nrz8XN";
    //public static final String EB_SIGNATURE = "MIIDrzCCApegAwIBAgIETomSXjANBgkqhkiG9w0BAQsFADCBhjELMAkGA1UEBhMCSU4xDDAKBgNVBAgTA0tBUjESMBAGA1UEBxMJQmFuZ2Fsb3JlMRswGQYDVQQKExJNb3Rvcm9sYSBTb2x1dGlvbnMxGzAZBgNVBAsTEk1vdG9yb2xhIFNvbHV0aW9uczEbMBkGA1UEAxMSTW90b3JvbGEgU29sdXRpb25zMCAXDTE0MDQxNTEyMzY0M1oYDzIwNjkwMTE2MTIzNjQzWjCBhjELMAkGA1UEBhMCSU4xDDAKBgNVBAgTA0tBUjESMBAGA1UEBxMJQmFuZ2Fsb3JlMRswGQYDVQQKExJNb3Rvcm9sYSBTb2x1dGlvbnMxGzAZBgNVBAsTEk1vdG9yb2xhIFNvbHV0aW9uczEbMBkGA1UEAxMSTW90b3JvbGEgU29sdXRpb25zMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwtkr17TgBhcP0kqUFTjuWtVdTKdRVmHKo0AiYTuRjScr5mevSPeb9DicjbQKG8hGyoJ9hfBKrSsLlOZ1Fr/9cpBNgkuX4knyzVK6wRA11Y13mIE9bYTFAixy9F05Kj0b+KaCXAfZ89LeT2cACbLe9HBaNgSBGyev/b57/qknECFNiJbt/iMMlI25oEf321lEv6zedCxoZGLuwORS6DcAlpMwN3kRZVviFu/8riIJzF1RUpuLDlP7I9tPCXS697PVq1rIabYlS5Wt9d3c7ZZ0PRkxjO/nD81odfCObcpUFEjNib9I3zMxYP2L8q2nYSeG+zOE3h0etzC044MfA0ozTQIDAQABoyEwHzAdBgNVHQ4EFgQU8bVkbmAq0bgwJwm3lYQcfXUDInYwDQYJKoZIhvcNAQELBQADggEBAHJiIvdHZNyLGaMuttJDJZR+0cUCvpCD/38eOXvm0fnxK+L/gDU2fLODrPpp2Qrq0dmViFdoj/nufJYP9Hwgl65NM/266Hcgvau/wNrUwZ3JTd1CwYPY4siOTtsVNO6eXGaZ4OG2to1e/Vaupp7Fx+fb7DQmaXG7Drm8T+/fyMzF00pkYwCpkyIrbZB83Q2i2+WbY5jrH6HhJCBstiw9Q7wv6rqmGvqECwyvUfuZVk5jdZaGzHKpsnqWH/9nAU7+YL3SpPfVOe+SlXamr/FEkFFVmlx5oGoG3WIgNZ47zxdtWizRF/saezFwn0mkZERcTybXSOeoHM4s+z1shxWuX+0=";
    /*-------------------- Query file from SSM --------------------------------*/
    public static final String AUTHORITY_FILE1 = "content://com.zebra.securestoragemanager.securecontentprovider/file/*";
    public static final String AUTHORITY = "com.zebra.showcaseapp.showcasecontentprovider.provider";
    //ZDM
    public static final String ZDM_PACKAGE = "com.zebra.devicemanager";
    public static final String ZDM_SERVICE_CLASS = "com.zebra.devicemanager.ZebraDeviceMgr";
    public static final String DEMO_FILE_NAME = "demoApp.zip";
    public static final String UPDATES_FILE_NAME = "Updates.zip";
    public static final String MASTER_DEMO_FILE_NAME = "masterDemoApp.zip";
    public static final String ENCRYPTED_CHECKSUM_FILE_NAME = "checksum.txt";
    public static final String EB_APK_FILE_NAME = "EnterpriseBrowser_v2.5.0.0.apk";
    public static final String EB_IS_LICENSE_REQUIRED = "EB_IS_LICENSE_REQUIRED";
    public static final String DEMO_FIREBASE_PATH = "demoApp/demoApp.zip";
    public static final String MASTER_DEMO_FIREBASE_PATH = "demoApp/masterDemoApp.zip";
    public static final String UPDATE_FILE_PATH = "demoApp/updateDemoApp.zip";
    public static final String BETA_FILE_PATH = "BetaApps/";
    public static final String UPDATE_FILE_NAME = "updateDemoApp.zip";
    public static final String UPDATE_SPECIFIC_FILE_PATH = "demoApp/Updates/demoApp/";
    public static final String UPDATE_SPECIFIC_DEFAULT_FILE_PATH = "demoApp/Updates/demoApp/defaultApps/";
    public static final String EB_APK_FIREBASE_PATH = "demoApp/EnterpriseBrowser_v2.5.0.0.apk";
    public static final String SENDING = "Sending";
    public static final String APPLICATION_ARCHIVE = "application/vnd.android.package-archive";
    public static final String PROVIDER = ".provider";
    public static final String STATUS = "status";
    public static final String COMMAND = "command";
    public static final String COMMAND_DOWNLOAD = "download";
    public static final String COMMAND_INSTALL = "installPackage";
    public static final String COMMAND_UPGRADE = "upgradePackage";
    public static final String MSG = "msg";
    public static final String RESULT = "result";
    public static final String COMPLETED = "COMPLETED";
    public static final String ERROR = "ERROR";
    public static final String EXCEPTION = "EXCEPTION";

    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String SUCCESS = "SUCCESS";
    public static final String STARTED = "STARTED";
    public static final String RESPONSE = "response";
    public static final String SUBMITTED_JSON = "submitted json";
    public static final String FILEUPDATEDATA = "FileUpdateData";
    public static final String APPUPDATEDATA = "AppUpdateData";
    public static final String APP_NAME = "name";
    public static final String APP_TYPE = "type";
    public static final String URL = "url";
    public static final String APK_NAME = "apkName";
    public static final String CALL_PURPOSE = "purpose";
    public static final int CODE_ITEM = 2;
    public static final int CODE_DIR = 1;
    public static final int ANALYTICS_DIR = 3;
    public static final int ANALYTICS_ITEM = 4;
    public static final int MASTER_APP_DIR = 5;
    public static final int MASTER_APP_ITEM = 6;

    public static final int UPDATE_APP_DIR = 7;
    public static final int UPDATE_APP_ITEM = 8;

    public static final int SETTING_APP_DIR = 9;
    public static final int SETTING_APP_ITEM = 10;

    public static final int UPDATE_DEMO_ZIP_DIR = 11;
    public static final int UPDATE_DEMO_ZIP_ITEM = 12;

    public static final int BETA_APP_DIR = 13;
    public static final int BETA_APP_ITEM = 14;

    //Uat1
    public static final String EB_DOWNLOAD_INSTALL_PATH = "download('https://firebasestorage.googleapis.com/v0/b/emc-scdemoapp-mindteck-uat1-t.appspot.com/o/demoApp%2FEnterpriseBrowser.apk?alt=media&token=f3d52443-6122-401b-beb3-45b60ae12a35', '/sdcard/EnterpriseBrowser.apk'); installPackage('/sdcard/EnterpriseBrowser.apk',1)";
    //Uat2
    //public static final String EB_DOWNLOAD_INSTALL_PATH = "download('https://firebasestorage.googleapis.com/v0/b/emc-scdemoapp-mindteck-uat2-t.appspot.com/o/demoApp%2FEnterpriseBrowser.apk?alt=media&token=4ec711da-8098-4b18-9245-036946d27963', '/sdcard/EnterpriseBrowser.apk'); installPackage('/sdcard/EnterpriseBrowser.apk',1)";
    //Uat2
    //public static String EB_DOWNLOAD_UPGRADE_PATH = "download('https://firebasestorage.googleapis.com/v0/b/emc-scdemoapp-mindteck-uat2-t.appspot.com/o/demoApp%2FEnterpriseBrowser.apk?alt=media&token=4ec711da-8098-4b18-9245-036946d27963', '/sdcard/EnterpriseBrowser.apk'); upgradePackage('/sdcard/EnterpriseBrowser.apk',1)";
    public static final String LICENCE_PATH = "file:///android_asset/end_user_licence_agreement.html";
    //Uat1
    public static String EB_DOWNLOAD_UPGRADE_PATH = "download('https://firebasestorage.googleapis.com/v0/b/emc-scdemoapp-mindteck-uat1-t.appspot.com/o/demoApp%2FEnterpriseBrowser.apk?alt=media&token=f3d52443-6122-401b-beb3-45b60ae12a35', '/sdcard/EnterpriseBrowser.apk'); upgradePackage('/sdcard/EnterpriseBrowser.apk',1)";
    public static int UPDATE_DOWNLOAD_PATH = 0;
    public static String SHOWCASE_DOWNLOAD_UPGRADE_PATH = "";
    public static String NATIVE_APP_INSTALL_PATH = "";


    private Constants() {
    }
}
