package com.amosnyirenda.bumper.handlers;

import com.amosnyirenda.bumper.core.DBConnector;
import com.amosnyirenda.bumper.core.DBQueryBuilder;
import com.amosnyirenda.bumper.core.DBQueryHandler;
import com.amosnyirenda.bumper.events.EventManager;
import com.amosnyirenda.bumper.events.EventType;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcQueryHandler implements DBQueryHandler {
    private final String query;
    private DBConnector connector;
    private EventManager eventManager;

    private JdbcQueryHandler(QueryBuilder builder) {
        this.query = builder.query.toString();
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
        List<String> list = new ArrayList<>();
        try (Connection conn = (Connection) connector.connect();
             Statement stmt = conn.createStatement()

        ) {
            long start = System.nanoTime();
            ResultSet rs = stmt.executeQuery(query);
            long end = System.nanoTime();
            long executionTimeMillis = (end - start) / 1_000_000;
            while (rs.next()) {
                list.add(rs.getString(field));
            }

            dispatch(EventType.COLUMN_VALUE_RETRIEVED, "Query: "+ query, "Took: " + executionTimeMillis + " ms," + " values " + list.size() + "values");
        } catch (SQLException e) {
            dispatch(EventType.QUERY_ERROR, query, "Failed to execute query " + e);
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> getEntries() {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = (Connection) connector.connect();
             Statement stmt = conn.createStatement();
        ) {
            long start = System.nanoTime();
            ResultSet rs = stmt.executeQuery(query);
            long end = System.nanoTime();
            long executionTimeMillis = (end - start) / 1_000_000;
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    row.put(columnName, columnValue);
                }
                rows.add(row);
            }
            dispatch(EventType.ROWS_FETCHED, "Query: "+ query, "Took: " + executionTimeMillis + " ms," + " Rows fetched: " + rows.size() + " rows");
        } catch (SQLException e) {
            dispatch(EventType.QUERY_ERROR, query, "Failed to execute query " + e);
        }
        return rows;
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
    public List<Map<String, Object>> getEntries(int limit) {
        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection conn = (Connection) connector.connect();
             Statement stmt = conn.createStatement();
             ) {

            long start = System.nanoTime();
            ResultSet rs = stmt.executeQuery(query);
            long end = System.nanoTime();
            long executionTimeMillis = (end - start) / 1_000_000;
            ResultSetMetaData metaData = rs.getMetaData();

            int columnCount = metaData.getColumnCount();

            int currentRow = 0;
            while (rs.next() && currentRow < limit) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object columnValue = rs.getObject(i);
                    row.put(columnName, columnValue);
                }
                rows.add(row);
                currentRow++;
            }

            dispatch(EventType.ROWS_FETCHED, "Query: "+ query, "Took: " + executionTimeMillis + " ms," + " Rows fetched: " + rows.size() + " rows");

        } catch (SQLException e) {
            dispatch(EventType.QUERY_ERROR, query, "Failed to execute query " + e);
        }

        return rows;
    }

    @Override
    public List<String> getFieldNames() {
        List<String> columnNames = new ArrayList<>();
        try (Connection conn = (Connection) connector.connect();
             Statement stmt = conn.createStatement()

        ) {
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnNames.add(metaData.getColumnLabel(i));
            }

            if (eventManager != null) {
                eventManager.notify(EventType.COLUMN_NAMES_RETRIEVED);
            }
        } catch (SQLException e) {
            dispatch(EventType.QUERY_ERROR, query, "Failed to execute query " + e);
        }
        return columnNames;
    }

    @Override
    public String toString() {
        return query;
    }


    public static class QueryBuilder implements DBQueryBuilder {
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


        public DBQueryHandler build() {
            return new JdbcQueryHandler(this);
        }
    }
}
