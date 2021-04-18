package com.aurora.phone.manager;

import android.content.Context;

import com.aurora.contact.entity.Contact;
import com.aurora.phone.entity.Favourite;

import io.reactivex.disposables.CompositeDisposable;

public class FavouritesManager {

    public static void addToFavourites(Context context, Contact contact) {
        final CompositeDisposable disposable = new CompositeDisposable();
        final Favourite favourite = getFavouriteFromContact(contact);
        /*favourite.setContactId(contact.getContactId());
        favourite.setGroupId(contact.getGroupId());*/
        favourite.setContact(contact);

    }

    public static void removeFromFavourites(Context context, Contact contact) {
        final CompositeDisposable disposable = new CompositeDisposable();
        final Favourite favourite = getFavouriteFromContact(contact);

    }

    public static void removeFromFavourites(Context context, Favourite favourite) {
        final CompositeDisposable disposable = new CompositeDisposable();

    }

    private static Favourite getFavouriteFromContact(Contact contact) {
        final Favourite favourite = new Favourite();
        /*favourite.setContactId(contact.getContactId());
        favourite.setGroupId(contact.getGroupId());*/
        favourite.setContact(contact);
        return favourite;
    }
}
