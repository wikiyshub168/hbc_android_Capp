# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

-dontshrink
-dontpreverify
-dontoptimize
-dontusemixedcaseclassnames

-flattenpackagehierarchy
-allowaccessmodification
-printmapping map.txt

-optimizationpasses 7
-dontnote
-verbose
-keepattributes Exceptions,InnerClasses
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-ignorewarnings

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.app.IntentService
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends java.lang.Throwable {*;}
-keep public class * extends java.lang.Exception {*;}
-keep public class com.hugboga.guide.data.entity.** {*;}
-keep public class com.hugboga.guide.fragment.** {*;}
-keep public class android.net.http.SslError

-keep public class com.hugboga.guide.R$*{
    public static final int *;
}

-keepattributes Exceptions,InnerClasses
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*

-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient

# 以下是自定义ZWebView中Java中js对象
-keepclassmembers class com.hugboga.guide.widget.ZWebView$javaObj {
   public *;
}

# 以下是xUtils的混淆规则
-keep class * extends java.lang.annotation.Annotation { *; }
-dontwarn com.lidroid.xutils.**
-keep interface com.lidroid.** { *; }
-keep class com.lidroid.** { *; }

# 以下是友盟混淆的规则
-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}

# Permission
-dontwarn com.zhy.m.**
-keep class com.zhy.m.** {*;}
-keep interface com.zhy.m.** { *; }
-keep class **$$PermissionProxy { *; }

# android-support-v4
-dontwarn android.support.**
-keep class android.support.** { *;}

# android-support-v7-appcompat
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *;}

# google-play-service
-dontwarn com.google.**
-keep class com.google.** { *;}

# libammsdk
-dontnote com.tencent.**
-keep class com.tencent.** {*;}

# jpush-sdk-release
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *;}

# com.umeng.fb
-dontwarn com.umeng.fb.**
-keep class com.umeng.fb.** { *;}

# com.yalantis.ucrop,剪切图片
-dontwarn com.yalantis.ucrop.**
-keep class com.yalantis.ucrop.** { *;}

# permission-lib,权限混淆
-dontwarn com.zhy.m.permission.**
-keep class com.zhy.m.permission.** { *;}
-keep interface com.zhy.m.permission.** { *; }
-keepclassmembers class **.$$PermissionProxy {
   public *;
   private *;
}

# Rong IM 2.4.6版本
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
 public *;
}
-keepattributes Exceptions,InnerClasses
-keep class io.rong.** {*;}
-keep class * implements io.rong.imlib.model.MessageContent{*;}
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keepclassmembers class * extends com.sea_monster.dao.AbstractDao {
 public static java.lang.String TABLENAME;
}
-keep class **$Properties
-dontwarn org.eclipse.jdt.annotation.**
-keep class com.ultrapower.** {*;}
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# adding this in to preserve line numbers so that the stack traces
# can be remapped
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable