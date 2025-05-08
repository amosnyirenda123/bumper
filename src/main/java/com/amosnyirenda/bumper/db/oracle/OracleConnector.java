package com.amosnyirenda.bumper.db.oracle;

import com.amosnyirenda.bumper.core.DBConnectionConfig;
import com.amosnyirenda.bumper.core.DBConnector;
import lombok.AllArgsConstructor;

import java.sql.Connection;

@AllArgsConstructor
public class OracleConnector implements DBConnector {
    private final DBConnectionConfig config;
    @Override
    public Connection connect() {
        return null;
    }

    @Override
    public void close() {

    }

}
