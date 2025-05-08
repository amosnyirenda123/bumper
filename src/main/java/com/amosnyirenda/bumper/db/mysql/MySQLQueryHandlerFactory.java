package com.amosnyirenda.bumper.db.mysql;

import com.amosnyirenda.bumper.core.DBQueryBuilder;
import com.amosnyirenda.bumper.core.DBQueryHandlerFactory;

public class MySQLQueryHandlerFactory implements DBQueryHandlerFactory {
    @Override
    public DBQueryBuilder createBuilder() {
        return new MySQLQueryHandler.QueryBuilder();
    }
}
