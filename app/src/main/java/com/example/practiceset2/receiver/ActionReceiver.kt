package com.example.practiceset2.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class ActionReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("ActionReceiver","onReceive broadcast")
        intent?.let {
            val notificationManager = context!!.applicationContext.getSystemService(
                AppCompatActivity.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.cancel(100000)
        }
    }

}