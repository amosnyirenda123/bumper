package com.amosnyirenda.bumper.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DBConnectionConfig {
    private final String url;
    private final String username;
    private final String password;
    private final String className;
}
