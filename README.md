# LSM Storage

My implementation of a NoSQL database is based on the LSM tree, utilizing the new tools in Java 21. While working on this project, I learned a lot about the internals of NoSQL databases, such as Cassandra and Google LevelDB. Additionally, I gained proficiency in using the new memory management tools in Java 21.
## Dao
"Dao" stands for Data Access Object, which is a database interface providing methods for interacting with data. In my project, there are two implementations of this interface.
**InMemoryDao** is a class that implements methods for working with data exclusively in memory using a "MemTable."
**PersistentDao** is the main class for working with data, implementing algorithms for persisting data to disk using SST tables.

## API
The DAO interface provides the following API methods
1. `Entry<T> get(T key)` - get entry with key and value by key
2. `void upsert(Entry<T> entry)` inserts the entry into the memtable or replaces it with a new one if it was already present in the memtable. An entry with a null value indicates that the value was deleted.
3. `Iterator<Entry<T>> get(T from, T to)` - returns an iterator over the all entries from the key `from` inclusive to the key `to` exclusive.
4. `Iterator<Entry<T>> allFrom(T from), allTo(T to) and all()` methods are special cases of `get(T from, T to)` method and return an iterator over the entries with specific range.
7. `void flush()` - flushes memtable into new SST table when memtable size exceeds a specified threshold. Method doesn't block any other Dao method. It's perfromed in a background thread.
8. `void compact()` - compacts all SST tables into one table. Method doesn't block any other Dao method. It's perfromed in a background thread.
9. `void close()` - close database. Waiting for all background process such as flushing or compaction. Calling other dao methods after calling `close()` is UB.

## Documentation
[Documentation](/) is online and bundled with source code.(now is unavailable)

## New features

With the use of Java 21, new classes have become available, making memory management more efficient and convenient, while also eliminating certain limitations present in popular databases. The use of [MemorySegment](https://cr.openjdk.org/~mcimadamore/jdk/FFM_22_PR/javadoc/java.base/java/lang/foreign/MemorySegment.html) as keys and values instead of conventional [ByteBuffer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/ByteBuffer.html), [String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html), and byte array has removed the 2 GB restriction, and allows convenient control of data lifespan and multi-threaded access using Arena. However, the database supports any type for key storage, including String, ByteBuffer, and byte array.

## Usage
```java
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
```

## Performance
Performance tests will be added in the future.
