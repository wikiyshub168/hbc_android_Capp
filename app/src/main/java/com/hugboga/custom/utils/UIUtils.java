package com.hugboga.custom.utils;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.hugboga.custom.MyApplication;

/**
 * Created by qingcha on 16/5/27.
 */
public final class UIUtils {

    public static int screenWidth;
    public static int screenHeight;
    public static int screenFullHeight;
    public static int statusBarHeight;

    private UIUtils() {}

    /**
     *  dp转成为px
     */
    public static int dip2px(float dpValue) {
        return (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                MyApplication.getAppContext().getResources().getDisplayMetrics()) + 0.5f);
    }

    /**
     *  sp转成为px
     */
    public static int sp2px(float spValue) {
        return (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue,
                MyApplication.getAppContext().getResources().getDisplayMetrics()) + 0.5f);
    }

    /**
     *  减去状态栏后的屏幕高度
     */
    public static int getScreenHeight() {
        initDisplayConfiguration(null);
        return screenHeight;
    }

    /**
     *  屏幕高度
     */
    public static int getScreenFullHeight() {
        initDisplayConfiguration(null);
        return screenFullHeight;
    }

    /**
     *  屏幕宽度
     */
    public static int getScreenWidth() {
        initDisplayConfiguration(null);
        return screenWidth;
    }

    /**
     *  状态栏高度
     */
    public static int getStatusBarHeight() {
        initDisplayConfiguration(null);
        return statusBarHeight;
    }


    public static void initDisplayConfiguration(Activity activity) {
        if (screenHeight > 0 && screenWidth > 0 && statusBarHeight > 0 && screenFullHeight > 0) {
            return;
        }
        DisplayMetrics dm = MyApplication.getAppContext().getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        statusBarHeight = getStatusHeight(activity);
        screenFullHeight = dm.heightPixels - statusBarHeight;
    }

    public static int getStatusHeight(Activity activity) {
        final int defaultStatusBarHeight = dip2px(25);
        if (activity == null) {
            return defaultStatusBarHeight;
        }
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (statusHeight == 0) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int resID = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = MyApplication.getAppContext().getResources().getDimensionPixelSize(resID);
            } catch (Exception e) {
                statusHeight = defaultStatusBarHeight;
            }
        }
        activity = null;
        return statusHeight;
    }

    public static int getStringWidth(TextView textView, String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        TextPaint paint = textView.getPaint();
        return (int)paint.measureText(str);
    }

    public static float getTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height() / 1.1f;
    }

    public static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }


    public static int getViewTop(View view){
        if(view!=null){
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            return location[1];
        }
        return 0;
    }

    public static int getActionBarSize() {
        int actionBarHeight = dip2px(55);
        TypedValue tv = new TypedValue();
        if (MyApplication.getAppContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, MyApplication.getAppContext().getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }
}
