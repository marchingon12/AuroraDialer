package com.aurora.phone;

import androidx.annotation.NonNull;

import com.aurora.phone.entity.CallLog;
import com.aurora.phone.util.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoadRecentLogsDate {

    private List<CallLog> callLogList;

    public LoadRecentLogsDate(List<CallLog> callLogList) {
        this.callLogList = callLogList;
    }

    public Map<String, List<CallLog>> execute() {

        final Map<String, List<CallLog>> map = new LinkedHashMap<>();

        for (int index = 0; index <= 2; index++) {
            final List<CallLog> filteredContacts = getLogsWithSameDate(callLogList, index);
            map.put(getDateString(index), filteredContacts);
        }
        return map;
    }

    private String getDateString(int index) {
        switch (index) {
            case 0:
                return "Today";
            case 1:
                return "Yesterday";
            default:
                return "Older";
        }
    }

    private List<CallLog> getLogsWithSameDate(@NonNull final List<CallLog> contactList, final int dateDiff) {
        final List<CallLog> contactsList = new ArrayList<>();

        for (final CallLog callLog : contactList) {
            if (dateDiff < 2) {
                if (Util.getDiffInDays(callLog.getDate()) == dateDiff) {
                    contactsList.add(callLog);
                }
            } else {
                if (Util.getDiffInDays(callLog.getDate()) >= dateDiff) {
                    contactsList.add(callLog);
                }
            }
        }
        return contactsList;
    }
}