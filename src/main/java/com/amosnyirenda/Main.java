package com.amosnyirenda;

import com.amosnyirenda.bumper.core.*;
import com.amosnyirenda.bumper.db.mysql.MySQLQueryHandler;
import com.amosnyirenda.bumper.db.mysql.MySQLQueryHandlerFactory;
import com.amosnyirenda.bumper.events.EventManager;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        DBConnectionManager connectionManager = new DBConnectionManager.ConnectionBuilder()
                .withUrl("jdbc:mysql://localhost:3306/laravel")
                .withPassword("")
                .withUsername("root")
                .withDb(DBType.MYSQL)
                .buildConnection();

        DBConnector connector = connectionManager.getConnector();
        EventManager eventManager = connectionManager.getEventManager();

        DBQueryBuilder builder = connectionManager.getQueryBuilder();
        DBQueryHandler handler = builder
                .select("*")
                .from("users")
                .build();

//        List<Map<String, Object>> rows = handler
//                .withConnector(connector)
//                .withEventManager(eventManager)
//                .getRows();
//
//        for (Map<String, Object> row : rows) {
//            System.out.println(row);
//        }
//        List<String> columnNames = handler.withConnector(connector).getColumnNames();
//        for (String columnName : columnNames) {
//            System.out.println(columnName);
//        }

        List<Map<String, Object>> rows1 = handler.withConnector(connector).withEventManager(eventManager).getRows();
        for (Map<String, Object> row : rows1) {
            System.out.println(row);
        }
    }
}