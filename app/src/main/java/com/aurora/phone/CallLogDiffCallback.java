package com.aurora.phone;

import androidx.recyclerview.widget.DiffUtil;

import com.aurora.phone.entity.CallLog;

import java.util.List;

public class CallLogDiffCallback extends DiffUtil.Callback {

    private List<CallLog> newList;
    private List<CallLog> oldList;

    public CallLogDiffCallback(List<CallLog> newList, List<CallLog> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldIndex, int newIndex) {
        return newList.get(newIndex).equals(oldList.get(oldIndex));
    }

    @Override
    public boolean areContentsTheSame(int oldIndex, int newIndex) {
        return true;
    }
}