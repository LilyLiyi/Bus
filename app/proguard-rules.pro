-keep public class * extends android.content.BroadcastReceiver
-keep class com.scrat.app.bus.push.MiMessageReceiver {*;}

-dontwarn okio.**

-keep class com.scrat.app.bus.model.**{*;}

-keepattributes Signature