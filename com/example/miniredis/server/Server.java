package com.example.miniredis.server;

import com.example.miniredis.command.CommandProcessor;
import com.example.miniredis.datastore.DataStore;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 6379;
    private final DataStore dataStore = new DataStore();
    private final CommandProcessor processor = new CommandProcessor(dataStore);
    private final ExecutorService clientPool = Executors.newCachedThreadPool();

    public void start() {
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
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))
        ) {
            out.write("Welcome to MiniRedis Java\n");
            out.flush();
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("exit")) break;
                String response = processor.process(line);
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