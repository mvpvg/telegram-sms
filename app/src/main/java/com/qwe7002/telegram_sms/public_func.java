package com.qwe7002.telegram_sms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class public_func {
    public static final String log_tag = "tg-sms";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static OkHttpClient get_okhttp_obj() {
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public static boolean is_numeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void send_sms(String send_to, String content, int sub_id) {
        android.telephony.SmsManager sms_manager = android.telephony.SmsManager.getDefault();
        if (sub_id != -1) {
            sms_manager = android.telephony.SmsManager.getSmsManagerForSubscriptionId(sub_id);
        }
        ArrayList<String> divideContents = sms_manager.divideMessage(content);
        sms_manager.sendMultipartTextMessage(send_to, null, divideContents, null, null);
    }


    public static void start_service(Context context, SharedPreferences sharedPreferences) {
        Intent battery_service = new Intent(context, battery_listener_service.class);
        Intent webhook_service = new Intent(context, webhook_service.class);
        boolean webhook_switch = sharedPreferences.getBoolean("webhook", false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(battery_service);
            if (webhook_switch) {
                context.startForegroundService(webhook_service);
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(battery_service);
            if (webhook_switch) {
                context.startService(webhook_service);
            }
        }
    }

    public static String get_phone_name(Context context, String phone_number) {
        String contact_name = null;
        if (checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone_number));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String cursor_name = cursor.getString(0);
                    if (!cursor_name.isEmpty())
                        contact_name = cursor_name;
                }
                cursor.close();
            }

        }
        return contact_name;
    }

    public static void write_log(Context context, String log) {
        Log.i(public_func.log_tag, log);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        SharedPreferences sharedPreferences = context.getSharedPreferences("log-data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String error_log = sharedPreferences.getString("error_log", "") + "\n" + simpleDateFormat.format(date) + " " + log;
        editor.putString("error_log", error_log);
        editor.apply();

    }
}
