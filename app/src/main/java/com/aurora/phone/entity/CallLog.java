package com.aurora.phone.entity;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;

import androidx.core.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CallLog {

    private int id;
    private int type;
    private int features;
    private String number;
    private int presentation;
    private String countryISO;
    private long date;
    private long duration;
    private long dataUsage;
    private boolean newLog;
    private String cachedName;
    private int cachedNumberType;
    private String cachedNumberLabel;
    private String voiceMailURI;
    private String geoCodedLocation;
    private long cachedPhotoId;
    private String cachedPhotoURI;
    private String cachedFormattedNumber;
    private String componentName;
    private long lastModified;
    private int blockReason;
    private int count;
    private List<CallLog> relatedLogs;

    public static List<CallLog> getCallLogs(Cursor cursor) {
        if (cursor == null)
            return new ArrayList<>();

        List<CallLog> callLogList = new ArrayList<>();
        while (cursor.moveToNext()) {
            CallLog.CallLogBuilder builder = CallLog.builder();
            builder
                    .id(cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls._ID)))
                    .cachedFormattedNumber(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_FORMATTED_NUMBER)))
                    .cachedName(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME)))
                    .cachedNumberLabel(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NUMBER_LABEL)))
                    .cachedPhotoId(cursor.getLong(cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_PHOTO_ID)))
                    .cachedPhotoURI(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_PHOTO_URI)))
                    .countryISO(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.COUNTRY_ISO)))
                    .dataUsage(cursor.getLong(cursor.getColumnIndex(android.provider.CallLog.Calls.DATA_USAGE)))
                    .date(cursor.getLong(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE)))
                    .duration(cursor.getLong(cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION)))
                    .features(cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls.FEATURES)))
                    .geoCodedLocation(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.GEOCODED_LOCATION)))
                    .newLog(cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW)) == 1)
                    .number(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER)))
                    .presentation(cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER_PRESENTATION)))
                    .type(cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE)))
                    .voiceMailURI(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.VOICEMAIL_URI)))
                    .componentName(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME)))
                    .build();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                builder.lastModified(cursor.getLong(cursor.getColumnIndex(android.provider.CallLog.Calls.LAST_MODIFIED)));
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                builder.blockReason(cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls.BLOCK_REASON)));
            }

            CallLog callLog = builder.build();
            callLogList.add(callLog);
        }
        return callLogList;
    }

    public static boolean deleteCallLog(Context context, CallLog callLog) {
        for (CallLog log : callLog.getRelatedLogs())
            deleteCallLogById(context, log.getId());
        return deleteCallLogById(context, callLog.getId());
    }

    public static boolean deleteCallLogById(Context context, int _ID) {
        if (checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) != PermissionChecker.PERMISSION_GRANTED) {
            return false;
        }
        int nRow = context.getContentResolver().delete(
                android.provider.CallLog.Calls.CONTENT_URI,
                android.provider.CallLog.Calls._ID + " = ? ",
                new String[]{String.valueOf(_ID)});
        return nRow >= 1;
    }
}