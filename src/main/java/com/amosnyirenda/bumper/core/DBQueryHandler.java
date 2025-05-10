package com.amosnyirenda.bumper.core;

import com.amosnyirenda.bumper.events.EventManager;

import java.beans.EventHandler;
import java.util.List;
import java.util.Map;

public interface DBQueryHandler {
    DBQueryHandler withConnector(DBConnector connector);
    DBQueryHandler withEventManager(EventManager eventManager);
    List<String> getColumn(String column);
    List<Map<String, Object>> getRows();
    List<Map<String, Object>> getRows(int rowLimit);
    List<String> getColumnNames();
}
