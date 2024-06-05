# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
#-avoid obfuscation just do optimization
-dontobfuscate
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable

#-dontwarn javax.annotation.**

#-dontwarn android.support.**
#-dontwarn android.view.**
#-dontwarn android.widget.**

-dontwarn com.google.common.primitives.**

-keep class com.zebra.showcaseapp.contentprovider.** {
*;
}
-keep public class com.symbol.osx.proxyframework.Authenticate
-keepclassmembers class com.symbol.osx.proxyframework.Authenticate {public private *;}

-keep class com.zebra.showcaseapp.data.** {
*;
}

-keep class com.zebra.showcaseapp.ui.** {
*;
}
-keep class com.zebra.showcaseapp.util.** {
*;
}
-keep public class com.symbol.osx.proxyframework.Authenticate
-keepclassmembers class com.symbol.osx.proxyframework.Authenticate {public private *;}

-keep public class * extends android.app.Activity {
public *;
}
-keep public class * extends android.app.Application {
public *;
}
-keep public class * extends android.content.BroadcastReceiver {
public *;
}
-keep public class * extends android.content.ContentProvider {
public *;
}

-keep class com.google.firebase.** { *; }
-keepclassmembers class com.google.firebase.** { *; }
-keep class com.google.firestore.** { *; }
-keepclassmembers class com.google.firestore.** { *; }
-keep class com.google.android.gms.** { *; }
-keepclassmembers class com.google.android.gms.** { *; }
-keep class com.google.protobuf.** { *; }
-keepclassmembers class com.google.protobuf.** { *; }

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
