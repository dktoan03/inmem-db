package com.example.miniredis.command;

import com.example.miniredis.datastore.DataStore;

public class CommandProcessor {
    private final DataStore dataStore;

    public CommandProcessor(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public String process(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length == 0) return "Invalid command";

        String cmd = parts[0].toUpperCase();
        try {
            switch (cmd) {
                case "SET":
                    dataStore.set(parts[1], parts[2]);
                    return "OK";
                case "GET":
                    return dataStore.get(parts[1]);
                case "DEL":
                    dataStore.del(parts[1]);
                    return "OK";
                case "HSET":
                    dataStore.hset(parts[1], parts[2], parts[3]);
                    return "OK";
                case "HGET":
                    return dataStore.hget(parts[1], parts[2]);
                case "LPUSH":
                    dataStore.lpush(parts[1], parts[2]);
                    return "OK";
                case "RPUSH":
                    dataStore.rpush(parts[1], parts[2]);
                    return "OK";
                case "LPOP":
                    return dataStore.lpop(parts[1]);
                case "RPOP":
                    return dataStore.rpop(parts[1]);
                default:
                    return "Unknown command";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}