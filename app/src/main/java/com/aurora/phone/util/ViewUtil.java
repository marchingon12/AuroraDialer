package com.aurora.phone.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ViewUtil {

    public static void setFullScreenLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }

    public static void configureActivityLayout(Activity activity) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags &= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(params);
        window.setStatusBarColor(Color.TRANSPARENT);
        ViewUtil.setFullScreenLightStatusBar(activity);
    }

    public static int getStyledAttribute(Context context, int styleID) {
        TypedArray arr = context.obtainStyledAttributes(new TypedValue().data, new int[]{styleID});
        int styledColor = arr.getColor(0, Color.WHITE);
        arr.recycle();
        return styledColor;
    }
}
