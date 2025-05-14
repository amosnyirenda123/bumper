package com.amosnyirenda.bumper.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectionManagerTest {

    @ParameterizedTest
    @MethodSource("dbProviders")
    void testConnectorNotNull(DBConnectionManager dbManager) {
        assertNotNull(dbManager.getConnector());
    }

    @ParameterizedTest
    @MethodSource("dbProviders")
    void testEventManagerNotNull(DBConnectionManager dbManager) {
        assertNotNull(dbManager.getEventManager());
    }

    @ParameterizedTest
    @MethodSource("dbProviders")
    void testQueryBuilder(DBConnectionManager dbManager) {
        assertNotNull(dbManager.getQueryBuilder());
    }
    @ParameterizedTest
    @MethodSource("dbProviders")
    void testHandlerFactory(DBConnectionManager dbManager) {
        assertFalse(dbManager.getHandlerFactories().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("dbProviders")
    void testConnectorSuppliers(DBConnectionManager dbManager) {
        assertFalse(dbManager.getConnectorSuppliers().isEmpty());
    }

    static Stream<DBConnectionManager> dbProviders() {
        return Stream.of(
                new DBConnectionManager.ConnectionBuilder().withDb(DBType.POSTGRESQL).buildConnection(),
                new DBConnectionManager.ConnectionBuilder().withDb(DBType.SQLITE).buildConnection(),
                new DBConnectionManager.ConnectionBuilder().withDb(DBType.MYSQL).buildConnection(),
                new DBConnectionManager.ConnectionBuilder().withDb(DBType.MONGO_DB).buildConnection(),
                new DBConnectionManager.ConnectionBuilder().withDb(DBType.ORACLE).buildConnection(),
                new DBConnectionManager.ConnectionBuilder().withDb(DBType.SQLSERVER).buildConnection()
        );
    }

}