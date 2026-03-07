package com.sg.simplekanban.commom.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.firestore.Source
import com.sg.simplekanban.domain.usecase.TableHistoryUseCase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateUtil {

    companion object{

        fun getDateFormated(calendar: Calendar, format: String = "yyyy/MM/dd-HH:mm:ss") : String {
            return SimpleDateFormat(format, Locale.getDefault()).format(calendar.time)
        }

        fun getCurrentDateFormated(format: String = "yyyy/MM/dd-HH:mm:ss") : String {
            return SimpleDateFormat(format, Locale.getDefault()).format(Calendar.getInstance().time)
        }

        fun getSourceOnlineOrCache(path: String?, context: Context, format: String? = "yyyy/MM/dd-HH:mm", tableHistoryUseCase: TableHistoryUseCase) : Source {
            if (!hasInternet(context)) return Source.CACHE
            var source = Source.DEFAULT

            if(path != null){
                val tableHistoryList = tableHistoryUseCase.getByPath(path)
                if(tableHistoryList.isNotEmpty()){

                    var lasGetOnlineDate = tableHistoryList[0].lastGetOnlineDate

                    try {
                        if(format == "yyyy/MM/dd-HH") {
                            lasGetOnlineDate = lasGetOnlineDate.removeRange(13, 16)
                        }
                        if(format == "yyyy/MM/dd") {
                            lasGetOnlineDate = lasGetOnlineDate.removeRange(9, 16)
                        }
                    } catch (e: Exception){

                    }

                    if ( getCurrentDateFormated(format ?: "yyyy/MM/dd-HH:mm") == lasGetOnlineDate){
                        source = Source.CACHE
                    }
                }
            }

            return source
        }

        @JvmStatic
        fun hasInternet(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
            return false
        }

    }
}