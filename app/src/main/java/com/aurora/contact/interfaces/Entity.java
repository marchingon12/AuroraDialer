package com.aurora.contact.interfaces;

import android.content.Context;

public abstract class Entity {

    private String mainData;
    private int contactId;
    private int labelId;
    private String labelName;

    /**
     * Used to create WithLabel objects with custom label
     *
     * @param mainData  main data from this object , e.g. phone phoneNumber , email
     * @param labelName name of custom label
     */
    public Entity(String mainData, String labelName) {
        this.mainData = mainData;
        this.contactId = -1;
        this.labelId = getCustomLabelId();
        this.labelName = labelName;
    }

    /**
     * <p>
     * Used to create WithLabel objects with specific in label
     * </p>
     * <p>
     * In case of invalid label will use default label for this type of data
     * </p>
     *
     * @param context  context
     * @param mainData main data from this object , e.g. phone phoneNumber , email
     * @param labelId  data for label , used to get name for this label with system default language , e.g. {@link com.aurora.contact.entity.PhoneNumber#TYPE_HOME}
     */
    public Entity(Context context, String mainData, int labelId) {
        this.mainData = mainData;
        this.contactId = -1;
        this.labelId = isValidLabel(labelId) ? labelId : getDefaultLabelId();
        this.labelName = getLabelNameResId(context, labelId);
    }


    /**
     * <p>
     * Used to create WithLabel objects with default label
     * </p>
     *
     * @param context  context
     * @param mainData main data from this object , e.g. phone phoneNumber , email
     */
    public Entity(Context context, String mainData) {
        this.mainData = mainData;
        this.contactId = -1;
        this.labelId = getDefaultLabelId();
        this.labelName = getLabelNameResId(context, labelId);
    }


    public Entity() {
    }

    /**
     * Gets label resource by data
     *
     * @param id data of this label
     * @return string data of this label
     */
    protected abstract String getLabelNameResId(Context context, int id);

    protected abstract int getDefaultLabelId();

    protected abstract boolean isValidLabel(int id);

    public abstract int getCustomLabelId();

    public String getMainData() {
        return mainData;
    }

    public Entity setMainData(String mainData) {
        this.mainData = mainData;
        return this;
    }

    public int getContactId() {
        return contactId;
    }

    public Entity setContactId(int contactId) {
        this.contactId = contactId;
        return this;
    }

    public int getLabelId() {
        return labelId;
    }

    public Entity setLabelId(int labelId) {
        this.labelId = labelId;
        return this;
    }

    public String getLabelName() {
        return labelName;
    }

    public Entity setLabelName(String labelName) {
        this.labelName = labelName;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        if (labelId != entity.labelId) return false;
        if (!mainData.equals(entity.mainData)) return false;
        return labelName.equals(entity.labelName);
    }

    @Override
    public int hashCode() {
        return mainData.hashCode();
    }
}
