package org.lsm.db.table;

import org.lsm.db.entry.Entry;

import java.io.Closeable;
import java.lang.foreign.MemorySegment;

public interface TableWriter extends Closeable {
    void writeEntry(Entry<MemorySegment> entry);

    void writeIndexHandle(Entry<MemorySegment> entry);

    void writeFooter(Footer footer);
}
