package com.amosnyirenda.bumper.core;

public interface DBCacheProvider<K, V> {
    void put(K key, V value);
    V get(K key);
    boolean contains(K key);
    void invalidate(K key);
    void clear();
}
