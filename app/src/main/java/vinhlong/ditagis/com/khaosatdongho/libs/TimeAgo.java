package vinhlong.ditagis.com.khaosatdongho.libs;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeAgo {
    public final static String monthAgo =  " tháng trước";
    public final static String weekAgo =  " tuần trước";
    public final static String daysAgo =  " ngày trước";
    public final static String hoursAgo = " giờ trước";
    public final static String minAgo =  " phút trước";
    public final static String secAgo = " giây trước";
    static int second = 1000; // milliseconds
    static int minute = 60;
    static int hour = minute * 60;
    static int day = hour * 24;
    static int week = day * 7;
    static int month = day * 30;
    static int year = month * 12;

    @SuppressLint("SimpleDateFormat")
    public static String DateDifference(long fromDate) {
        int diffInSec = Math.abs((int) (fromDate / (second)));
        String difference = "";
        if(diffInSec < minute)
        {
            difference = diffInSec+secAgo;
        }
        else if((diffInSec / hour) < 1)
        {
            difference = (diffInSec/minute)+minAgo;
        }
        else if((diffInSec/ day) < 1)
        {
            difference = (diffInSec/hour)+hoursAgo;
        }
        else if((diffInSec/ week) < 1)
        {
            difference = (diffInSec/day)+daysAgo;
        }
        else if((diffInSec/month)<1)
        {
            difference = (diffInSec / week)+weekAgo;
        }
        else if((diffInSec/year)<1)
        {
            difference = (diffInSec / month)+monthAgo;
        }
        else
        {
            // return date
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(fromDate);

            SimpleDateFormat format_before = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");

            difference = format_before.format(c.getTime());
        }
        return difference;
    }
}