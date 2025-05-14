package com.amosnyirenda.bumper.factories;

import com.amosnyirenda.bumper.core.DBQueryBuilder;
import com.amosnyirenda.bumper.core.DBQueryHandlerFactory;
import com.amosnyirenda.bumper.handlers.MongoDBQueryHandler;

/**
 * Factory class for creating {@link MongoDBQueryHandler} instances.
 * <p>
 * This implementation of {@link DBQueryHandlerFactory} is specific to MongoDB,
 * a NoSQL database. It allows the configuration of target collections and databases
 * before building query handlers that interact with MongoDB.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 *     DBQueryHandlerFactory factory = new MongoDBQueryHandlerFactory();
 *     DBQueryHandler handler = factory
 *         .target("books")
 *         .use("library")
 *         .build();
 * }</pre>
 * </p>
 *
 * @author Amos Nyirenda
 * @see MongoDBQueryHandler
 * @see DBQueryHandlerFactory
 */

public class MongoDBQueryHandlerFactory implements DBQueryHandlerFactory {
    @Override
    public DBQueryBuilder createBuilder() {
        return new MongoDBQueryHandler.QueryBuilder();
    }
}
