package com.amosnyirenda.bumper.core;
import com.amosnyirenda.bumper.drivers.JdbcConnector;
import com.amosnyirenda.bumper.drivers.MongoDBConnector;
import com.amosnyirenda.bumper.drivers.RedisConnector;
import com.amosnyirenda.bumper.events.EventManager;
import com.amosnyirenda.bumper.events.EventType;
import com.amosnyirenda.bumper.factories.JdbcQueryHandlerFactory;
import com.amosnyirenda.bumper.factories.MongoDBQueryHandlerFactory;
import com.amosnyirenda.bumper.factories.RedisQueryHandlerFactory;
import com.amosnyirenda.bumper.utils.LoggingListener;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * This class handles database connections using JDBC.
 * It supports MySQL, PostgreSQL, SQLite, and other relational databases.
 * It selects the correct connector based on the DBType argument
 * @author Amos Nyirenda
 * @version 1.0
 */

@Getter
public class DBConnectionManager {
    private final String url;
    private final String username;
    private final String password;
    private final DBType dbType;
    private final String className;

    private  DBConnector connector;
    @Getter
    private final EventManager eventManager;

    private final Map<DBType, Function<DBConnectionRequest, DBConnector>> connectorSuppliers = new HashMap<>();
    private final Map<DBType, DBQueryHandlerFactory> handlerFactories = new HashMap<>();



    public DBConnectionManager(ConnectionBuilder connectionBuilder) {
        this.url = connectionBuilder.url;
        this.username = connectionBuilder.username;
        this.password = connectionBuilder.password;
        this.dbType = connectionBuilder.dbType;
        this.className = connectionBuilder.className;


        initConnector(connectionBuilder.dbType);
        initHandlerFactories(connectionBuilder.dbType);
        eventManager = new EventManager(EventType.values());

        LoggingListener logger = new LoggingListener();
        for (EventType type : EventType.values()) {
            eventManager.subscribe(type, logger);
        }
    }

    private void initConnector(DBType dbType) {
        switch (dbType) {
            case MYSQL, POSTGRESQL, ORACLE, SQLITE, SQLSERVER ->
                    connectorSuppliers.put(dbType,request -> new JdbcConnector( request.getConfig(), request.getEventManager()));
            case MONGO_DB ->
                    connectorSuppliers.put(dbType,request -> new MongoDBConnector( request.getConfig(), request.getEventManager()));
            case REDIS -> connectorSuppliers.put(dbType,request -> new RedisConnector(request.getConfig(), request.getEventManager()));
            default -> throw new UnsupportedOperationException("No Database connector registered for: " + dbType);
        }
    }

    private void initHandlerFactories(DBType dbType) {
        switch (dbType) {
            case MYSQL, POSTGRESQL, ORACLE, SQLITE, SQLSERVER ->
                    handlerFactories.put(dbType, new JdbcQueryHandlerFactory());
            case MONGO_DB ->
                    handlerFactories.put(dbType, new MongoDBQueryHandlerFactory());
            case REDIS -> handlerFactories.put(dbType, new RedisQueryHandlerFactory());
            default -> throw new UnsupportedOperationException("No Database factory registered for: " + dbType);
        }
    }


    public DBConnector getConnector() {
        if (connector != null) return connector;
        Function<DBConnectionRequest, DBConnector> supplier = connectorSuppliers.get(dbType);
        if (supplier == null) {
            throw new UnsupportedOperationException("No connector registered for DB type: " + dbType);
        }
        connector = supplier.apply(new DBConnectionRequest(
                new DBConnectionConfig(url, username, password, className),
                this.getEventManager()
        ));
        return connector;
    }


    public DBQueryBuilder getQueryBuilder() {
        DBQueryHandlerFactory factory = handlerFactories.get(dbType);
        if (factory == null) {
            throw new UnsupportedOperationException("No query builder registered for DB type: " + dbType);
        }
        return factory.createBuilder();
    }
    public static class ConnectionBuilder {
        private String url;
        private String password;
        private String username;
        private  DBType dbType;
        private String className;


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
        public ConnectionBuilder withDb(DBType dbType){
            this.dbType = dbType;
            return this;
        }


        public ConnectionBuilder fromProperties(Properties props) {
            this.url = props.getProperty("url");
            this.username = props.getProperty("username");
            this.password = props.getProperty("password");
            this.dbType = DBType.valueOf(props.getProperty("dbType").toUpperCase());
            return this;
        }

        private void setClassName(DBType dbType) {
            switch(dbType){
                case MYSQL:
                    this.className = "com.mysql.cj.jdbc.Driver";
                    break;
                case ORACLE:
                    this.className = "oracle.jdbc.driver.OracleDriver";
                    break;
                case POSTGRESQL:
                    this.className = "org.postgresql.Driver";
                    break;
                case SQLSERVER:
                    this.className = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                    break;
                case SQLITE:
                    this.className = "org.sqlite.JDBC";
                    break;
                case MONGO_DB:
                    this.className = "org.mongodb.morph.MorphDriver";
                    break;
                case REDIS:
                    this.className = "redis.clients.jedis.Jedis";
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported DB type: " + dbType);
            }
        }

        public DBConnectionManager buildConnection(){
            setClassName(this.dbType);
            return new DBConnectionManager(this);
        }

    }


}
