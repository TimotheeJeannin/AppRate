package com.tjeannin.apprate;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by bdonahue on 1/10/14.
 */
public class AppInfo {
    private static final String TAG = AppInfo.class.getSimpleName();

    /**
     * @param context A context of the current application.
     * @return The application name of the current application.
     */
    public static final String getApplicationName(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;

        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }

        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : context.getString(R.string.application_name_unknown));
    }

    /**
     * Get the application version code
     *
     * @param context
     * @return The version name or null if there was an error
     */
    public static final Integer getApplicationVersionCode(Context context) {
        try {
            if (context != null) {
                PackageManager pm = context.getPackageManager();
                if (pm != null) {
                    PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if (pi != null) {
                        return pi.versionCode;
                    }
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Unable to get application version code");
        }

        return 0;
    }

    public static String getApplicationVersionName(Context context) {
        try {
            if (context != null) {
                PackageManager pm = context.getPackageManager();
                if (pm != null) {
                    PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if (pi != null) {
                        return pi.versionName;
                    }
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Unable to get application version name");
        }

        return "";
    }
}
