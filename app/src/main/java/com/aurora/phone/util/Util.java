package com.aurora.phone.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telecom.TelecomManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.aurora.phone.Constants;
import com.aurora.phone.MainActivity;
import com.github.florent37.runtimepermission.PermissionResult;
import com.github.florent37.runtimepermission.rx.RxPermissions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;

public class Util {

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(
                Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public static void setContactsAvailable(Context context, boolean value) {
        PrefUtil.putBoolean(context, Constants.PREFERENCE_CONTACTS_AVAILABLE, value);
    }

    public static boolean isContactsAvailable(Context context) {
        return PrefUtil.getBoolean(context, Constants.PREFERENCE_CONTACTS_AVAILABLE);
    }

    public static boolean checkDefaultDialer(Context context) {
        String packageName = context.getPackageName();
        if (!context.getSystemService(TelecomManager.class).getDefaultDialerPackage().equals(packageName)) {
            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName);
            ((MainActivity) context).startActivityForResult(intent, 1337);
            return false;
        }
        return true;
    }

    public static void checkPermissions(Context context) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(new RxPermissions((MainActivity) context)
                .request(Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE)
                .subscribe(result -> {
                }, throwable -> {
                    final PermissionResult result = ((RxPermissions.Error) throwable).getResult();
                    if (result.hasDenied()) {
                        for (String permission : result.getDenied()) {
                            Log.e("User denied : %s", permission);
                        }
                        result.askAgain();
                    }

                    if (result.hasForeverDenied()) {
                        for (String permission : result.getForeverDenied()) {
                            Log.e("User permanently denied : %s", permission);
                        }
                        result.goToSettings();
                    }
                }));
    }

    public static void toggleSoftInput(Context context, boolean show) {
        IBinder windowToken = ((MainActivity) context).getWindow().getDecorView().getWindowToken();
        InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && windowToken != null)
            if (show)
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            else
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
    }

    public static synchronized void vibrate(@NonNull Context context, long millis) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(millis);
        }
    }

    public static synchronized void playTone(@NonNull Context context, String numStr) {
        try {
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, 80);
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int ringerMode = audioManager.getRingerMode();
            int tone;
            if (numStr.equals("*"))
                tone = 11;
            else if (numStr.equals("#"))
                tone = 12;
            else
                tone = Integer.parseInt(numStr);
            if ((ringerMode == AudioManager.RINGER_MODE_SILENT) || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
                return;
            }
            toneGenerator.startTone(tone, 150);
        } catch (Exception ignored) {

        }
    }

    public static long getDiffInDays(Long timeInMilli) {
        try {
            long duration = Calendar.getInstance().getTimeInMillis() - timeInMilli;
            return TimeUnit.MILLISECONDS.toDays(duration);
        } catch (Exception e) {
            return 1;
        }
    }

    public static String getTimeFromMilli(Long timeInMilli) {
        try {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:m a", Locale.getDefault());
            return simpleDateFormat.format(new Date(timeInMilli));
        } catch (Exception e) {
            return "NA";
        }
    }

    public static String getDateFromMilli(Long timeInMilli) {
        try {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YY", Locale.getDefault());
            return simpleDateFormat.format(new Date(timeInMilli));
        } catch (Exception e) {
            return "NA";
        }
    }

    public static long getMilliFromDate(String data, long Default) {
        try {
            return Date.parse(data);
        } catch (Exception e) {
        }
        return Default;
    }
}
