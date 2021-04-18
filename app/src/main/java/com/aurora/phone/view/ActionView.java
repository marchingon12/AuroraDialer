package com.aurora.phone.view;

import android.content.Context;
import android.media.AudioManager;
import android.telecom.Call;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.aurora.phone.AuroraApplication;
import com.aurora.phone.R;
import com.aurora.phone.ViewActionHandler;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.AUDIO_SERVICE;

public class ActionView extends ConstraintLayout implements ViewActionHandler {

    private AudioManager audioManager;
    private Context context;
    private Call call;
    private int colorNumber = 0;

    public ActionView(Context context) {
        super(context);
    }

    public ActionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.call = AuroraApplication.call;
        this.audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_action, this);
        ButterKnife.bind(this, view);
    }

    public int getColorNumber() {
        return colorNumber;
    }

    public void setColorNumber(int colorNumber) {
        this.colorNumber = colorNumber;
    }

    @OnClick(R.id.action_pause)
    public void pauseCall(Action action) {
        action.setChecked(!action.isChecked(), getColorNumber());
        if (action.isChecked())
            call.hold();
        else
            call.unhold();
    }

    @OnClick(R.id.action_mute)
    public void muteCall(Action action) {
        action.setChecked(!action.isChecked(), getColorNumber());
        new Thread(() -> {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setMicrophoneMute(action.isChecked());
        }).start();
    }

    @OnClick(R.id.action_speaker)
    public void toggleSpeaker(Action action) {
        action.setChecked(!action.isChecked(), getColorNumber());
        new Thread(() -> {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(action.isChecked());
        }).start();
    }

    @OnClick(R.id.action_record)
    public void recordCall(Action action) {
        action.setChecked(!action.isChecked(), getColorNumber());
    }

    @OnClick(R.id.action_add)
    public void addCall(Action action) {
        action.setChecked(!action.isChecked(), getColorNumber());
    }

    @OnClick(R.id.action_keypad)
    public void showKeypad(Action action) {

    }

    @Override
    public void start() {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void stop() {

    }
}
