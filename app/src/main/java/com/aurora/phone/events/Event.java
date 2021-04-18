package com.aurora.phone.events;

import lombok.Data;


@Data
public class Event {

    private SubType subType;
    private String stringExtra;
    private int intExtra;
    private int status;

    public Event(SubType subType, String stringExtra, int intExtra, int status) {
        this.subType = subType;
        this.stringExtra = stringExtra;
        this.intExtra = intExtra;
        this.status = status;
    }

    public Event(SubType subType, String stringExtra, int intExtra) {
        this.subType = subType;
        this.stringExtra = stringExtra;
        this.intExtra = intExtra;
    }

    public Event(SubType subType, String stringExtra) {
        this.subType = subType;
        this.stringExtra = stringExtra;
    }

    public Event(SubType subType, int status) {
        this.subType = subType;
        this.status = status;
    }

    public Event(SubType subType) {
        this.subType = subType;
    }

    public enum SubType {
        LOG_DELETE
    }
}
