package com.aurora.contact.interfaces;

import android.content.Context;
import android.net.Uri;
import android.provider.CallLog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CallLogsLiveData extends CursorLiveData {

    public CallLogsLiveData(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public String[] getCursorProjection() {
        return null;
    }

    @Nullable
    @Override
    public String getCursorSelection() {
        return null;
    }

    @Nullable
    @Override
    public String[] getCursorSelectionArgs() {
        return null;
    }

    @Nullable
    @Override
    public String getCursorSortOrder() {
        return CallLog.Calls.DATE + " DESC";
    }

    @NonNull
    @Override
    public Uri getCursorUri() {
        return CallLog.Calls.CONTENT_URI;
    }
}
