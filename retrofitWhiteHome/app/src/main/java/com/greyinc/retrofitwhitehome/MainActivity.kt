package com.greyinc.retrofitwhitehome

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : AppCompatActivity() {
    val pushNot=PushNotifications()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pushNot.createNotificationChannel(this)
        pushNot.scheduleNotification(this)
        pushNot.sendNotification(this)


        lifecycleScope.launch {
            val data = RetrofitRequests().fetchBriefingRoomData()
            if (data != null) {
                RetrofitRequests().parseHtml(data,this@MainActivity)

            } else {
                //Not get data
                Toast.makeText(this@MainActivity,"No get data",Toast.LENGTH_LONG)
                    .show()
            }
        }

    }
}