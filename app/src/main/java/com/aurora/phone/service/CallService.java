package com.aurora.phone.service;

import android.content.Intent;
import android.telecom.Call;
import android.telecom.InCallService;

import com.aurora.phone.AuroraApplication;
import com.aurora.phone.OnGoingCallActivity;

public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Intent intent = new Intent(this, OnGoingCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        AuroraApplication.call = call;

    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        AuroraApplication.call = null;
    }
}
