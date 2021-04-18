package com.aurora.phone.entity;

import androidx.room.Entity;

import com.aurora.contact.entity.Contact;

import lombok.Data;

@Data
@Entity(tableName = "Favourite", primaryKeys = {"groupId", "contactId"})
public class Favourite {
    private long contactId;
    private long groupId;
    private Contact contact;
}
