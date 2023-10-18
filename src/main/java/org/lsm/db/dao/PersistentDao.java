package org.lsm.db.dao;

import org.lsm.Entry;
import org.lsm.db.TablesOrganizer;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.nio.file.Path;
import java.util.Iterator;

public class PersistentDao extends InMemoryDao {
    /**
     * Organizer class, that controls all sst tables.
     */
    private final TablesOrganizer organizer;

    public PersistentDao(Path path) {
        this.organizer = new TablesOrganizer(path, memTable);
    }

    @Override
    public void flush() throws IOException {
        organizer.flushMemTable();
    }

    @Override
    public void close() throws IOException {
        if (memTable.size() != 0) flush();
        organizer.close();
        super.close();
    }

    @Override
    public Iterator<Entry<MemorySegment>> get(MemorySegment from, MemorySegment to) {
        return organizer.mergeIterator(from, true, to, false);
    }

    @Override
    public Entry<MemorySegment> get(MemorySegment key) {
        return organizer.mergeIterator(key, true, key, true).next();
    }
}
