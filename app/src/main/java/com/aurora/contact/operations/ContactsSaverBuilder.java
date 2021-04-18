package com.aurora.contact.operations;

import android.content.Context;

import com.aurora.contact.entity.Contact;

import java.util.Collections;
import java.util.List;

public class ContactsSaverBuilder {
    private Context context;

    public ContactsSaverBuilder(Context context) {
        this.context = context;
    }

    /**
     * Saves to phone database list of contacts
     *
     * @param contactList list of contacts you want to save
     * @return array with newly created contacts ids
     */
    public int[] saveContactsList(List<Contact> contactList) {
        return new ContactsSaver(context.getContentResolver())
                .insertContacts(contactList);
    }

    /**
     * Saves contacts with all data to phone database
     *
     * @param contact contact you want to save
     * @return newly created contacts data
     */
    public int saveContact(Contact contact) {
        List<Contact> contacts = Collections.singletonList(contact);
        int[] ids = new ContactsSaver(context.getContentResolver())
                .insertContacts(contacts);
        return ids[0];
    }
}
