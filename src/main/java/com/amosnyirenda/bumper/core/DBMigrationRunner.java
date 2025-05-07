package com.amosnyirenda.bumper.core;

public interface DBMigrationRunner {
    void migrate();         // Runs all new migrations
    void rollbackLast();    // Rollbacks last migration
    void reset();           // Rolls back all and re-applies
}
