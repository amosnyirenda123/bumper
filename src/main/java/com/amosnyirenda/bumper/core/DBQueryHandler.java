package com.amosnyirenda.bumper.core;

import java.util.List;
import java.util.Map;

public interface DBQueryHandler {
    DBQueryHandler withConnector(DBConnector connector);
    void execute();
    List<String> getColumn(String column);
    List<Map<String, Object>> getRows();
    List<Map<String, Object>> getRows(int rowLimit);
    List<String> getColumnNames();
}
