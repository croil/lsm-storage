package org.lsm.db;


import org.lsm.Entry;

/**
 * Inspired by cassandra db.
 */
public abstract class Cell<T> implements Entry<T> {
    public abstract long valueSize();

    public abstract boolean isTombstone();
}
