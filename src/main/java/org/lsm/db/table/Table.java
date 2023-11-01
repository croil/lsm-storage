package org.lsm.db.table;

import org.lsm.Entry;
import org.lsm.db.iterator.TableIterator;

import java.io.Closeable;
import java.lang.foreign.MemorySegment;
import java.util.Iterator;

public interface Table<K> extends Iterable<Entry<K>>, Closeable {

    TableIterator<K> tableIterator(K from, boolean fromInclusive, K to, boolean toInclusive);

    @Override
    default TableIterator<K> iterator() {
        return tableIterator(null, true, null, true);
    }

    int rows();

    long byteSize();

    void clear();
}
