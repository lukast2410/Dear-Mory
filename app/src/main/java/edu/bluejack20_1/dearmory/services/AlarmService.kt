package edu.bluejack20_1.dearmory.services

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.Vibrator
import android.util.Log
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import edu.bluejack20_1.dearmory.activities.LogInActivity
import edu.bluejack20_1.dearmory.notifications.NotificationChannelApp


class AlarmService: Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
//        mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
//        mediaPlayer!!.isLooping = true
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        Log.d("coot", "create")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, LogInActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val alarmTitle = "Moryyy"
        val notification: Notification = Notification.Builder(this, NotificationChannelApp.channel_1_id)
            .setContentTitle(alarmTitle)
            .setContentText("Ring Ring .. Ring Ring")
            .setContentIntent(pendingIntent)
            .build()
//        mediaPlayer!!.start()
        val pattern = longArrayOf(0, 1000, 1000)
        vibrator!!.vibrate(pattern, 0)
        startForeground(1, notification)
        Log.d("coot", "inside")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer!!.stop()
        vibrator!!.cancel()
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}