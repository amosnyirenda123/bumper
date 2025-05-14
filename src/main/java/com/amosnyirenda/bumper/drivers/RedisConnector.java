package com.amosnyirenda.bumper.drivers;

import com.amosnyirenda.bumper.core.DBConnectionConfig;
import com.amosnyirenda.bumper.core.DBConnector;
import com.amosnyirenda.bumper.events.EventManager;
import com.amosnyirenda.bumper.events.EventType;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;

@RequiredArgsConstructor
public class RedisConnector implements DBConnector {
    private final DBConnectionConfig config;
    private final EventManager eventManager;
    private Jedis jedis;

    @Override
    public Jedis connect() {

        try{
            long start = System.nanoTime();
            this.jedis = new Jedis(config.getUrl());
            long end = System.nanoTime();
            long elapsed = (end - start) / 1_000_000;

            dispatch(EventType.CONNECTION_ESTABLISHED, this.jedis.getConnection().toString(), "Took: " + elapsed + "ms");
            return this.jedis;
        }catch (Exception e){
            dispatch(EventType.CONNECTION_ERROR, this.jedis.getConnection().toString(), e.getMessage());
            return null;
        }

    }

    private void dispatch(EventType event, Object... payload) {
        if(eventManager != null){
            eventManager.notify(event, payload);
        }
    }

    @Override
    public void close() {
        if(jedis != null){
            try{
                jedis.close();
            }catch (Exception e){
                dispatch(EventType.CONNECTION_ERROR, this.jedis.getConnection().toString(), e.getMessage());
            }
        }
    }
}
