package com.aurora.phone.view;

import android.content.Context;
import android.telecom.Call;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aurora.contact.entity.Contact;
import com.aurora.phone.AuroraApplication;
import com.aurora.phone.R;
import com.aurora.phone.ViewActionHandler;
import com.aurora.phone.manager.CallManager;
import com.aurora.phone.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallerView extends LinearLayout implements ViewActionHandler {

    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.line1)
    TextView line1;
    @BindView(R.id.line2)
    TextView line2;
    @BindView(R.id.line3)
    TextView line3;

    private Context context;
    private Call call;
    private Callback callback = new Callback();
    private Contact contact;

    public CallerView(Context context) {
        super(context);
    }

    public CallerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.call = AuroraApplication.call;
        this.contact = CallManager.getDisplayContact(context, call);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_caller, this);
        ButterKnife.bind(this, view);

        img.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_circular_grow));
        call.registerCallback(callback);
        setupDisplayContact();
    }

    public void setupDisplayContact() {
        line2.setText(contact.getCompositeName());
        line3.setText(contact.getPhoneNumber());
    }

    public void updateUI(int state) {
        setupDisplayContact();
        switch (state) {
            case Call.STATE_ACTIVE:
                setupDisplayContact();
                break;
            case Call.STATE_DISCONNECTED:
                line2.setText("Disconnecting");
                break;
            case Call.STATE_RINGING:
                line2.setText("Ringing");
                break;
            case Call.STATE_DIALING:
                line2.setText("Dialing");
                break;
            case Call.STATE_CONNECTING:
                line2.setText("Connecting");
                break;
            case Call.STATE_HOLDING:
                line2.setText("On hold");
                break;
            default:
                break;
        }

        if (state != Call.STATE_RINGING && state != Call.STATE_DISCONNECTED) {
            Log.e("Caller View : Calling UI");
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void stop() {
        if (call != null)
            call.unregisterCallback(callback);
        img.clearAnimation();
    }


    public class Callback extends Call.Callback {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
            Log.e("CallerView : State changed -> %s", state);
            updateUI(state);
        }

        @Override
        public void onDetailsChanged(Call call, Call.Details details) {
            super.onDetailsChanged(call, details);
            Log.i("Details changed: %s", details.toString());
        }
    }
}
