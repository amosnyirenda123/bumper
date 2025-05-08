package com.amosnyirenda.bumper.core;

public interface DBQueryHandler {
    DBQueryHandler withConnector(DBConnector connector);
    void execute();
}
