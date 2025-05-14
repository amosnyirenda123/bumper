package com.amosnyirenda;

import com.amosnyirenda.bumper.core.*;
import com.amosnyirenda.bumper.events.EventManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        DBConnectionManager connectionManager = new DBConnectionManager.ConnectionBuilder()
                .withUrl("jdbc:postgresql://localhost:5432/postgres")
                .withUsername("postgres")
                .withPassword("cmdvscod511824")
                .withDb(DBType.POSTGRESQL)
                .buildConnection();

        DBConnector connector = connectionManager.getConnector();
        EventManager eventManager = connectionManager.getEventManager();

        DBQueryBuilder builder = connectionManager.getQueryBuilder();
        DBQueryHandler handler = builder
                .target("books")
                .buildHandler()
                .withConnector(connector)
                .withEventManager(eventManager);

        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "JavaScript and PHP");
        updates.put("author", "John Smith");

        boolean updated = handler.update("id", 3 , updates);

        System.out.println(updated ? "Updated" : "Not Updated");

    }
}