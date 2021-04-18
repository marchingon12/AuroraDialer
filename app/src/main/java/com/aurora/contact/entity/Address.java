package com.aurora.contact.entity;

import android.content.Context;
import android.provider.ContactsContract;

import com.aurora.contact.interfaces.Entity;

public class Address extends Entity {

    public static final int TYPE_HOME = 1;
    public static final int TYPE_WORK = 2;
    public static final int TYPE_OTHER = 3;


    public Address(String mainData, String labelName) {
        super(mainData, labelName);
    }

    public Address(Context context, String mainData, int labelId) {
        super(context, mainData, labelId);
    }

    public Address(Context context, String mainData) {
        super(context, mainData);
    }

    @Override
    protected String getLabelNameResId(Context context, int id) {
        return context.getString(ContactsContract.CommonDataKinds.StructuredPostal.getTypeLabelResource(id));
    }

    @Override
    protected int getDefaultLabelId() {
        return TYPE_HOME;
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
