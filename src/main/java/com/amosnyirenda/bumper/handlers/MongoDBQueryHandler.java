package com.amosnyirenda.bumper.handlers;

import com.amosnyirenda.bumper.core.DBConnector;
import com.amosnyirenda.bumper.core.DBQueryBuilder;
import com.amosnyirenda.bumper.core.DBQueryHandler;
import com.amosnyirenda.bumper.drivers.MongoDBConnector;
import com.amosnyirenda.bumper.events.EventManager;
import com.amosnyirenda.bumper.events.EventType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.*;
import com.mongodb.ConnectionString;

import java.util.*;

public class MongoDBQueryHandler implements DBQueryHandler {
    private final String query;
    private DBConnector connector;
    private EventManager eventManager;

    MongoDBQueryHandler(QueryBuilder queryBuilder){
        this.query = queryBuilder.query.toString();
    }

    @Override
    public DBQueryHandler withConnector(DBConnector connector) {
        this.connector =  connector;
        return this;
    }

    private MongoCollection<Document> getCollection() {
        if (connector == null) {
            throw new IllegalStateException("MongoClient is not initialized. Call connect() first.");
        }

        String dbName = "library";
        return ((MongoClient)connector.connect()).getDatabase(dbName).getCollection("books");

    }

    @Override
    public DBQueryHandler withEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
        return this;
    }

    @Override
    public List<String> getField(String field) {
        List<String> values = new ArrayList<>();
        try {
            long start = System.nanoTime();
            MongoCollection<Document> collection = getCollection();
            long end = System.nanoTime();
            long elapsed = (end - start) / 1000000L;
            FindIterable<Document> documents = collection.find();

            for (Document doc : documents) {
                if (doc.containsKey(field)) {
                    values.add(String.valueOf(doc.get(field)));
                }
            }
            dispatch(EventType.COLUMN_VALUE_RETRIEVED, "Took: " + elapsed + " ms");

        } catch (Exception e) {
            dispatch(EventType.QUERY_ERROR, "Error retrieving field: " + field + " - " + e.getMessage());
        }
        return values;
    }

    @Override
    public List<Map<String, Object>> getEntries() {
        List<Map<String, Object>> entries = new ArrayList<>();
        try {
            long start = System.nanoTime();
            MongoCollection<Document> collection = getCollection();
            long end = System.nanoTime();
            long elapsed = (end - start) / 1000000;
            for (Document doc : collection.find()) {
                entries.add(new HashMap<>(doc));
            }
            dispatch(EventType.ROWS_FETCHED, "Total rows fetched: " + entries.size(), "Took: " + elapsed + " ms");
        } catch (Exception e) {
            dispatch(EventType.QUERY_ERROR, "Error retrieving entries: " + e.getMessage());
        }
        return entries;
    }

    @Override
    public List<Map<String, Object>> getEntries(int limit) {
        List<Map<String, Object>> entries = new ArrayList<>();
        try {
            long start = System.nanoTime();
            MongoCollection<Document> collection = getCollection();
            long end = System.nanoTime();
            long elapsed = (end - start) / 1000000;
            for (Document doc : collection.find().limit(limit)) {
                entries.add(new HashMap<>(doc));
            }
            dispatch(EventType.ROWS_FETCHED, "Total rows fetched: " + entries.size() + ", Took: " + elapsed + " ms");
        } catch (Exception e) {
            dispatch(EventType.QUERY_ERROR, "Error retrieving limited entries: " + e.getMessage());
        }
        return entries;
    }

    @Override
    public List<String> getFieldNames() {
        Set<String> fields = new LinkedHashSet<>();
        try {
            long start = System.nanoTime();
            MongoCollection<Document> collection = getCollection();
            long end = System.nanoTime();
            long elapsed = (end - start) / 1000000;
            for (Document doc : collection.find()) {
                fields.addAll(doc.keySet());
            }
            dispatch(EventType.COLUMN_NAMES_RETRIEVED, "Took: " + elapsed + " ms");
        } catch (Exception e) {
            dispatch(EventType.QUERY_ERROR, "Error retrieving field names: " + e.getMessage());
        }
        return new ArrayList<>(fields);
    }


    private void dispatch(EventType type) {
        if(eventManager != null) {
            eventManager.notify(type);
        }
    }

    private void dispatch(EventType type, Object ...payload) {
        if(eventManager != null) {
            eventManager.notify(type, payload);
        }
    }

    @Override
    public String toString() {
        return query;
    }

    public static class QueryBuilder implements DBQueryBuilder{
        private final StringBuilder query = new StringBuilder();

        private QueryBuilder append(String sqlPart) {
            query.append(" ").append(sqlPart);
            return this;
        }
        @Override
        public QueryBuilder query(String condition) {
            return append(condition);
        }

        @Override
        public QueryBuilder query(String condition, Object ...params) {
            return append(String.format(condition, params));
        }

        @Override
        public DBQueryHandler build() {
            return new MongoDBQueryHandler(this);
        }
    }

}
