package com.amosnyirenda.bumper.factories;

import com.amosnyirenda.bumper.core.DBQueryBuilder;
import com.amosnyirenda.bumper.core.DBQueryHandlerFactory;
import com.amosnyirenda.bumper.handlers.MongoDBQueryHandler;

public class MongoDBQueryHandlerFactory implements DBQueryHandlerFactory {
    @Override
    public DBQueryBuilder createBuilder() {
        return new MongoDBQueryHandler.QueryBuilder();
    }
}
