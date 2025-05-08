package com.amosnyirenda.bumper.db.mysql;

import com.amosnyirenda.bumper.core.DBConnector;
import com.amosnyirenda.bumper.core.DBQueryBuilder;
import com.amosnyirenda.bumper.core.DBQueryHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class MySQLQueryHandler implements DBQueryHandler {
    private final String query;
    private DBConnector connector;

    private MySQLQueryHandler(QueryBuilder builder) {
        this.query = builder.query.toString();
    }

    public MySQLQueryHandler withConnector(DBConnector connector) {
        this.connector = connector;
        return this;
    }

    @Override
    public void execute() {
//        try (Connection conn = connector.connect();
//             Statement stmt = conn.createStatement()) {
//            stmt.execute(this.query);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        System.out.println("Executing query: " + query);
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

