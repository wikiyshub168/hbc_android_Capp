package com.huangbaoche.hbcframe.util;

import java.util.Calendar;

/**
 * Created  on 16/3/31.
 */
public class FastClickUtils {

    public static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime = 0;


    public static boolean isFastClick() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if ((currentTime - lastClickTime) > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            return false;
        }
        return true;
    }
}
