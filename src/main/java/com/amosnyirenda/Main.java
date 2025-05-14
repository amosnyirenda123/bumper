package com.amosnyirenda;

import com.amosnyirenda.bumper.core.*;
import com.amosnyirenda.bumper.events.EventManager;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        DBConnectionManager connectionManager = new DBConnectionManager.ConnectionBuilder()
                .withUrl("jdbc:mysql://localhost:3306/laravel")
                .withUsername("root")
                .withDb(DBType.MYSQL)
                .buildConnection();

        DBConnector connector = connectionManager.getConnector();
        EventManager eventManager = connectionManager.getEventManager();

        DBQueryBuilder builder = connectionManager.getQueryBuilder();
        DBQueryHandler handler = builder
                .target("books")
                .buildHandler()
                .withConnector(connector)
                .withEventManager(eventManager);

        Map<String, Object> book = new HashMap<>();
        book.put("title", "Effective Java");
        book.put("author", "John Smith");
        book.put("year", "2015");


        boolean inserted = handler.insert(book);
        System.out.println(inserted ? "Inserted" : "Not inserted");
    }
}