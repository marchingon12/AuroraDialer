package com.aurora.phone;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aurora.contact.entity.Contact;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoadContactsUseCase {

    private List<Contact> originalContactList;

    public LoadContactsUseCase(List<Contact> originalContactList) {
        this.originalContactList = originalContactList;
    }

    public Map<String, List<Contact>> execute(@NonNull final Context context) {
        final Map<String, List<Contact>> map = new LinkedHashMap<>();

        for (char letter = 'A'; letter <= 'Z'; letter++) {
            final List<Contact> filteredContacts = getContactsWithLetter(originalContactList, letter);
            if (filteredContacts.size() > 0) {
                map.put(String.valueOf(letter), filteredContacts);
            }
        }

        return map;
    }

    private List<Contact> getContactsWithLetter(@NonNull final List<Contact> contactList, final char letter) {
        final List<Contact> contactsList = new ArrayList<>();

        for (final Contact contact : contactList) {
            if (contact.getCompositeName().charAt(0) == letter) {
                contactsList.add(contact);
            }
        }
        return contactsList;
    }
}