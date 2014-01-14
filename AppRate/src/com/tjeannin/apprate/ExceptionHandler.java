package com.tjeannin.apprate;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.Thread.UncaughtExceptionHandler;

public class ExceptionHandler implements UncaughtExceptionHandler {

    private UncaughtExceptionHandler mDefaultExceptionHandler;

    private SharedPreferences mPreferences;

    // Constructor.
    public ExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler, Context context) {
        mPreferences = context.getSharedPreferences(PrefsContract.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        mDefaultExceptionHandler = uncaughtExceptionHandler;
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        mPreferences.edit().putBoolean(PrefsContract.PREF_APP_HAS_CRASHED, true).commit();

        // Call the original handler.
        mDefaultExceptionHandler.uncaughtException(thread, throwable);
    }
}