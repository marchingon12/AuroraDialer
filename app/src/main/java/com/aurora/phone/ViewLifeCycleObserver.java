package com.aurora.phone;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.aurora.phone.util.Log;

public class ViewLifeCycleObserver implements LifecycleObserver {

    private ViewActionHandler viewActionHandler;

    public ViewLifeCycleObserver registerHandler(ViewActionHandler handler) {
        this.viewActionHandler = handler;
        return this;
    }

    public ViewLifeCycleObserver registerLifecycle(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
        return this;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void start() {
        Log.e("View Started");
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        Log.e("View Started");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stop() {
        Log.e("View Stopped");
    }
}
