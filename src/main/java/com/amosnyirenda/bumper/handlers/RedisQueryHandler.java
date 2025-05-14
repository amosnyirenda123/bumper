package com.amosnyirenda.bumper.handlers;

import com.amosnyirenda.bumper.core.DBConnector;
import com.amosnyirenda.bumper.core.DBQueryBuilder;
import com.amosnyirenda.bumper.core.DBQueryHandler;
import com.amosnyirenda.bumper.events.EventManager;
import com.amosnyirenda.bumper.events.EventType;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RedisQueryHandler implements DBQueryHandler {
    private String targetString;
    private String useString;
    private DBConnector connector;
    private EventManager eventManager;

    public RedisQueryHandler(QueryBuilder queryBuilder) {
        this.targetString = queryBuilder.targetString;
        this.useString = queryBuilder.useString;
    }
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
    public List<String> getField(String field) {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> getEntries() {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> getEntries(int limit) {
        return List.of();
    }

    @Override
    public boolean insert(Object... documents) {
        return false;
    }

    @Override
    public boolean insert(Map<String, Object> data) {
        try {
            String id = data.getOrDefault("id", UUID.randomUUID().toString()).toString();
            Map<String, String> hash = new HashMap<>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                hash.put(entry.getKey(), entry.getValue().toString());
            }
            ((Jedis)this.connector).hmset(id, hash); // Store as Redis hash
            return true;
        } catch (Exception e) {
            dispatch(EventType.INSERT, "Error inserting data: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean insert(List<String> columns, List<Object> values, boolean ifNotExist) {
        return false;
    }

    @Override
    public boolean delete(String id) {
        try {
            return ((Jedis)this.connector).del(id) > 0;
        } catch (Exception e) {
            dispatch(EventType.DELETE, "ID: " + id);
            return false;
        }
    }

    private void dispatch(EventType type, Object ...payload) {
        if(eventManager != null) {
            eventManager.notify(type, payload);
        }
    }

    @Override
    public boolean delete(String targetColumn, Object targetValue) {
        return false;
    }

    @Override
    public boolean update(String targetColumn, Object targetValue, Map<String, Object> updates) {
        return false;
    }

    @Override
    public boolean update(String id, Map<String, Object> updates) {
        try {
            Map<String, String> updateMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                updateMap.put(entry.getKey(), entry.getValue().toString());
            }
            ((Jedis)this.connector).hmset(id, updateMap); // Replaces or adds fields
            return true;
        } catch (Exception e) {
            dispatch(EventType.QUERY_ERROR, "Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> getFieldNames() {
        return List.of();
    }

    public static class QueryBuilder implements DBQueryBuilder {
        private String targetString;
        private String useString;
        @Override
        public DBQueryBuilder target(String source) {
            return null;
        }

        @Override
        public DBQueryBuilder target(String source, Object... params) {
            return null;
        }

        @Override
        public DBQueryBuilder use(String param) {
            return null;
        }

        @Override
        public DBQueryBuilder query(String query) {
            return this;
        }

        @Override
        public DBQueryHandler buildHandler() {
            return new RedisQueryHandler(this);
        }
    }
}
