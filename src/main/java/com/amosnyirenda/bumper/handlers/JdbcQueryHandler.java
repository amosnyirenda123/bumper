package com.amosnyirenda.bumper.handlers;

import com.amosnyirenda.bumper.core.DBConnector;
import com.amosnyirenda.bumper.core.DBQueryHandler;
import com.amosnyirenda.bumper.events.EventManager;

import java.util.List;
import java.util.Map;

public class JdbcQueryHandler implements DBQueryHandler {
    private DBConnector connector;
    private EventManager eventManager;

    @Override
    public DBQueryHandler withConnector(DBConnector connector) {
        this.connector = connector;
        return this;
    }

    @Override
    public DBQueryHandler withEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
        return this;
    }

    @Override
    public List<String> getColumn(String column) {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> getRows() {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> getRows(int rowLimit) {
        return List.of();
    }

    @Override
    public List<String> getColumnNames() {
        return List.of();
    }
}
