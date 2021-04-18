package com.aurora.phone;

import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.aurora.contact.entity.Contact;
import com.aurora.phone.util.ImageUtil;
import com.aurora.phone.util.Log;
import com.aurora.phone.util.ViewUtil;
import com.aurora.phone.view.ActionView;
import com.aurora.phone.view.CallerView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnGoingCallActivity extends AppCompatActivity {

    @BindView(R.id.view_action)
    ActionView actionView;
    @BindView(R.id.call_accept_fab)
    ExtendedFloatingActionButton fabAccept;
    @BindView(R.id.view_caller)
    CallerView viewCaller;
    @BindView(R.id.call_end_fab)
    ExtendedFloatingActionButton callEndFab;
    @BindView(R.id.container)
    CoordinatorLayout container;

    private int callState;
    private Call call;
    private Callback callback = new Callback();
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtil.configureActivityLayout(this);
        setContentView(R.layout.activity_ongoing_call);
        configureActivityState();

        ButterKnife.bind(this);

        call = AuroraApplication.call;

        final int colorNum = new Random().nextInt(5);
        actionView.setColorNumber(colorNum);

        GradientDrawable gradientDrawable = ImageUtil.getGradientDrawable(colorNum);
        viewCaller.setBackground(gradientDrawable);

        new ViewLifeCycleObserver()
                .registerLifecycle(getLifecycle())
                .registerHandler(viewCaller);

        new ViewLifeCycleObserver()
                .registerLifecycle(getLifecycle())
                .registerHandler(actionView);

    }

    private void configureActivityState() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (km != null) {
                km.requestDismissKeyguard(this, null);
            }
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        call.registerCallback(callback);
    }

    @Override
    protected void onDestroy() {
        call.unregisterCallback(callback);
        super.onDestroy();
    }

    @OnClick(R.id.call_end_fab)
    public void endCall() {
        call.disconnect();
    }

    @OnClick(R.id.call_accept_fab)
    public void acceptCall() {
        call.answer(0);
    }

    private void updateUI(int state) {
        switch (state) {
            case Call.STATE_ACTIVE:

                break;
            case Call.STATE_DISCONNECTED:
                finishAfterTransition();
                break;
            case Call.STATE_RINGING:
                break;
            case Call.STATE_DIALING:
                break;
            case Call.STATE_CONNECTING:
                break;
            case Call.STATE_HOLDING:
                break;
            default:
                break;
        }

        if (state == Call.STATE_RINGING)
            fabAccept.show();
        else
            fabAccept.hide();

        if (state != Call.STATE_RINGING && state != Call.STATE_DISCONNECTED) {
            Log.e("Calling UI");
        }
        /*if (state == Call.STATE_DISCONNECTED) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            long connectTime = call.getDetails().getConnectTimeMillis();
            long durationTime = (currentTime - connectTime);
            Log.e("Duration %d", durationTime);
            endCall();
        }*/
        callState = state;
    }

    public class Callback extends Call.Callback {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
            Log.i("State changed: %s", state);
            callState = state;
            updateUI(state);
        }

        @Override
        public void onDetailsChanged(Call call, Call.Details details) {
            super.onDetailsChanged(call, details);
            Log.i("Details changed: %s", details.toString());
            updateUI(call.getState());
        }
    }
}
