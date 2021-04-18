package com.aurora.contact.operations;

import com.aurora.contact.entity.Contact;

public class ContactDataFactory {
    /**
     * @return Creates empty contact data object
     */
    public static Contact createEmpty() {
        return new Contact() {
        };
    }
}
