package edu.bluejack20_1.dearmory.receivers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.activities.LogInActivity
import edu.bluejack20_1.dearmory.notifications.NotificationChannelApp
import edu.bluejack20_1.dearmory.services.AlarmService


class AlertReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, p1: Intent) {

        if(Intent.ACTION_BOOT_COMPLETED == p1.action){
            startRescheduleAlarmsService(context)
            Log.d("coot complit","1")
        }else{
            startAlarmService(context, p1)
            Log.d("coot complit","2")
        }

        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
        val repeatingIntent: Intent = Intent(context, LogInActivity::class.java)
        repeatingIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            repeatingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
//
//        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context)
//            .setContentIntent(pendingIntent)
//            .setSmallIcon(android.R.drawable.arrow_up_float)
//            .setContentTitle("Title here")
//            .setContentText("Text here")
//            .setAutoCancel(true)
//        Log.d("asd", "10")
//        notificationManager.notify(100, builder.build())
//        Log.d("asd", "11")

        val label = p1.getSerializableExtra("label").toString()
        val tempID = p1.getSerializableExtra("remID")

        var notification : Notification
        if(p1.getSerializableExtra("vibrate").toString() == "on"){
            notification = context.let {
                NotificationCompat.Builder(it, NotificationChannelApp.channel_1_id)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.journal)
                    .setContentTitle(label)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                    .setLights(Color.BLUE, 3000, 3000)
                    .setWhen(System.currentTimeMillis())
                    .build()
            }!!

        }else{
            notification = context.let {
                NotificationCompat.Builder(it, NotificationChannelApp.channel_1_id)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.journal)
                    .setContentTitle(label)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setLights(Color.BLUE, 3000, 3000)
                    .setWhen(System.currentTimeMillis())
                    .build()
            }!!
        }

//        notificationManager.notify(1, notification)

//        val notificationHelper: NotificationHelper? = context?.let { NotificationHelper(it) }
//        val nb : NotificationCompat.Builder? = notificationHelper?.getChannelNotification("1", "asd")
//        if (nb != null) {
//            notificationHelper.getManager().notify(1, nb.build())
//        }
    }

    private fun startRescheduleAlarmsService(context: Context) {
//        val intentService = Intent(context, RescheduleAlarmsService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(intentService)
//        } else {
//            context.startService(intentService)
//        }
    }

    private fun startAlarmService(context: Context, intent: Intent) {
        val intentService = Intent(context, AlarmService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService)
            Log.d("coot", "1")
        } else {
            context.startService(intentService)
            Log.d("coot", "2")
        }
    }

}