package com.amosnyirenda.bumper.drivers;

import com.amosnyirenda.bumper.core.DBConnectionConfig;
import com.amosnyirenda.bumper.core.DBConnector;
import com.amosnyirenda.bumper.events.EventManager;
import com.amosnyirenda.bumper.events.EventType;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;


/**
 * Provides JDBC-based database connectivity for relational databases.
 * <p>
 * This class implements the {@link DBConnector} interface and allows
 * establishing and closing connections using JDBC for supported databases
 * such as MySQL, PostgreSQL, SQLite, Oracle, and SQL Server.
 * </p>
 *
 * Example usage:
 * <pre>
 *     JdbcConnector connector = new JdbcConnector("jdbc:mysql://localhost:3306/db", "user", "pass");
 *     Connection conn = connector.connect();
 * </pre>
 *
 * Supported database URLs vary by vendor.
 *
 * @author Amos Nyirenda
 */

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

    /**
     * Dispatches an event with optional payload to registered listeners.
     * <p>
     * This method is typically used internally to signal connection errors
     * or other notable occurrences during database operations.
     * </p>
     *
     * @param event the type of event being dispatched
     * @param payload optional data associated with the event
     */

    private void dispatch(EventType event, Object... payload) {
        if(eventManager != null){
            eventManager.notify(event, payload);
        }
    }


    /**
     * Closes the connection to the MongoDB server if it is open.
     * <p>
     * This method should be called to release MongoDB resources when they are
     * no longer needed.
     * </p>
     */
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
