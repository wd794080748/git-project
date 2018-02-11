package com.wangdong.weathernow.util;

import android.util.Log;

public class DebugLog {
    public static boolean isDebug = true;

    public static void debugLog(String TAG,String str){
        if(isDebug){
            Log.i(TAG, str);
        }
    }
}
