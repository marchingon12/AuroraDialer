package com.aurora.contact.interfaces;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContentResolverCompat;
import androidx.lifecycle.LiveData;

import com.aurora.phone.util.Log;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public abstract class CursorLiveData extends LiveData<Cursor> {

    @NonNull
    private final Context context;

    @NonNull
    private final LiveContentObserver observer;

    public CursorLiveData(@NonNull Context context) {
        this.context = context;
        this.observer = new LiveContentObserver(new Handler());
    }

    @Nullable
    public abstract String[] getCursorProjection();

    @Nullable
    public abstract String getCursorSelection();

    @Nullable
    public abstract String[] getCursorSelectionArgs();

    @Nullable
    public abstract String getCursorSortOrder();

    @NonNull
    public abstract Uri getCursorUri();

    private void loadData() {
        loadData(false);
    }

    private void loadData(boolean reQuery) {
        if (!reQuery) {
            Cursor cursor = getValue();
            if (cursor != null && !cursor.isClosed()) {
                return;
            }
        }

        Observable.just(getCallLogCursor())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(cursor -> setValue(cursor))
                .doOnError(throwable -> {
                    Log.e(throwable.getMessage());
                })
                .subscribe();
    }

    private Cursor getCallLogCursor() {
        Cursor cursor = ContentResolverCompat.query(
                context.getContentResolver(),
                getCursorUri(),
                getCursorProjection(),
                getCursorSelection(),
                getCursorSelectionArgs(),
                getCursorSortOrder(),
                null
        );

        if (cursor != null) {
            context.getContentResolver().registerContentObserver(getCursorUri(), true, observer);
        }
        return cursor;
    }

    @Override
    protected void onActive() {
        loadData();
        Log.e("Cursor Live Data Created");
    }

    @Override
    protected void onInactive() {
        Log.e("Cursor Live Data Destroyed");
        super.onInactive();
    }

    @Override
    protected void setValue(Cursor newCursor) {
        Cursor oldCursor = getValue();
        if (oldCursor != null) {
            oldCursor.close();
        }
        super.setValue(newCursor);
    }

    public final class LiveContentObserver extends ContentObserver {

        public LiveContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            loadData(true);
        }
    }
}