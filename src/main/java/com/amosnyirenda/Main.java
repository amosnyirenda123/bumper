package com.amosnyirenda;

import com.amosnyirenda.bumper.core.*;
import com.amosnyirenda.bumper.events.EventManager;

public class Main {
    public static void main(String[] args) {

        DBConnectionManager connectionManager = new DBConnectionManager.ConnectionBuilder()
                .withUrl("")
                .withUsername("")
                .withPassword("")
                .withDb(DBType.MONGO_DB)
                .buildConnection();

        DBConnector connector = connectionManager.getConnector();
        EventManager eventManager = connectionManager.getEventManager();

        DBQueryBuilder builder = connectionManager.getQueryBuilder();
        DBQueryHandler handler = builder
                .target("books")
                .use("library")
                .buildHandler()
                .withConnector(connector)
                .withEventManager(eventManager);
    }
}