package com.amosnyirenda;

import com.amosnyirenda.bumper.core.*;
import com.amosnyirenda.bumper.db.mysql.MySQLConnector;
import com.amosnyirenda.bumper.db.mysql.MySQLQueryHandler;
import com.amosnyirenda.bumper.db.mysql.MySQLQueryHandlerFactory;

public class Main {
    public static void main(String[] args) {

        DBConnectionManager connectionManager = new DBConnectionManager.ConnectionBuilder()
                .withUrl("jdbc:mysql://localhost:3306/db")
                .withPassword("password")
                .withUsername("root")
                .withDb(DBType.MYSQL)
                .buildConnection();

//        DBConnector connector = connectionManager.getConnector();

        DBQueryBuilder builder = connectionManager.getQueryBuilder();
        DBQueryHandler handler = builder
                .select("*")
                .from("users")
                .where("name = %s", "'amos'")
                .build();
        handler.execute();
    }
}