package com.aurora.contact.interfaces;

import com.aurora.contact.entity.Contact;

/**
 * This interface defines ability to filter Contacts
 */
interface Filterable {
    /**
     * <p>
     * Main method for filtering contacts
     * </p>
     *
     * @param contact contact what should get exercised with filter
     * @return true if should not be filtered , false otherwise
     */
    boolean passedFilter(Contact contact);
}
