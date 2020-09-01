package com.example.work.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public abstract class SharePreference {
    private SharePreference() {
    }

    public static void setSharedPref(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences("SharedData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    public static String getSharedPref(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences("SharedData", MODE_PRIVATE);

//return value
        return sharedPref.getString(key, "");
    }
}
