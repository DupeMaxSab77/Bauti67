# ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# GSON
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Application classes
-keep class com.bautiapp.tv.** { *; }
-keepnames class com.bautiapp.tv.** { *; }

# Model classes
-keep class com.bautiapp.tv.data.** { *; }

# Kotlin
-keepclassmembers class kotlin.Metadata {
    *** NAME$;
    *** FILE$;
    *** LINE$;
}
-dontwarn kotlin.**
-dontwarn kotlinx.**
