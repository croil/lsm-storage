package org.lsm.db.table;

import org.lsm.db.Cell;
import org.lsm.db.iterator.PeekTableIterator;

import java.io.Closeable;
import java.io.IOException;

public interface TableMap<K, V> extends Closeable {

    Cell<K> getCell(K key);

    K ceilKey(K key);

    default K floorKey(K key) { //TODO: Implement it
        throw new UnsupportedOperationException("Floor key operation is not supported yet");
    }

    int size() throws IOException;

    PeekTableIterator<K> keyIterator(K key, K to);
}
