package ru.eliseevtech.openweathersdk.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class LruTimedCache<K, V> {

    private final int maxSize;
    private final Duration ttl;
    private final LinkedHashMap<K, Entry<V>> map;

    public LruTimedCache(int maxSize, Duration ttl) {
        this.maxSize = maxSize;
        this.ttl = ttl;
        this.map = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, Entry<V>> eldest) {
                return size() > LruTimedCache.this.maxSize;
            }
        };
    }

    public synchronized Optional<V> getIfFresh(K key) {
        Entry<V> e = map.get(key);

        if (e == null) {
            return Optional.empty();
        }

        if (Instant.now().isAfter(e.storedAt.plus(ttl))) {
            map.remove(key);
            return Optional.empty();
        }

        return Optional.of(e.value);
    }

    public synchronized void put(K key, V value) {
        map.put(key, new Entry<>(value, Instant.now()));
    }

    public synchronized Map<K, V> snapshotAll() {
        Map<K, V> out = new LinkedHashMap<>();
        map.forEach((k, e) -> out.put(k, e.value));
        return out;
    }

    public synchronized void clear() {
        map.clear();
    }

    private static final class Entry<V> {
        final V value;
        final Instant storedAt;

        Entry(V v, Instant t) {
            this.value = v;
            this.storedAt = t;
        }
    }

}
