package com.aurora.phone.entity;

import androidx.room.Entity;

import com.aurora.contact.entity.Contact;
import com.aurora.phone.CallType;

import lombok.Data;

@Data
@Entity(tableName = "History", primaryKeys = {"groupId", "contactId", "time"})
public class History {
    private long groupId;
    private long contactId;
    private long duration;
    private long time;
    private CallType type = CallType.INCOMING;
    private Contact contact;
}
