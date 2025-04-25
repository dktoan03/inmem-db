package com.example.miniredis.datastore;

import java.util.concurrent.*;

public class DataStore {
    private final ConcurrentHashMap<String, Object> store = new ConcurrentHashMap<>();

    public void set(String key, String value) {
        store.put(key, value);
    }

    public String get(String key) {
        Object value = store.get(key);
        return value instanceof String ? (String) value : null;
    }

    public void del(String key) {
        store.remove(key);
    }

    public void hset(String key, String field, String value) {
        store.computeIfAbsent(key, k -> new ConcurrentHashMap<String, String>());
        ConcurrentHashMap<String, String> hash = (ConcurrentHashMap<String, String>) store.get(key);
        hash.put(field, value);
    }

    public String hget(String key, String field) {
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
    }

    public void rpush(String key, String value) {
        store.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<String>());
        ConcurrentLinkedDeque<String> list = (ConcurrentLinkedDeque<String>) store.get(key);
        list.addLast(value);
    }

    public String lpop(String key) {
        ConcurrentLinkedDeque<String> list = (ConcurrentLinkedDeque<String>) store.get(key);
        if (list != null) {
            return list.pollFirst();
        }
        return null;
    }

    public String rpop(String key) {
        ConcurrentLinkedDeque<String> list = (ConcurrentLinkedDeque<String>) store.get(key);
        if (list != null) {
            return list.pollLast();
        }
        return null;
    }
}