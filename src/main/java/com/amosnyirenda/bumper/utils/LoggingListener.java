package com.amosnyirenda.bumper.utils;

import com.amosnyirenda.bumper.events.EventListener;
import com.amosnyirenda.bumper.events.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class LoggingListener implements EventListener {
    private static final Logger logger = LoggerFactory.getLogger(LoggingListener.class);

    @Override
    public void onEvent(EventType event, Object... payload) {
        switch (event) {
            case INSERT -> logger.info("Insert operation initiated.");
            case UPDATE -> logger.info("Update operation initiated.");
            case DELETE -> logger.info("Delete operation initiated.");
            case QUERY -> logger.info("Query operation executed.");
            case CREATE -> logger.info("Create operation performed.");
            case QUERY_ERROR -> logger.error("An error occurred during a query.");
            case CONNECTION_ESTABLISHED -> logger.info("Database connection successfully established.");
            case CONNECTION_CLOSED -> logger.info("Database connection closed.");
            case CONNECTION_ERROR -> logger.error("Database connection error.");
            case DISCONNECT -> logger.info("Client disconnected.");
            case COLUMN_NAMES_RETRIEVED -> logger.debug("Column names were retrieved.");
            case ROWS_FETCHED -> logger.debug("Database rows fetched.");
            case COLUMN_VALUE_RETRIEVED -> logger.debug("A column value was retrieved.");
            default -> logger.info("Unhandled event: {}", event);
        }

        if (payload != null && payload.length > 0) {
            for (Object obj : payload) {
                logger.debug("Payload: {}", obj);
            }
        } else {
            logger.debug("No payload provided for event: {}", event);
        }
    }

    @Override
    public void onEvent(EventType event) {
        logger.info("Event triggered: {}", event);
    }
}
