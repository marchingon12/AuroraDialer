package com.aurora.phone.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telecom.Call;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aurora.contact.entity.Contact;
import com.aurora.phone.AuroraApplication;
import com.aurora.phone.util.ContactUtils;
import com.aurora.phone.util.Log;

public class CallManager {


    public static Contact getDisplayContact(Context context,Call call) {

        String uri = null;

        if (call.getState() == Call.STATE_DIALING) {
            Toast.makeText(context, "Dialing", Toast.LENGTH_LONG).show();
        }

        if (call.getDetails().getHandle() != null) {
            uri = Uri.decode(call.getDetails().getHandle().toString());
        }

        //if (uri != null && uri.isEmpty()) return ContactUtils.UNKNOWN;

        // If uri contains 'voicemail' this is a... voicemail dah
        // if (uri.contains("voicemail")) return ContactUtils.VOICEMAIL;

        String telephoneNumber = null;

        // If uri contains 'tel' this is a normal phoneNumber
        if (uri.contains("tel:")) telephoneNumber = uri.replace("tel:", "");

        //if (telephoneNumber == null || telephoneNumber.isEmpty())
        //    return ContactUtils.UNKNOWN; // Unknown phoneNumber

        if (telephoneNumber.contains(" ")) telephoneNumber = telephoneNumber.replace(" ", "");

        Contact contact = ContactUtils.getContactByPhoneNumber(context, telephoneNumber); // Get the contacts with the phoneNumber

        if (contact == null)
            return new Contact(telephoneNumber, telephoneNumber, null); // No known contacts for the phoneNumber, return the phoneNumber
        else if (contact.getCompositeName() == null)
            return new Contact(telephoneNumber, telephoneNumber, null); // No known contacts for the phoneNumber, return the phoneNumber

        return contact;
    }

    public static void call(@NonNull Context context, @NonNull Contact contact, int simNumber) {
        Log.i("Trying to call: %s", contact.getCompositeName());
        String uri;
        String number = contact.getPhoneList().get(0).getMainData();
        try {
            if (number.contains("#"))
                uri = "tel: " + Uri.encode(number);
            else
                uri = "tel: " + number;
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
            callIntent.putExtra("simSlot", simNumber);
            context.startActivity(callIntent);
        } catch (SecurityException e) {
            Log.e("Couldn't call %s", number);
        }
    }

    public static void call(@NonNull Context context, @NonNull String number, int simNumber) {
        Log.i("Trying to call: %s", number);
        String uri;
        try {
            if (number.contains("#"))
                uri = "tel: " + Uri.encode(number);
            else
                uri = "tel: " + number;
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
            callIntent.putExtra("simSlot", simNumber);
            context.startActivity(callIntent);
        } catch (SecurityException e) {
            Log.e("Couldn't call %s", number);
        }
    }

}
