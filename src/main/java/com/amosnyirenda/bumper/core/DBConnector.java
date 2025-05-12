package com.amosnyirenda.bumper.core;

import java.sql.Connection;

public interface DBConnector extends AutoCloseable {
    Object connect();
    @Override
    void close();
}
