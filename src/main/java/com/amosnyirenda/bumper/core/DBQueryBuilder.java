package com.amosnyirenda.bumper.core;



public interface DBQueryBuilder {

    DBQueryBuilder query(String condition);
    DBQueryBuilder query(String condition, Object ...params);
    DBQueryHandler build();
}
