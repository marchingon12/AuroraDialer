package com.aurora.contact.entity;

import android.content.Context;
import android.provider.ContactsContract;

import com.aurora.contact.interfaces.Entity;

public class PhoneNumber extends Entity {

    public static final int TYPE_HOME = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_WORK = 3;
    public static final int TYPE_FAX_WORK = 4;
    public static final int TYPE_FAX_HOME = 5;
    public static final int TYPE_PAGER = 6;
    public static final int TYPE_OTHER = 7;
    public static final int TYPE_CALLBACK = 8;
    public static final int TYPE_CAR = 9;
    public static final int TYPE_COMPANY_MAIN = 10;
    public static final int TYPE_ISDN = 11;
    public static final int TYPE_MAIN = 12;
    public static final int TYPE_OTHER_FAX = 13;
    public static final int TYPE_RADIO = 14;
    public static final int TYPE_TELEX = 15;
    public static final int TYPE_TTY_TDD = 16;
    public static final int TYPE_WORK_MOBILE = 17;
    public static final int TYPE_WORK_PAGER = 18;
    public static final int TYPE_ASSISTANT = 19;
    public static final int TYPE_MMS = 20;

    public PhoneNumber(String mainData, String labelName) {
        super(mainData, labelName);
    }

    public PhoneNumber(Context context, String mainData, int labelId) {
        super(context, mainData, labelId);
    }

    public PhoneNumber(Context context, String mainData) {
        super(context, mainData);
    }

    @Override
    protected String getLabelNameResId(Context context, int id) {
        return context.getString(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(id));
    }

    @Override
    protected int getDefaultLabelId() {
        return TYPE_HOME;
    }

    @Override
    protected boolean isValidLabel(int id) {
        return id >= 0 && id <= 20;
    }

    @Override
    public int getCustomLabelId() {
        return 0;
    }
}
