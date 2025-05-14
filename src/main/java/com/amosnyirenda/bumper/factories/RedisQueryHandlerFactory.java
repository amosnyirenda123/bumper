package com.amosnyirenda.bumper.factories;

import com.amosnyirenda.bumper.core.DBQueryBuilder;
import com.amosnyirenda.bumper.core.DBQueryHandlerFactory;
import com.amosnyirenda.bumper.handlers.RedisQueryHandler;

public class RedisQueryHandlerFactory implements DBQueryHandlerFactory {
    @Override
    public DBQueryBuilder createBuilder() {
        return new RedisQueryHandler.QueryBuilder();
    }
}
