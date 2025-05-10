package com.amosnyirenda.bumper.events;

public interface EventListener {
    void onEvent(EventType event, Object ...payload);
    void onEvent(EventType event);
}
