package com.aurora.phone.operations;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.aurora.contact.entity.Contact;
import com.aurora.phone.ContactType;

import java.util.ArrayList;
import java.util.List;

public class LocalContactsTask extends ContextWrapper {
    private Context context;

    public LocalContactsTask(Context context) {
        super(context);
        this.context = context;
    }

    private static ContactType getEmailType(int dataType) {
        if (ContactsContract.CommonDataKinds.Email.TYPE_HOME == dataType) {
            return ContactType.HOME;
        } else if (ContactsContract.CommonDataKinds.Email.TYPE_WORK == dataType) {
            return ContactType.WORK;
        }
        return ContactType.OTHER;
    }

    private static ContactType getAddressType(int dataType) {
        if (ContactsContract.CommonDataKinds.SipAddress.TYPE_HOME == dataType) {
            return ContactType.HOME;
        } else if (ContactsContract.CommonDataKinds.SipAddress.TYPE_WORK == dataType) {
            return ContactType.WORK;
        }
        return ContactType.OTHER;
    }

    private static ContactType getWebsiteType(int dataType) {
        if (ContactsContract.CommonDataKinds.Website.TYPE_HOME == dataType) {
            return ContactType.HOME;
        } else if (ContactsContract.CommonDataKinds.Website.TYPE_WORK == dataType) {
            return ContactType.WORK;
        }
        return ContactType.OTHER;
    }

    private static ContactType getPhoneType(int dataType) {
        switch (dataType) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return ContactType.HOME;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return ContactType.WORK;
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return ContactType.MOBILE;
            default:
                return ContactType.OTHER;
        }
    }

    public List<Contact> fetchAllSystemContacts() {
        final ContentResolver contentResolver = context.getContentResolver();
        final List<Contact> contactList = new ArrayList<>();
        final List<Integer> rawContactsIdList = getRawContactsIdList();

       /* for (int i = 0; i < rawContactsIdList.size(); i++) {

            final Contact contact = new Contact();
            final Integer rawContactId = rawContactsIdList.get(i);

            //contact.setRawContactId(rawContactId);

            final Uri dataContentUri = ContactsContract.Data.CONTENT_URI;

            Cursor cursorsor = contentResolver.query(
                    dataContentUri,
                    null,
                    ContactsContract.Data.RAW_CONTACT_ID + "=" + rawContactId,
                    null,
                    null
            );

            if (cursorsor != null && cursorsor.getCount() > 0) {
                cursorsor.moveToFirst();
                contact.setContactId(cursorsor.getLong(cursorsor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                do {
                    String mimeType = cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                    getColumnValueByMimeType(cursorsor, mimeType, contact);
                } while (cursorsor.moveToNext());
                contactList.add(contact);
            }
        }*/
        return contactList;
    }

    private void getColumnValueByMimeType(Cursor cursorsor, String mimeType, Contact contact) {

        /*switch (mimeType) {
            case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                String emailAddress = cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                int emailTypeInt = cursorsor.getInt(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                ContactWrapper emailData = new ContactWrapper();
                emailData.setType(getEmailType(emailTypeInt));
                emailData.setData(emailAddress);

                List<ContactWrapper> wrapperList = contact.getEmailList();
                wrapperList.add(emailData);

                contact.setEmailList(wrapperList);
                break;

            case ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE:
                String imProtocol = cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
                String imId = cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                break;

            case ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE:
                String nickName = cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                contact.setNickName(nickName);
                break;

            case ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE:
                contact.setCompany(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY)));
                contact.setDepartment(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DEPARTMENT)));
                contact.setTitle(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE)));
                contact.setJobDescription(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION)));
                contact.setOfficeLocation(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.OFFICE_LOCATION)));
                break;

            case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                String phoneNumber = cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumber = PhoneNumberUtil.normalizeDiallableCharsOnly(phoneNumber);
                int phoneTypeInt = cursorsor.getInt(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                ContactWrapper phoneData = new ContactWrapper();
                phoneData.setType(getPhoneType(phoneTypeInt));
                phoneData.setData(phoneNumber);

                List<ContactWrapper> phoneList = contact.getPhoneList();
                phoneList.add(phoneData);

                contact.setPhoneList(phoneList);
                break;

            case ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE:
                String address = cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS));
                int addressTypeInt = cursorsor.getInt(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.SipAddress.TYPE));

                ContactWrapper addressData = new ContactWrapper();
                addressData.setType(getAddressType(addressTypeInt));
                addressData.setData(address);

                List<ContactWrapper> addressList = contact.getAddressList();
                addressList.add(addressData);

                contact.setAddressList(addressList);
                break;

            case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                contact.setDisplayName(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME)));
                contact.setGivenName(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
                contact.setFamilyName(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));
                break;

            case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                contact.setCountry(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)));
                contact.setCity(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)));
                contact.setRegion(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION)));
                contact.setStreet(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)));
                contact.setPostCode(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)));
                contact.setPostType(cursorsor.getInt(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE)));
                break;

            case ContactsContract.CommonDataKinds.Identity.CONTENT_ITEM_TYPE:
                contact.setIdentity(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.IDENTITY)));
                contact.setNamespace(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.NAMESPACE)));
                break;

            case ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE:
                contact.setPhoto(cursorsor.getBlob(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO)));
                contact.setPhotoFieldId(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_FILE_ID)));
                break;

            case ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE:
                contact.setGroupId(cursorsor.getInt(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID)));
                break;

            case ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE:
                String websiteUrl = cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                int webTypeInt = cursorsor.getInt(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Website.TYPE));

                ContactWrapper websiteData = new ContactWrapper();
                websiteData.setType(getWebsiteType(webTypeInt));
                websiteData.setData(websiteUrl);

                List<ContactWrapper> websiteList = contact.getWebsiteList();
                websiteList.add(websiteData);
                contact.setWebsiteList(websiteList);
                break;

            case ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE:
                contact.setNote(cursorsor.getString(cursorsor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE)));
                break;
        }*/
    }

    private List<Integer> getRawContactsIdList() {
        List<Integer> ret = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        String[] queryColumnArr = {ContactsContract.RawContacts._ID};
        Cursor cursorsor = contentResolver.query(rawContactUri, queryColumnArr, null, null, null);
        if (cursorsor != null && cursorsor.getCount() > 0) {
            cursorsor.moveToFirst();
            do {
                int idColumnIndex = cursorsor.getColumnIndex(ContactsContract.RawContacts._ID);
                int rawContactsId = cursorsor.getInt(idColumnIndex);
                ret.add(rawContactsId);
            } while (cursorsor.moveToNext());
            cursorsor.close();
        }
        return ret;
    }
}
