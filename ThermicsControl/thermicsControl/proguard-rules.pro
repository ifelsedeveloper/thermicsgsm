# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in <android-sdk>/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class nameTemplate to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# please KEEP ALL THE NAMES
#-keepnames class ** { *; }


# ButterKnife rules
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Retrofit rules
#-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
#-keepattributes Signature
#-keepattributes Exceptions
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# OkHttp rules
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
# Retrofit Java8
-dontwarn retrofit2.Platform$Java8
-keep class com.jeremyfeinstein.slidingmenu.** { *; }
# Otto rules
#-keepclassmembers class ** {
#    @com.squareup.otto.Subscribe public *;
#    @com.squareup.otto.Produce public *;
#}

# RxJava rules
# RxAndroid will soon ship with rules so this may not be needed in the future
# https://github.com/ReactiveX/RxAndroid/issues/219
-dontwarn sun.misc.Unsafe
-keep class rx.** { *; }

# EasyAdapter rules
#-keepclassmembers class * extends uk.co.ribotTemplate.easyadapter.ItemViewHolder {
#    public <init>(...);
# }

# Gson rules
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
# TODO change to match your package model
# Keep non static or private fields of models so Gson can find their names
-keepclassmembers class com.velvetech.android.occly.data.model.** {
    !static !private <fields>;
}

# Some models used by gson are inner classes inside the retrofit service
-keepclassmembers class com.velvetech.android.occly.data.remote.RibotsService$** {
    !static !private <fields>;
}
-keepclassmembers class com.velvetech.android.occly.data.remote.BackendApiService$** {
    !static !private <fields>;
}
-keepclassmembers class com.velvetech.android.occly.data.local.PreferencesHelper** {
    !static !private <fields>;
}

# Produces useful obfuscated stack traces
# http://proguard.sourceforge.net/manual/examples.html#stacktrace
-keepattributes SourceFile,LineNumberTable


-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

-keep class com.jeremyfeinstein.slidingmenu.** { *; }

-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.**
-keep class com.github.mikephil.charting.** { *; }
-dontwarn io.realm.**