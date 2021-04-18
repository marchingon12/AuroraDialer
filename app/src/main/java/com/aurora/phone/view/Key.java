package com.aurora.phone.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aurora.phone.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Key extends RelativeLayout {

    @BindView(R.id.key)
    TextView txtKey;
    @BindView(R.id.key_char)
    TextView txtKeyChar;

    private String key;
    private String keyChar;

    public Key(Context context) {
        super(context);
    }

    public Key(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Key,
                0, 0);

        try {
            key = a.getString(R.styleable.Key_key);
            keyChar = a.getString(R.styleable.Key_kayChar);
        } finally {
            a.recycle();
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_key, this);
        ButterKnife.bind(this, view);
        txtKey.setText(key);
        txtKeyChar.setText(keyChar);
    }

    public Key(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Key(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public String getKey() {
        return key;
    }

    public String getKeyChar() {
        return keyChar;
    }
}
