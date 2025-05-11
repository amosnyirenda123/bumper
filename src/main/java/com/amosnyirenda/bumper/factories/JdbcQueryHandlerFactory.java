package com.amosnyirenda.bumper.factories;

import com.amosnyirenda.bumper.core.DBQueryBuilder;
import com.amosnyirenda.bumper.core.DBQueryHandlerFactory;
import com.amosnyirenda.bumper.handlers.JdbcQueryHandler;

public class JdbcQueryHandlerFactory implements DBQueryHandlerFactory {
    @Override
    public DBQueryBuilder createBuilder() {
        return new JdbcQueryHandler.QueryBuilder();
    }
}
