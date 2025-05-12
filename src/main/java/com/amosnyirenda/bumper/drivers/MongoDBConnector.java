package com.amosnyirenda.bumper.drivers;

import com.amosnyirenda.bumper.core.DBConnectionConfig;
import com.amosnyirenda.bumper.core.DBConnector;
import com.amosnyirenda.bumper.events.EventManager;
import com.amosnyirenda.bumper.events.EventType;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;


@RequiredArgsConstructor
public class MongoDBConnector implements DBConnector {
    private final DBConnectionConfig config;
    private final EventManager eventManager;
    private MongoClient mongoClient;

    @Override
    public MongoClient connect() {
        try {
            long start = System.nanoTime();
            this.mongoClient = MongoClients.create(config.getUrl());
            long end = System.nanoTime();
            long elapsed = (end - start) / 1_000_000;

            dispatch(EventType.CONNECTION_ESTABLISHED, config.getUrl(), "Took: " + elapsed + "ms");

            return mongoClient;
        } catch (Exception e) {
            dispatch(EventType.CONNECTION_ERROR, "MongoDB connection failed: " + e.getMessage());
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
