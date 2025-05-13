package com.amosnyirenda.bumper.core;



public interface DBQueryBuilder {

    DBQueryBuilder target(String source);
    DBQueryBuilder target(String source, Object ...params);
    DBQueryBuilder use(String param);
    DBQueryHandler build();
}
