package com.example.miniredis.datastore;

import java.util.concurrent.*;

public class DataStore {
    private final ConcurrentHashMap<String, Object> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> expireTimes = new ConcurrentHashMap<>();

    public DataStore() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::cleanExpiredKeys, 1, 1, TimeUnit.SECONDS);
    }

    private void cleanExpiredKeys() {
        long now = System.currentTimeMillis();
        for (String key : expireTimes.keySet()) {
            if (expireTimes.get(key) < now) {
                store.remove(key);
                expireTimes.remove(key);
            }
        }
    }

    public void set(String key, String value) {
        store.put(key, value);
        expireTimes.remove(key);
    }

    public String get(String key) {
        if (isExpired(key)) return null;
        Object value = store.get(key);
        return value instanceof String ? (String) value : null;
    }

    public void del(String key) {
        store.remove(key);
        expireTimes.remove(key);
    }

    public void hset(String key, String field, String value) {
        store.computeIfAbsent(key, k -> new ConcurrentHashMap<String, String>());
        ConcurrentHashMap<String, String> hash = (ConcurrentHashMap<String, String>) store.get(key);
        hash.put(field, value);
        expireTimes.remove(key);
    }

    public String hget(String key, String field) {
        if (isExpired(key)) return null;
        ConcurrentHashMap<String, String> hash = (ConcurrentHashMap<String, String>) store.get(key);
        if (hash != null) {
            return hash.get(field);
        }
        return null;
    }

    public void lpush(String key, String value) {
        store.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<String>());
        ConcurrentLinkedDeque<String> list = (ConcurrentLinkedDeque<String>) store.get(key);
        list.addFirst(value);
        expireTimes.remove(key);
    }

    public void rpush(String key, String value) {
        store.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<String>());
        ConcurrentLinkedDeque<String> list = (ConcurrentLinkedDeque<String>) store.get(key);
        list.addLast(value);
        expireTimes.remove(key);
    }

    public String lpop(String key) {
        if (isExpired(key)) return null;
        ConcurrentLinkedDeque<String> list = (ConcurrentLinkedDeque<String>) store.get(key);
        if (list != null) {
            return list.pollFirst();
        }
        return null;
    }

    public String rpop(String key) {
        if (isExpired(key)) return null;
        ConcurrentLinkedDeque<String> list = (ConcurrentLinkedDeque<String>) store.get(key);
        if (list != null) {
            return list.pollLast();
        }
        return null;
    }

    public void expire(String key, int seconds) {
        if (store.containsKey(key)) {
            expireTimes.put(key, System.currentTimeMillis() + seconds * 1000);
        }
    }

    public long ttl(String key) {
        if (!store.containsKey(key)) return -2;
        Long expireTime = expireTimes.get(key);
        if (expireTime == null) return -1;
        long ttlMillis = expireTime - System.currentTimeMillis();
        return ttlMillis > 0 ? ttlMillis / 1000 : -2;
    }

    private boolean isExpired(String key) {
        Long expireTime = expireTimes.get(key);
        if (expireTime == null) return false;
        if (expireTime < System.currentTimeMillis()) {
            store.remove(key);
            expireTimes.remove(key);
            return true;
        }
        return false;
    }
}
