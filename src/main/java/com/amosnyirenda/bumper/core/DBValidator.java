package com.amosnyirenda.bumper.core;

import com.amosnyirenda.bumper.config.DBConfig;
import com.amosnyirenda.bumper.exceptions.DBValidationException;

public interface DBValidator {
    boolean validateQuery(String query);
    boolean validateConnectionParams(DBConfig config);
    void validateEntity(Object entity) throws DBValidationException;
}
