package com.tjeannin.apprate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.lang.Thread.UncaughtExceptionHandler;

public class AppRate {

    private static final String TAG = "AppRater";

    private Activity mHostActivity;

    private OnClickListener mClickListener;

    private OnClickListener mSendFeedbackClickListener;

    private OnClickListener mDoYouLikeAppClickListener;

    private SharedPreferences mPreferences;

    private AlertDialog.Builder mDialogBuilder = null;

    private AlertDialog.Builder mSendFeedbackDialogBuilder = null;

    private AlertDialog.Builder mDoYouLikeAppDialogBuilder = null;

    private long mMinLaunchesUntilPrompt = 0;

    private long mMinDaysUntilPrompt = 0;

    private boolean mShowIfHasCrashed = true;

    private boolean mResetOnAppUpgrade = false;

    private boolean mShowDoYouLikeTheAppFlow = false;

    private String mSendFeedbackEmailAddress;

    private String mSendFeedbackSubject;

    private String mSendFeedbackBody;

    private AppRaterEventListener mAppRaterEventListener;

    public AppRate(Activity hostActivity) {
        mHostActivity = hostActivity;
        mPreferences = hostActivity.getSharedPreferences(PrefsContract.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * @param minLaunchesUntilPrompt The minimum number of times the user lunches the application before showing the rate dialog.<br/>
     *                               Default value is 0 times.
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setMinLaunchesUntilPrompt(long minLaunchesUntilPrompt) {
        mMinLaunchesUntilPrompt = minLaunchesUntilPrompt;
        return this;
    }

    /**
     * @param minDaysUntilPrompt The minimum number of days before showing the rate dialog.<br/>
     *                           Default value is 0 days.
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setMinDaysUntilPrompt(long minDaysUntilPrompt) {
        mMinDaysUntilPrompt = minDaysUntilPrompt;
        return this;
    }

    /**
     * @param showIfCrash If <code>false</code> the rate dialog will not be shown if the application has crashed once.<br/>
     *                    Default value is <code>true</code>.
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setShowIfAppHasCrashed(boolean showIfCrash) {
        mShowIfHasCrashed = showIfCrash;
        return this;
    }

    /**
     * @param resetOnAppUpgrade If <code>true</code> the rate dialog tracking will be reset when the application is upgraded.
     *                          This will allow the rate the application dialog to be shown again.
     *                          Default value is <code>false</code>.
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setResetOnAppUpgrade(boolean resetOnAppUpgrade) {
        mResetOnAppUpgrade = resetOnAppUpgrade;
        return this;
    }

    /**
     * Use this method if you want to customize the style and content of the rate dialog.<br/>
     * When using the {@link AlertDialog.Builder} you should use:
     * <ul>
     * <li>{@link AlertDialog.Builder#setPositiveButton} for the <b>rate</b> button.</li>
     * <li>{@link AlertDialog.Builder#setNeutralButton} for the <b>rate later</b> button.</li>
     * <li>{@link AlertDialog.Builder#setNegativeButton} for the <b>never rate</b> button.</li>
     * </ul>
     *
     * @param customBuilder The custom dialog you want to use as the rate dialog.
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setCustomDialog(AlertDialog.Builder customBuilder) {
        mDialogBuilder = customBuilder;
        return this;
    }

    /**
     * Use this method if you want to customize the style and content of the do you like the app dialog.<br/>
     * When using the {@link AlertDialog.Builder} you should use:
     * <ul>
     * <li>{@link AlertDialog.Builder#setPositiveButton} for the <b>yes</b> button.</li>
     * <li>{@link AlertDialog.Builder#setNegativeButton} for the <b>no</b> button.</li>
     * </ul>
     *
     * @param customBuilder The custom dialog you want to use as the do you like the app dialog.
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setCustomSendFeedbackDialog(AlertDialog.Builder customBuilder) {
        mSendFeedbackDialogBuilder = customBuilder;
        return this;
    }

    /**
     * Use this method if you want to customize the style and content of the send feedback dialog.<br/>
     * When using the {@link AlertDialog.Builder} you should use:
     * <ul>
     * <li>{@link AlertDialog.Builder#setPositiveButton} for the <b>yes</b> button.</li>
     * <li>{@link AlertDialog.Builder#setNegativeButton} for the <b>no</b> button.</li>
     * </ul>
     *
     * @param customBuilder The custom dialog you want to use as the send feedback dialog.
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setCustomDoYouLikeAppDialog(AlertDialog.Builder customBuilder) {
        mDoYouLikeAppDialogBuilder = customBuilder;
        return this;
    }

    /**
     * Enable do you like the app flow instead of showing just the rating dialog.
     * The flow will be as follows:
     * <ul>
     * <li>A Dialog asking if the user likes the app.</li>
     * <li>If they say yes they are directed to the rating dialog.</li>
     * <li>If they say no they are directed to a dialog asking them if they would like to leave feedback for the
     * app.</li>
     * </ul>
     * <p/>
     *
     * @param sendFeedbackEmailAddress The email address that the user will send feedback to
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate showDoYouLikeTheAppFlow(String sendFeedbackEmailAddress) {
        mShowDoYouLikeTheAppFlow = true;
        mSendFeedbackEmailAddress = sendFeedbackEmailAddress;
        return this;
    }

    /**
     * Set the subject for the send feedback dialog.
     *
     * @param subject
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setSendFeedbackSubject(String subject) {
        mSendFeedbackSubject = subject;
        return this;
    }

    /**
     * Set the body for the send feedback dialog.
     *
     * @param body
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setSendFeedbackBody(String body) {
        mSendFeedbackBody = body;
        return this;
    }

    /**
     * Reset all the data collected about number of launches and days until first launch.
     *
     * @param context A context.
     */
    public static void reset(Context context) {
        context.getSharedPreferences(PrefsContract.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit();
        Log.d(TAG, "Cleared AppRate shared preferences.");
    }

    /**
     * Display the rate dialog if needed.
     */
    public void init() {
        Log.d(TAG, "Init AppRate");

        Editor editor = mPreferences.edit();
        performAppUpgradeCheck(editor);

        if (!mShowIfHasCrashed) {
            initExceptionHandler();
        }

        if (mPreferences.getBoolean(PrefsContract.PREF_DONT_SHOW_AGAIN, false) || (
                mPreferences.getBoolean(PrefsContract.PREF_APP_HAS_CRASHED, false) && !mShowIfHasCrashed)) {
            return;
        }

        // Get and increment launch counter.
        long launch_count = mPreferences.getLong(PrefsContract.PREF_LAUNCH_COUNT, 0) + 1;
        editor.putLong(PrefsContract.PREF_LAUNCH_COUNT, launch_count);

        // Get date of first launch.
        Long date_firstLaunch = mPreferences.getLong(PrefsContract.PREF_DATE_FIRST_LAUNCH, 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong(PrefsContract.PREF_DATE_FIRST_LAUNCH, date_firstLaunch);
        }

        // Show the first dialog if needed.
        if (launch_count >= mMinLaunchesUntilPrompt) {
            if (System.currentTimeMillis() >= date_firstLaunch + (mMinDaysUntilPrompt * DateUtils.DAY_IN_MILLIS)) {
                if (mShowDoYouLikeTheAppFlow) {
                    showDoYouLikeAppDialog();
                } else {
                    showDialog();
                }

                // Notify listener that we have shown a dialog starting the flow
                if (mAppRaterEventListener != null) {
                    mAppRaterEventListener.onAppRaterDialogsShown();
                }
            }
        }

        editor.commit();
    }

    /**
     * Checks and saves off the current version information for the application. If the application has been upgraded, and configured to do so, then
     * reset tracking information to allow the rate dialog to be shown again.
     */
    private void performAppUpgradeCheck(Editor editor) {
        Integer currentAppVersionCode = AppInfo.getApplicationVersionCode(mHostActivity.getApplicationContext());
        Integer lastRunAppVersionCode = mPreferences.getInt(PrefsContract.PREF_APP_VERSION_CODE, -1);

        // If the version has been initialized, we are being upgraded, and the user enabled resetting on upgrading
        if (lastRunAppVersionCode != -1 && currentAppVersionCode > lastRunAppVersionCode && mResetOnAppUpgrade) {
            AppRate.reset(mHostActivity);
        }

        // Save off the current app version code
        editor.putInt(PrefsContract.PREF_APP_VERSION_CODE, currentAppVersionCode);
        editor.commit();
    }

    /**
     * Initialize the {@link ExceptionHandler}.
     */
    private void initExceptionHandler() {
        Log.d(TAG, "Init AppRate ExceptionHandler");

        UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();

        // Don't register again if already registered.
        if (!(currentHandler instanceof ExceptionHandler)) {
            // Register default exceptions handler.
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(currentHandler, mHostActivity));
        }
    }

    /**
     * Shows the rate dialog using either the default or user provided builder.
     */
    private void showDialog() {
        if (mDialogBuilder != null) {
            showCustomDialog(mDialogBuilder);
        } else {
            showDefaultDialog();
        }
    }

    /**
     * Shows the do you like the app dialog using either the default or user provided builder.
     */
    private void showDoYouLikeAppDialog() {
        if (mDoYouLikeAppDialogBuilder != null) {
            showCustomDoYouLikeAppDialog(mDoYouLikeAppDialogBuilder);
        } else {
            showDefaultDoYouLikeAppDialog();
        }
    }

    /**
     * Shows the send feedback dialog using either the default or user provided builder.
     */
    private void showSendFeedbackDialog() {
        if (mSendFeedbackDialogBuilder != null) {
            showCustomSendFeedbackDialog(mSendFeedbackDialogBuilder);
        } else {
            showDefaultSendFeedbackDialog();
        }
    }

    /**
     * Shows the default rate dialog.
     *
     * @return
     */
    private void showDefaultDialog() {
        Log.d(TAG, "Create default dialog.");

        String title = mHostActivity.getString(R.string.dialog_title, AppInfo.getApplicationName(mHostActivity.getApplicationContext()));
        String message = mHostActivity.getString(R.string.dialog_message, AppInfo.getApplicationName(mHostActivity.getApplicationContext()));
        String positiveButtonText = mHostActivity.getString(R.string.dialog_positive_button);
        String neutralButtonText = mHostActivity.getString(R.string.dialog_neutral_button);
        String negativeButtonText = mHostActivity.getString(R.string.dialog_negative_button);

        new AlertDialog.Builder(mHostActivity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, mDialogOnClickListener)
                .setNegativeButton(negativeButtonText, mDialogOnClickListener)
                .setNeutralButton(neutralButtonText, mDialogOnClickListener)
                .setOnCancelListener(mDialogOnCancelListener)
                .create().show();
    }

    /**
     * Shows the default do you like app dialog.
     *
     * @return
     */
    private void showDefaultDoYouLikeAppDialog() {
        Log.d(TAG, "Create default do you like app dialog.");

        String title = mHostActivity.getString(R.string.like_app_dialog_title, AppInfo.getApplicationName(mHostActivity.getApplicationContext()));
        String message = mHostActivity.getString(R.string.like_app_dialog_message, AppInfo.getApplicationName(mHostActivity.getApplicationContext()));
        String positiveButtonText = mHostActivity.getString(R.string.like_app_dialog_positive_button);
        String negativeButtonText = mHostActivity.getString(R.string.like_app_dialog_negative_button);

        new AlertDialog.Builder(mHostActivity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, mDoYouLikeAppDialogOnClickListener)
                .setNegativeButton(negativeButtonText, mDoYouLikeAppDialogOnClickListener)
                .setOnCancelListener(mDoYouLikeAppDialogOnCancelListener)
                .create().show();
    }

    /**
     * Shows the default send feedback dialog.
     *
     * @return
     */
    private void showDefaultSendFeedbackDialog() {
        Log.d(TAG, "Create default send feedback dialog.");

        String title = mHostActivity.getString(R.string.send_feedback_dialog_title, AppInfo.getApplicationName(mHostActivity.getApplicationContext()));
        String message = mHostActivity.getString(R.string.send_feedback_dialog_message, AppInfo.getApplicationName(mHostActivity.getApplicationContext()));
        String positiveButtonText = mHostActivity.getString(R.string.send_feedback_dialog_positive_button);
        String negativeButtonText = mHostActivity.getString(R.string.send_feedback_dialog_negative_button);

        new AlertDialog.Builder(mHostActivity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, mSendFeedbackDialogOnClickListener)
                .setNegativeButton(negativeButtonText, mSendFeedbackDialogOnClickListener)
                .setOnCancelListener(mSendFeedbackDialogOnCancelListener)
                .create().show();
    }

    /**
     * Show the custom rate dialog.
     *
     * @return
     */
    private void showCustomDialog(AlertDialog.Builder builder) {
        Log.d(TAG, "Create custom dialog.");
        buildDialogWithWrapperedClickHandlers(builder, mDialogOnClickListener, mDialogOnCancelListener);
    }

    /**
     * Show the custom do you like app dialog.
     *
     * @return
     */
    private void showCustomDoYouLikeAppDialog(AlertDialog.Builder builder) {
        Log.d(TAG, "Create custom do you like app dialog.");
        buildDialogWithWrapperedClickHandlers(builder, mDoYouLikeAppDialogOnClickListener, mDoYouLikeAppDialogOnCancelListener);
    }

    /**
     * Show the custom send feedback dialog.
     *
     * @return
     */
    private void showCustomSendFeedbackDialog(AlertDialog.Builder builder) {
        Log.d(TAG, "Create custom send feedback dialog.");
        buildDialogWithWrapperedClickHandlers(builder, mSendFeedbackDialogOnClickListener, mSendFeedbackDialogOnCancelListener);
    }

    /**
     * Create the dialog using the provided builder. After created reassign all on click listeners to route through the provided listener.
     *
     * @param builder
     * @param onClickListener
     * @param onCancelListener
     */
    private void buildDialogWithWrapperedClickHandlers(AlertDialog.Builder builder, OnClickListener onClickListener, OnCancelListener onCancelListener) {
        // Make the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Wrapper each of the button click listeners with our own that gets called first
        updateDialogButtonClickListeners(dialog, AlertDialog.BUTTON_POSITIVE, onClickListener);
        updateDialogButtonClickListeners(dialog, AlertDialog.BUTTON_NEUTRAL, onClickListener);
        updateDialogButtonClickListeners(dialog, AlertDialog.BUTTON_NEGATIVE, onClickListener);

        // Wrapper the on cancel listener
        dialog.setOnCancelListener(onCancelListener);
    }

    /**
     * Update all of the dialogs buttons to use the provided onClickListener
     *
     * @param dialog
     * @param whichButton
     * @param onClickListener
     */
    private void updateDialogButtonClickListeners(AlertDialog dialog, int whichButton, OnClickListener onClickListener) {
        if (dialog != null) {
            Button button = dialog.getButton(whichButton);
            if (button != null) {
                String buttonText = (String) button.getText();
                dialog.setButton(whichButton, buttonText, onClickListener);
            }
        }
    }

    /**
     * @param onClickListener A listener to be called back on click actions to the rate dialog.
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setOnClickListener(OnClickListener onClickListener) {
        mClickListener = onClickListener;
        return this;
    }

    /**
     * @param onClickListener A listener to be called back on click actions to the do you like the app dialog.
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setDoYouLikeAppOnClickListener(OnClickListener onClickListener) {
        mDoYouLikeAppClickListener = onClickListener;
        return this;
    }

    /**
     * @param onClickListener A listener to be called back on click actions to the send feedback dialog.
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setSendFeedbackOnClickListener(OnClickListener onClickListener) {
        mSendFeedbackClickListener = onClickListener;
        return this;
    }

    public interface AppRaterEventListener {
        public void onAppRaterDialogsShown();
    }

    /**
     * @param appRaterEventListener listener to be called back when the app rater launches its first dialog to begin a flow.
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setAppRaterEventListener(AppRaterEventListener appRaterEventListener) {
        mAppRaterEventListener = appRaterEventListener;
        return this;
    }

    private OnClickListener mDialogOnClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Editor editor = mPreferences.edit();

            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    try {
                        mHostActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mHostActivity.getPackageName())));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(mHostActivity, mHostActivity.getString(R.string.toast_play_store_missing_error), Toast.LENGTH_SHORT).show();
                    }
                    editor.putBoolean(PrefsContract.PREF_DONT_SHOW_AGAIN, true);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    editor.putBoolean(PrefsContract.PREF_DONT_SHOW_AGAIN, true);
                    break;

                case DialogInterface.BUTTON_NEUTRAL:
                    editor.putLong(PrefsContract.PREF_DATE_FIRST_LAUNCH, System.currentTimeMillis());
                    editor.putLong(PrefsContract.PREF_LAUNCH_COUNT, 0);
                    break;

                default:
                    break;
            }

            editor.commit();
            dialog.dismiss();

            if (mClickListener != null) {
                mClickListener.onClick(dialog, which);
            }
        }
    };

    private OnClickListener mDoYouLikeAppDialogOnClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // Dismiss the do you like the app dialog
            dialog.dismiss();

            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    // Show the rating dialog
                    showDialog();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // They don't like the app; don't prompt them again
                    doNotShowDialogAgain();

                    // Show the send feedback dialog
                    showSendFeedbackDialog();
                    break;

                default:
                    break;
            }

            if (mDoYouLikeAppClickListener != null) {
                mDoYouLikeAppClickListener.onClick(dialog, which);
            }
        }
    };

    private OnClickListener mSendFeedbackDialogOnClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Editor editor = mPreferences.edit();

            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    SendFeedback sendFeedback = new SendFeedback(mHostActivity);
                    sendFeedback.promptForFeedback(mSendFeedbackEmailAddress, mSendFeedbackSubject, mSendFeedbackBody);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // Nothing to do
                    break;

                default:
                    break;
            }

            editor.commit();
            dialog.dismiss();

            if (mSendFeedbackClickListener != null) {
                mSendFeedbackClickListener.onClick(dialog, which);
            }
        }
    };

    private OnCancelListener mDialogOnCancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            resetLaunchData();
        }
    };

    private void resetLaunchData() {
        Editor editor = mPreferences.edit();
        editor.putLong(PrefsContract.PREF_DATE_FIRST_LAUNCH, System.currentTimeMillis());
        editor.putLong(PrefsContract.PREF_LAUNCH_COUNT, 0);
        editor.commit();
    }

    private void doNotShowDialogAgain() {
        Editor editor = mPreferences.edit();
        editor.putBoolean(PrefsContract.PREF_DONT_SHOW_AGAIN, true);
        editor.commit();
    }

    private OnCancelListener mDoYouLikeAppDialogOnCancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            resetLaunchData();
        }
    };

    private OnCancelListener mSendFeedbackDialogOnCancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            doNotShowDialogAgain();
        }
    };
}