package com.amosnyirenda.bumper.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages event subscriptions and dispatching for various types of database operations.
 * <p>
 * This class enables the registration and notification of {@link EventListener}s
 * for specific {@link EventType}s such as query execution, connection errors,
 * or successful inserts.
 * </p>
 *
 * Example usage:
 * <pre>
 *     EventManager manager = new EventManager(EventType.QUERY_SUCCESS, EventType.QUERY_ERROR);
 *     manager.subscribe(EventType.QUERY_SUCCESS, event -> System.out.println("Query succeeded!"));
 *     manager.notify(EventType.QUERY_SUCCESS);
 * </pre>
 *
 * @author Amos Nyirenda
 */

public class EventManager {
    Map<EventType, List<EventListener>> listeners = new HashMap<>();

    /**
     * Constructs an {@code EventManager} that supports the specified event types.
     *
     * @param operations a list of {@link EventType}s that can be subscribed to and triggered
     */
    public EventManager(EventType... operations) {
        for (EventType operation : operations) {
            this.listeners.put(operation, new ArrayList<>());
        }
    }

    /**
     * Subscribes a listener to a specific event type.
     *
     * @param eventType the type of event to listen for
     * @param listener  the {@link EventListener} that will be triggered when the event occurs
     */

    public void subscribe(EventType eventType, EventListener listener) {
        List<EventListener> users = listeners.get(eventType);
        users.add(listener);
    }

    /**
     * Unsubscribes a listener from a specific event type.
     *
     * @param eventType the event type to unsubscribe from
     * @param listener  the listener to remove
     */

    public void unsubscribe(EventType eventType, EventListener listener) {
        List<EventListener> users = listeners.get(eventType);
        users.remove(listener);
    }


    /**
     * Notifies all listeners subscribed to a given event type without additional payload.
     *
     * @param eventType the event to be triggered
     */
    public void notify(EventType eventType) {
        List<EventListener> users = listeners.get(eventType);
        for (EventListener listener : users) {
            listener.onEvent(eventType);
        }
    }

    /**
     * Notifies all listeners subscribed to a given event type with an optional payload.
     *
     * @param eventType the event to be triggered
     * @param payload   additional data to pass to the listeners (e.g., error messages or data)
     */

    public void notify(EventType eventType, Object ...payload) {
        List<EventListener> users = listeners.get(eventType);
        for (EventListener listener : users) {
            listener.onEvent(eventType, payload);
        }
    }
}
