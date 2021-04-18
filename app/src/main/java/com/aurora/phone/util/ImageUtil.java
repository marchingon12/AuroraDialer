package com.aurora.phone.util;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.view.View.LAYER_TYPE_SOFTWARE;

public class ImageUtil {


    private static final List<int[]> gradientInts = new ArrayList<>();
    private static final List<Integer> gradientAccentInts = new ArrayList<>();
    private static final List<Integer> solidColors = new ArrayList<>();

    static {
        gradientInts.add(new int[]{0xFFFEB692, 0xFFEA5455});
        gradientInts.add(new int[]{0xFFC9CFFC, 0xFF7367F0});
        gradientInts.add(new int[]{0xFFFCE38A, 0xFFF38181});
        gradientInts.add(new int[]{0xFF90F7EC, 0xFF32CCBC});
        gradientInts.add(new int[]{0xFF81FBB8, 0xFF28C76F});
        gradientInts.add(new int[]{0xFFFDEB71, 0xFFFF6C00});
    }

    static {
        solidColors.add(0xFFFFB900);
        solidColors.add(0xFF28C76F);
        solidColors.add(0xFFEE3440);
        solidColors.add(0xFF7367F0);
        solidColors.add(0xFF00AEFF);
        solidColors.add(0xFF32CCBC);
    }

    static {
        gradientAccentInts.add(0xFFEA5455);
        gradientAccentInts.add(0xFF7367F0);
        gradientAccentInts.add(0xFFF38181);
        gradientAccentInts.add(0xFF32CCBC);
        gradientAccentInts.add(0xFF28C76F);
        gradientAccentInts.add(0xFFFF6C00);
    }

    @ColorInt
    public static int getSolidColor(int colorIndex) {
        return solidColors.get(colorIndex % solidColors.size());
    }

    @ColorInt
    public static int getGradientAccentColor(int colorIndex) {
        return gradientAccentInts.get(colorIndex % solidColors.size());
    }

    public static GradientDrawable getGradientDrawable(int colorIndex, int shape) {
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TR_BL,
                gradientInts.get(colorIndex % gradientInts.size()));
        gradientDrawable.setAlpha(200);
        gradientDrawable.setShape(shape);
        if (shape == GradientDrawable.RECTANGLE)
            gradientDrawable.setCornerRadius(32f);
        return gradientDrawable;
    }

    public static GradientDrawable getGradientDrawable(int color) {
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TR_BL,
                gradientInts.get(color % gradientInts.size()));
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setAlpha(200);
        return gradientDrawable;
    }

    public static GradientDrawable getDynamicGradientDrawable(@ColorInt int colorStart, @ColorInt int colorEnd) {
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{colorStart, colorEnd});
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        return gradientDrawable;
    }

    public static GradientDrawable getSwipeGradientDrawable(int direction) {
        final List<int[]> gradientInts = new ArrayList<>();
        gradientInts.add(new int[]{0xFFFDEB71, 0xFFFF6C00});
        gradientInts.add(new int[]{0xFF28C76F, 0xFF81FBB8});

        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TR_BL,
                gradientInts.get(direction));
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setAlpha(200);
        return gradientDrawable;
    }

    public static Drawable getBackgroundWithColorShadow(@NonNull View view, int backgroundColor, int shadowColor, int radius, int shadow) {
        final float[] outerRadius = new float[8];
        Arrays.fill(outerRadius, radius);
        final Rect rect = new Rect();
        final ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setPadding(rect);
        shapeDrawable.getPaint().setColor(ColorUtils.setAlphaComponent(backgroundColor, 180));
        shapeDrawable.getPaint().setShadowLayer(shadow, 0, 0, ColorUtils.setAlphaComponent(shadowColor, 120));
        view.setLayerType(LAYER_TYPE_SOFTWARE, shapeDrawable.getPaint());
        shapeDrawable.setShape(new RoundRectShape(outerRadius, null, null));
        final LayerDrawable drawable = new LayerDrawable(new Drawable[]{shapeDrawable});
        drawable.setLayerInset(0, shadow, shadow, shadow, shadow);
        return drawable;
    }

    public static String getNameTag(String displayName) {
        try {
            String[] array = displayName.trim().split(" ");
            if (array.length == 1) {
                return array[0].substring(0, 2);
            } else {
                return array[0].substring(0, 1) + array[1].substring(0, 1);
            }
        } catch (Exception e) {
            return "#";
        }
    }
}
