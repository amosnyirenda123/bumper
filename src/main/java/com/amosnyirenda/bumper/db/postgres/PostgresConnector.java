package com.amosnyirenda.bumper.db.postgres;

import com.amosnyirenda.bumper.core.DBConnectionConfig;
import com.amosnyirenda.bumper.core.DBConnector;
import lombok.AllArgsConstructor;

import java.sql.Connection;

@AllArgsConstructor
public class PostgresConnector implements DBConnector {
    private final DBConnectionConfig config;
    @Override
    public Connection connect() {
        return null;
    }

    @Override
    public void close() {

    }


}
