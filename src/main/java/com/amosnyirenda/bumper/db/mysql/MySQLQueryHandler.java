package com.amosnyirenda.bumper.db.mysql;

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


public class MySQLQueryHandler implements DBQueryHandler {
    private final String query;
    private DBConnector connector;
    private EventManager eventManager;

    private MySQLQueryHandler(QueryBuilder builder) {
        this.query = builder.query.toString();
    }

    public MySQLQueryHandler withConnector(DBConnector connector) {
        this.connector = connector;
        return this;
    }

    @Override
    public MySQLQueryHandler withEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
        return this;
    }


    @Override
    public List<String> getColumn(String column) {
        List<String> list = new ArrayList<>();
        try (Connection conn = connector.connect();
             Statement stmt = conn.createStatement()

        ) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                 list.add(rs.getString(column));
            }

            if(eventManager != null) {
                eventManager.notify(EventType.COLUMN_VALUE_RETRIEVED);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get column"+ e);
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> getRows() {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = connector.connect();
             Statement stmt = conn.createStatement()

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
    public List<Map<String, Object>> getRows(int rowLimit) {
        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection conn = connector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            int currentRow = 0;
            while (rs.next() && currentRow < rowLimit) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object columnValue = rs.getObject(i);
                    row.put(columnName, columnValue);
                }
                rows.add(row);
                currentRow++;
            }

            if(eventManager != null) {
                eventManager.notify(EventType.ROWS_FETCHED);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute query: " + e.getMessage(), e);
        }

        return rows;
    }

    @Override
    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<>();
        try (Connection conn = connector.connect();
             Statement stmt = conn.createStatement()

        ) {
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnNames.add(metaData.getColumnLabel(i));
            }

            if (eventManager != null) {
                eventManager.notify(EventType.COLUMN_NAMES_RETRIEVED);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get column names."+ e);
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
        public DBQueryBuilder createTable(String table) {
            return null;
        }

        @Override
        public DBQueryBuilder addColumn(String column, String dataType, String constraint) {
            return null;
        }

        @Override
        public DBQueryBuilder insertInto(String table) {
            return null;
        }

        @Override
        public DBQueryBuilder columns(String... columns) {
            return null;
        }

        @Override
        public DBQueryBuilder values(String... values) {
            return null;
        }

        public QueryBuilder select(String columns) {
            return append("SELECT " + columns);
        }

        public QueryBuilder from(String table) {
            return append("FROM " + table);
        }

        public QueryBuilder where(String condition) {
            return append("WHERE " + condition);
        }

        public QueryBuilder where(String condition, Object ...params) {
            return append("WHERE " + String.format(condition, params));
        }

        public QueryBuilder and(String condition) {
            return append("AND " + condition);
        }

        public QueryBuilder or(String condition) {
            return append("OR " + condition);
        }

        public QueryBuilder join(String table, String onClause) {
            return append("JOIN " + table + " ON " + onClause);
        }

        public QueryBuilder leftJoin(String table, String onClause) {
            return append("LEFT JOIN " + table + " ON " + onClause);
        }

        public QueryBuilder rightJoin(String table, String onClause) {
            return append("RIGHT JOIN " + table + " ON " + onClause);
        }
        public QueryBuilder leftOuterJoin(String table, String onClause) {
            return append("LEFT OUTER JOIN " + table + " ON " + onClause);
        }
        public QueryBuilder rightOuterJoin(String table, String onClause) {
            return append("RIGHT OUTER JOIN " + table + " ON " + onClause);
        }

        public QueryBuilder leftInnerJoin(String table, String onClause) {
            return append("LEFT INNER JOIN " + table + " ON " + onClause);
        }
        public QueryBuilder rightInnerJoin(String table, String onClause) {
            return append("RIGHT INNER JOIN " + table + " ON " + onClause);
        }

        public QueryBuilder naturalJoin(String table) {
            return append("NATURAL JOIN " + table);
        }

        public QueryBuilder groupBy(String clause) {
            return append("GROUP BY " + clause);
        }
        public QueryBuilder having(String condition) {
            return append("HAVING " + condition);
        }

        public QueryBuilder orderByDESC(String clause) {
            return append("ORDER BY " + clause + " DESC");
        }
        public QueryBuilder orderByASC(String clause) {
            return append("ORDER BY " + clause + " ASC");
        }

        public QueryBuilder limit(int limit) {
            return append("LIMIT " + limit);
        }

        public DBQueryHandler build() {
            return new MySQLQueryHandler(this);
        }
    }
}

