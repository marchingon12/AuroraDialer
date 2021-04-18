package com.aurora.contact.entity;

import android.content.Context;
import android.provider.ContactsContract;

import com.aurora.contact.interfaces.Entity;

public class IMAddress extends Entity {
    public static final int PROTOCOL_AIM = 0;
    public static final int PROTOCOL_MSN = 1;
    public static final int PROTOCOL_YAHOO = 2;
    public static final int PROTOCOL_SKYPE = 3;
    public static final int PROTOCOL_QQ = 4;
    public static final int PROTOCOL_GOOGLE_TALK = 5;
    public static final int PROTOCOL_ICQ = 6;
    public static final int PROTOCOL_JABBER = 7;
    public static final int PROTOCOL_NETMEETING = 8;

    public IMAddress(String mainData, String labelName) {
        super(mainData, labelName);
    }

    public IMAddress(Context context, String mainData, int labelId) {
        super(context, mainData, labelId);
    }

    public IMAddress(Context context, String mainData) {
        super(context, mainData);
    }

    @Override
    protected String getLabelNameResId(Context context, int id) {
        return context.getString(ContactsContract.CommonDataKinds.Im.getProtocolLabelResource(id));
    }

    @Override
    protected int getDefaultLabelId() {
        return PROTOCOL_AIM;
    }

    @Override
    protected boolean isValidLabel(int id) {
        return id >= 0 && id <= 8;
    }

    @Override
    public int getCustomLabelId() {
        return -1;
    }
}
