package com.app.appsinrek.utils;

import android.text.format.DateUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeAgo {
public static final List<Long> times = Arrays.asList(
        TimeUnit.DAYS.toMillis(365),
        TimeUnit.DAYS.toMillis(30),
        TimeUnit.DAYS.toMillis(1),
        TimeUnit.HOURS.toMillis(1),
        TimeUnit.MINUTES.toMillis(1),
        TimeUnit.SECONDS.toMillis(1) );
public static final List<String> timesString = Arrays.asList("year","month","day","hour","minute","second");

public synchronized static String toDuration(long duration) {
    try {
        StringBuffer res = new StringBuffer();
        long now = System.currentTimeMillis();
        CharSequence ago = DateUtils.getRelativeTimeSpanString(duration, now, DateUtils.MINUTE_IN_MILLIS);
        if (ago.toString().equals(""))
            return "0 seconds ago";
        else
            return ago.toString();
    }catch(Exception e){
        return "0 seconds ago";
    }
}
}