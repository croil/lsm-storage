package org.lsm.db.table;

import org.lsm.Entry;
import org.lsm.db.iterator.PeekTableIterator;

import java.io.Closeable;
import java.io.IOException;
import java.lang.foreign.MemorySegment;

public interface TableMap<K, V> extends Closeable {

    Entry<MemorySegment> getEntry(K key);

    K ceilKey(K key);

    default K floorKey(K key) { //TODO: Implement it
        throw new UnsupportedOperationException("Floor key operation is not supported yet");
    }

    int size() throws IOException;

    PeekTableIterator<K> keyIterator(K key, K to);

    boolean contains(K sstKey);

    boolean isTombstone(Entry<K> entry);
}
