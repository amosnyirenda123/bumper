package com.amosnyirenda.bumper.drivers;

import com.amosnyirenda.bumper.core.DBConnectionConfig;
import com.amosnyirenda.bumper.core.DBConnector;
import com.amosnyirenda.bumper.events.EventManager;
import com.amosnyirenda.bumper.events.EventType;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;

@RequiredArgsConstructor
public class JdbcConnector implements DBConnector {
    private final DBConnectionConfig config;
    private final EventManager eventManager;
    private Connection connection;
    @Override
    public Connection connect() {
        try {
            Class.forName(config.getClassName());
            long start = System.nanoTime();
            this.connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
            long end = System.nanoTime();
            long elapsed = (end - start) / 1_000_000;
            if (this.connection != null) {
                dispatch(EventType.CONNECTION_ESTABLISHED, connection.getMetaData().getURL(), "Took: " + elapsed + "ms");
            }
            return this.connection;
        } catch (Exception e) {
            dispatch(EventType.CONNECTION_ERROR, "Error establishing connection: " + e.getMessage());
            return null;
        }
    }

    private void dispatch(EventType event, Object... payload) {
        if(eventManager != null){
            eventManager.notify(event, payload);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                dispatch(EventType.CONNECTION_CLOSED, "Connection Closed");
            }
        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
            dispatch(EventType.CONNECTION_ERROR, "Error closing connection: " + e.getMessage());
        }
    }
}
