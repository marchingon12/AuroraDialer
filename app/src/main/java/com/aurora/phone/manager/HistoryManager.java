package com.aurora.phone.manager;

import com.aurora.contact.entity.Contact;
import com.aurora.phone.entity.History;

public class HistoryManager {


    public static History getHistoryFromContact(Contact contact) {
        final History history = new History();
        history.setContactId(contact.getContactId());
        //history.setGroupId(contact.getGroupId());
        history.setContact(contact);
        return history;
    }
}
