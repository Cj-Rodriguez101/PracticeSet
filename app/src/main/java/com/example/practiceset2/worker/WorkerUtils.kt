package com.example.practiceset2.worker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.practiceset2.R
import com.example.practiceset2.activity.MainActivity
import com.example.practiceset2.network.UserDto
import com.example.practiceset2.receiver.ActionReceiver

private const val ARG_PARAM1 = "param1"
@SuppressLint("UnspecifiedImmutableFlag")
fun makeStatusNotification(userDto: UserDto, context: Context) {

    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = "VERBOSE_NOTIFICATION_CHANNEL_NAME"
        val description = "VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("CHANNEL_ID56", name, importance)
        channel.description = description

        // Add the channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    val bundle = Bundle().apply {
        putParcelable(ARG_PARAM1, userDto)
    }
    val pendingIntent = NavDeepLinkBuilder(context)
        .setComponentName(MainActivity::class.java)
        .setGraph(R.navigation.nav_graph)
        .setDestination(R.id.baseAboutFragment)
        .setArguments(bundle)
        .createPendingIntent()

    val deleteIntent = PendingIntent.getBroadcast(context, 0,
        Intent(context, ActionReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

    // Create the notification
    val builder = NotificationCompat.Builder(context, "CHANNEL_ID56")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_success_check_circle_24))
        .setContentTitle(userDto.name)
        .setContentText("${userDto.gender}+\n${userDto.email}")
        //.setStyle(NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(context.resources, R.drawable.ic_success_check_circle_24)))
        .setStyle(NotificationCompat.InboxStyle())
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .addAction(R.drawable.ic_success_check_circle_24, "delete", deleteIntent)
        //.setAutoCancel(true)
        .setVibrate(LongArray(0))

    // Show the notification
    NotificationManagerCompat.from(context).notify(100000, builder.build())
}