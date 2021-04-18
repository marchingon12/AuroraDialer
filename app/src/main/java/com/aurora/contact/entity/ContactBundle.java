package com.aurora.contact.entity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.SparseArray;

import com.aurora.contact.enums.FieldType;
import com.aurora.contact.interfaces.Entity;
import com.aurora.contact.operations.ContactsGetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static android.provider.ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE;
import static android.provider.ContactsContract.CommonDataKinds.Organization.TITLE;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ContactBundle {

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

    private Context context;
    private ContentResolver resolver;
    private SelectionType selectionType;
    private String selection;

    private List<FieldType> fieldTypeList;
    private SparseArray<List<PhoneNumber>> phonesDataMap;
    private SparseArray<List<Address>> addressDataMap;
    private SparseArray<List<Email>> emailDataMap;
    private SparseArray<List<SpecialDate>> specialDateMap;
    private SparseArray<List<Relation>> relationMap;
    private SparseArray<List<IMAddress>> imAddressesDataMap;
    private SparseArray<List<String>> websitesDataMap;
    private SparseArray<String> notesDataMap;
    private SparseArray<String> nicknameDataMap;
    private SparseArray<String> sipDataMap;
    private SparseArray<Organization> organisationDataMap;
    private SparseArray<NameData> nameDataMap;
    private SparseArray<List<Group>> groupsDataMap;

    public ContactBundle(Context context, SelectionType selectionType, String selection) {
        this.context = context;
        this.selectionType = selectionType;
        this.selection = selection;
        this.resolver = context.getContentResolver();
        this.fieldTypeList = new ArrayList<>();
        this.fieldTypeList.addAll(Arrays.asList(FieldType.values()));
    }

    private String getSelection() {
        switch (selectionType) {
            case CONTACT_ID:
                return " = ? AND contact_id = " + selection;
            default:
                return "= ? ";
        }
    }

    ContactBundle getContactBundle() {
        fieldTypeList.addAll(Arrays.asList(FieldType.values()));
        SparseArray<List<PhoneNumber>> phonesDataMap = fieldTypeList.contains(FieldType.PHONE_NUMBERS)
                ? getDataMap(getCursorFromContentType(WITH_LABEL_PROJECTION, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
                (mainData, contactId, labelId, labelName) -> {
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
                ? getDataMap(getCursorFromContentType(WITH_LABEL_PROJECTION, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE),
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
                ? getDataMap(getCursorFromContentType(WITH_LABEL_PROJECTION, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE),
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
                ? getDataMap(getCursorFromContentType(WITH_LABEL_PROJECTION, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE),
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
                ? getDataMap(getCursorFromContentType(WITH_LABEL_PROJECTION, ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE),
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
                ? getStringDataMap(ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                : new SparseArray<>();

        SparseArray<String> nicknameDataMap = fieldTypeList.contains(FieldType.NICKNAME)
                ? getStringDataMap(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)
                : new SparseArray<>();

        SparseArray<String> sipDataMap = fieldTypeList.contains(FieldType.SIP)
                ? getStringDataMap(ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE)
                : new SparseArray<>();

        SparseArray<Organization> organisationDataMap = fieldTypeList.contains(FieldType.ORGANIZATION)
                ? getOrganizationDataMap()
                : new SparseArray<>();


        SparseArray<NameData> nameDataMap = fieldTypeList.contains(FieldType.NAME_DATA)
                ? getNameDataMaps()
                : new SparseArray<>();

        SparseArray<List<Group>> groupsDataMap = fieldTypeList.contains(FieldType.GROUPS)
                ? getGroupsDataMaps()
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

    public SparseArray<List<PhoneNumber>> getPhoneDataSparseArray() {
        return getDataMap(getCursorFromContentType(WITH_LABEL_PROJECTION, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
                (mainData, contactId, labelId, labelName) -> {
                    PhoneNumber number;
                    if (labelName != null)
                        number = new PhoneNumber(mainData, labelName);
                    else
                        number = new PhoneNumber(context, mainData, labelId);
                    number.setContactId(contactId);
                    return number;
                });
    }

    private SparseArray<List<String>> getWebSitesMap() {
        SparseArray<List<String>> idSiteMap = new SparseArray<>();
        Cursor cursor = getCursorFromContentType(new String[]{ID_KEY, MAIN_DATA_KEY},
                ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);

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
        Cursor cursor = resolver.query(ContactsContract.Groups.CONTENT_URI, new String[]{ContactsContract.Groups._ID, ContactsContract.Groups.TITLE}, null, null, null);
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

    private SparseArray<List<Group>> getGroupsDataMaps() {
        SparseArray<List<Group>> idListGroupMap = new SparseArray<>();
        SparseArray<Group> groupMapById = getGroupsMap();

        Cursor cursor = getCursorFromContentType(new String[]{ID_KEY, MAIN_DATA_KEY},
                ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);

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

    private SparseArray<NameData> getNameDataMaps() {
        Cursor cursor = getCursorFromContentType(new String[]{
                ID_KEY,
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.PREFIX,
                ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
                ContactsContract.CommonDataKinds.StructuredName.SUFFIX,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME
        }, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);

        SparseArray<NameData> nameDataSparseArray = new SparseArray<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ID_KEY));
                if (nameDataSparseArray.get(id) == null)
                    nameDataSparseArray.put(id, NameData
                            .builder()
                            .fullName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME)))
                            .firstName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)))
                            .surname(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)))
                            .namePrefix(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PREFIX)))
                            .middleName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME)))
                            .nameSuffix(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.SUFFIX)))
                            .phoneticFirst(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME)))
                            .phoneticMiddle(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME)))
                            .phoneticLast(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME)))
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
                ContactsContract.CommonDataKinds.Im.PROTOCOL,
                ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL
        }, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ID_KEY));
                String data = cursor.getString(cursor.getColumnIndex(MAIN_DATA_KEY));
                int labelId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
                String customLabel = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL));
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
        Cursor noteCur = getCursorFromContentType(new String[]{ID_KEY, MAIN_DATA_KEY, TITLE}, CONTENT_ITEM_TYPE);
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

    private Cursor getCursorFromContentType(String[] projection, String contentType) {
        String selection = ContactsContract.Data.MIMETYPE + getSelection();
        String[] selectionParam = new String[]{contentType};
        return resolver.query(ContactsContract.Data.CONTENT_URI, projection, selection, selectionParam, null);
    }

    private <T extends Entity> SparseArray<List<T>> getDataMap(Cursor cursor, ContactsGetter.WithLabelCreator<T> creator) {
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

    public enum SelectionType {
        CONTACT_ID,
        CONTACT_NAME,
        CONTACT_NUMBER
    }
}
