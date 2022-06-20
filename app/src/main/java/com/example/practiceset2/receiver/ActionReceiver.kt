package com.example.practiceset2.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class ActionReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val notificationManager = context!!.applicationContext.getSystemService(
                AppCompatActivity.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.cancel(100000)
        }
    }

}