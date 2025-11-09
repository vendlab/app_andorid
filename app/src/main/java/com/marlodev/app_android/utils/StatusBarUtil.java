package com.marlodev.app_android.utils;

import android.app.Activity;
import android.view.View;
import android.view.Window;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

public class StatusBarUtil {
    public static void setStatusBarColor(Activity activity, @ColorRes int color, boolean lightIcons) {
        Window window = activity.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(activity, color));
        if (lightIcons) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.getDecorView().setSystemUiVisibility(0);
        }
    }
}
