package com.amosnyirenda.bumper.core;
import com.amosnyirenda.bumper.drivers.JdbcConnector;
import com.amosnyirenda.bumper.events.EventManager;
import com.amosnyirenda.bumper.events.EventType;
import com.amosnyirenda.bumper.factories.JdbcQueryHandlerFactory;
import com.amosnyirenda.bumper.utils.LoggingListener;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

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

    private final Map<DBType, Function<DBConnectionConfig, DBConnector>> connectorSuppliers = new HashMap<>();
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
                    connectorSuppliers.put(dbType, JdbcConnector::new);
        }
    }

    private void initHandlerFactories(DBType dbType) {
        switch (dbType) {
            case MYSQL, POSTGRESQL, ORACLE, SQLITE, SQLSERVER ->
                    handlerFactories.put(dbType, new JdbcQueryHandlerFactory());
        }
    }

    public void registerConnector(DBType type, Function<DBConnectionConfig, DBConnector> supplier) {
        connectorSuppliers.put(type, supplier);
    }


    public void registerFactory(DBType type, DBQueryHandlerFactory factory) {
        handlerFactories.put(type, factory);
    }

    public DBConnector getConnector() {
        if (connector != null) return connector;
        Function<DBConnectionConfig, DBConnector> supplier = connectorSuppliers.get(dbType);
        if (supplier == null) {
            throw new UnsupportedOperationException("No connector registered for DB type: " + dbType);
        }
        connector = supplier.apply(new DBConnectionConfig(url, username, password, className));
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
