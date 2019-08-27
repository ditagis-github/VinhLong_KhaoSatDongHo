package vinhlong.ditagis.com.khaosatdongho.libs

import android.annotation.SuppressLint

import java.text.SimpleDateFormat
import java.util.Calendar

object TimeAgo {
    val monthAgo = " tháng trước"
    val weekAgo = " tuần trước"
    val daysAgo = " ngày trước"
    val hoursAgo = " giờ trước"
    val minAgo = " phút trước"
    val secAgo = " giây trước"
    internal var second = 1000 // milliseconds
    internal var minute = 60
    internal var hour = minute * 60
    internal var day = hour * 24
    internal var week = day * 7
    internal var month = day * 30
    internal var year = month * 12

    @SuppressLint("SimpleDateFormat")
    fun DateDifference(fromDate: Long): String {
        val diffInSec = Math.abs((fromDate / second).toInt())
        var difference = ""
        if (diffInSec < minute) {
            difference = diffInSec.toString() + secAgo
        } else if (diffInSec / hour < 1) {
            difference = (diffInSec / minute).toString() + minAgo
        } else if (diffInSec / day < 1) {
            difference = (diffInSec / hour).toString() + hoursAgo
        } else if (diffInSec / week < 1) {
            difference = (diffInSec / day).toString() + daysAgo
        } else if (diffInSec / month < 1) {
            difference = (diffInSec / week).toString() + weekAgo
        } else if (diffInSec / year < 1) {
            difference = (diffInSec / month).toString() + monthAgo
        } else {
            // return date
            val c = Calendar.getInstance()
            c.timeInMillis = fromDate

            val format_before = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss")

            difference = format_before.format(c.time)
        }
        return difference
    }
}