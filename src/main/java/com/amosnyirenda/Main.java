package com.amosnyirenda;

import com.amosnyirenda.bumper.db.mysql.MySQLQueryHandler;

public class Main {
    public static void main(String[] args) {

//        DBConnectionManager connectionManager = new DBConnectionManager.ConnectionBuilder()
//                .withUrl("jdbc:mysql://localhost:3306/db")
//                .withPassword("password")
//                .withUsername("root")
//                .withDb("mysql")
//                .buildConnection();
//
//        try(DBConnector connector = connectionManager.getConnector()){
//            connector.connect();
//        }

        MySQLQueryHandler qb = new MySQLQueryHandler.QueryBuilder()
                .select("id, name, age")
                .from("users")
                .where("id IN ( name = %s", "amos")
                .select("user_id")
                .from("users")
                .having("age > 10")
                .orderByASC("something")
                .where("total > 100 )")
                .build();

        qb.execute();




    }
}