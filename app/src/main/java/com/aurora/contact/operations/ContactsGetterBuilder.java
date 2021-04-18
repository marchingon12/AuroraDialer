package com.aurora.contact.operations;

import android.content.Context;
import android.provider.ContactsContract;

import com.aurora.contact.entity.Contact;
import com.aurora.contact.enums.FieldType;
import com.aurora.contact.enums.Sorting;
import com.aurora.contact.interfaces.BaseFilter;
import com.aurora.contact.utils.FilterUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ContactsGetterBuilder {
    private Context context;
    private Sorting sorting = Sorting.BY_DISPLAY_NAME_ASC;
    private StringBuilder stringBuilder = new StringBuilder();
    private List<String> paramList = new ArrayList<>(2);
    private List<BaseFilter> filterList = new ArrayList<>(8);
    private List<FieldType> fieldTypeList = new ArrayList<>(8);

    public ContactsGetterBuilder(Context context) {
        this.context = context;
    }

    /**
     * <p>
     * Sets sort order for all contacts
     * </p>
     * <p>
     * Sort types could be found here {@link Sorting}
     * </p>
     * <p>
     * By default is ascending by display name
     * </p>
     *
     * @param sortOrder order to sort
     */
    public ContactsGetterBuilder setSortOrder(Sorting sortOrder) {
        this.sorting = sortOrder;
        return this;
    }

    /**
     * <p>
     * Should get all contacts or contacts only with phones
     * </p>
     * <p>
     * Note : Will automatically query for phone numbers.
     * </p>
     * <p>
     * No need to explicitly add Phone numbers to field list
     * </p>
     * By default returns all contacts
     */
    public ContactsGetterBuilder onlyWithPhones() {
        if (stringBuilder.length() != 0)
            stringBuilder.append(" AND ");
        stringBuilder.append(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)
                .append(" = 1");
        addField(FieldType.PHONE_NUMBERS);
        return this;
    }

    /**
     * <p>
     * Should get contacts only with photos or not
     * </p>
     * By default returns all contacts
     */
    public ContactsGetterBuilder onlyStarred() {
        if (stringBuilder.length() != 0)
            stringBuilder.append(" AND ");
        stringBuilder.append(ContactsContract.Contacts.STARRED)
                .append(" IS 1");
        return this;
    }

    /**
     * <p>
     * Should get contacts only with photos or not
     * </p>
     * By default returns all contacts
     */
    public ContactsGetterBuilder onlyWithPhotos() {
        if (stringBuilder.length() != 0)
            stringBuilder.append(" AND ");
        stringBuilder.append(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                .append(" IS NOT NULL");
        return this;
    }

    /**
     * Searches for contacts with name that contains sequence
     *
     * @param nameLike sequence to search for
     */
    public ContactsGetterBuilder withNameLike(String nameLike) {
        if (stringBuilder.length() != 0)
            stringBuilder.append(" AND ");
        stringBuilder.append(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                .append(" LIKE ?");
        paramList.add("%" + nameLike + "%");
        return this;
    }

    /**
     * Searches for contacts with this name
     *
     * @param name name to search for
     */
    public ContactsGetterBuilder withName(String name) {
        if (stringBuilder.length() != 0)
            stringBuilder.append(" AND ");
        stringBuilder.append(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                .append(" = ?");
        paramList.add(name);
        return this;
    }

    /**
     * Searches for contacts that contains this phoneNumber sequence
     *
     * @param number phoneNumber sequence to search for
     */
    public ContactsGetterBuilder withPhoneLike(final String number) {
        filterList.add(FilterUtils.withPhoneLikeFilter(number));
        return onlyWithPhones();
    }

    /**
     * Searches for contacts with this phoneNumber
     *
     * @param number phoneNumber to search for
     */
    public ContactsGetterBuilder withPhone(final String number) {
        filterList.add(FilterUtils.withPhoneFilter(number));
        return onlyWithPhones();
    }

    /**
     * Searches for contacts with this email
     * Implicitly adds Email field
     *
     * @param email email to search for
     */
    public ContactsGetterBuilder withEmail(final String email) {
        addField(FieldType.EMAILS);
        filterList.add(FilterUtils.withEmailFilter(email));
        return this;
    }

    /**
     * Searches for contacts that contains sequence
     * Implicitly adds Email field
     *
     * @param sequence sequence to search for
     */
    public ContactsGetterBuilder withEmailLike(final String sequence) {
        addField(FieldType.EMAILS);
        filterList.add(FilterUtils.withEmailLikeFilter(sequence));
        return this;
    }

    /**
     * Searches for contacts with this phoneNumber
     * Implicitly adds Address field
     *
     * @param number phoneNumber to search for
     */
    public ContactsGetterBuilder withAddress(final String number) {
        addField(FieldType.ADDRESS);
        filterList.add(FilterUtils.withAddressFilter(number));
        return this;
    }

    /**
     * Searches for addresses that contains this sequence
     * Implicitly adds Address field
     *
     * @param sequence sequence to search for
     */
    public ContactsGetterBuilder withAddressLike(final String sequence) {
        addField(FieldType.ADDRESS);
        filterList.add(FilterUtils.withAddressLikeFilter(sequence));
        return this;
    }


    private <T extends Contact> List<T> applyFilters(List<T> contactList) {
        for (BaseFilter filter : filterList) {
            for (Iterator<T> iterator = contactList.iterator(); iterator.hasNext(); ) {
                Contact contact = iterator.next();
                if (!filter.passedFilter(contact))
                    iterator.remove();
            }
        }
        return contactList;
    }

    /**
     * <p>
     * Applies custom filter to query on contacts list
     * </p>
     * <p>
     * Additional filters and example implementations could be found here {@link FilterUtils}
     * </p>
     *
     * @param filter filter to apply
     */
    public ContactsGetterBuilder applyCustomFilter(BaseFilter filter) {
        filterList.add(filter);
        return this;
    }

    /**
     * <p>
     * Enables all fields for query
     * </p>
     * <p>
     * Note : Consider to enable fields you need with {@link #addField(FieldType...)} to increase performance
     * </p>
     */
    public ContactsGetterBuilder allFields() {
        addField(FieldType.values());
        return this;
    }

    /**
     * <p>
     * Enables fields that should be queried
     * </p>
     * <p>
     * Number of fields influence on performance
     * </p>
     *
     * @param fieldType field type you want to add
     */
    public ContactsGetterBuilder addField(FieldType... fieldType) {
        fieldTypeList.addAll(Arrays.asList(fieldType));
        return this;
    }

    private ContactsGetter initGetter() {
        ContactsGetter getter;
        if (stringBuilder.length() == 0)
            getter = new ContactsGetter(context, fieldTypeList, sorting.getSorting(), null, null);
        else
            getter = new ContactsGetter(context, fieldTypeList, sorting.getSorting(), generateSelectionArgs(), generateSelection());
        return getter;
    }

    /**
     * Builds list of contacts
     *
     * @param T class of object you want to get data
     */
    public <T extends Contact> List<T> buildList(Class<? extends Contact> T) {
        return applyFilters(initGetter()
                .setContactDataClass(T)
                .getContacts());
    }

    /**
     * Builds list of contacts
     */
    public List<Contact> buildList() {
        return applyFilters(initGetter().getContacts());
    }

    public Contact getContactById(long contactId) {
        fieldTypeList.addAll(Arrays.asList(FieldType.values()));
        ContactsGetter getter = new ContactsGetter(context, fieldTypeList, sorting.getSorting(),
                generateSelectionArgs(), ContactsContract.CommonDataKinds.Phone._ID + " IS " + contactId);
        return getter.getContact();
    }

    /**
     * Gets contact by local data
     *
     * @param id data to search for
     * @return contact with data specified by options or null if no contact with this data
     */
    public Contact getById(int id) {
        if (stringBuilder.length() != 0)
            stringBuilder.append(" AND ");
        stringBuilder.append(ContactsContract.CommonDataKinds.Phone._ID)
                .append(" = ?");
        paramList.add(String.valueOf(id));
        return firstOrNull();
    }

    /**
     * Gets contact by local data
     *
     * @param id data to search for
     * @param T  class of object you want to get data
     * @return contact with data specified by options or null if no contact with this data
     */
    public <T extends Contact> T getById(int id, Class<T> T) {
        if (stringBuilder.length() != 0)
            stringBuilder.append(" AND ");
        stringBuilder.append(ContactsContract.CommonDataKinds.Phone._ID)
                .append(" = ?");
        paramList.add(String.valueOf(id));
        return firstOrNull(T);
    }

    /**
     * Get first contact of null if no contacts with these params
     */
    public Contact firstOrNull() {
        List<Contact> contacts = buildList();
        if (contacts.isEmpty())
            return null;
        else
            return contacts.get(0);
    }

    /**
     * Get first contact of null if no contacts with these params
     *
     * @param T class of object you want to get data
     */
    public <T extends Contact> T firstOrNull(Class<T> T) {
        List<T> contacts = buildList(T);
        if (contacts.isEmpty())
            return null;
        else
            return contacts.get(0);
    }

    protected String generateSelection() {
        return stringBuilder.toString();
    }

    protected String[] generateSelectionArgs() {
        return paramList.toArray(new String[paramList.size()]);
    }
}
