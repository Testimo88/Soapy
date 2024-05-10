package com.android.soapy.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    private static SimpleDateFormat sdf;


    public static String formatTimestamp(long timestamp) {
        try {
            if (timestamp>60*1000){
                sdf = new SimpleDateFormat("HH:mm:ss");
            }else {
                sdf= new SimpleDateFormat("mm:ss");

            }
            Date date = new Date(timestamp);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
