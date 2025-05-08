package com.amosnyirenda.bumper.db.mongodb;

import com.amosnyirenda.bumper.core.DBConnectionConfig;
import com.amosnyirenda.bumper.core.DBConnector;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;

@AllArgsConstructor
public class MongoDBConnector implements DBConnector {
    private final DBConnectionConfig config;
    @Override
    public Connection connect() {
        try{
            try(Connection conn = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword())) {
                return conn;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {

    }


}
