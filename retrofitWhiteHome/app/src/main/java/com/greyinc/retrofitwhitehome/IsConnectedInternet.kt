package com.greyinc.retrofitwhitehome

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class IsConnectedInternet {

    fun isInternetAvailable(context: Context): Boolean {
        val connectActivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network=connectActivityManager.activeNetwork?:return false
        val networkCapabilities=connectActivityManager.getNetworkCapabilities(network)?:return false
        return when{
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
            else->false
        }
    }


}