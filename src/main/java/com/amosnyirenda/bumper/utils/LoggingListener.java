package com.amosnyirenda.bumper.utils;

import com.amosnyirenda.bumper.events.EventListener;
import com.amosnyirenda.bumper.events.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingListener implements EventListener {
    private static final Logger logger = LoggerFactory.getLogger(LoggingListener.class);
    @Override
    public void onEvent(EventType event, Object ...payload) {
        logger.info("Event triggered: {}", event);

        if (payload != null && payload.length > 0) {
            for (Object obj : payload) {
                logger.info("Payload: {}", obj);
            }
        } else {
            logger.info("No payload provided for event: {}", event);
        }
    }

    @Override
    public void onEvent(EventType event) {
        logger.info("Event triggered: {}", event);
    }
}
