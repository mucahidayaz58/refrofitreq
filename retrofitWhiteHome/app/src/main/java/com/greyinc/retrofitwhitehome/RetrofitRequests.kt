package com.greyinc.retrofitwhitehome
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.lang.Exception
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.ArrayList
import java.util.Locale

class RetrofitRequests {
    val client=OkHttpClient()
    val arrayList= ArrayList<String>()
    var arrayList2=ArrayList<String>()
    var sayac=0
    @RequiresApi(Build.VERSION_CODES.O)
    val date2= LocalDate.now()

    suspend fun fetchBriefingRoomData():String?{
        return withContext(Dispatchers.IO){
            try {
                val request=Request.Builder().url("https://www.whitehouse.gov/briefing-room/").build()
                val response=client.newCall(request).execute()
                if (response.isSuccessful){
                    response.body?.string()
                }else{
                    null
                }

            }catch (e:Exception){
                e.printStackTrace()
                null
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun parseHtml(responseData: String?,context:Context) {
        val sharedPreferences=context.getSharedPreferences("WhiteHousePreferences",Context.MODE_PRIVATE)
        val monthName=date2.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val yil=date2.year
        val day=date2.dayOfMonth
        val editor=sharedPreferences.edit()
        val document= responseData?.let { Jsoup.parse(it) }
        val elements=document!!.select("time")
        val elements2= document.select("h2")
        //println("$monthName $day, $yil")

        for (element in elements){
            if (element.text()=="$monthName $day, $yil") {
                arrayList2.add(element.text())
                sayac=arrayList2.size
                sharedPreferences.edit().putInt("sayacim",sayac).apply()
                for (el in elements2) {
                    arrayList.add(el.text())

                }
            }
        }


        if (arrayList.size<=0&&arrayList2.size<=0){
            editor.putString("empty","bos")
            println(retSayac())
            editor.apply()
        }else{
            editor.putString("empty","notBos")
            editor.putString("title",arrayList[1])
            editor.putInt("sayac",retSayac())
            editor.putString("date",arrayList2[0])
            editor.apply()
        }

    }

    fun retSayac(): Int {
        return sayac
    }
}