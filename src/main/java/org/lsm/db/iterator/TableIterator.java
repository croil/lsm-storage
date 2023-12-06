package org.lsm.db.iterator;

import org.lsm.db.entry.Entry;

import java.util.Iterator;

public interface TableIterator<T> extends Iterator<Entry<T>> {
    int getTableNumber();
}
