package com.amosnyirenda.bumper.db.mysql;

import com.amosnyirenda.bumper.core.DBConnectionConfig;
import com.amosnyirenda.bumper.core.DBConnectionManager;
import com.amosnyirenda.bumper.core.DBConnector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;

@RequiredArgsConstructor
public class MySQLConnector implements DBConnector {
    private final DBConnectionConfig config;
    private Connection connection;
    @Override
    public Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
            return this.connection;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }


}
