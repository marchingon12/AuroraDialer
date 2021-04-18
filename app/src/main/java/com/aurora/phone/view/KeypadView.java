package com.aurora.phone.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.aurora.phone.R;
import com.aurora.phone.manager.CallManager;
import com.aurora.phone.util.Util;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class KeypadView extends LinearLayout {

    @BindView(R.id.txt_input_number)
    TextInputEditText txtNumber;
    @BindView(R.id.action_add)
    ImageView imgAdd;
    @BindView(R.id.action_backspace)
    ImageView imgBackspace;
    @BindView(R.id.action_sim1)
    ExtendedFloatingActionButton fabSim1;
    @BindView(R.id.action_sim2)
    ExtendedFloatingActionButton fabSim2;

    private Context context;

    public KeypadView(Context context) {
        super(context);
    }

    public KeypadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_numpad, this);
        ButterKnife.bind(this, view);

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (manager != null) {
            if (getSIMType() == SimType.SINGLE) {
                fabSim2.hide();
                fabSim1.setText(manager.getNetworkOperatorName());
            } else if (getSIMType() == SimType.DUAL) {
                fabSim1.show();
                fabSim2.show();
            } else if (getSIMType() == SimType.NONE) {
                fabSim1.hide();
                fabSim2.hide();
            }
        }
    }

    public SimType getSIMType() {
        SubscriptionManager manager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (manager != null && context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            SubscriptionInfo infoSim1 = manager.getActiveSubscriptionInfoForSimSlotIndex(0);
            SubscriptionInfo infoSim2 = manager.getActiveSubscriptionInfoForSimSlotIndex(1);
            if (infoSim1 != null && infoSim2 != null) {
                return SimType.DUAL;
            } else if (infoSim1 != null)
                return SimType.SINGLE;
            else
                return SimType.NONE;
        }
        return SimType.NONE;
    }

    public String getNumber() {
        return txtNumber.getEditableText().toString();
    }

    @OnClick({R.id.key0, R.id.key1, R.id.key2, R.id.key3, R.id.key4, R.id.key5, R.id.key6, R.id.key7,
            R.id.key8, R.id.key9, R.id.key10, R.id.key11})
    public void addKeyNum(Key key) {
        final String numStr = key.getKey();
        final int pos = txtNumber.getSelectionEnd();
        StringBuffer number = new StringBuffer(txtNumber.getEditableText());
        number.insert(pos, numStr);
        Util.playTone(context, numStr);
        Util.vibrate(context, 30);
        txtNumber.setText(number);
        txtNumber.setSelection(pos + 1);
        imgAdd.setVisibility(VISIBLE);
    }

    @OnLongClick({R.id.key0, R.id.key1, R.id.key2, R.id.key3, R.id.key4, R.id.key5, R.id.key6, R.id.key7,
            R.id.key8, R.id.key9, R.id.key10, R.id.key11})
    public void addKeyChar(Key key) {
        //txtNumber.append(keyView.getKey());
    }

    @OnClick(R.id.action_backspace)
    public void deleteSingleChar() {
        int pos = txtNumber.getSelectionEnd();
        if (pos <= 0)
            return;
        StringBuffer number = new StringBuffer(txtNumber.getEditableText());
        number.deleteCharAt(pos - 1);
        txtNumber.setText(number);
        txtNumber.setSelection(pos - 1);
    }

    @OnLongClick(R.id.action_backspace)
    public void deleteAll() {
        txtNumber.setText("");
        imgAdd.setVisibility(INVISIBLE);
    }

    @OnClick(R.id.action_sim1)
    public void dialSim1() {
        String validatedNumber = getNumber();
        if (validatedNumber.isEmpty())
            Toast.makeText(context, "Invalid Number", Toast.LENGTH_SHORT).show();
        else {
            CallManager.call(getContext(), validatedNumber, 1);
        }
    }

    @OnClick(R.id.action_sim2)
    public void dialSim2() {
        String validatedNumber = getNumber();
        if (validatedNumber.isEmpty())
            Toast.makeText(context, "Invalid Number", Toast.LENGTH_SHORT).show();
        else {
            CallManager.call(getContext(), validatedNumber, 2);
        }
    }

    private enum SimType {
        SINGLE,
        DUAL,
        TRI,
        NONE
    }
}
