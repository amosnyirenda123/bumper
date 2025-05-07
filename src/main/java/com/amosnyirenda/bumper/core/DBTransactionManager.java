package com.amosnyirenda.bumper.core;

public interface DBTransactionManager {
    void beginTransaction();
    void commit();
    void rollback();
    boolean isInTransaction();
}
