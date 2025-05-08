package com.amosnyirenda.bumper.core;

public interface DBQueryMethods {
    public void select(String sql);
    public void from(String sql);
    public void where(String sql);
    public void groupBy(String sql);
    public void orderBy(String sql);
    public void limit(String sql);
    public void execute(String sql);
}
