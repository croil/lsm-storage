package org.lsm.db;


import org.lsm.Entry;

/**
 * Inspired by cassandra db.
 */
public abstract class Cell<T> implements Entry<T> {
    public interface Factory<T> {
        Cell<T> create(Entry<T> entry);
        Cell<T> create(T key, T value);
    }


    public abstract long valueSize();

    public abstract boolean isTombstone();
}
