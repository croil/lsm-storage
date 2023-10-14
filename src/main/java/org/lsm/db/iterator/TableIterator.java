package org.lsm.db.iterator;

import java.util.Iterator;

public interface TableIterator<T> extends Iterator<T> {
    int getTableNumber();
}
