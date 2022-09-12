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
#-renamesourcefileattribute SourceFile
-keep class com.shepherd.app.data.dto.** { *; }
-keep class com.shepherd.app.data.remote.** { *; }
-keep class com.shepherd.app.network.retrofit.** { *; }
-keep class com.shepherd.app.ui.component.addNewEvent.** { *; }
-keep class com.shepherd.app.view_model.** { *; }
-keep class com.shepherd.app.ui.base.** { *; }

-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontnote okhttp3.**

-keep class com.google.gson.stream.** { *; }

-keepclasseswithmembernames class * {
     public <methods>;
}