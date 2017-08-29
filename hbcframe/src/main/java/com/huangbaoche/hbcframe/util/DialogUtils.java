package com.huangbaoche.hbcframe.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

public class DialogUtils {

    public static void showAlertDialog(Context context, String content){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        if (!TextUtils.isEmpty(content)) {
            dialog.setMessage(content);
        }
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,"确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showAlertDialogOneBtn(Context context,String content,String okText){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        if (!TextUtils.isEmpty(content)) {
            dialog.setMessage(content);
        }
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,okText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showAlertDialog(Context context,String content,String okText,DialogInterface.OnClickListener onClick){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        if (!TextUtils.isEmpty(content)) {
            dialog.setMessage(content);
        }
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, okText, onClick);
        dialog.show();
    }

    public static AlertDialog showAlertDialog(Context context, String content, String okText, String cancleText,
                                              DialogInterface.OnClickListener okClick, DialogInterface.OnClickListener cancleClick){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        if (!TextUtils.isEmpty(content)) {
            dialog.setMessage(content);
        }
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,okText, okClick);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancleText, cancleClick);
        dialog.show();
        return dialog;
    }

    public static AlertDialog showAlertDialog(Context context, boolean cancelable, String title, String content, String okText, String cancleText
            ,DialogInterface.OnClickListener okClick, DialogInterface.OnClickListener cancleClick){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        if (!TextUtils.isEmpty(content)) {
            dialog.setMessage(content);
        }
        dialog.setCancelable(cancelable);
        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        }
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, okText, okClick);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancleText, cancleClick);
        dialog.show();
        return dialog;
    }


    public static AlertDialog showAlertDialog(Context context, String title, String content, String okText, String cancleText,
                                              DialogInterface.OnClickListener okClick, DialogInterface.OnClickListener cancleClick){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        if (!TextUtils.isEmpty(content)) {
            dialog.setMessage(content);
        }
        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        }
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,okText, okClick);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancleText, cancleClick);
        dialog.show();
        return dialog;
    }

    public static AlertDialog showAlertDialogCancelable(Context context, String content, String okText, String cancleText,
                                                        DialogInterface.OnClickListener okClick, DialogInterface.OnClickListener cancleClick){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        if (!TextUtils.isEmpty(content)) {
            dialog.setMessage(content);
        }
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,okText, okClick);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancleText, cancleClick);
        dialog.show();
        return dialog;
    }

    public static AlertDialog showAlertDialog(Context context, String title, String content, String okText, String cancleText, String neutralText,
                                              DialogInterface.OnClickListener okClick, DialogInterface.OnClickListener cancleClick
            ,DialogInterface.OnClickListener neutralClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        if (!TextUtils.isEmpty(content)) {
            dialog.setMessage(content);
        }
        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        }
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, okText, okClick);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancleText, cancleClick);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, neutralText, neutralClick);
        dialog.show();
        return dialog;
    }

    public static AlertDialog showAlertDialog(Context context,boolean cancelable, String title, String content,String okText,DialogInterface.OnClickListener onClick){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(cancelable);
        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        }
        if (!TextUtils.isEmpty(content)) {
            dialog.setMessage(content);
        }
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, okText, onClick);
        dialog.show();
        return dialog;
    }

    public static AlertDialog showAlertDialog(Context context, boolean cancelable, boolean canceledOnTouchOutside, String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        }
        if (!TextUtils.isEmpty(content)) {
            dialog.setMessage(content);
        }
        dialog.show();
        return dialog;
    }

}