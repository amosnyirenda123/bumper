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
    boolean insert(Object ...documents);
    boolean insert(Map<String, Object> data);
    boolean insert(List<String> columns, List<Object> values, boolean ifNotExist);
    boolean delete(String id);
    boolean delete(String targetColumn, Object targetValue);
    boolean update(String targetColumn, Object targetValue, Map<String, Object> updates);
    boolean update(String id, Map<String, Object> updates);
    List<String> getFieldNames();
}
