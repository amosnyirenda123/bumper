package com.amosnyirenda.bumper.core;

import com.amosnyirenda.bumper.events.EventManager;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DBConnectionRequest {
    private final DBConnectionConfig config;
    private final EventManager eventManager;
}
