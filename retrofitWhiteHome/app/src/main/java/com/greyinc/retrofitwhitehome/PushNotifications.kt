package com.greyinc.retrofitwhitehome

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PushNotifications: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channelId="channelId"
            val channelName="First Channels"
            val importance=NotificationManager.IMPORTANCE_DEFAULT
            val channel=NotificationChannel(channelId,channelName,importance).apply {
                description="Burasi benim bildirim kanalim"
            }
            val notificationManager:NotificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification(context: Context){
        val sharedPreferences=context.getSharedPreferences("WhiteHousePreferences",Context.MODE_PRIVATE)
        val title=sharedPreferences.getString("title","0")
        var sayac=sharedPreferences.getInt("sayac",0)
        var state=title
        val date=sharedPreferences.getString("date","0")
        val empty=sharedPreferences.getString("empty","notEmpty")
        val channelId="channelId"
        val intent=Intent(context,MainActivity::class.java).apply {
            flags=Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (empty=="bos"){
            state="No new posts yet"
            sayac=0
        }else{
            state=title
            sharedPreferences.edit().putInt("sayac",sayac).apply()
        }
        val pendingIntent:PendingIntent=PendingIntent.getActivity(context,0,intent,
            PendingIntent.FLAG_MUTABLE)
        val builder=NotificationCompat.Builder(context,channelId)
            .setSmallIcon(R.drawable.whitehouse)
            .setContentTitle(state)
            .setContentText("$sayac new post")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)){
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

            }
            notify(1,builder.build())

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(context: Context){
        val alarmManager=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent=Intent(context,PushNotifications::class.java)
        val pendingIntent=PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_MUTABLE)
        val intervalMilis=20000L //30 Minute 1800000L
        val triggerTime=System.currentTimeMillis()+intervalMilis
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,triggerTime,intervalMilis,pendingIntent)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val shared=context!!.getSharedPreferences("WhiteHousePreferences",Context.MODE_PRIVATE)
        val date=shared.getString("date","0")
        val editor=shared.edit()


        GlobalScope.launch {
            val data = RetrofitRequests().fetchBriefingRoomData()
            if (data != null) {
                RetrofitRequests().parseHtml(data,context)

            } else {
                //Not get data
                Toast.makeText(context,"No get data", Toast.LENGTH_LONG)
                    .show()
            }
        }

        var counter=shared.getInt("counter",0)
        val sayac=shared.getInt("sayacim",0)

        println("counter: $counter")
        println("sayac $sayac")

        if (sayac>counter){
            sendNotification(context)
            counter=sayac
            editor.putInt("counter",counter)
            editor.apply()
        }
        scheduleNotification(context)

    }
}