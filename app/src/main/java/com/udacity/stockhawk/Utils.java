package com.udacity.stockhawk;

import java.text.SimpleDateFormat;

/**
 * Created by nihar on 2/22/2017.
 */

public class Utils {

    public static String formatDate(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        return dateFormat.format(date);
    }
}
