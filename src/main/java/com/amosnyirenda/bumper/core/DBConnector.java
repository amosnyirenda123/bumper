package com.amosnyirenda.bumper.core;

import java.sql.Connection;

public interface DBConnector extends AutoCloseable {
    Connection connect();
    @Override
    void close();
}
