package com.amosnyirenda.bumper.factories;

import com.amosnyirenda.bumper.core.DBQueryBuilder;
import com.amosnyirenda.bumper.core.DBQueryHandlerFactory;
import com.amosnyirenda.bumper.handlers.JdbcQueryHandler;

/**
 * Factory class for creating {@link JdbcQueryHandler} instances.
 * <p>
 * This implementation of {@link DBQueryHandlerFactory} is specific to relational
 * databases using JDBC (e.g., MySQL, PostgreSQL, SQLite, Oracle, SQL Server).
 * It is responsible for constructing query handlers tailored to SQL-based systems.
 * </p>
 *
 * <p>
 * Usage example:
 * <pre>{@code
 *     DBQueryHandlerFactory factory = new JdbcQueryHandlerFactory();
 *     DBQueryHandler handler = factory
 *         .target("SELECT * FROM books")
 *         .use("library")
 *         .build();
 * }</pre>
 * </p>
 *
 * @author Amos Nyirenda
 * @see JdbcQueryHandler
 * @see DBQueryHandlerFactory
 */

public class JdbcQueryHandlerFactory implements DBQueryHandlerFactory {
    @Override
    public DBQueryBuilder createBuilder() {
        return new JdbcQueryHandler.QueryBuilder();
    }
}
