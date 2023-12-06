package org.lsm;

import org.lsm.db.entry.BaseEntry;
import org.lsm.db.dao.Dao;
import org.lsm.db.entry.Entry;
import org.lsm.db.dao.PersistentDao;
import org.lsm.db.table.KeyComparator;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;


public class Main {

    //TODO: To factory.
    private static MemorySegment fromString(String data) {
        Objects.requireNonNull(data, "String data is null");
        return MemorySegment.ofArray(data.getBytes(StandardCharsets.UTF_8));
    }

    private static String toString(MemorySegment data) {
        Objects.requireNonNull(data, "Memory segment data is null");
        return new String(data.toArray(ValueLayout.JAVA_BYTE), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        Path dbPath = Path.of(System.getProperty("user.dir")).resolve("db");
        try (Dao<MemorySegment, Entry<MemorySegment>> dao = new PersistentDao(dbPath)) {
            MemorySegment key = fromString("k00001"); // from String to MemorySegment
            MemorySegment value = fromString("v00001");
            dao.upsert(new BaseEntry<>(key, value));
            System.out.println(toString(dao.get(key).value())); // v00001
            dao.upsert(new BaseEntry<>(key, null));
            System.out.println(dao.get(key).value() == null); // true
        } catch (IOException ex) { // close + flush
            System.err.println(ex.getMessage());
        }
    }
}
