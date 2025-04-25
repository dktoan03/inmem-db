package com.example.miniredis.pubsub;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PubSubManager {
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<BufferedWriter>> subscribers = new ConcurrentHashMap<>();

    public void subscribe(String channel, BufferedWriter clientWriter) {
        subscribers.computeIfAbsent(channel, k -> new CopyOnWriteArrayList<>()).add(clientWriter);
    }

    public int publish(String channel, String message) {
        CopyOnWriteArrayList<BufferedWriter> subs = subscribers.get(channel);
        if (subs == null) return 0;

        int receivers = 0;
        for (BufferedWriter writer : subs) {
            try {
                writer.write("Message from [" + channel + "]: " + message + "\n");
                writer.flush();
                receivers++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return receivers;
    }
}
