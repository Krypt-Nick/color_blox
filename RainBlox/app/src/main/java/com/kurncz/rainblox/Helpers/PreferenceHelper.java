package com.kurncz.rainblox.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 12/15/16.
 */

public class PreferenceHelper {
    private static final String PREFERENCE_FILE = "Preference";

    public static final String X_WINS = "Xwins";
    public static final String O_WINS = "OWins";
    public static final String SESSION_ID = "Session";

    public static void setSharedPreferenceInt(Context context, String key, int value){
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getSharedPreferenceInt(Context context, String key, int defValue){
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_FILE, 0);
        return settings.getInt(key, defValue);
    }

    public static void setSharedPreferenceString(Context context, String key, String value){
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSharedPreferenceString(Context context, String key, String defValue){
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_FILE, 0);
        return settings.getString(key, defValue);
    }

}
