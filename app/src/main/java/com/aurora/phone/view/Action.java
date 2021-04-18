package com.aurora.phone.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aurora.phone.R;
import com.aurora.phone.util.ColorUtil;
import com.aurora.phone.util.ImageUtil;
import com.aurora.phone.util.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Action extends RelativeLayout {

    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.txt)
    TextView txt;

    private String actionTxt;
    private int actionIcon;
    private Context context;

    private int colorNumber = 0;
    private boolean checked = false;

    public Action(Context context) {
        super(context);
        this.context = context;
    }

    public Action(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Action,
                0, 0);
        try {
            actionTxt = a.getString(R.styleable.Action_actionName);
            actionIcon = a.getResourceId(R.styleable.Action_actionIcon, R.drawable.ic_mic_off);
        } finally {
            a.recycle();
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_action, this);
        ButterKnife.bind(this, view);
        txt.setText(actionTxt);
        img.setImageDrawable(context.getDrawable(actionIcon));
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked, int colorNumber) {
        this.checked = checked;
        if (checked) {
            final int color = ImageUtil.getGradientAccentColor(colorNumber);
            layout.setBackground(context.getDrawable(R.drawable.action_bg_checked));
            layout.setBackgroundTintList(ColorStateList.valueOf(color));
            txt.setTextColor(ColorUtil.manipulateColor(color, 0.75f));
            img.setColorFilter(ColorUtil.manipulateColor(color, 0.75f));
        } else {
            layout.setBackground(null);
            txt.setTextColor(ViewUtil.getStyledAttribute(context, android.R.attr.textColorPrimary));
            img.clearColorFilter();
        }
    }
}
