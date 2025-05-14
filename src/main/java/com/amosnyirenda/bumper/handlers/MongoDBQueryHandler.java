package com.amosnyirenda.bumper.handlers;

import com.amosnyirenda.bumper.core.DBConnector;
import com.amosnyirenda.bumper.core.DBQueryBuilder;
import com.amosnyirenda.bumper.core.DBQueryHandler;
import com.amosnyirenda.bumper.events.EventManager;
import com.amosnyirenda.bumper.events.EventType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.*;
import org.bson.types.ObjectId;

import java.util.*;

/**
 * Handles the construction and execution of MongoDB queries.
 * <p>
 * This class abstracts interactions with MongoDB
 * for common CRUD operations.
 * </p>
 *
 * <p>Use the {@code withConnector()} and {@code withEventManager()} methods to configure
 * execution behavior before calling any database operations.</p>
 * {@code withConnector() } is used to inject the connector from where the MongoClient is derived
 * {@code withEventManager()} is important for registering events
 * @author
 * Amos Nyirenda
 */

public class MongoDBQueryHandler implements DBQueryHandler {
    private final String query;
    private final String database;
    private DBConnector connector;
    private EventManager eventManager;


    /**
     * Constructs a query handler for a given collection and database.
     *
     * @param queryBuilder contains the query and database name derived from the query builder
     */
    MongoDBQueryHandler(QueryBuilder queryBuilder){
        this.query = queryBuilder.query.toString();
        this.database = queryBuilder.database;
    }


    /**
     * Sets the connector used to interact with the database.
     *
     * @param connector the database connector
     * @return this instance for method chaining
     */
    @Override
    public DBQueryHandler withConnector(DBConnector connector) {
        this.connector =  connector;
        return this;
    }

    /**
     * References the collection the caller wishes to work on.
     * @return MongoClient
     */

    private MongoCollection<Document> getCollection() {
        if (connector == null) {
            throw new IllegalStateException("MongoClient is not initialized. Call connect() first.");
        }

        return ((MongoClient)connector.connect()).getDatabase(this.database).getCollection(this.query);

    }

    /**
     * @param eventManager the event manager
     * @return this instance for method chaining
     */
    @Override
    public DBQueryHandler withEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
        return this;
    }

    /**
     * Retrieves values of a field from documents in a  collection
     *
     * @param field the database connector
     * @return list of values
     */
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

    /**
     * Retrieves the entries of a collection (All documents and their properties)
     * @return list of values
     */
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


    /**
     * Inserts one or more documents into the target database collection or table.
     *
     * @param documents one or more documents to be inserted. Each can be a {@code Map<String, Object>} or a {@code Document}.
     * @return {@code true} if the insert operation was successful, otherwise {@code false}
     * @throws IllegalArgumentException if a document is of an unsupported type
     */

    @Override
    public boolean insert(Object... documents) {
        try {
            MongoCollection<Document> collection = getCollection();
            List<Document> docsToInsert = new ArrayList<>();

            for (Object obj : documents) {
                if (obj instanceof Document) {
                    docsToInsert.add((Document) obj);
                } else if (obj instanceof Map<?, ?> mapObj) {
                    Map<String, Object> safeMap = new HashMap<>();
                    for (Map.Entry<?, ?> entry : mapObj.entrySet()) {
                        if (!(entry.getKey() instanceof String)) {
                            throw new IllegalArgumentException("Map keys must be strings");
                        }
                        safeMap.put((String) entry.getKey(), entry.getValue());
                    }
                    docsToInsert.add(new Document(safeMap));
                } else {
                    throw new IllegalArgumentException("Unsupported document type: " + obj.getClass().getName());
                }
            }

            if (!docsToInsert.isEmpty()) {
                if (docsToInsert.size() == 1) {
                    collection.insertOne(docsToInsert.get(0));
                } else {
                    collection.insertMany(docsToInsert);
                }

                dispatch(EventType.INSERT, docsToInsert.size() + " documents inserted");
                return true;
            }

        } catch (Exception e) {
            dispatch(EventType.QUERY_ERROR, "Insert failed: " + e.getMessage());
        }
        return false;
    }


    @Override
    public boolean insert(Map<String, Object> data) {
        return false;
    }

    @Override
    public boolean insert(List<String> columns, List<Object> values, boolean ifNotExist) {
        return false;
    }

    /**
     * Deletes a document or record by its ID.
     *
     * @param id the identifier of the record to delete
     * @return {@code true} if the deletion is successful, otherwise {@code false}
     */

    @Override
    public boolean delete(String id) {
        try {
            MongoCollection<Document> collection = getCollection();
            ObjectId objectId;

            try {
                objectId = new ObjectId(id);
            } catch (IllegalArgumentException e) {
                dispatch(EventType.QUERY_ERROR, "Invalid ID format: " + id);
                return false;
            }

            DeleteResult result = collection.deleteOne(Filters.eq("_id", objectId));
            dispatch(EventType.DELETE, "Delete count: " + result.getDeletedCount());
            return result.getDeletedCount() > 0;

        } catch (Exception e) {
            dispatch(EventType.QUERY_ERROR, "Delete failed for ID " + id + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a document or record by its ID.
     *
     * @param targetColumn column name to be used in the where clause
     * @param targetValue value to be used in the where clause
     * @return {@code true} if the deletion is successful, otherwise {@code false}
     */

    @Override
    public boolean delete(String targetColumn, Object targetValue) {
        return false;
    }



    @Override
    public boolean update(String targetColumn, Object targetValue, Map<String, Object> updates) {
        return false;
    }


    /**
     * Deletes a document or record by its ID.
     *
     * @param id of Document
     * @param updates map of key value pairs where the key is the prop to update
     * @return {@code true} if the deletion is successful, otherwise {@code false}
     */
    @Override
    public boolean update(String id, Map<String, Object> updates) {
        try {
            MongoCollection<Document> collection = getCollection();
            ObjectId objectId;

            try {
                objectId = new ObjectId(id);
            } catch (IllegalArgumentException e) {
                dispatch(EventType.QUERY_ERROR, "Invalid ID format: " + id);
                return false;
            }

            Document updateDoc = new Document();
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                updateDoc.append(entry.getKey(), entry.getValue());
            }

            UpdateResult result = collection.updateOne(
                    Filters.eq("_id", objectId),
                    new Document("$set", updateDoc)
            );

            return result.getModifiedCount() > 0;

        } catch (Exception e) {
            dispatch(EventType.QUERY_ERROR, "Update failed for ID " + id + ": " + e.getMessage());
            return false;
        }
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


    /**
     * A builder class for creating and executing database queries.
     *
     * <p>This class supports both relational and NoSQL databases
     * through a unified API.
     *
     * @author Amos Nyirenda
     */

    public static class QueryBuilder implements DBQueryBuilder{
        private String database;
        private final StringBuilder query = new StringBuilder();

        private QueryBuilder append(String sqlPart) {
            query.append(sqlPart);
            return this;
        }

        /**
         * Receives as argument table name for relational databases or collection name for NoSQL databases.
         * Receives sql query
         * @param source which is a String.
         */

        @Override
        public QueryBuilder target(String source) {
            return append(source);
        }


        @Override
        public QueryBuilder target(String source, Object ...params) {
            return append(String.format(source, params));
        }

        @Override
        public DBQueryBuilder use(String param) {
            this.database = param;
            return this;
        }

        @Override
        public DBQueryBuilder query(String query) {
            return this;
        }

        @Override
        public DBQueryHandler buildHandler() {
            return new MongoDBQueryHandler(this);
        }
    }

}
