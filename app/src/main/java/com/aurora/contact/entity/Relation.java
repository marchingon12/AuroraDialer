package com.aurora.contact.entity;

import android.content.Context;
import android.provider.ContactsContract;

import com.aurora.contact.interfaces.Entity;

public class Relation extends Entity {

    public static final int TYPE_ASSISTANT = 1;
    public static final int TYPE_BROTHER = 2;
    public static final int TYPE_CHILD = 3;
    public static final int TYPE_DOMESTIC_PARTNER = 4;
    public static final int TYPE_FATHER = 5;
    public static final int TYPE_FRIEND = 6;
    public static final int TYPE_MANAGER = 7;
    public static final int TYPE_MOTHER = 8;
    public static final int TYPE_PARENT = 9;
    public static final int TYPE_PARTNER = 10;
    public static final int TYPE_REFERRED_BY = 11;
    public static final int TYPE_RELATIVE = 12;
    public static final int TYPE_SISTER = 13;
    public static final int TYPE_SPOUSE = 14;


    public Relation(String mainData, String labelName) {
        super(mainData, labelName);
    }

    public Relation(Context context, String mainData, int labelId) {
        super(context, mainData, labelId);
    }

    public Relation(Context context, String mainData) {
        super(context, mainData);
    }

    @Override
    protected String getLabelNameResId(Context context, int id) {
        return context.getString(ContactsContract.CommonDataKinds.Relation.getTypeLabelResource(id));
    }

    @Override
    protected int getDefaultLabelId() {
        return TYPE_ASSISTANT;
    }

    @Override
    protected boolean isValidLabel(int id) {
        return id >= 1 && id <= 14;
    }

    @Override
    public int getCustomLabelId() {
        return 0;
    }
}
