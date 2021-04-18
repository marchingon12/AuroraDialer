package com.aurora.contact.entity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Contact {

    private int contactId;
    private String lookupKey;
    private List<Email> emailList;
    private List<PhoneNumber> phoneList;
    private List<Address> addressesList;
    private List<String> websitesList;
    private List<IMAddress> imAddressesList;
    private List<Relation> relationsList;
    private List<SpecialDate> specialDatesList;
    private List<Group> groupList;
    private String note;
    private String nickName;
    private String phoneNumber;
    private String sipAddress;
    private Uri photoUri;
    private Organization organization;
    private NameData nameData;
    private String compositeName;
    private String accountName;
    private String accountType;
    private long lastModificationDate;
    private Uri updatedPhotoUri;
    private Bitmap updatedBitmap;
    private boolean starred;


    public Contact(String name, @NonNull List<PhoneNumber> phoneNumbers, Uri photoUri) {
        this.compositeName = name;
        this.phoneList = phoneNumbers;
        this.photoUri = photoUri;
    }

    public Contact(String name, @NonNull String phoneNumber, @Nullable Uri photoUri) {
        this.compositeName = name;
        this.photoUri = photoUri;
        this.phoneNumber = phoneNumber;
    }

    public Contact(Cursor cursor) {
        String photoUriString = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
        Uri photoUri = photoUriString == null ? Uri.EMPTY : Uri.parse(photoUriString);
        this.compositeName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
        this.photoUri = photoUri;
        this.phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    }


    public static List<Contact> getLiteContactList(Context context, Cursor cursor) {
        List<Contact> contactList = new ArrayList<>();

        if (cursor == null) {
            return contactList;
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            long date = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));
            String photoUriString = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri photoUri = photoUriString == null ? Uri.EMPTY : Uri.parse(photoUriString);

            Contact contact = Contact
                    .builder()
                    .contactId(id)
                    .lookupKey(lookupKey)
                    .lastModificationDate(date)
                    .compositeName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)))
                    .starred(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.STARRED)) == 1)
                    .phoneList(new ArrayList<>())
                    .photoUri(photoUri)
                    .build();
            contactList.add(contact);
        }
        cursor.close();
        return contactList;
    }

    public static List<Contact> getSearchContactList(Context context, Cursor cursor) {
        List<Contact> contactList = new ArrayList<>();

        if (cursor == null) {
            return contactList;
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String photoUriString = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            Uri photoUri = photoUriString == null ? Uri.EMPTY : Uri.parse(photoUriString);

            Contact contact = Contact
                    .builder()
                    .contactId(id)
                    .compositeName(name)
                    .phoneNumber(number)
                    .photoUri(photoUri)
                    .build();
            contactList.add(contact);
        }
        cursor.close();
        return contactList;
    }

    public static Contact getContact(Context context, Cursor cursor) {

        cursor.moveToFirst();

        int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        long date = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));
        String photoUriString = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        Uri photoUri = photoUriString == null ? Uri.EMPTY : Uri.parse(photoUriString);

        ContactBundle contactBundle = new ContactBundle(context, ContactBundle.SelectionType.CONTACT_ID, String.valueOf(id))
                .getContactBundle();

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
}