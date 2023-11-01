package org.lsm.db.dao;

import org.lsm.Dao;
import org.lsm.Entry;
import org.lsm.db.table.KeyComparator;
import org.lsm.db.table.MemTable;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Comparator;
import java.util.Iterator;

public class InMemoryDao implements Dao<MemorySegment, Entry<MemorySegment>> {
    private final MemTable memTable;

    public InMemoryDao() {
        this.memTable = new MemTable(new KeyComparator());
    }

    @Override
    public Iterator<Entry<MemorySegment>> get(MemorySegment from, MemorySegment to) {
        return memTable.tableIterator(from, true, to, false);
    }

    @Override
    public Entry<MemorySegment> get(MemorySegment key) {
        return memTable.tableIterator(key, true, key, true).next();
    }

    @Override
    public void upsert(Entry<MemorySegment> entry) {
        memTable.upsert(entry);
    }

    @Override
    public void close() throws IOException {
        memTable.close();
    }
}
