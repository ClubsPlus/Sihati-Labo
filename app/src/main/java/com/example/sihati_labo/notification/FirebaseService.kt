package com.example.sihati_labo.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.example.sihati_labo.R
import com.example.sihati_labo.pages.mainPage.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


private const val CHANNEL_ID = "my_chanel"

class FirebaseService : FirebaseMessagingService() {

    companion object{
        var sharedPref: SharedPreferences? = null

        var token: String?
        get(){
            return  sharedPref?.getString("token", "")
        }
        set(value) {
            sharedPref?.edit()?.putString("token",value)?.apply()
        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this,MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        createNotificationChannel(notificationManager)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentTitle(message.data["message"])
            .setSmallIcon(R.drawable.logo_green)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID,notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "ChannelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName,IMPORTANCE_HIGH).apply {
            description = "Votre resultat est pret"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}