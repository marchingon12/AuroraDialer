package com.aurora.contact.entity;

import android.content.Context;
import android.provider.ContactsContract;

import com.aurora.contact.interfaces.Entity;

public class SpecialDate extends Entity {
    public static final int TYPE_ANNIVERSARY = 1;
    public static final int TYPE_OTHER = 2;
    public static final int TYPE_BIRTHDAY = 3;

    public SpecialDate(String mainData, String labelName) {
        super(mainData, labelName);
    }

    public SpecialDate(Context context, String mainData, int labelId) {
        super(context, mainData, labelId);
    }

    public SpecialDate(Context context, String mainData) {
        super(context, mainData);
    }

    @Override
    protected String getLabelNameResId(Context context, int id) {
        return context.getString(ContactsContract.CommonDataKinds.Event.getTypeResource(id));
    }

    @Override
    protected int getDefaultLabelId() {
        return TYPE_ANNIVERSARY;
    }

    @Override
    protected boolean isValidLabel(int id) {
        return id >= 1 && id <= 3;
    }

    @Override
    public int getCustomLabelId() {
        return 0;
    }
}
