package com.aurora.phone.events;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

public class RxBus {

    private final Relay<Event> bus = PublishRelay.create();

    public Relay<Event> getBus() {
        return bus;
    }
}