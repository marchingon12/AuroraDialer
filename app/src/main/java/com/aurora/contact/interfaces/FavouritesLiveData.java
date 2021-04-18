package com.aurora.contact.interfaces;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FavouritesLiveData extends CursorLiveData {

    public FavouritesLiveData(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public String[] getCursorProjection() {
        return new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP,
                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.STARRED
        };
    }

    @Nullable
    @Override
    public String getCursorSelection() {
        return ContactsContract.Contacts.STARRED + " IS 1";
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
        return ContactsContract.Contacts.CONTENT_URI;
    }
}
