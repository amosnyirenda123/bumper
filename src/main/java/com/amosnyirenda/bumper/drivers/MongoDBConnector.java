package com.amosnyirenda.bumper.drivers;

import com.amosnyirenda.bumper.core.DBConnectionConfig;
import com.amosnyirenda.bumper.core.DBConnector;
import com.amosnyirenda.bumper.events.EventManager;
import com.amosnyirenda.bumper.events.EventType;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;



/**
 * Provides MongoDB-specific connection management and utility functions.
 * <p>
 * This class implements the {@link DBConnector} interface to establish and close
 * connections to a MongoDB server. It also supports event dispatching for tracking
 * connection-related events or errors.
 * </p>
 *
 * Example usage:
 * <pre>
 *     MongoDBConnector connector = new MongoDBConnector();
 *     MongoClient client = connector.connect();
 * </pre>
 *
 * @author Amos Nyirenda
 */

@RequiredArgsConstructor
public class MongoDBConnector implements DBConnector {
    private final DBConnectionConfig config;
    private final EventManager eventManager;
    private MongoClient mongoClient;


    /**
     * Establishes a connection to the MongoDB server.
     *
     * @return a {@link MongoClient} instance for interacting with MongoDB
     */


    @Override
    public MongoClient connect() {
        try {
            long start = System.nanoTime();
            this.mongoClient = MongoClients.create(config.getUrl());
            long end = System.nanoTime();
            long elapsed = (end - start) / 1_000_000;

            dispatch(EventType.CONNECTION_ESTABLISHED, config.getClassName(), "Took: " + elapsed + "ms");

            return mongoClient;
        } catch (Exception e) {
            dispatch(EventType.CONNECTION_ERROR, "MongoDB connection failed: " + e.getMessage());
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
        if (mongoClient != null) {
            try {
                mongoClient.close();
                dispatch(EventType.CONNECTION_CLOSED, "MongoDB connection closed successfully.");
            } catch (Exception e) {
                dispatch(EventType.CONNECTION_ERROR, "Error while closing MongoDB connection: " + e.getMessage());
            }
        } else {
            dispatch(EventType.CONNECTION_ERROR, "MongoDB connection was never established or already closed.");
        }
    }

}
