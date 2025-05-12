package com.amosnyirenda.bumper.core;

import com.amosnyirenda.bumper.events.EventManager;

import java.util.List;
import java.util.Map;

public interface DBQueryHandler {
    DBQueryHandler withConnector(DBConnector connector);
    DBQueryHandler withEventManager(EventManager eventManager);
    List<String> getField(String field);
    List<Map<String, Object>> getEntries();
    List<Map<String, Object>> getEntries(int limit);
    List<String> getFieldNames();
}
