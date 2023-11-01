package org.lsm.db.table;

import org.lsm.BaseEntry;
import org.lsm.Entry;
import org.lsm.db.Utils;
import org.lsm.db.exceptions.FileChannelException;
import org.lsm.db.iterator.TableIterator;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class SSTable implements Table<MemorySegment> {
    /**
     * Constable size of SSTable.
     */
    private final int rows; // todo: replace with long?

    /**
     * Unique number of this SST.
     */
    private final int sstNumber; // System.currentTimeMillis() timestamp in metaHandleBLock
    private final Arena arena;
    private final Path path;
    private final MemorySegment index;
    private final MemorySegment data;
    private final long byteSize;
    private final Comparator<MemorySegment> comparator;

    public SSTable(Path path, Comparator<MemorySegment> comparator, int sstNumber) {
        this.arena = Arena.ofShared();
        try (FileChannel sstChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            FileChannel.MapMode mode = FileChannel.MapMode.READ_ONLY;
            long footerOffset = sstChannel.size() - Utils.FOOTER_SIZE;
            MemorySegment footerSegment = sstChannel.map(mode, footerOffset, Utils.FOOTER_SIZE, arena);
            Footer footer = Footer.createFooter(footerSegment);
            Handle indexHandle = footer.getIndexHandle();
            this.index = sstChannel.map(mode, indexHandle.offset(), indexHandle.size(), arena);
            this.data = sstChannel.map(mode, 0L, indexHandle.offset(), arena);
            this.byteSize = indexHandle.offset();
            this.rows = Math.toIntExact(indexHandle.size() / (2L * Long.BYTES) - 1); //todo: fix to long or rem
            this.sstNumber = sstNumber;
            this.comparator = comparator;
            this.path = path;
        } catch (IOException ex) {
            throw new FileChannelException("Couldn't create FileChannel by path" + path, ex);
        }
    }

    @Override
    public void close() {
        if (arena.scope().isAlive()) {
            arena.close();
        }
    }

    @Override
    public int rows() {
        return rows;
    }

    @Override
    public TableIterator<MemorySegment> tableIterator(MemorySegment from, boolean fromInclusive,
                                                      MemorySegment to, boolean toInclusive) {
        return new TableIterator<>() {
            int start = (from == null ? 0 : binarySearch(from)) + Boolean.compare(!fromInclusive, false);
            final int end = (to == null ? rows : binarySearch(to)) + Boolean.compare(toInclusive, true);

            @Override
            public int getTableNumber() {
                return sstNumber;
            }

            @Override
            public boolean hasNext() {
                return start < rows && start <= end;
            }

            @Override
            public Entry<MemorySegment> next() {
                return new BaseEntry<>(getKeyByIndex(start), getValueByIndex(start++));
            }
        };
    }

    @Override
    public void clear() {
        try {
            Files.deleteIfExists(path); //todo: clear makes table invalid, but still opened.
        } catch (IOException e) {
            throw new FileChannelException("Couldn't remove file channel by path " + path, e);
        }
    }

    @Override
    public long byteSize() {
        return byteSize;
    }

    private MemorySegment getKeyByIndex(int index) {
        Objects.checkIndex(index, rows);
        long keyOffset = getKeyOffset(index);
        long valueOffset = Math.abs(getValueOffset(index));
        return data.asSlice(keyOffset, valueOffset - keyOffset);
    }

    private MemorySegment getValueByIndex(int index) {
        Objects.checkIndex(index, rows);
        long valueOffset = getValueOffset(index);
        if (valueOffset < 0) {
            return null;
        }
        long nextKeyOffset = getKeyOffset(index + 1);
        return data.asSlice(valueOffset, nextKeyOffset - valueOffset);
    }


    private long getKeyOffset(int i) {
        long rawOffset = 2L * i * Long.BYTES;
        return index.get(ValueLayout.JAVA_LONG_UNALIGNED, rawOffset);
    }

    private long getValueOffset(int i) {
        long rawOffset = (2L * i + 1) * (long) Long.BYTES;
        return index.get(ValueLayout.JAVA_LONG_UNALIGNED, rawOffset);
    }

    private int binarySearch(MemorySegment key) {
        int l = 0;
        int r = rows - 1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            MemorySegment middle = getKeyByIndex(mid);
            if (comparator.compare(key, middle) <= 0) {
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }
        return l;
    }
}
