package com.aurora.contact.interfaces;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SearchLiveData extends CursorLiveData {

    private String selection;

    public SearchLiveData(@NonNull Context context) {
        super(context);
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    @Nullable
    @Override
    public String[] getCursorProjection() {
        return null;
    }

    @Nullable
    @Override
    public String getCursorSelection() {
        return selection;
    }

    @Nullable
    @Override
    public String[] getCursorSelectionArgs() {
        return new String[0];
    }

    @Nullable
    @Override
    public String getCursorSortOrder() {
        return null;
    }

    @NonNull
    @Override
    public Uri getCursorUri() {
        return ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    }
}
