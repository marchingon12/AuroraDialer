package com.aurora.contact.operations;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.util.SparseArray;

import com.aurora.contact.entity.Address;
import com.aurora.contact.entity.Contact;
import com.aurora.contact.entity.ContactBundle;
import com.aurora.contact.entity.Email;
import com.aurora.contact.entity.Group;
import com.aurora.contact.entity.IMAddress;
import com.aurora.contact.entity.NameData;
import com.aurora.contact.entity.Organization;
import com.aurora.contact.entity.PhoneNumber;
import com.aurora.contact.entity.Relation;
import com.aurora.contact.entity.SpecialDate;
import com.aurora.contact.enums.FieldType;
import com.aurora.contact.interfaces.Entity;

import java.util.ArrayList;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Event;
import static android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import static android.provider.ContactsContract.CommonDataKinds.Nickname;
import static android.provider.ContactsContract.CommonDataKinds.Note;
import static android.provider.ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE;
import static android.provider.ContactsContract.CommonDataKinds.Organization.TITLE;
import static android.provider.ContactsContract.CommonDataKinds.Phone;
import static android.provider.ContactsContract.CommonDataKinds.SipAddress;
import static android.provider.ContactsContract.CommonDataKinds.StructuredName;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import static android.provider.ContactsContract.CommonDataKinds.Website;

public class ContactsGetter {

    private static final String MAIN_DATA_KEY = "data1";
    private static final String LABEL_DATA_KEY = "data2";
    private static final String CUSTOM_LABEL_DATA_KEY = "data3";
    private static final String ID_KEY = "contact_id";

    private static final String[] WITH_LABEL_PROJECTION = new String[]{
            ID_KEY,
            MAIN_DATA_KEY,
            LABEL_DATA_KEY,
            CUSTOM_LABEL_DATA_KEY
    };

