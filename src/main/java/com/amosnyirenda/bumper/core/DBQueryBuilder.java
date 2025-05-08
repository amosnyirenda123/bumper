package com.amosnyirenda.bumper.core;


import com.amosnyirenda.bumper.db.mysql.MySQLQueryHandler;

public interface DBQueryBuilder {
    DBQueryBuilder select(String columns);
    DBQueryBuilder from(String table);
    DBQueryBuilder where(String condition);
    DBQueryBuilder where(String condition, Object ...params);
    DBQueryBuilder and(String condition);
    DBQueryBuilder or(String condition);
    DBQueryBuilder join(String table, String onClause);
    DBQueryBuilder leftJoin(String table, String onClause);
    DBQueryBuilder rightJoin(String table, String onClause);
    DBQueryBuilder leftOuterJoin(String table, String onClause);
    DBQueryBuilder rightOuterJoin(String table, String onClause);
    DBQueryBuilder leftInnerJoin(String table, String onClause);
    DBQueryBuilder rightInnerJoin(String table, String onClause);
    DBQueryBuilder naturalJoin(String table);
    DBQueryBuilder groupBy(String clause);
    DBQueryBuilder having(String condition);
    DBQueryBuilder orderByDESC(String clause);
    DBQueryBuilder orderByASC(String clause);
    DBQueryBuilder limit(int limit);
    DBQueryHandler build();
}
