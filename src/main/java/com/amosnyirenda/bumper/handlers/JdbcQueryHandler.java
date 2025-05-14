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
import java.util.stream.Collectors;

public class JdbcQueryHandler implements DBQueryHandler {
    private final String query;
    private final String tableName;
    private final String database;
    private DBConnector connector;
    private EventManager eventManager;

    private JdbcQueryHandler(QueryBuilder builder) {

        this.query = builder.query.toString();
        this.tableName = builder.tableName;
        this.database = builder.database;
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
    public boolean insert(Object... documents) {
        return false;
    }


    @Override
    public boolean insert(Map<String, Object> data) {
        if (data == null || data.isEmpty()) return false;

        String sql = "INSERT INTO " + tableName + " (" +
                String.join(", ", data.keySet()) +
                ") VALUES (" +
                data.keySet().stream().map(k -> "?").collect(Collectors.joining(", ")) +
                ")";

        try (PreparedStatement stmt = ((Connection) connector.connect()).prepareStatement(sql)) {
            int index = 1;
            for (Object value : data.values()) {
                stmt.setObject(index++, value);
            }
            long start = System.nanoTime();
            stmt.executeUpdate();
            long end = System.nanoTime();
            long executionTimeMillis = (end - start) / 1_000_000;
            dispatch(EventType.INSERT, "Query: " + sql, "Took: " + executionTimeMillis + " ms");
            return true;
        } catch (SQLException e) {
            dispatch(EventType.QUERY_ERROR, "Insert failed: " + e.getMessage());
            return false;
        }
    }



    @Override
    public boolean insert(List<String> columns, List<Object> values, boolean ifNotExist) {
        if (!ifNotExist) {
            return insert(columns);
        }
        String sql = "INSERT IGNORE INTO " + tableName + " (" +
                String.join(", ", columns) + ") VALUES (" +
                columns.stream().map(c -> "?").collect(Collectors.joining(", ")) + ")";

        try (PreparedStatement stmt = ((Connection)connector.connect()).prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            dispatch(EventType.QUERY_ERROR, "Insert-if-not-exist failed: " + e.getMessage());
            return false;
        }
    }


    @Override
    public boolean delete(String id) {
        return false;
    }

    @Override
    public boolean delete(String targetColumn, Object targetValue) {
        String sql = "DELETE FROM " + tableName + String.format(" WHERE %s = ?", targetColumn);
        try (PreparedStatement stmt = ((Connection)connector.connect()).prepareStatement(sql)) {
            stmt.setObject(1, targetValue);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            dispatch(EventType.QUERY_ERROR, "Delete failed: " + e.getMessage());
            return false;
        }
    }


    @Override
    public boolean update(String targetColumn, Object targetValue, Map<String, Object> updates) {
        if (updates == null || updates.isEmpty()) return false;

        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        sql.append(updates.keySet().stream().map(k -> k + " = ?").collect(Collectors.joining(", ")));
        sql.append(String.format(" WHERE %s = ?", targetColumn));


        try (PreparedStatement stmt = ((Connection)connector.connect()).prepareStatement(sql.toString())) {
            int i = 1;
            for (Object val : updates.values()) {
                stmt.setObject(i++, val);
            }
            stmt.setObject(i, targetValue);
            int updated = stmt.executeUpdate();
            return updated > 0;
        } catch (SQLException e) {
            dispatch(EventType.QUERY_ERROR, "Update failed: " + e.getMessage());
            return false;
        }
    }


    @Override
    public boolean update(String id, Map<String, Object> updates) {
        return false;
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
        private String tableName;
        private String database;

        private QueryBuilder append(String sqlPart) {
            query.append(" ").append(sqlPart);
            return this;
        }


        @Override
        public QueryBuilder target(String source) {
            tableName = source;
            return this;
        }

        @Override
        public QueryBuilder target(String source, Object ...params) {
            tableName =  String.format(source, params);
            return this;
        }

        @Override
        public DBQueryBuilder use(String param) {
            this.database = param;
            return this;
        }


        public DBQueryHandler buildHandler() {
            return new JdbcQueryHandler(this);
        }
    }
}
