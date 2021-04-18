package com.aurora.contact.operations;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;

import com.aurora.contact.entity.Address;
import com.aurora.contact.entity.Contact;
import com.aurora.contact.entity.Email;
import com.aurora.contact.entity.IMAddress;
import com.aurora.contact.entity.NameData;
import com.aurora.contact.entity.Organization;
import com.aurora.contact.entity.PhoneNumber;
import com.aurora.contact.entity.Relation;
import com.aurora.contact.entity.SpecialDate;
import com.aurora.contact.interfaces.Entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class ContactsSaver {
    private ContentResolver mResolver;

    public ContactsSaver(ContentResolver resolver) {
        mResolver = resolver;
    }

    public int[] insertContacts(List<Contact> contactList) {
        ArrayList<ContentValues> cvList = new ArrayList<>(100);

        ContentProviderResult[] results = createContacts(contactList);
        int[] ids = new int[results.length];
        for (int i = 0; i < results.length; i++) {
            int id = Integer.parseInt(results[i].uri.getLastPathSegment());
            generateInsertOperations(cvList, contactList.get(i), id);
            ids[i] = id;
        }
        mResolver.bulkInsert(ContactsContract.Data.CONTENT_URI, cvList.toArray(new ContentValues[cvList.size()]));
        return ids;
    }

    private void generateInsertOperations(List<ContentValues> contentValuesList, Contact contact, int id) {
        for (PhoneNumber number : contact.getPhoneList()) {
            contentValuesList.add(getWithLabelCV(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, number, id));
        }
        for (Address address : contact.getAddressesList()) {
            contentValuesList.add(getWithLabelCV(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE, address, id));
        }
        for (Email email : contact.getEmailList()) {
            contentValuesList.add(getWithLabelCV(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, email, id));
        }
        for (SpecialDate specialDate : contact.getSpecialDatesList()) {
            contentValuesList.add(getWithLabelCV(ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE, specialDate, id));
        }
        for (Relation relation : contact.getRelationsList()) {
            contentValuesList.add(getWithLabelCV(ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE, relation, id));
        }
        for (String webSite : contact.getWebsitesList()) {
            contentValuesList.add(getStringTypeCV(ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE, webSite, id));
        }
        for (IMAddress imAddress : contact.getImAddressesList()) {
            contentValuesList.add(getImAddressCV(imAddress, id));
        }

        if (!contact.getNote().isEmpty())
            contentValuesList.add(getStringTypeCV(ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE, contact.getNote(), id));
        if (!contact.getNickName().isEmpty())
            contentValuesList.add(getStringTypeCV(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE, contact.getNickName(), id));
        if (!contact.getSipAddress().isEmpty())
            contentValuesList.add(getStringTypeCV(ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE, contact.getSipAddress(), id));
        contentValuesList.add(getNameDataCV(contact, id));
        Organization cursorrentOrganization = contact.getOrganization();
        if (!cursorrentOrganization.getName().isEmpty() || !cursorrentOrganization.getTitle().isEmpty())
            contentValuesList.add(getOrganizationTypeCV(cursorrentOrganization, id));
        saveUpdatedPhoto(id, contact);
    }

    /**
     * Save updated photo for the specified raw-contact.
     */
    private void saveUpdatedPhoto(long rawContactId, Contact contact) {
        try {
            InputStream inputStream;
            if (contact.getUpdatedPhotoUri() != null) {
                inputStream = mResolver.openInputStream(contact.getUpdatedPhotoUri());
                contact.setUpdatedPhotoUri(null);
            } else if (contact.getUpdatedBitmap() != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                contact.getUpdatedBitmap().compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                inputStream = new ByteArrayInputStream(bitmapdata);
                contact.setUpdatedBitmap(null);
            } else {
                return;
            }

            final Uri outputUri = Uri.withAppendedPath(
                    ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawContactId),
                    ContactsContract.RawContacts.DisplayPhoto.CONTENT_DIRECTORY);

            FileOutputStream outputStream;
            outputStream = mResolver
                    .openAssetFileDescriptor(outputUri, "rw").createOutputStream();
            final byte[] buffer = new byte[16 * 1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, length);
            outputStream.close();
            inputStream.close();
        } catch (Exception ignored) {
        }
    }

    private ContentValues getWithLabelCV(String contentType, Entity entity, int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, id);
        contentValues.put(ContactsContract.Data.MIMETYPE, contentType);
        contentValues.put(ContactsContract.Data.DATA1, entity.getMainData());
        contentValues.put(ContactsContract.Data.DATA2, entity.getLabelId());
        if (entity.getLabelId() == entity.getCustomLabelId())
            contentValues.put(ContactsContract.Data.DATA3, entity.getLabelName());
        return contentValues;
    }

    private ContentValues getNameDataCV(Contact contact, int id) {
        NameData cursorrent = contact.getNameData();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, id);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, cursorrent.getFullName().isEmpty()
                ? contact.getCompositeName()
                : cursorrent.getFullName());
        if (!cursorrent.getFirstName().isEmpty())
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, cursorrent.getFirstName());
        if (!cursorrent.getSurname().isEmpty())
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, cursorrent.getSurname());
        if (!cursorrent.getNamePrefix().isEmpty())
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.PREFIX, cursorrent.getNamePrefix());
        if (!cursorrent.getNameSuffix().isEmpty())
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.SUFFIX, cursorrent.getNameSuffix());
        if (!cursorrent.getMiddleName().isEmpty())
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, cursorrent.getMiddleName());
        if (!cursorrent.getPhoneticFirst().isEmpty())
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME, cursorrent.getPhoneticFirst());
        if (!cursorrent.getPhoneticMiddle().isEmpty())
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME, cursorrent.getPhoneticMiddle());
        if (!cursorrent.getPhoneticLast().isEmpty())
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME, cursorrent.getPhoneticLast());
        return contentValues;
    }

    private ContentValues getImAddressCV(IMAddress imAddress, int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, id);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.Data.DATA1, imAddress.getMainData());
        contentValues.put(ContactsContract.Data.DATA2, ContactsContract.CommonDataKinds.Im.TYPE_HOME);
        contentValues.put(ContactsContract.Data.DATA5, imAddress.getLabelId());
        if (imAddress.getLabelId() == imAddress.getCustomLabelId())
            contentValues.put(ContactsContract.Data.DATA6, imAddress.getLabelName());
        return contentValues;
    }

    private ContentValues getStringTypeCV(String contentType, String data, int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, id);
        contentValues.put(ContactsContract.Data.MIMETYPE, contentType);
        contentValues.put(ContactsContract.Data.DATA1, data);
        return contentValues;
    }

    private ContentValues getOrganizationTypeCV(Organization organization, int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, id);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.Data.DATA1, organization.getName());
        contentValues.put(ContactsContract.Data.DATA4, organization.getTitle());
        return contentValues;
    }

    private ContentProviderResult[] createContacts(List<Contact> contacts) {
        ContentProviderResult[] results = null;
        ArrayList<ContentProviderOperation> op_list = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, contact.getAccountType())
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, contact.getAccountName())
                    .build());
        }
        try {
            results = mResolver.applyBatch(ContactsContract.AUTHORITY, op_list);
        } catch (Exception ignored) {
        }
        return results;
    }

}
