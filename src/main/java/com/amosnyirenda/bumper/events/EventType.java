package com.amosnyirenda.bumper.events;


/**
 * Represents various types of database-related events that can occur during
 * the lifecycle of database operations.
 * <p>
 * These event types are used in conjunction with {@link EventManager} and
 * {@link EventListener} to handle database activities such as inserts, updates,
 * queries, connection events, and error handling.
 * </p>
 *
 * Example:
 * <pre>
 *     eventManager.subscribe(EventType.QUERY, event -> System.out.println("Query executed"));
 * </pre>
 *
 * @author Amos Nyirenda
 */
public enum EventType {
    /**
     * Indicates a document or record was inserted.
     */
    INSERT,

    /**
     * Indicates a document or record was updated.
     */
    UPDATE,

    /**
     * Indicates a document or record was deleted.
     */
    DELETE,

    /**
     * Indicates a database query was executed.
     */
    QUERY,

    /**
     * Indicates a database object (e.g., table or collection) was created.
     */
    CREATE,

    /**
     * Indicates an error occurred while executing a query.
     */
    QUERY_ERROR,

    /**
     * Indicates a successful database connection was established.
     */
    CONNECTION_ESTABLISHED,

    /**
     * Indicates a database connection was successfully closed.
     */

    CONNECTION_CLOSED,

    /**
     * Indicates an error occurred while attempting to connect to the database.
     */

    CONNECTION_ERROR,


    /**
     * Indicates an explicit disconnect operation occurred.
     */

    DISCONNECT,

    /**
     * Indicates that the names of the columns were successfully retrieved.
     */

    COLUMN_NAMES_RETRIEVED,

    /**
     * Indicates that rows of data were successfully fetched from the database.
     */
    ROWS_FETCHED,

    /**
     * Indicates that a specific column value was retrieved.
     */
    COLUMN_VALUE_RETRIEVED,
}
