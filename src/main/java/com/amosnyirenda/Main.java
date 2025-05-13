package com.amosnyirenda;

import com.amosnyirenda.bumper.core.*;
import com.amosnyirenda.bumper.events.EventManager;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        DBConnectionManager connectionManager = new DBConnectionManager.ConnectionBuilder()
                .withUrl("")
                .withDb(DBType.MONGO_DB)
                .buildConnection();

        DBConnector connector = connectionManager.getConnector();
        EventManager eventManager = connectionManager.getEventManager();

        DBQueryBuilder builder = connectionManager.getQueryBuilder();
        DBQueryHandler handler = builder
                .target("books")
                .use("library")
                .build();

        List<Map<String, Object>> rows1 = handler.withConnector(connector).withEventManager(eventManager).getEntries();
        for (Map<String, Object> row : rows1) {
            System.out.println(row);
        }

//        List<String> rows2 = handler.withConnector(connector).withEventManager(eventManager).getField("year");
//        for (String row : rows2) {
//            System.out.println(row);
//        }
    }
}