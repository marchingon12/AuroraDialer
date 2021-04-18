package com.aurora.phone.entity;

import com.aurora.phone.ContactType;

import lombok.Data;

public @Data
class ContactWrapper {
    private ContactType type;
    private String data;
}
