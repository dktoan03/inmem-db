package com.example.miniredis.server;

import com.example.miniredis.command.CommandProcessor;
import com.example.miniredis.datastore.DataStore;
import com.example.miniredis.persistence.PersistenceManager;
import com.example.miniredis.pubsub.PubSubManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 6379;
    private final DataStore dataStore = new DataStore();
    private final PersistenceManager persistenceManager = new PersistenceManager(dataStore);
    private final PubSubManager pubSubManager = new PubSubManager();
    private final CommandProcessor processor = new CommandProcessor(dataStore, pubSubManager);
    private final ExecutorService clientPool = Executors.newCachedThreadPool();

    public void start() {
        persistenceManager.loadSnapshot();
        persistenceManager.startAutoSave();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("MiniRedis TCP Server started on port " + PORT);
            while (true) {
                Socket client = serverSocket.accept();
                clientPool.submit(() -> handleClient(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket client) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))) {
            out.write("Welcome to MiniRedis Java\n");
            out.flush();
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                String response = processor.process(parts, out);
                out.write(response + "\n");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}
