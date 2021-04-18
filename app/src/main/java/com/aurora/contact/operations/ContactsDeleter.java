package com.aurora.contact.operations;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactsDeleter {

    ContentResolver resolver;

    public ContactsDeleter(ContentResolver resolver) {
        this.resolver = resolver;
    }

    public boolean deleteContactWithId(String contactId) {
        Uri uri = Uri.withAppendedPath(ContactsContract.RawContacts.CONTENT_URI, contactId);
        int deleted = resolver.delete(uri, null, null);
        return deleted > 0;
    }
}
