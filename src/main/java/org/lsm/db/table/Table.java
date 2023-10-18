package org.lsm.db.table;

import org.lsm.Entry;
import org.lsm.db.iterator.TableIterator;

import java.io.Closeable;
import java.io.IOException;
import java.lang.foreign.MemorySegment;

public interface Table<K> extends Closeable {

    int size() throws IOException;

    TableIterator<K> tableIterator(K from, boolean fromInclusive, K to, boolean toInclusive);
}
