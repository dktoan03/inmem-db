package com.example.miniredis.command;

import com.example.miniredis.datastore.DataStore;
import com.example.miniredis.pubsub.PubSubManager;

import java.io.BufferedWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CommandProcessor {
    private final DataStore dataStore;
    private final PubSubManager pubSubManager;

    public CommandProcessor(DataStore dataStore, PubSubManager pubSubManager) {
        this.dataStore = dataStore;
        this.pubSubManager = pubSubManager;
    }

    public String process(String[] parts, BufferedWriter clientWriter) {
        if (parts.length == 0)
            return "Invalid command";

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
                case "SETX":
                    dataStore.set(parts[1], parts[3]);
                    dataStore.expire(parts[1], Integer.parseInt(parts[2]));
                    return "OK";
                case "EXPIRE":
                    dataStore.expire(parts[1], Integer.parseInt(parts[2]));
                    return "OK";
                case "TTL":
                    return String.valueOf(dataStore.ttl(parts[1]));
                case "SUBSCRIBE":
                    pubSubManager.subscribe(parts[1], clientWriter);
                    return "Subscribed to " + parts[1];
                case "PUBLISH":
                    int receivers = pubSubManager.publish(parts[1], parts[2]);
                    return "Message published to " + receivers + " subscribers";
                default:
                    return "Unknown command";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
