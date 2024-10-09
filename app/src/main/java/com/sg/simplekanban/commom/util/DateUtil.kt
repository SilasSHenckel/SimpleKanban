package com.sg.simplekanban.commom.util

import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class DateUtil {


    companion object{

        fun getCurrentDateFormated(format: String = "yyyy/MM/dd-HH:mm:ss") : String {
            return SimpleDateFormat(format, Locale.getDefault()).format(Calendar.getInstance().time)
        }

    }
}