    private static final String[] CONTACTS_PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.STARRED
    };

    private static final String[] ADDITIONAL_DATA_PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.RawContacts.ACCOUNT_TYPE,
            ContactsContract.RawContacts.ACCOUNT_NAME,
            ContactsContract.RawContacts.STARRED
    };

    private ContentResolver resolver;
    private Context context;
    private List<FieldType> fieldTypeList;
    private String[] selectionArgs;
    private String sorting;
    private String selection;
    private Class<? extends Contact> contactClass;

    ContactsGetter(Context context, List<FieldType> enabledFields, String sorting, String[] selectionArgs, String selection) {
        this.context = context;
        this.resolver = context.getContentResolver();
        this.fieldTypeList = enabledFields;
        this.selectionArgs = selectionArgs;
        this.sorting = sorting;
        this.selection = selection;
    }

    ContactsGetter setContactDataClass(Class<? extends Contact> contactClass) {
        this.contactClass = contactClass;
        return this;
    }

    private Cursor getContactsCursorWithSelection(String ordering, String selection, String[] selectionArgs) {
        return resolver.query(ContactsContract.Contacts.CONTENT_URI, CONTACTS_PROJECTION, selection, selectionArgs, ordering);
    }

    private Cursor getContactsCursorWithAdditionalData() {
        return resolver.query(ContactsContract.RawContacts.CONTENT_URI, ADDITIONAL_DATA_PROJECTION, null, null, null);
    }

    private Cursor getContactsCursorWithAdditionalData(String ordering, String selection, String[] selectionArgs) {
        return resolver.query(ContactsContract.RawContacts.CONTENT_URI, ADDITIONAL_DATA_PROJECTION, selection, selectionArgs, ordering);
    }

    private <T extends Contact> T getContactData() {
        if (contactClass == null) {
            return (T) new Contact();
        }
        try {
            return (T) contactClass.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Contact getContact() {
        Cursor cursor = getContactsCursorWithSelection(sorting, selection, selectionArgs);
        Cursor additionalDataCursor = getContactsCursorWithAdditionalData();
        ContactBundle contactBundle = buildContactBundle();
        cursor.moveToFirst();
        additionalDataCursor.moveToFirst();
        return getContact(cursor, contactBundle);
    }

    public Contact getContact(Cursor cursor, ContactBundle contactBundle) {

        int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        long date = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));
        String photoUriString = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        Uri photoUri = photoUriString == null ? Uri.EMPTY : Uri.parse(photoUriString);

        return Contact.builder()
                .contactId(id)
                .lookupKey(lookupKey)
                .lastModificationDate(date)
                .photoUri(photoUri)
                .compositeName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)))
                .starred(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.STARRED)) == 1)
                .phoneList(contactBundle.getPhonesDataMap().get(id))
                .addressesList(contactBundle.getAddressDataMap().get(id))
                .emailList(contactBundle.getEmailDataMap().get(id))
                .websitesList(contactBundle.getWebsitesDataMap().get(id))
                .note(contactBundle.getNotesDataMap().get(id))
                .imAddressesList(contactBundle.getImAddressesDataMap().get(id))
                .relationsList(contactBundle.getRelationMap().get(id))
                .specialDatesList(contactBundle.getSpecialDateMap().get(id))
                .nickName(contactBundle.getNicknameDataMap().get(id))
                .organization(contactBundle.getOrganisationDataMap().get(id))
                .sipAddress(contactBundle.getSipDataMap().get(id))
                .nameData(contactBundle.getNameDataMap().get(id))
                .groupList(contactBundle.getGroupsDataMap().get(id))
                .build();
    }

    public ContactBundle buildContactBundle() {
        SparseArray<List<PhoneNumber>> phonesDataMap = fieldTypeList.contains(FieldType.PHONE_NUMBERS) ?
                getDataMap(getCursorFromContentType(WITH_LABEL_PROJECTION, Phone.CONTENT_ITEM_TYPE),
                        (mainData, contactId, labelId, labelName) ->
                        {
                            PhoneNumber number;
                            if (labelName != null)
                                number = new PhoneNumber(mainData, labelName);
                            else
                                number = new PhoneNumber(context, mainData, labelId);
                            number.setContactId(contactId);
                            return number;
                        })
                : new SparseArray<>();

        SparseArray<List<Address>> addressDataMap = fieldTypeList.contains(FieldType.ADDRESS)
                ? getDataMap(getCursorFromContentType(WITH_LABEL_PROJECTION, StructuredPostal.CONTENT_ITEM_TYPE),
                (mainData, contactId, labelId, labelName) -> {
                    Address address;
                    if (labelName != null)
                        address = new Address(mainData, labelName);
                    else
                        address = new Address(context, mainData, labelId);
                    address.setContactId(contactId);
                    return address;
                })
                : new SparseArray<>();

        SparseArray<List<Email>> emailDataMap = fieldTypeList.contains(FieldType.EMAILS)
                ? getDataMap(getCursorFromContentType(WITH_LABEL_PROJECTION, CommonDataKinds.Email.CONTENT_ITEM_TYPE),
                (mainData, contactId, labelId, labelName) -> {
                    Email email;
                    if (labelName != null)
                        email = new Email(mainData, labelName);
                    else
                        email = new Email(context, mainData, labelId);
                    email.setContactId(contactId);
                    return email;
                })
                : new SparseArray<>();

        SparseArray<List<SpecialDate>> specialDateMap = fieldTypeList.contains(FieldType.SPECIAL_DATES)
                ? getDataMap(getCursorFromContentType(WITH_LABEL_PROJECTION, Event.CONTENT_ITEM_TYPE),
                (mainData, contactId, labelId, labelName) -> {
                    SpecialDate specialData;
                    if (labelName != null)
                        specialData = new SpecialDate(mainData, labelName);
                    else
                        specialData = new SpecialDate(context, mainData, labelId);
                    specialData.setContactId(contactId);
                    return specialData;
                })
                : new SparseArray<>();

        SparseArray<List<Relation>> relationMap = fieldTypeList.contains(FieldType.RELATIONS)
                ? getDataMap(getCursorFromContentType(WITH_LABEL_PROJECTION, CommonDataKinds.Relation.CONTENT_ITEM_TYPE),
                (mainData, contactId, labelId, labelName) -> {
                    Relation relation;
                    if (labelName != null)
                        relation = new Relation(mainData, labelName);
                    else
                        relation = new Relation(context, mainData, labelId);
                    relation.setContactId(contactId);
                    return relation;
                })
                : new SparseArray<>();

        SparseArray<List<IMAddress>> imAddressesDataMap = fieldTypeList.contains(FieldType.IM_ADDRESSES)
                ? getIMAddressesMap()
                : new SparseArray<>();

        SparseArray<List<String>> websitesDataMap = fieldTypeList.contains(FieldType.WEBSITES)
                ? getWebSitesMap()
                : new SparseArray<>();

        SparseArray<String> notesDataMap = fieldTypeList.contains(FieldType.NOTES)
                ? getStringDataMap(Note.CONTENT_ITEM_TYPE)
                : new SparseArray<>();

        SparseArray<String> nicknameDataMap = fieldTypeList.contains(FieldType.NICKNAME)
                ? getStringDataMap(Nickname.CONTENT_ITEM_TYPE)
                : new SparseArray<>();

        SparseArray<String> sipDataMap = fieldTypeList.contains(FieldType.SIP)
                ? getStringDataMap(SipAddress.CONTENT_ITEM_TYPE)
                : new SparseArray<>();

        SparseArray<Organization> organisationDataMap = fieldTypeList.contains(FieldType.ORGANIZATION)
                ? getOrganizationDataMap()
                : new SparseArray<>();


        SparseArray<NameData> nameDataMap = fieldTypeList.contains(FieldType.NAME_DATA)
                ? getNameDataMap()
                : new SparseArray<>();

        SparseArray<List<Group>> groupsDataMap = fieldTypeList.contains(FieldType.GROUPS)
                ? getGroupsDataMap()
                : new SparseArray<>();

        return ContactBundle.builder()
                .addressDataMap(addressDataMap)
                .emailDataMap(emailDataMap)
                .groupsDataMap(groupsDataMap)
                .imAddressesDataMap(imAddressesDataMap)
                .nameDataMap(nameDataMap)
                .nicknameDataMap(nicknameDataMap)
                .notesDataMap(notesDataMap)
                .organisationDataMap(organisationDataMap)
                .phonesDataMap(phonesDataMap)
                .relationMap(relationMap)
                .sipDataMap(sipDataMap)
                .specialDateMap(specialDateMap)
                .websitesDataMap(websitesDataMap)
                .build();
    }

    public <T extends Contact> List<T> getContacts() {
        Cursor cursor = getContactsCursorWithSelection(sorting, selection, selectionArgs);
        Cursor additionalDataCursor = getContactsCursorWithAdditionalData();
        SparseArray<T> contactsSparse = new SparseArray<>();
        List<T> result = new ArrayList<>();

        if (cursor == null)
            return result;

        ContactBundle contactBundle = buildContactBundle();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Contact contact = getContact(cursor, contactBundle);
            contactsSparse.put(id, (T) contact);
            result.add((T) contact);
        }

        cursor.close();

        while (additionalDataCursor.moveToNext()) {
            int id = additionalDataCursor.getInt(additionalDataCursor.getColumnIndex(ContactsContract.RawContacts._ID));
            Contact relatedContact = contactsSparse.get(id);
            if (relatedContact != null) {
                String accountType = additionalDataCursor.getString(additionalDataCursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE));
                String accountName = additionalDataCursor.getString(additionalDataCursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME));
                relatedContact.setAccountName(accountName);
                relatedContact.setAccountType(accountType);
                relatedContact.setStarred(additionalDataCursor.getInt(additionalDataCursor.getColumnIndex(ContactsContract.RawContacts.STARRED)) == 1);
            }
        }
        additionalDataCursor.close();
        return result;
    }

    private SparseArray<List<String>> getWebSitesMap() {
        SparseArray<List<String>> idSiteMap = new SparseArray<>();
        Cursor cursor = getCursorFromContentType(new String[]{ID_KEY, MAIN_DATA_KEY},
                Website.CONTENT_ITEM_TYPE);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ID_KEY));
                String website = cursor.getString(cursor.getColumnIndex(MAIN_DATA_KEY));
                List<String> cursorrentWebsiteList = idSiteMap.get(id);
                if (cursorrentWebsiteList == null) {
                    cursorrentWebsiteList = new ArrayList<>();
                    cursorrentWebsiteList.add(website);
                    idSiteMap.put(id, cursorrentWebsiteList);
                } else cursorrentWebsiteList.add(website);
            }
            cursor.close();
        }
        return idSiteMap;
    }

    private SparseArray<Group> getGroupsMap() {
        SparseArray<Group> idGroupMap = new SparseArray<>();
        Cursor cursor = resolver.query(
                ContactsContract.Groups.CONTENT_URI,
                new String[]{
                        ContactsContract.Groups._ID,
                        ContactsContract.Groups.TITLE
                }, null, null, null
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Groups._ID));
                String title = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));
                idGroupMap.put(id, Group
                        .builder()
                        .groupId(id)
                        .groupTitle(title)
                        .build());
            }
            cursor.close();
        }
        return idGroupMap;
    }

    private SparseArray<List<Group>> getGroupsDataMap() {
        SparseArray<List<Group>> idListGroupMap = new SparseArray<>();
        SparseArray<Group> groupMapById = getGroupsMap();

        Cursor cursor = getCursorFromContentType(new String[]{ID_KEY, MAIN_DATA_KEY},
                GroupMembership.CONTENT_ITEM_TYPE);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ID_KEY));
                int groupId = cursor.getInt(cursor.getColumnIndex(MAIN_DATA_KEY));
                List<Group> groupList = idListGroupMap.get(id);
                if (groupList == null) {
                    groupList = new ArrayList<>();
                    groupList.add(groupMapById.get(groupId));
                    idListGroupMap.put(id, groupList);
                } else
                    groupList.add(groupMapById.get(groupId));
            }
            cursor.close();
        }
        return idListGroupMap;
    }

    private SparseArray<NameData> getNameDataMap() {
        Cursor cursor = getCursorFromContentType(new String[]{
                ID_KEY,
                StructuredName.DISPLAY_NAME,
                StructuredName.GIVEN_NAME,
                StructuredName.PHONETIC_MIDDLE_NAME,
                StructuredName.PHONETIC_FAMILY_NAME,
                StructuredName.FAMILY_NAME,
                StructuredName.PREFIX,
                StructuredName.MIDDLE_NAME,
                StructuredName.SUFFIX,
                StructuredName.PHONETIC_GIVEN_NAME
        }, StructuredName.CONTENT_ITEM_TYPE);

        SparseArray<NameData> nameDataSparseArray = new SparseArray<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ID_KEY));
                if (nameDataSparseArray.get(id) == null)
                    nameDataSparseArray.put(id, NameData
                            .builder()
                            .fullName(cursor.getString(cursor.getColumnIndex(StructuredName.DISPLAY_NAME)))
                            .firstName(cursor.getString(cursor.getColumnIndex(StructuredName.GIVEN_NAME)))
                            .surname(cursor.getString(cursor.getColumnIndex(StructuredName.FAMILY_NAME)))
                            .namePrefix(cursor.getString(cursor.getColumnIndex(StructuredName.PREFIX)))
                            .middleName(cursor.getString(cursor.getColumnIndex(StructuredName.MIDDLE_NAME)))
                            .nameSuffix(cursor.getString(cursor.getColumnIndex(StructuredName.SUFFIX)))
                            .phoneticFirst(cursor.getString(cursor.getColumnIndex(StructuredName.PHONETIC_GIVEN_NAME)))
                            .phoneticMiddle(cursor.getString(cursor.getColumnIndex(StructuredName.PHONETIC_MIDDLE_NAME)))
                            .phoneticLast(cursor.getString(cursor.getColumnIndex(StructuredName.PHONETIC_FAMILY_NAME)))
                            .build()
                    );
            }
            cursor.close();
        }
        return nameDataSparseArray;
    }

    private SparseArray<List<IMAddress>> getIMAddressesMap() {
        SparseArray<List<IMAddress>> idImAddressMap = new SparseArray<>();
        Cursor cursor = getCursorFromContentType(new String[]{
                ID_KEY,
                MAIN_DATA_KEY,
                Im.PROTOCOL,
                Im.CUSTOM_PROTOCOL
        }, Im.CONTENT_ITEM_TYPE);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ID_KEY));
                String data = cursor.getString(cursor.getColumnIndex(MAIN_DATA_KEY));
                int labelId = cursor.getInt(cursor.getColumnIndex(Im.PROTOCOL));
                String customLabel = cursor.getString(cursor.getColumnIndex(Im.CUSTOM_PROTOCOL));
                IMAddress cursorrent;
                if (customLabel == null)
                    cursorrent = new IMAddress(context, data, labelId);
                else
                    cursorrent = new IMAddress(data, customLabel);
                List<IMAddress> cursorrentWebsiteList = idImAddressMap.get(id);
                if (cursorrentWebsiteList == null) {
                    cursorrentWebsiteList = new ArrayList<>();
                    cursorrentWebsiteList.add(cursorrent);
                    idImAddressMap.put(id, cursorrentWebsiteList);
                } else cursorrentWebsiteList.add(cursorrent);
            }
            cursor.close();
        }
        return idImAddressMap;
    }

    private SparseArray<String> getStringDataMap(String contentType) {
        SparseArray<String> idNoteMap = new SparseArray<>();
        Cursor cursor = getCursorFromContentType(new String[]{
                ID_KEY,
                MAIN_DATA_KEY
        }, contentType);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ID_KEY));
                String note = cursor.getString(cursor.getColumnIndex(MAIN_DATA_KEY));
                if (note != null) idNoteMap.put(id, note);
            }
            cursor.close();
        }
        return idNoteMap;
    }

    private SparseArray<Organization> getOrganizationDataMap() {
        SparseArray<Organization> idOrganizationMap = new SparseArray<>();
        Cursor noteCur = getCursorFromContentType(new String[]{
                ID_KEY,
                MAIN_DATA_KEY,
                TITLE
        }, CONTENT_ITEM_TYPE);

        if (noteCur != null) {
            while (noteCur.moveToNext()) {
                int id = noteCur.getInt(noteCur.getColumnIndex(ID_KEY));
                String organizationName = noteCur.getString(noteCur.getColumnIndex(MAIN_DATA_KEY));
                String organizationTitle = noteCur.getString(noteCur.getColumnIndex(TITLE));
                idOrganizationMap.put(id, Organization.builder()
                        .name(organizationName)
                        .title(organizationTitle)
                        .build());
            }
            noteCur.close();
        }
        return idOrganizationMap;
    }

    private <T extends Entity> SparseArray<List<T>> getDataMap(Cursor cursor, WithLabelCreator<T> creator) {
        SparseArray<List<T>> dataSparseArray = new SparseArray<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ID_KEY));
                String data = cursor.getString(cursor.getColumnIndex(MAIN_DATA_KEY));
                int labelId = cursor.getInt(cursor.getColumnIndex(LABEL_DATA_KEY));
                String customLabel = cursor.getString(cursor.getColumnIndex(CUSTOM_LABEL_DATA_KEY));
                T tempCursor = creator.create(data, id, labelId, customLabel);
                List<T> tempDataList = dataSparseArray.get(id);
                if (tempDataList == null) {
                    tempDataList = new ArrayList<>();
                    tempDataList.add(tempCursor);
                    dataSparseArray.put(id, tempDataList);
                } else tempDataList.add(tempCursor);
            }
            cursor.close();
        }
        return dataSparseArray;
    }

    private Cursor getCursorFromContentType(String[] projection, String contentType) {
        String selection = ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionParam = new String[]{contentType};
        return resolver.query(ContactsContract.Data.CONTENT_URI,
                projection, selection, selectionParam, null);
    }

    public interface WithLabelCreator<T extends Entity> {
        T create(String mainData, int contactId, int labelId, String labelName);
    }
}