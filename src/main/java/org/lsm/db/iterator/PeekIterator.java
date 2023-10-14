package org.lsm.db.iterator;

import java.util.Iterator;

public interface PeekIterator<T> extends Iterator<T> {
    T peek();
}
