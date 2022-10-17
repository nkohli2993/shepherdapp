package com.shepherdapp.app.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.BuildConfig
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.ui.component.login.LoginActivity
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by Deepak Rattan on 14/09/22
 */
class FCMService : FirebaseMessagingService() {
    private var primaryChannel = "default"
    private var TAG = "FCMService"


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken:$token ")

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (BuildConfig.DEBUG)
            Log.d(TAG, "Message Data : " + message.data.toString())

        val bundle = Bundle()
        for ((key, value) in message.data) {
            bundle.putString(key, value)
        }
        generateNotification(this, bundle)
    }


    @SuppressLint("InvalidWakeLockTag")
    fun generateNotification(context: Context, extra: Bundle) {
        val message = extra.get("body") as String?
        val notificationId = 1

        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val mBuilder = NotificationCompat.Builder(context, primaryChannel)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(extra.get("title") as String?)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setLargeIcon(largeIcon)

        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        mBuilder.setSound(notificationSound)
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        val vibrate = longArrayOf(0, 100, 200, 300)
        mBuilder.setVibrate(vibrate)
        val resultIntent: Intent

        //Check if user is already loggedIn
        val token = Prefs.with(ShepherdApp.appContext)!!.getString(Const.USER_TOKEN, "")
        resultIntent = if (token.isNullOrEmpty()) {
            Intent(context, LoginActivity::class.java)
        } else {
            Intent(context, HomeActivity::class.java)
        }

        try {
            val r = RingtoneManager.getRingtone(applicationContext, notificationSound)
            r.play()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        resultIntent.action = Intent.ACTION_MAIN
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        resultIntent.putExtra("detail", extra)
        resultIntent.putExtra("isNotification", true)
        // Handled the notification crash issue of Android 12 OS
        val resultPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this,
                notificationId ?: 1,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {

            PendingIntent.getActivity(
                this,
                notificationId ?: 1,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        /* val resultPendingIntent = PendingIntent.getActivity(
             this,
             noti_id ?: 1,
             resultIntent,
             // PendingIntent.FLAG_UPDATE_CURRENT
             flag
         )*/
        mBuilder.setWhen(System.currentTimeMillis())
        mBuilder.setContentIntent(resultPendingIntent)
        val mPowerManager = this.getSystemService(Context.POWER_SERVICE) as PowerManager
        var wl: PowerManager.WakeLock? = null
        if (mPowerManager != null) {
            wl = mPowerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "tag"
            )
        }
        wl?.acquire(1000)
        val mNotificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") val chan1 = NotificationChannel(
                primaryChannel,
                "notification_channel_default", NotificationManager.IMPORTANCE_DEFAULT
            )
            chan1.lightColor = Color.GREEN
            chan1.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val audioAttributes = AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            chan1.setSound(notificationSound, audioAttributes)
            chan1.setShowBadge(true)
            mNotificationManager.createNotificationChannel(chan1)
        }

        if (extra.containsKey("image-url")) {
            val bitmap: Bitmap? = getBitmapFromUrl(extra.get("from_image") as String?)
            mBuilder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null)
            ).setLargeIcon(bitmap)
        }

        mNotificationManager.notify(notificationId ?: 1, mBuilder.build())
    }

    private fun getBitmapFromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG)
                Log.e("awesome", "Error in getting notification image: " + e.localizedMessage)
            null
        }
    }

}