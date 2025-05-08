package com.amosnyirenda.bumper.core;

import com.amosnyirenda.bumper.db.mongodb.MongoDBConnector;
import com.amosnyirenda.bumper.db.mongodb.MongoDBQueryHandler;
import com.amosnyirenda.bumper.db.mysql.MySQLConnector;
import com.amosnyirenda.bumper.db.mysql.MySQLQueryHandler;
import com.amosnyirenda.bumper.db.oracle.OracleConnector;
import com.amosnyirenda.bumper.db.oracle.OracleQueryHandler;
import com.amosnyirenda.bumper.db.postgres.PostgresConnector;
import com.amosnyirenda.bumper.db.postgres.PostgresQueryHandler;
import com.amosnyirenda.bumper.db.redis.RedisConnector;
import com.amosnyirenda.bumper.db.redis.RedisQueryHandler;
import com.amosnyirenda.bumper.db.sqlserver.SQLServerConnector;
import com.amosnyirenda.bumper.db.sqlserver.SQLServerQueryHandler;
import lombok.Getter;

import java.sql.Connection;
import java.util.Properties;

@Getter
public class DBConnectionManager {
    private final String url;
    private final String username;
    private final String password;
    private final String dbType;


    public DBConnectionManager(ConnectionBuilder connectionBuilder) {
        this.url = connectionBuilder.url;
        this.username = connectionBuilder.username;
        this.password = connectionBuilder.password;
        this.dbType = connectionBuilder.dbType;

    }

    public DBConnector getConnector() {
        DBConnectionConfig config = new DBConnectionConfig(url, username, password);
        if(dbType == null){
            //Throw exception
            return null;
        }
        switch (dbType.toLowerCase()) {
            case "mysql":
                return new MySQLConnector(config);
            case "postgresql":
                return new PostgresConnector(config);
            case "oracle":
                return new OracleConnector(config);
            case "sqlserver":
                return new SQLServerConnector(config);
            case "redis":
                return new RedisConnector(config);
            case "mongodb":
                return new MongoDBConnector(config);
            default:
                //throw exception here
                System.out.println("Couldn't connect to database: " + dbType);
        }

        return null;
    }

    public DBQueryHandler getQueryHandler() {
        if(dbType == null){
            //Throw exception
            return null;
        }
        switch (dbType.toLowerCase()) {
            case "mysql":
//                return new MySQLQueryHandler();
                return null;
            case "postgresql":
                return new PostgresQueryHandler();
            case "oracle":
                return new OracleQueryHandler();
            case "sqlserver":
                return new SQLServerQueryHandler();
            case "redis":
                return new RedisQueryHandler();
            case "mongodb":
                return new MongoDBQueryHandler();
            default:
                System.out.println("Couldn't connect to database: " + dbType);
        }

        return null;
    }
    public static class ConnectionBuilder {
        private String url;
        private String password;
        private String username;
        private String dbType;

        public ConnectionBuilder withUrl(String url){
            this.url = url;
            return this;
        }
        public ConnectionBuilder withUsername(String username){
            this.username = username;
            return this;
        }
        public ConnectionBuilder withPassword(String password){
            this.password = password;
            return this;
        }
        public ConnectionBuilder withDb(String dbType){
            this.dbType = dbType;
            return this;
        }

        public ConnectionBuilder fromProperties(Properties props){
            this.url = props.getProperty("url");
            this.username = props.getProperty("username");
            this.password = props.getProperty("password");
            this.dbType = props.getProperty("dbType");
            return this;
        }

        public DBConnectionManager buildConnection(){
            return new DBConnectionManager(this);
        }

    }


}
