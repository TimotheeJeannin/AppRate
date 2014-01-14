package com.tjeannin.apprate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

/**
 * This class will craft a feedback email to allow the user to send feedback.
 */
public class SendFeedback {
    private Activity mActivity;

    public SendFeedback(Activity activity) {
        mActivity = activity;
    }

    /**
     * Prompt the user for feedback by generating and sending an intent to open their email client.
     *
     * @param feedbackEmailAddress
     * @param subject              (Optional) If null default subject will be used
     * @param body                 (Optional) If null the default body will be used
     */
    public void promptForFeedback(String feedbackEmailAddress, String subject, String body) {
        if (mActivity == null) return;

        // Generate default body text
        StringBuilder defaultBodyText = new StringBuilder();
        defaultBodyText.append("\n\n\n\n");
        defaultBodyText.append("------------------------------------\n\n");
        defaultBodyText.append(mActivity.getString(R.string.feedback_email_header));
        defaultBodyText.append("\n\n");
        defaultBodyText.append("\n" + mActivity.getString(R.string.email_heading_android_device, getDeviceName()));
        defaultBodyText.append("\n" + mActivity.getString(R.string.email_heading_android_version, Build.VERSION.RELEASE));
        defaultBodyText.append("\n" + mActivity.getString(R.string.email_heading_app_version, AppInfo.getApplicationVersionName(mActivity)));

        String defaultSubjectText = mActivity.getString(R.string.feedback_email_subject_line, AppInfo.getApplicationName(mActivity));

        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", feedbackEmailAddress, null));
        i.putExtra(Intent.EXTRA_SUBJECT, subject != null ? subject : defaultSubjectText);
        i.putExtra(Intent.EXTRA_TEXT, body != null ? body : defaultBodyText.toString());

        try {
            mActivity.startActivity(Intent.createChooser(i, mActivity.getString(R.string.send_email)));
        } catch (android.content.ActivityNotFoundException ex) {
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
