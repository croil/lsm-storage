package org.lsm.db.iterator;

import org.lsm.Entry;
import org.lsm.db.Candidate;

import java.lang.foreign.MemorySegment;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

public class MergeIterator implements Iterator<Entry<MemorySegment>> {
    private final Queue<Candidate<MemorySegment>> queue;
    private Entry<MemorySegment> entry;
    private MemorySegment lowerBoundKey;
    private final Comparator<MemorySegment> memoryComparator;

    public MergeIterator(List<TableIterator<MemorySegment>> iterators, Comparator<MemorySegment> memoryComparator) {
        this.memoryComparator = memoryComparator;
        this.queue = iterators.stream()
                .map(it -> new Candidate<>(it, memoryComparator))
                .filter(Candidate::nonLast)
                .collect(Collectors.toCollection(PriorityBlockingQueue::new));
        entry = getEntry();
    }

    private Entry<MemorySegment> getEntry() {
        if (queue.isEmpty()) {
            return null;
        }
        while (!queue.isEmpty()) {
            var candidate = queue.poll();
            if (lowerBoundKey == null || memoryComparator.compare(lowerBoundKey, candidate.entry().key()) < 0) {
                lowerBoundKey = candidate.entry().key();
                var entry = candidate.entry();
                candidate.update();
                if (candidate.nonLast()) {
                    queue.add(candidate);
                }
                if (entry.value() != null) { // Tombstone check.
                    return entry;
                }
            } else {
                candidate.update();
                if (candidate.nonLast()) {
                    queue.add(candidate);
                }
            }
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return this.entry != null;
    }

    @Override
    public Entry<MemorySegment> next() {
        var entry = this.entry;
        this.entry = getEntry();
        return entry;
    }
}
