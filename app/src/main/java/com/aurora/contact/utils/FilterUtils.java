package com.aurora.contact.utils;

import com.aurora.contact.entity.Address;
import com.aurora.contact.entity.Contact;
import com.aurora.contact.entity.Email;
import com.aurora.contact.entity.PhoneNumber;
import com.aurora.contact.interfaces.BaseFilter;
import com.aurora.contact.interfaces.FieldFilter;
import com.aurora.contact.interfaces.ListFilter;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.List;

public class FilterUtils {

    public static BaseFilter<PhoneNumber, String> withPhoneLikeFilter(final String number) {
        return new ListFilter<PhoneNumber, String>() {
            @Override
            protected String getFilterPattern() {
                return number;
            }

            @Override
            protected String getFilterData(PhoneNumber data) {
                return data.getMainData();
            }

            @Override
            protected List<PhoneNumber> getFilterContainer(Contact contact) {
                return contact.getPhoneList();
            }

            @Override
            protected boolean getFilterCondition(String data, String pattern) {
                return formatNumber(data).contains(pattern);
            }

            private String formatNumber(String number) {
                return number.replaceAll("[^0-9+]", "");
            }
        };
    }

    public static BaseFilter<PhoneNumber, String> withPhoneFilter(final String number) {
        return new ListFilter<PhoneNumber, String>() {
            @Override
            protected String getFilterPattern() {
                return number;
            }

            @Override
            protected String getFilterData(PhoneNumber data) {
                return data.getMainData();
            }

            @Override
            protected List<PhoneNumber> getFilterContainer(Contact contact) {
                return contact.getPhoneList();
            }

            @Override
            protected boolean getFilterCondition(String data, String pattern) {
                PhoneNumberUtil.MatchType matchType = PhoneNumberUtil.getInstance().isNumberMatch(data, pattern);
                return (matchType == PhoneNumberUtil.MatchType.EXACT_MATCH) || (matchType == PhoneNumberUtil.MatchType.NSN_MATCH);
            }
        };
    }

    public static BaseFilter<Email, String> withEmailFilter(final String email) {
        return new ListFilter<Email, String>() {
            @Override
            protected String getFilterPattern() {
                return email;
            }

            @Override
            protected String getFilterData(Email data) {
                return data.getMainData();
            }

            @Override
            protected List<Email> getFilterContainer(Contact contact) {
                return contact.getEmailList();
            }

            @Override
            protected boolean getFilterCondition(String data, String pattern) {
                return data.equalsIgnoreCase(pattern);
            }
        };
    }

    public static BaseFilter<Email, String> withEmailLikeFilter(final String email) {
        return new ListFilter<Email, String>() {
            @Override
            protected String getFilterPattern() {
                return email;
            }

            @Override
            protected String getFilterData(Email data) {
                return data.getMainData();
            }

            @Override
            protected List<Email> getFilterContainer(Contact contact) {
                return contact.getEmailList();
            }

            @Override
            protected boolean getFilterCondition(String data, String pattern) {
                return data.toLowerCase().contains(pattern.toLowerCase());
            }
        };
    }

    public static BaseFilter<Address, String> withAddressLikeFilter(final String address) {
        return new ListFilter<Address, String>() {
            @Override
            protected String getFilterPattern() {
                return address;
            }

            @Override
            protected String getFilterData(Address data) {
                return data.getMainData();
            }

            @Override
            protected List<Address> getFilterContainer(Contact contact) {
                return contact.getAddressesList();
            }

            @Override
            protected boolean getFilterCondition(String data, String pattern) {
                return data.toLowerCase().contains(pattern.toLowerCase());
            }
        };
    }

    public static BaseFilter<Address, String> withAddressFilter(final String address) {
        return new ListFilter<Address, String>() {
            @Override
            protected String getFilterPattern() {
                return address;
            }

            @Override
            protected String getFilterData(Address data) {
                return data.getMainData();
            }

            @Override
            protected List<Address> getFilterContainer(Contact contact) {
                return contact.getAddressesList();
            }

            @Override
            protected boolean getFilterCondition(String data, String pattern) {
                return data.equalsIgnoreCase(pattern);
            }
        };
    }

    public static BaseFilter<Contact, String> withNoteLike(final String noteLike) {
        return new FieldFilter<Contact, String>() {
            @Override
            protected String getFilterPattern() {
                return noteLike;
            }

            @Override
            protected String getFilterData(Contact data) {
                return data.getNote();
            }

            @Override
            protected boolean getFilterCondition(String data, String pattern) {
                return data.toLowerCase().contains(pattern.toLowerCase());
            }
        };
    }

    public static BaseFilter<Contact, String> withNote(final String note) {
        return new FieldFilter<Contact, String>() {
            @Override
            protected String getFilterPattern() {
                return note;
            }

            @Override
            protected String getFilterData(Contact data) {
                return data.getNote();
            }

            @Override
            protected boolean getFilterCondition(String data, String pattern) {
                return data.equalsIgnoreCase(pattern);
            }
        };
    }
}
